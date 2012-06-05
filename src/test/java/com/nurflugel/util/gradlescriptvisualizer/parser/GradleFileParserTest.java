package com.nurflugel.util.gradlescriptvisualizer.parser;

import com.nurflugel.util.gradlescriptvisualizer.domain.Line;
import com.nurflugel.util.gradlescriptvisualizer.domain.Task;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.testng.Assert.*;

@Test(groups = "gradle")
public class GradleFileParserTest
{
  private static final String   SOURCE_PATH_IDEA   = "build/resources/test/gradle/";
  private static final String   SOURCE_PATH_GRADLE = "resources/test/gradle/";
  private static final String   PARSE_FILE_NAME    = "parsetest.gradle";
  private static final String[] TASK_NAMES         =
  {
    "copyHelp",                            //
    "copyLibs",                            //
    "copyResources",                       //
    "formatTestResults",                   //
    "installApp",                          //
    "listRuntimeJars",                     //
    "publishWebstart",                     //
    "signJars",                            //
    "test",                                //
    "transform",                           //
    "wrapper"                              //
  };

  @Test
  public void testCanary()
  {
    assertTrue(true);
  }

  @Test(groups = "failed")
  public void testFailHere()
  {
    File here  = new File(".");
    File there = new File(getFilePath(PARSE_FILE_NAME));

    ///Users/douglas_bullard/Documents/JavaStuff/Google_Code/AntScriptVisualizer_Google/gradleTrunk/build/.
    ///Users/douglas_bullard/Documents/JavaStuff/Google_Code/AntScriptVisualizer_Google/gradleTrunk/build/build/resources/test/gradle/parsetest.gradle
    if (false)
    {
      assertEquals(here.getAbsolutePath(), "dibble",
                   "Should have been some sort of path here.... \ndot path is: " + here.getAbsolutePath() + " parse file path is :"
                     + there.getAbsolutePath());
    }
  }

  /** We do this because when unit tests run in the IDE the base file path is different than when running under Gradle, so we have to adjust it. */
  public static String getFilePath(String fileName)
  {
    String  property            = System.getProperty("running.in.gradle");
    boolean isGradleEnvironment = BooleanUtils.toBooleanObject(property, "yes", null, "dibble");

    return isGradleEnvironment ? (SOURCE_PATH_GRADLE + fileName)
                               : (SOURCE_PATH_IDEA + fileName);
  }

  @Test(expectedExceptions = IOException.class)
  public void testReadBadFile() throws IOException
  {
    GradleFileParser parser = new GradleFileParser();

    parser.parseFile("dibble.gradle");
  }

  @Test
  public void testReadLinesFromFile() throws IOException
  {
    GradleFileParser parser = new GradleFileParser();

    // parser.parseFile(getFilePath(PARSE_FILE_NAME));
    List<Line> lines = parser.readLinesInFile(new File(getFilePath(PARSE_FILE_NAME)));

    assertFalse(lines.isEmpty());
  }

  @Test
  public void testFindTaskLines() throws IOException
  {
    GradleFileParser parser = new GradleFileParser();

    parser.parseFile(getFilePath(PARSE_FILE_NAME));

    List<Task> tasks = parser.getTasks();

    assertEquals(tasks.size(), TASK_NAMES.length, "Should have got a different size " + Arrays.toString(tasks.toArray()));
  }

  @Test
  public void testFindTaskNames() throws IOException
  {
    GradleFileParser parser = new GradleFileParser();

    parser.parseFile(getFilePath(PARSE_FILE_NAME));

    List<Task> tasks = parser.getTasks();

    for (Task task : tasks)
    {
      assertTrue(ArrayUtils.contains(TASK_NAMES, task.getTaskName()), "Couldn't find task " + task);
    }
  }

  @Test
  public void testFindTaskNamesInMap() throws IOException
  {
    GradleFileParser parser = new GradleFileParser();

    parser.parseFile(getFilePath(PARSE_FILE_NAME));

    Map<String, Task> tasks = parser.getTasksMap();

    for (String taskName : TASK_NAMES)
    {
      assertTrue(tasks.containsKey(taskName));
    }
  }

  @Test
  public void testFindTasksWithDependencies() throws IOException
  {
    GradleFileParser parser = new GradleFileParser();

    parser.parseFile(getFilePath(PARSE_FILE_NAME));

    Map<String, Task> tasks = parser.getTasksMap();

    validateTaskDependencies(tasks, "formatTestResults", "test", 1);
  }

  @Test
  public void testFindFileImports() throws IOException
  {
    GradleFileParser parser = new GradleFileParser();

    parser.parseFile(getFilePath("importTasks.gradle"));

    Map<String, Task> tasks = parser.getTasksMap();

    assertTrue(tasks.containsKey("publishWebstart"));
  }

  @Test
  public void testFindUrlImports() throws IOException
  {
    GradleFileParser parser = new GradleFileParser();

    parser.parseFile(getFilePath("importTasksFromUrl.gradle"));

    Map<String, Task> tasks = parser.getTasksMap();

    assertTrue(tasks.containsKey("publishWebstart"));
  }

  private void validateTaskDependencies(Map<String, Task> tasks, String taskName, String dependsOnTaskName, int expectedSize)
  {
    Task task = tasks.get(taskName);

    assertNotNull(task);

    Task       dependsOnTask = tasks.get(dependsOnTaskName);
    List<Task> taskList      = task.getDependsOn();

    assertEquals(taskList.size(), expectedSize, " got back task list: " + Arrays.toString(taskList.toArray()));

    Task foundTask = taskList.get(expectedSize - 1);

    assertEquals(foundTask.getTaskName(), dependsOnTask.getTaskName());  // validate that the tasks are the same task name
    assertEquals(foundTask, dependsOnTask);                              // validate that the tasks are the same task object
  }

  // just doing this to get a printout of the tasks...
  @Test
  public void testBigFile() throws IOException
  {
    GradleFileParser parser = new GradleFileParser();

    parser.parseFile(getFilePath("master-build.gradle"));

    List<Task> tasks = parser.getTasks();

    for (Task task : tasks)
    {
      task.printTask(0);
    }
  }

  // test case like check.dependsOn integrationTest
  @Test
  public void testImplicitDeclarationTask() throws IOException
  {
    GradleFileParser parser = new GradleFileParser();
    String[]         lines  = { "task dibble", "check.dependsOn integrationTest" };
    List<Line>       list   = getLinesFromArray(lines);

    parser.findTasksInLines(list);

    Map<String, Task> tasksMap = parser.getTasksMap();

    assertTrue(tasksMap.containsKey("dibble"));  // no brainer, should already work
    assertTrue(tasksMap.containsKey("check"));
  }

  // test case like check.dependsOn integrationTest
  @Test
  public void testImplicitDeclarationTask2() throws IOException
  {
    GradleFileParser parser = new GradleFileParser();
    String[]         lines  = { "task dibble", "check.dependsOn integrationTest" };
    List<Line>       list   = getLinesFromArray(lines);

    parser.findTasksInLines(list);

    Map<String, Task> tasksMap = parser.getTasksMap();

    assertTrue(tasksMap.containsKey("integrationTest"));
  }

  // test case like check.dependsOn integrationTest
  @Test
  public void testImplicitDeclarationDependsOnTask() throws IOException
  {
    GradleFileParser parser = new GradleFileParser();
    String[]         lines  = { "task dibble", "check.dependsOn integrationTest" };
    List<Line>       list   = getLinesFromArray(lines);

    parser.findTasksInLines(list);

    Map<String, Task> tasksMap = parser.getTasksMap();
    Task              check    = tasksMap.get("check");

    assertTrue(check.getDependsOn().get(0).getTaskName().equals("integrationTest"));
  }

  private List<Line> getLinesFromArray(String[] lines)
  {
    List<Line> results = new ArrayList<Line>();

    for (String line : lines)
    {
      results.add(new Line(line));
    }

    return results;
  }

  // test imported scripts recursively
  // ==>test find task dependsOn if task exists elsewhere in build script
  // test find dependsOn in task modification
  // test find dependsOn in iterative task modification
  // determine type of task
}
