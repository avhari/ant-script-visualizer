package com.nurflugel.util.gradlescriptvisualizer.parser;

import com.nurflugel.util.gradlescriptvisualizer.domain.Line;
import com.nurflugel.util.gradlescriptvisualizer.domain.Task;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.nurflugel.util.gradlescriptvisualizer.domain.Task.findOrCreateImplicitTasksByExecute;
import static com.nurflugel.util.gradlescriptvisualizer.domain.Task.findOrCreateImplicitTasksByLine;
import static com.nurflugel.util.gradlescriptvisualizer.domain.Task.findOrCreateTaskByLine;
import static org.apache.commons.io.FileUtils.checksumCRC32;
import static org.apache.commons.io.FileUtils.readLines;
import static org.apache.commons.io.FilenameUtils.getFullPath;
import static org.apache.commons.lang.StringUtils.*;

/** Created with IntelliJ IDEA. User: douglas_bullard Date: 5/30/12 Time: 22:07 To change this template use File | Settings | File Templates. */
public class GradleFileParser
{
  private Map<String, Task> taskMap       = new HashMap<String, Task>();
  private Map<File, Long>   fileChecksums;

  public GradleFileParser(Map<File, Long> fileChecksums)
  {
    this.fileChecksums = fileChecksums;
  }

  // -------------------------- OTHER METHODS --------------------------
  public List<Task> getTasks() throws IOException
  {
    return new ArrayList<Task>(taskMap.values());
  }

  public Map<String, Task> getTasksMap()
  {
    return taskMap;
  }

  public void parseFile(File file) throws IOException
  {
    System.out.println("file = " + file.getAbsolutePath());

    if (file.exists())
    {
      List<Line> lines = readLinesInFile(file);

      processLines(file, lines);
    }
    else
    {
      throw new FileNotFoundException("Expected file not found: " + file.getAbsolutePath());
    }
  }

  /**
   * We wrap the text lines into object lines so we can determine parsing strings or lines better. Later on, we may modify the line class to be more
   * broad than a single line of text.
   */
  List<Line> readLinesInFile(File file) throws IOException
  {
    List<Line>   lines     = new ArrayList<Line>();
    List<String> textLines = readLines(file);

    for (String textLine : textLines)
    {
      lines.add(new Line(textLine));
    }

    return lines;
  }

  private void processLines(File file, List<Line> lines) throws IOException
  {
    findTasksInLines(lines);
    findImports(lines, file);
  }

  void findTasksInLines(List<Line> lines)
  {
    for (Line line : lines)
    {
      String trimmedLine = line.getText().trim();

      if (trimmedLine.startsWith("task "))
      {
        Task task = findOrCreateTaskByLine(taskMap, line);

        taskMap.put(task.getTaskName(), task);
      }

      if (trimmedLine.contains(".dependsOn"))
      {
        findOrCreateImplicitTasksByLine(taskMap, trimmedLine);
      }

      if (trimmedLine.contains(".execute"))
      {
        findOrCreateImplicitTasksByExecute(taskMap, trimmedLine);

        // todo  now find the task which this is referred inside of - doAfter, doBefore, etc -
        Task containingTask = findContainingTask(lines, line);
      }
    }
  }

  /**
   * Given the line in the collection of lines, scan backwards until you find something which matches...
   *
   * <p>todo Better yet, when a task is explicitly declared, do this sort of thing there...</p>
   */
  private Task findContainingTask(List<Line> lines, Line line)
  {
    return null;
  }

  void findImports(List<Line> lines, File file) throws IOException
  {
    for (Line line : lines)
    {
      String text = line.getText().trim();

      if (text.startsWith("apply from: "))
      {
        text = substringAfter(text, "apply from: ");
        text = remove(text, '\'');
        text = remove(text, '\"');

        if (text.startsWith("http:"))
        {
          findUrlImport(text);
        }
        else
        {
          String fileName = trim(text);

          // non-absolute path must be resolved relative to the current file
          if (!fileName.startsWith("/"))
          {
            String parent = getFullPath(file.getAbsolutePath());

            fileName = parent + fileName;
          }

          parseFile(fileName);
        }
      }
    }
  }

  private void findUrlImport(String location) throws IOException
  {
    URL        url       = new URL(location);
    String     bigString = IOUtils.toString(url);
    String[]   tokens    = bigString.split("\n");
    List<Line> lines     = new ArrayList<Line>();

    for (String token : tokens)
    {
      lines.add(new Line(token));
    }

    processLines(null, lines);  // todo deal with null file location as it's a URL somehow...
  }

  public void parseFile(String fileName) throws IOException
  {
    File file = new File(fileName);

    fileChecksums.put(file, checksumCRC32(file));  // todo figure out what the main files are and only rerender them
    parseFile(file);
  }

  public void purgeAll()
  {
    taskMap.clear();
  }
}
