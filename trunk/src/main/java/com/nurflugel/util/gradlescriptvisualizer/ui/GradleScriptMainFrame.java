package com.nurflugel.util.gradlescriptvisualizer.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.nurflugel.util.Os;
import com.nurflugel.util.gradlescriptvisualizer.Preferences;
import com.nurflugel.util.gradlescriptvisualizer.domain.Task;
import com.nurflugel.util.gradlescriptvisualizer.output.DotFileGenerator;
import com.nurflugel.util.gradlescriptvisualizer.parser.GradleFileParser;
import org.apache.commons.lang.StringUtils;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import static com.nurflugel.util.Os.findOs;
import static com.nurflugel.util.Util.center;
import static com.nurflugel.util.Util.setLookAndFeel;
import static javax.swing.JFileChooser.APPROVE_OPTION;
import static org.apache.commons.lang.StringUtils.isBlank;

/** Created with IntelliJ IDEA. User: douglas_bullard Date: 6/3/12 Time: 14:32 To change this template use File | Settings | File Templates. */
public class GradleScriptMainFrame
{
  private JButton      selectGradleScriptButton;
  private JCheckBox    watchFileForChangesCheckBox;
  private JRadioButton generateJustDOTFilesRadioButton;
  private JRadioButton generatePNGPDFFilesRadioButton;
  private JPanel       mainPanel;
  private JButton      quitButton;
  private JFrame       frame;
  private Preferences  preferences;
  private String       dotExecutablePath;
  private Os           os;

  public GradleScriptMainFrame()
  {
    os    = findOs();
    frame = new JFrame();
    frame.setContentPane(mainPanel);
    initializeUi();
    selectGradleScriptButton.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
          try
          {
            selectGradleScript();
          }
          catch (IOException e)
          {
            e.printStackTrace();
          }
        }
      });
    quitButton.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
          doQuitAction();
        }
      });
    preferences       = new Preferences();
    dotExecutablePath = preferences.getDotExecutablePath();  // todo this is ugly, fix it somehow

    if (isBlank(dotExecutablePath))
    {
      dotExecutablePath = os.getDefaultDotPath();
    }

    preferences.setDotExecutablePath(dotExecutablePath);
  }

  private void initializeUi()
  {
    setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel", frame);
    frame.pack();
    center(frame);

    // frame.setTitle("Gradle Script Visualizer v" + VERSION);
    frame.setTitle("Gradle Script Visualizer ");
    frame.setVisible(true);
  }

  private void selectGradleScript() throws IOException
  {
    JFileChooser chooser = new JFileChooser();
    String       lastDir = preferences.getLastDir();

    if (lastDir != null)
    {
      chooser.setCurrentDirectory(new File(lastDir));
    }

    chooser.setFileFilter(new FileNameExtensionFilter("Gradle scripts", "gradle", "groovy"));
    chooser.setMultiSelectionEnabled(true);

    int returnVal = chooser.showOpenDialog(frame);

    if (returnVal == APPROVE_OPTION)
    {
      File[] selectedFiles = chooser.getSelectedFiles();

      if (selectedFiles.length > 0)
      {
        preferences.setLastDir(selectedFiles[0].getParent());
      }

      GradleFileParser parser = new GradleFileParser();

      for (File selectedFile : selectedFiles)
      {
        parser.parseFile(selectedFile);
        System.out.println("selectedFile = " + selectedFile);

        List<Task>       tasks            = parser.getTasks();
        DotFileGenerator dotFileGenerator = new DotFileGenerator();
        List<String>     lines            = dotFileGenerator.createOutput(tasks);
        File             dotFile          = dotFileGenerator.writeOutput(lines, selectedFile.getAbsolutePath());

        try
        {
          os.openFile(dotFile.getAbsolutePath());
        }
        catch (InvocationTargetException e)
        {
          e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
          e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
          e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
          e.printStackTrace();
        }
      }

      // preferences.setLastDir(selectedFiles[0].getParent());
      // AntFileParser fileParser = new AntFileParser(os, preferences, this, selectedFiles);
      try
      {
        // fileParser.processBuildFile(true);
      }
      catch (Exception e)
      {
        e.printStackTrace();  // todo message dialog here
      }
    }
  }

  private void doQuitAction()
  {
    getOutputPreferencesFromUi();
    preferences.save();
    System.exit(0);
  }

  private void getOutputPreferencesFromUi() {}

  // --------------------------- main() method ---------------------------
  public static void main(String[] args)
  {
    GradleScriptMainFrame ui = new GradleScriptMainFrame();
  }

  {
    // GUI initializer generated by IntelliJ IDEA GUI Designer
    // >>> IMPORTANT!! <<<
    // DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR call it in your code!
   *
   * @noinspection  ALL
   */
  private void $$$setupUI$$$()
  {
    mainPanel = new JPanel();
    mainPanel.setLayout(new GridLayoutManager(6, 4, new Insets(0, 0, 0, 0), -1, -1));

    final Spacer spacer1 = new Spacer();

    mainPanel.add(spacer1,
                  new GridConstraints(2, 3, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                      GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));

    final JPanel panel1 = new JPanel();

    panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
    mainPanel.add(panel1,
                  new GridConstraints(3, 1, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                      GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                      GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Graphic Options"));
    watchFileForChangesCheckBox = new JCheckBox();
    watchFileForChangesCheckBox.setEnabled(false);
    watchFileForChangesCheckBox.setText("Watch file for changes");
    panel1.add(watchFileForChangesCheckBox,
               new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                   null, null, null, 0, false));

    final JPanel panel2 = new JPanel();

    panel2.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    panel1.add(panel2,
               new GridConstraints(1, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Type of output"));
    generateJustDOTFilesRadioButton = new JRadioButton();
    generateJustDOTFilesRadioButton.setEnabled(false);
    generateJustDOTFilesRadioButton.setText("Generate just DOT files");
    panel2.add(generateJustDOTFilesRadioButton,
               new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                   null, null, null, 0, false));
    generatePNGPDFFilesRadioButton = new JRadioButton();
    generatePNGPDFFilesRadioButton.setEnabled(false);
    generatePNGPDFFilesRadioButton.setText("Generate PNG/PDF files");
    panel2.add(generatePNGPDFFilesRadioButton,
               new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                   null, null, null, 0, false));

    final Spacer spacer2 = new Spacer();

    mainPanel.add(spacer2,
                  new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1,
                                      GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));

    final JPanel panel3 = new JPanel();

    panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    mainPanel.add(panel3,
                  new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                      GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                      GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

    final JLabel label1 = new JLabel();

    label1.setFont(new Font(label1.getFont().getName(), Font.BOLD, 20));
    label1.setHorizontalAlignment(0);
    label1.setHorizontalTextPosition(10);
    label1.setText("Gradle Script Visualizer");
    panel3.add(label1,
               new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW,
                                   GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(400, -1), null, 0, false));

    final Spacer spacer3 = new Spacer();

    mainPanel.add(spacer3,
                  new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                      GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));

    final JPanel panel4 = new JPanel();

    panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
    mainPanel.add(panel4,
                  new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                      GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                      GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    quitButton = new JButton();
    quitButton.setText("Quit");
    panel4.add(quitButton,
               new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                   null, null, null, 0, false));
    selectGradleScriptButton = new JButton();
    selectGradleScriptButton.setText("Select Gradle script");
    panel4.add(selectGradleScriptButton,
               new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                   null, null, null, 0, false));
  }

  /** @noinspection  ALL */
  public JComponent $$$getRootComponent$$$()
  {
    return mainPanel;
  }
}