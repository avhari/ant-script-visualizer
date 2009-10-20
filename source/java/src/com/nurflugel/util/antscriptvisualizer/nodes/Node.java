/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Nov 27, 2004 Time: 3:39:20 PM
 */
package com.nurflugel.util.antscriptvisualizer.nodes;

import com.nurflugel.util.antscriptvisualizer.AntParserUi;
import static com.nurflugel.util.antscriptvisualizer.OutputHandler.NEW_LINE;
import com.nurflugel.util.antscriptvisualizer.Utility;
import org.jdom.Element;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Base representation of a Node - amost all objects in the graph are types of nodes. Since they have so much common behavior, it made sense to have a
 * superclass. Screw the people who say inheritence is a bad idea!
 */
@SuppressWarnings({ "ProtectedField" })
public abstract class Node
{
  protected Antfile  buildFile;
  protected Element  element;
  protected NodeType nodeType;
  protected String   color;
  protected String   name;
  protected String   shape;

  @SuppressWarnings({ "OverriddenMethodCallInConstructor", "AbstractMethodCallInConstructor" })
  protected Node()
  {
    setNodeType();
  }

  @SuppressWarnings({ "OverriddenMethodCallInConstructor", "AbstractMethodCallInConstructor" })
  protected Node(String name, Antfile antfile)
  {
    Map properties = antfile.getProperties();

    this.name = Utility.expandPropertyName(name, properties);
    buildFile = antfile;
    setNodeType();
  }

  protected abstract void setNodeType();

  @Override
  @SuppressWarnings({ "AccessingNonPublicFieldOfAnotherObject" })
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }

    if (!(o instanceof Node))
    {
      return false;
    }

    final Node node = (Node) o;

    if ((buildFile != null) ? (!buildFile.getBuildFile().getAbsolutePath().equals(node.buildFile.getBuildFile().getAbsolutePath()))
                            : (node.buildFile != null))
    {
      return false;
    }

    return !((name != null) ? (!name.equals(node.name))
                            : (node.name != null));
  }

  @Override
  public int hashCode()
  {
    int result = ((name != null) ? name.hashCode()
                                 : 0);

    result = (29 * result) + ((buildFile.getBuildFile().getAbsolutePath() != null) ? buildFile.getBuildFile().getAbsolutePath().hashCode()
                                                                                   : 0);

    return result;
  }

  @Override
  public String toString()
  {
    return "name='" + name + "'";
  }

  /** what build file is this node in? */
  public Antfile getBuildFile()
  {
    return buildFile;
  }

  public String getColor()
  {
    return color;
  }

  /** Get any extra formatting information needed for the DOT output. */
  public String getDependencyExtraInfo()
  {
    return "";
  }

  public Element getElement()
  {
    return element;
  }

  public void setElement(Element element)
  {
    this.element = element;
  }

  public String getLabel()
  {
    return name;
  }

  public String getName()
  {
    return name;
  }

  protected void setName(String name)
  {
    this.name = name;
  }

  public String getShape()
  {
    return shape;
  }

  public boolean isDependency()
  {
    return false;
  }

  public boolean isMacrodef()
  {
    return false;
  }

  protected void setAntfile(Antfile antfile)
  {
    buildFile = antfile;
  }

  /** Write the DOT file output for this node. */
  public void writeOutput(DataOutputStream out, AntParserUi ui) throws IOException
  {
    if (name != null)
    {
      String niceName = getNiceName();

      if (shouldPrint(ui.shouldShowTaskdefs(), ui.shouldShowMacrodefs(), ui.shouldShowAntcalls(), ui.shouldShowAntcalls(), ui.shouldShowTargets()))
      {
        String line = "\t\t" + niceName + " [label=\"" + name + "\" shape=" + shape + " color=" + color + " ]; ";

        out.writeBytes(line + NEW_LINE);
      }
    }
  }

  /** Get's a name for the node which will work with GraphViz - it' doesn't like /, ', etc. */
  public String getNiceName()
  {
    String nicename      = Utility.replaceBadChars(name).trim();

    String buildFileName = Utility.replaceBadChars(getBuildFile().getBuildFile().getAbsolutePath());

    return buildFileName + "_" + NodeType.TARGET + "_" + nicename;
  }

  /**
   * Should this node print out in the final image?
   *
   * @param   includeTaskdefs   if true, taskdefs will be printed
   * @param   includeMacrodefs  if true, macrodefs will be printed
   * @param   includeAnts       if true, ants will be printed
   * @param   includeAntCalls   if true, antcalls will be printed
   * @param   includeTargets    if true, targets will be printed
   *
   * @return
   */
  @SuppressWarnings({ "OverlyComplexBooleanExpression" })
  public boolean shouldPrint(boolean includeTaskdefs, boolean includeMacrodefs, boolean includeAnts, boolean includeAntCalls, boolean includeTargets)
  {
    boolean isTaskdef  = (includeTaskdefs && (nodeType == NodeType.TASKDEF));
    boolean istarget   = (includeTargets && (nodeType == NodeType.TARGET));
    boolean isMacrodef = (includeMacrodefs && (nodeType == NodeType.MACRODEF));
    boolean isAntCall  = (includeAntCalls && (nodeType == NodeType.ANTCALL));
    boolean isAnt      = (includeAnts && (nodeType == NodeType.ANT));

    return (isTaskdef || istarget || isMacrodef || isAnt || isAntCall);
  }
}
