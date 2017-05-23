package net.seninp.grammarviz.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import net.seninp.grammarviz.logic.RPMHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.miginfocom.swing.MigLayout;
import net.seninp.grammarviz.controller.GrammarVizController;
import net.seninp.grammarviz.logic.GrammarVizChartData;
import net.seninp.grammarviz.model.GrammarVizMessage;
import net.seninp.grammarviz.session.UserSession;
import net.seninp.jmotif.sax.NumerosityReductionStrategy;
import net.seninp.util.StackTrace;
import javax.swing.*;

/**
 * View component of Sequitur MVC GUI.
 * 
 * @author psenin
 * 
 */
public class GrammarVizView implements Observer, ActionListener {

  private static final String APPLICATION_MOTTO = "GrammarViz 2.0: visualizing time series grammars";

  // static block - we instantiate the logger
  //
  private static final Logger LOGGER = LoggerFactory.getLogger(GrammarVizView.class);

  // relevant string constants and formatters go here
  //
  // private static final String COMMA = ",";
  private static final String CR = "\n";
  private static final String TITLE_FONT = "helvetica";
  private SimpleDateFormat logDateFormat = new SimpleDateFormat("HH:mm:ss' '");

  // String is the king - constants for actions
  //
  /** Select data file action key. */
  protected static final String SELECT_FILE = "select_file";
  /** Load data action key. */
  protected static final String LOAD_DATA = "load_data";
  /** Load RPM model action key. */
  protected static final String LOAD_MODEL = "load_model";
  /** Guess parameters action key. */
  protected static final String GUESS_PARAMETERS = "guess_parameters";
  /** Process data action key. */
  protected static final String PROCESS_DATA = "process_data";
  /** Train model action key. */
  protected static final String TRAIN_MODEL = "train_model";
  /** Test model action key. */
  protected static final String TEST_MODEL = "test_model";
  /** Reduce overlaps data action key. */
  protected static final String CLUSTER_RULES = "cluster_rules";
  /** Rank rules action key. */
  protected static final String PRUNE_RULES = "rank_rules";
  /** Find periodicity action key. */
  protected static final String FIND_PERIODICITY = "find_periodicity";
  /** Reduce overlaps data action key. */
  protected static final String DISPLAY_CHART = "display_sequitur_chart";
  /** Display density data action key. */
  protected static final String DISPLAY_DENSITY_DATA = "display_density";
  /** Display density data action key. */
  protected static final String DISPLAY_LENGTH_HISTOGRAM = "display_length_histogram";
  /** Display density data action key. */
  protected static final String DISPLAY_ANOMALIES_DATA = "display_anomalies_data";
  /** Save chart action key. */
  protected static final String SAVE_CHART = "save_chart";
  /** Save model action key. */
  protected static final String SAVE_MODEL = "save_model";

  protected static final String RESET_GUESS_BUTTON_LISTENER = "reset_guess_button_listener";

  /** Chunking/Sliding switch action key. */
  protected static final String USE_SLIDING_WINDOW_ACTION_KEY = "sliding_window_key";

  /** Global/Local normalization switch action key. */
  protected static final String USE_GLOBAL_NORMALIZATION_ACTION = "global_normalization_key";

  /** The action command for Options dialog. */
  private static final String OPTIONS_MENU_ITEM = "menu_item_options";

  /** The action command for About dialog. */
  private static final String ABOUT_MENU_ITEM = "menu_item_about";


  /** Frame for the GUI. */
  private static final JFrame frame = new JFrame(APPLICATION_MOTTO);

  /** The main menu bar. */
  private static final JMenuBar menuBar = new JMenuBar();

  /** Global controller handler - controller is supplier of action handlers. */
  private GrammarVizController controller;

  // data source related variables
  //
  private JPanel dataSourcePane;
  private JTextField dataFilePathField;
  private JButton selectFileButton;
  private JTextField dataRowsLimitTextField;
  private JButton dataLoadButton;
  private JButton modelLoadButton;

  // SAX parameters related fields
  //
  private JPanel saxParametersPane;
  private JCheckBox useSlidingWindowCheckBox;
  private JLabel globalNormalizatonLabel;
  private JCheckBox useGlobalNormalizationCheckBox;
  private JLabel windowSizeLabel;
  private JTextField SAXwindowSizeField;
  private JLabel paaSizeLabel;
  private JTextField SAXpaaSizeField;
  private JTextField SAXalphabetSizeField;
  private JButton guessParametersButton; // the guess dialog trigger

  // SAX numerosity reduction dialog group
  //
  private JPanel numerosityReductionPane;
  private ButtonGroup numerosityButtonsGroup = new ButtonGroup();
  private JRadioButton numerosityReductionOFFButton = new JRadioButton("OFF");
  private JRadioButton numerosityReductionExactButton = new JRadioButton("Exact");
  private JRadioButton numerosityReductionMINDISTButton = new JRadioButton("MINDIST");

  // The process action pane
  //
  private JPanel discretizePane;
  private JButton discretizeButton;

  // RPM
  //
  private JLabel rpmIterationLabel;
  private JTextField rpmIterationeField;
  private JPanel trainPane;
  private JButton trainButton;
  private JButton testButton;

  // data charting panel
  //
  private GrammarvizChartPanel dataChartPane;

  // sequitur rules table and other tables panel
  //
  private JTabbedPane tabbedRulesPane;
  private GrammarRulesPanel sequiturRulesPane;
  private PackedRulesPanel packedRulesPane;
  private RulesPeriodicityPanel rulesPeriodicityPane;
  private GrammarVizAnomaliesPanel anomaliesPane;
  private GrammarVizRPMPanel rpmPanel;
  private GrammarVizRPMRepPanel rpmRepPanel;

  // rule(s) charting auxiliary panel
  //
  private GrammarvizRuleChartPanel ruleChartPane;

  // workflow pane - buttons
  //
  private JPanel workflowManagementPane;
  private JButton clusterRulesButton;
  // private JButton findPeriodicityButton;
  private JButton rankRulesButton;
  private JButton displayChartButton;
  private JButton displayRulesDensityButton;
  private JButton displayRulesLenHistogramButton;
  private JButton findAnomaliesButton;
  private JButton saveChartButton;
  private JButton saveModelButton;

  private boolean isTimeSeriesLoaded = false;
  private boolean isModeRPM = false;

  // logging area
  //
  private static final JTextArea logTextArea = new JTextArea();
  private static final JScrollPane logTextPane = new JScrollPane(logTextArea,
      ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

  /**
   * Constructor.
   * 
   * @param controller The controller used for the application flow control.
   */
  public GrammarVizView(GrammarVizController controller) {
    this.controller = controller;
    this.controller.getSession().addActionListener(this);
  }

  /**
   * Shows the GUI.
   */
  public void showGUI() {
    // Schedule a job for the event-dispatching thread:
    // creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (ClassNotFoundException e) {
          System.err.println("ClassNotFoundException: " + e.getMessage());
        }
        catch (InstantiationException e) {
          System.err.println("InstantiationException: " + e.getMessage());
        }
        catch (IllegalAccessException e) {
          System.err.println("IllegalAccessException: " + e.getMessage());
        }
        catch (UnsupportedLookAndFeelException e) {
          System.err.println("UnsupportedLookAndFeelException: " + e.getMessage());
        }
        catch (Exception e) {
          System.err.print(StackTrace.toString(e));
        }

        configureGUI();
        disableAllExceptSelectButton();
      }
    });
  }

  /**
   * Initialize the dialog
   */
  private void configureGUI() {

    // set look and fill
    JFrame.setDefaultLookAndFeelDecorated(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // build main UI components
    //
    buildMenuBar();

    buildDataSourcePane();
    buildSAXParamsPane();

    buildChartPane();

    buildSequiturPane();

    buildWorkflowPane();

    buildLogPane();

    // put listeners in place for the Sequitur rule panel
    //
    sequiturRulesPane.addPropertyChangeListener(dataChartPane);
    sequiturRulesPane.addPropertyChangeListener(ruleChartPane);
    dataChartPane.addPropertyChangeListener(GrammarVizMessage.MAIN_CHART_CLICKED_MESSAGE,
        sequiturRulesPane);

    // put listeners in place for the Clustered/Packed rule panel
    //
    packedRulesPane.addPropertyChangeListener(dataChartPane);
    packedRulesPane.addPropertyChangeListener(ruleChartPane);
    dataChartPane.addPropertyChangeListener(GrammarVizMessage.MAIN_CHART_CLICKED_MESSAGE,
        packedRulesPane);

    // put listeners in place for the Periodicity rule panel
    //
    rulesPeriodicityPane.addPropertyChangeListener(dataChartPane);
    rulesPeriodicityPane.addPropertyChangeListener(ruleChartPane);
    // dataChartPane.addPropertyChangeListener(GrammarVizMessage.MAIN_CHART_CLICKED_MESSAGE,
    // rulesPeriodicityPane);

    // put listeners in place for the Anomalies rule panel
    //
    anomaliesPane.addPropertyChangeListener(dataChartPane);
    anomaliesPane.addPropertyChangeListener(ruleChartPane);

    rpmRepPanel.addPropertyChangeListener(ruleChartPane);

    // set the main panel layout
    MigLayout mainFrameLayout = new MigLayout("", "[fill,grow,center]",
        "[][][fill,grow 50][fill,grow 50][][]");
    frame.getContentPane().setLayout(mainFrameLayout);

    // set the menu bar
    frame.setJMenuBar(menuBar);

    // place panels
    frame.getContentPane().add(dataSourcePane, "wrap");

    frame.getContentPane().add(saxParametersPane, "grow, split");
    frame.getContentPane().add(numerosityReductionPane, "split");
    frame.getContentPane().add(discretizePane, "");
    frame.getContentPane().add(trainPane, "wrap");

    frame.getContentPane().add(dataChartPane, "wrap");

    frame.getContentPane().add(tabbedRulesPane, "w 70%, split");
    frame.getContentPane().add(ruleChartPane, "w 30%, wrap");

    frame.getContentPane().add(workflowManagementPane, "wrap");

    frame.getContentPane().add(logTextPane, "h 80:100:100,wrap");

    // Show frame
    frame.pack();
    frame.setSize(new Dimension(1200, 840));
    frame.setVisible(true);
  }

  /**
   * Build the application menu bar.
   */
  private void buildMenuBar() {

    // Build the File menu.
    //
    //
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic(KeyEvent.VK_F);
    fileMenu.getAccessibleContext().setAccessibleDescription("The file menu");
    // Open file item
    JMenuItem openFileItem = new JMenuItem("Select", KeyEvent.VK_O);
    openFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    openFileItem.getAccessibleContext().setAccessibleDescription("Open a data file");
    openFileItem.setActionCommand(SELECT_FILE);
    openFileItem.addActionListener(this);
    fileMenu.add(openFileItem);
    // add a separator
    fileMenu.addSeparator();
    // an exit item
    JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
    exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
    exitItem.getAccessibleContext().setAccessibleDescription("Exit from here");
    exitItem.addActionListener(this);
    fileMenu.add(exitItem);

    // Build the Options menu.
    //
    //
    JMenu settingsMenu = new JMenu("Settings");
    settingsMenu.setMnemonic(KeyEvent.VK_S);
    settingsMenu.getAccessibleContext().setAccessibleDescription("Settings menu");
    // an exit item
    JMenuItem optionsItem = new JMenuItem("GrammarViz options", KeyEvent.VK_P);
    optionsItem.setActionCommand(OPTIONS_MENU_ITEM);
    optionsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
    optionsItem.getAccessibleContext().setAccessibleDescription("Options");
    optionsItem.addActionListener(this);
    settingsMenu.add(optionsItem);

    // Build the About menu.
    JMenu helpMenu = new JMenu("Help");
    helpMenu.setMnemonic(KeyEvent.VK_F1);
    helpMenu.getAccessibleContext().setAccessibleDescription("Help & About");

    // a help item
    JMenuItem helpItem = new JMenuItem("Help", KeyEvent.VK_H);
    helpItem.getAccessibleContext().setAccessibleDescription("Get some help here.");
    exitItem.addActionListener(controller);
    helpMenu.add(helpItem);

    // an about item
    JMenuItem aboutItem = new JMenuItem("About", KeyEvent.VK_A);
    aboutItem.getAccessibleContext().setAccessibleDescription("About the app.");
    aboutItem.setActionCommand(ABOUT_MENU_ITEM);
    aboutItem.addActionListener(this);
    helpMenu.add(aboutItem);

    // make sure that controller is connected with Exit item
    //
    exitItem.addActionListener(controller);

    menuBar.add(fileMenu);
    menuBar.add(settingsMenu);
    menuBar.add(helpMenu);
  }

  private void buildDataSourcePane() {

    dataSourcePane = new JPanel();

    // Layout, insets: T, L, B, R.
    dataSourcePane.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(BevelBorder.LOWERED), "Data source", TitledBorder.LEFT,
        TitledBorder.CENTER, new Font(TITLE_FONT, Font.PLAIN, 10)));
    MigLayout dataSourcePaneLayout = new MigLayout("insets 0 2 2 2",
        "[][fill,grow 80][]10[][fill, grow 20][]", "[]");
    dataSourcePane.setLayout(dataSourcePaneLayout);

    // file label
    //
    JLabel fileNameLabel = new JLabel("Data file: ");

    // field
    dataFilePathField = new JTextField("");
    fileNameLabel.setLabelFor(dataFilePathField);

    // the Browse button
    selectFileButton = new JButton("Browse");
    selectFileButton.setMnemonic('B');

    dataSourcePane.add(fileNameLabel, "");
    dataSourcePane.add(dataFilePathField, "");
    dataSourcePane.add(selectFileButton, "");

    // add the action listener
    //
    selectFileButton.addActionListener(controller.getBrowseFilesListener());
    // dataFilePathField.getDocument().addDocumentListener(controller.getDataFileNameListener());

    // data rows interval section
    //
    JLabel lblCountRows = new JLabel("Row limit (0=all):");
    dataRowsLimitTextField = new JTextField("0");
    dataSourcePane.add(lblCountRows, "");
    dataSourcePane.add(dataRowsLimitTextField, "");

    // the load button
    //
    dataLoadButton = new JButton("Load data");
    dataLoadButton.setMnemonic('L');
    // add the action listener
    dataLoadButton.setActionCommand(LOAD_DATA);
    dataLoadButton.addActionListener(this);
    dataSourcePane.add(dataLoadButton, "");

    // Button to load previously computed RPM models
    modelLoadButton = new JButton("Load model");
    modelLoadButton.setActionCommand(LOAD_MODEL);
    modelLoadButton.addActionListener(this);
    dataSourcePane.add(modelLoadButton, "");
  }

  /**
   * Builds a parameters pane.
   */
  private void buildSAXParamsPane() {

    saxParametersPane = new JPanel();
    saxParametersPane.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(BevelBorder.LOWERED), "SAX parameters", TitledBorder.LEFT,
        TitledBorder.CENTER, new Font(TITLE_FONT, Font.PLAIN, 10)));

    // insets: T, L, B, R.
    // MigLayout saxPaneLayout = new MigLayout("insets 3 2 2 2",
    //     "[][]10[][]10[][fill,grow]10[][fill,grow]10[][fill,grow]10[][]", "[]");


    // using box layout to automatically expand remaining fields
    // when global normalization checkbox disappears
    BoxLayout saxPaneLayout = new BoxLayout(saxParametersPane, BoxLayout.X_AXIS);
    saxParametersPane.setLayout(saxPaneLayout);

    // the sliding window parameter
    // JLabel slideWindowLabel = new JLabel("Slide the window");
    useSlidingWindowCheckBox = new JCheckBox("Slide window");
    useSlidingWindowCheckBox.setSelected(this.controller.getSession().useSlidingWindow);
    useSlidingWindowCheckBox.setActionCommand(USE_SLIDING_WINDOW_ACTION_KEY);
    useSlidingWindowCheckBox.addActionListener(this);

    // globalNormalizatonLabel = new JLabel("Global normalizaton");
    useGlobalNormalizationCheckBox = new JCheckBox("Global normalizaton  ");
    useGlobalNormalizationCheckBox.setSelected(this.controller.getSession().useGlobalNormalization);
    useGlobalNormalizationCheckBox.setActionCommand(USE_GLOBAL_NORMALIZATION_ACTION);
    useGlobalNormalizationCheckBox.addActionListener(this);

    windowSizeLabel = new JLabel("Window:");
    SAXwindowSizeField = new JTextField(String.valueOf(this.controller.getSession().saxWindow),2);
    SAXwindowSizeField.setMinimumSize(SAXwindowSizeField.getPreferredSize());

    paaSizeLabel = new JLabel("  PAA:");
    SAXpaaSizeField = new JTextField(String.valueOf(this.controller.getSession().saxPAA));
    SAXpaaSizeField.setMinimumSize(SAXpaaSizeField.getPreferredSize());

    JLabel alphabetSizeLabel = new JLabel("  Alphabet:");
    SAXalphabetSizeField = new JTextField(String.valueOf(this.controller.getSession().saxAlphabet));
    SAXalphabetSizeField.setMinimumSize(SAXalphabetSizeField.getPreferredSize());


    saxParametersPane.add(windowSizeLabel);
    saxParametersPane.add(SAXwindowSizeField);

    saxParametersPane.add(paaSizeLabel);
    saxParametersPane.add(SAXpaaSizeField);

    saxParametersPane.add(alphabetSizeLabel);
    saxParametersPane.add(SAXalphabetSizeField);

    guessParametersButton = new JButton("Guess");
    guessParametersButton.setMnemonic('G');
    guessParametersButton.setActionCommand(GUESS_PARAMETERS);
    guessParametersButton.addActionListener(this);
    saxParametersPane.add(guessParametersButton, "");

    // saxParametersPane.add(slideWindowLabel);
    saxParametersPane.add(useSlidingWindowCheckBox);

    // saxParametersPane.add(globalNormalizatonLabel);
    saxParametersPane.add(useGlobalNormalizationCheckBox);

    // numerosity reduction pane
    //
    numerosityReductionPane = new JPanel();
    numerosityReductionPane.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(BevelBorder.LOWERED), "Numerosity reduction",
        TitledBorder.LEFT, TitledBorder.CENTER, new Font(TITLE_FONT, Font.PLAIN, 10)));

    // insets: T, L, B, R.
    MigLayout numerosityPaneLayout = new MigLayout("insets 3 2 6 2", "[]5[]5[]", "[]");
    numerosityReductionPane.setLayout(numerosityPaneLayout);

    numerosityReductionOFFButton.setActionCommand(NumerosityReductionStrategy.NONE.toString());
    numerosityButtonsGroup.add(numerosityReductionOFFButton);
    numerosityReductionOFFButton.addActionListener(this);
    numerosityReductionPane.add(numerosityReductionOFFButton);

    numerosityReductionExactButton.setActionCommand(NumerosityReductionStrategy.EXACT.toString());
    numerosityButtonsGroup.add(numerosityReductionExactButton);
    numerosityReductionExactButton.addActionListener(this);
    numerosityReductionPane.add(numerosityReductionExactButton);

    numerosityReductionMINDISTButton
        .setActionCommand(NumerosityReductionStrategy.MINDIST.toString());
    numerosityButtonsGroup.add(numerosityReductionMINDISTButton);
    numerosityReductionMINDISTButton.addActionListener(this);
    numerosityReductionPane.add(numerosityReductionMINDISTButton);

    this.controller.getSession().numerosityReductionStrategy = NumerosityReductionStrategy.EXACT;
    numerosityReductionExactButton.setSelected(true);

    // PROCESS button
    //
    discretizeButton = new JButton("Discretize"); // Obviously changed from process at some point
    discretizeButton.setMnemonic('P');
    discretizeButton.setActionCommand(PROCESS_DATA);
    discretizeButton.addActionListener(this);

    discretizePane = new JPanel();
    discretizePane.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(BevelBorder.LOWERED), "Run GI", TitledBorder.LEFT,
        TitledBorder.CENTER, new Font(TITLE_FONT, Font.PLAIN, 10)));
    // insets: T, L, B, R.
    MigLayout processPaneLayout = new MigLayout("insets 3 2 4 2", "5[]5", "[]");
    discretizePane.setLayout(processPaneLayout);
    discretizePane.add(discretizeButton, "");


    //  RPM train iterations field
    rpmIterationLabel = new JLabel("Iterations");
    rpmIterationeField = new JTextField(String.valueOf(this.controller.getSession().rpmNumberOfIterations), 2);
    rpmIterationeField.setMinimumSize(rpmIterationeField.getPreferredSize());
    //  RPM train button and test buttons
    trainButton = new JButton("Train");
    trainButton.setMnemonic('R');
    trainButton.setActionCommand(TRAIN_MODEL);
    trainButton.addActionListener(this);
    testButton = new JButton("Test");
    testButton.setMnemonic('e');
    testButton.setActionCommand(TEST_MODEL);
    testButton.addActionListener(this);

    trainPane = new JPanel();
    trainPane.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(BevelBorder.LOWERED), "Run RPM", TitledBorder.LEFT,
        TitledBorder.CENTER, new Font(TITLE_FONT, Font.PLAIN, 10)));
    // insets: T, L, B, R.
    processPaneLayout = new MigLayout("insets 3 2 4 2", "5[]5", "[]");
    trainPane.setLayout(processPaneLayout);

    trainPane.add(rpmIterationLabel);
    trainPane.add(rpmIterationeField);
    trainPane.add(trainButton, "");
    trainPane.add(testButton, "");
  }

  private void buildChartPane() {
    // MotifChartPanel _chart = new MotifChartPanel(null);
    dataChartPane = new GrammarvizChartPanel();
    dataChartPane.addActionListener(this);
    dataChartPane.session = this.controller.getSession();
    dataChartPane.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(BevelBorder.LOWERED), "Data display", TitledBorder.LEFT,
        TitledBorder.CENTER, new Font(TITLE_FONT, Font.PLAIN, 10)));
    MigLayout chartPaneLayout = new MigLayout("insets 0 0 0 0", "[fill,grow]", "[fill,grow]");
    dataChartPane.setLayout(chartPaneLayout);

    // needed to be able to stop guessing...
    //
    dataChartPane.setOperationalButton(this.guessParametersButton);
  }

  /**
   * Builds all objects and widgets related to Sequitur tables.
   */
  private void buildSequiturPane() {

    // first the tabbed pane which holds other panels
    //
    tabbedRulesPane = new JTabbedPane();

    // now add the raw Sequitur rules panel
    //
    sequiturRulesPane = new GrammarRulesPanel();
    MigLayout sequiturPaneLayout = new MigLayout(",insets 0 0 0 0", "[fill,grow]", "[fill,grow]");
    sequiturRulesPane.setLayout(sequiturPaneLayout);

    tabbedRulesPane.addTab("Grammar rules", null, sequiturRulesPane, "Shows grammar rules");
    // tabbedRulesPane.addTab("Sequitur", sequiturRulesPane);
    // tabbedRulesPane.setIgnoreRepaint(false);

    // now add the prototype of reduced rules panel
    //
    packedRulesPane = new PackedRulesPanel();
    MigLayout packedRulesPaneLayout = new MigLayout(",insets 0 0 0 0", "[fill,grow]",
        "[fill,grow]");
    packedRulesPane.setLayout(packedRulesPaneLayout);
    tabbedRulesPane.addTab("Regularized rules", null, packedRulesPane,
        "Shows reduced by overlapping criterion rules subset");

    // now add the rules periodicity panel
    //
    rulesPeriodicityPane = new RulesPeriodicityPanel();
    MigLayout rulesPeriodicityPaneLayout = new MigLayout(",insets 0 0 0 0", "[fill,grow]",
        "[fill,grow]");
    rulesPeriodicityPane.setLayout(rulesPeriodicityPaneLayout);
    tabbedRulesPane.addTab("Rules periodicity", null, rulesPeriodicityPane,
        "Shows rules periodicity");

    // now add the anomalies panel
    //
    anomaliesPane = new GrammarVizAnomaliesPanel();
    MigLayout anomaliesPaneLayout = new MigLayout(",insets 0 0 0 0", "[fill,grow]", "[fill,grow]");
    anomaliesPane.setLayout(anomaliesPaneLayout);
    tabbedRulesPane.addTab("GrammarViz anomalies", null, anomaliesPane,
        "Shows anomalous subsequences");

    // now add the RPM Representative Panel
    //
    rpmRepPanel = new GrammarVizRPMRepPanel();
    MigLayout rpmRepPanelLayout = new MigLayout(",insets 0 0 0 0", "[fill,grow]", "[fill,grow]");
    rpmRepPanel.setLayout(rpmRepPanelLayout);
    tabbedRulesPane.addTab("RPM Representative Classes", null, rpmRepPanel, "Show RPM Representative Classes");

    // now add the RPM Classification Panel
    //
    rpmPanel = new GrammarVizRPMPanel();
    MigLayout rpmPanelLayout = new MigLayout(",insets 0 0 0 0", "[fill,grow]", "[fill,grow]");
    rpmPanel.setLayout(rpmPanelLayout);
    tabbedRulesPane.addTab("RPM Classification", null, rpmPanel, "Show RPM Classification Statistics");

    // now format the tabbed pane
    //
    tabbedRulesPane.setBorder(
        BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED),
            "Grammar rules (search in list by clicking into list and pressing CTRL-F)",
            TitledBorder.LEFT, TitledBorder.CENTER, new Font(TITLE_FONT, Font.PLAIN, 10)));
    // MigLayout tabbedPaneLayout = new MigLayout(",insets 0 0 0 2", "[fill,grow]",
    // "[fill,grow]");
    // tabbedRulesPane.setLayout(tabbedPaneLayout);

    // the rule chart panel
    //
    ruleChartPane = new GrammarvizRuleChartPanel();
    ruleChartPane.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(BevelBorder.LOWERED), "Rule subsequences, normalized",
        TitledBorder.LEFT, TitledBorder.CENTER, new Font(TITLE_FONT, Font.PLAIN, 10)));
    MigLayout ruleChartPaneLayout = new MigLayout(",insets 0 2 0 0", "[fill,grow]", "[fill,grow]");
    ruleChartPane.setLayout(ruleChartPaneLayout);
    ruleChartPane.setController(this.controller);

  }

  private void buildWorkflowPane() {

    workflowManagementPane = new JPanel();
    workflowManagementPane.setBorder(
        BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED),
            "Workflow management: load > process > display", TitledBorder.LEFT, TitledBorder.CENTER,
            new Font(TITLE_FONT, Font.PLAIN, 10)));
    MigLayout workflowPaneLayout = new MigLayout(",insets 2 2 2 2", "[fill,grow]", "[fill,grow]");
    workflowManagementPane.setLayout(workflowPaneLayout);

    rankRulesButton = new JButton("Prune rules");
    rankRulesButton.setMnemonic('U');
    rankRulesButton.setActionCommand(PRUNE_RULES);
    rankRulesButton.addActionListener(this);

    clusterRulesButton = new JButton("Cluster rules");
    clusterRulesButton.setMnemonic('C');
    clusterRulesButton.setActionCommand(CLUSTER_RULES);
    clusterRulesButton.addActionListener(this);

    displayChartButton = new JButton("Grammar rules");
    displayChartButton.setMnemonic('R');
    displayChartButton.setActionCommand(DISPLAY_CHART);
    displayChartButton.addActionListener(this);

    displayRulesDensityButton = new JButton("Rules density");
    displayRulesDensityButton.setMnemonic('D');
    displayRulesDensityButton.setActionCommand(DISPLAY_DENSITY_DATA);
    displayRulesDensityButton.addActionListener(this);

    displayRulesLenHistogramButton = new JButton("Rule length histogram");
    displayRulesLenHistogramButton.setMnemonic('H');
    displayRulesLenHistogramButton.setActionCommand(DISPLAY_LENGTH_HISTOGRAM);
    displayRulesLenHistogramButton.addActionListener(this);

    findAnomaliesButton = new JButton("Find anomalies");
    findAnomaliesButton.setMnemonic('A');
    findAnomaliesButton.setActionCommand(DISPLAY_ANOMALIES_DATA);
    findAnomaliesButton.addActionListener(this);

    saveChartButton = new JButton("Save chart");
    saveChartButton.setMnemonic('S');
    saveChartButton.setActionCommand(SAVE_CHART);
    saveChartButton.addActionListener(this);

    saveModelButton = new JButton("Save model");
    saveModelButton.setMnemonic('m');
    saveModelButton.setActionCommand(SAVE_MODEL);
    saveModelButton.addActionListener(this);

    // workflowManagementPane.add(processButton);
    workflowManagementPane.add(displayChartButton);
    workflowManagementPane.add(displayRulesLenHistogramButton);
    workflowManagementPane.add(clusterRulesButton);
    workflowManagementPane.add(rankRulesButton);
    workflowManagementPane.add(displayRulesDensityButton);
    workflowManagementPane.add(findAnomaliesButton);
    workflowManagementPane.add(saveChartButton);
    workflowManagementPane.add(saveModelButton);
  }

  /**
   * Build the logging panel.
   */
  private void buildLogPane() {
    // logging panel
    logTextArea.setFont(new Font("MonoSpaced", Font.PLAIN, 10));
    logTextArea.setEditable(false);
    logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
    logTextPane.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
    logTextPane.setAutoscrolls(true);
    log(Level.INFO, "running GrammarViz 2.0 demo");
  }

  /**
   * Shut downs the application.
   */
  private void shutdown() {
    Runtime.getRuntime().exit(0);
  }

  /**
   * Logs message.
   * 
   * @param level The logging level to use.
   * @param message The log message.
   */
  protected void log(Level level, String message) {
    message = message.replaceAll("\n", "");
    String dateStr = logDateFormat.format(System.currentTimeMillis());
    if (message.startsWith("model") || message.startsWith("controller")) {
      logTextArea.append(dateStr + message + CR);
    }
    else {
      logTextArea.append(dateStr + "view: " + message + CR);
    }
    logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
    LOGGER.info(dateStr + message);
  }

  @Override
  public void update(Observable o, Object arg) {
    if (arg instanceof GrammarVizMessage) {

      final GrammarVizMessage message = (GrammarVizMessage) arg;

      // new log message
      //
      if (GrammarVizMessage.STATUS_MESSAGE.equalsIgnoreCase(message.getType())) {
        log(Level.ALL, (String) message.getPayload());
      }

      // new FileName
      //
      else if (GrammarVizMessage.DATA_FNAME.equalsIgnoreCase(message.getType())) {

        Runnable doSetPath = new Runnable() {
          @Override
          public void run() {
            dataFilePathField.setText((String) message.getPayload());
            dataFilePathField.repaint();
            disableAllExceptSelectButton();
            dataLoadButton.setEnabled(true);
            modelLoadButton.setEnabled(true);
          }
        };
        SwingUtilities.invokeLater(doSetPath);

      } else if (GrammarVizMessage.TIME_SERIES_MESSAGE.equalsIgnoreCase(message.getType())) {

        // setting the chart first
        //
        dataChartPane.showTimeSeries((double[][]) message.getPayload());

        Runnable clearPanels = new Runnable() {
          @Override
          public void run() {
            sequiturRulesPane.clearPanel();
            ruleChartPane.clear();
            rulesPeriodicityPane.clear();
            anomaliesPane.clear();
            frame.repaint();
            disableAllExceptSelectButton();
            dataLoadButton.setEnabled(true);
            modelLoadButton.setEnabled(true);
            guessParametersButton.setEnabled(true);
            discretizeButton.setEnabled(true);
          }
        };
        SwingUtilities.invokeLater(clearPanels);

        this.isTimeSeriesLoaded = true;
        this.controller.getSession().chartData = null;
      }

      // chart object
      //
      else if (GrammarVizMessage.CHART_MESSAGE.equalsIgnoreCase(message.getType())) {

        this.controller.getSession().chartData = (GrammarVizChartData) message.getPayload();

        // setting the chart first
        //
        dataChartPane.setSession(this.controller.getSession());

        // and the rules pane second
        //
        sequiturRulesPane.setChartData(this.controller.getSession());

        // and the "snapshots panel"
        //
        ruleChartPane.setChartData(this.controller.getSession());

        // and the rules periodicity panel
        //
        rulesPeriodicityPane.setChartData(this.controller.getSession());

        // and the anomalies panel
        //
        anomaliesPane.setChartData(this.controller.getSession());

        enableAllButtons();
        // dataChartPane.getChart().setNotify(true);
        frame.revalidate();
        frame.repaint();
      }
      // Enable RPM when RPM data was loaded
      else if (GrammarVizMessage.RPM_DATA_MESSAGE.equalsIgnoreCase(message.getType())) {
        Runnable enableRPM = new Runnable() {
          @Override
          public void run() {
            trainButton.setEnabled(true);
          }
        };
        SwingUtilities.invokeLater(enableRPM);

      }
      // Return Results from RPM Training and load them into the GUI
      else if (GrammarVizMessage.RPM_TRAIN_RESULTS_UPDATE_MESSAGE.equalsIgnoreCase(message.getType())) {
        controller.getSession().rpmHandler = (RPMHandler) message.getPayload();
        ruleChartPane.setChartData(this.controller.getSession());
        this.rpmRepPanel.setResults(this.controller.getSession());

        Runnable updateParam = new Runnable() {
          @Override
          public void run() {
            RPMHandler rpmHandler = controller.getSession().rpmHandler;
            testButton.setEnabled(true);
            saveModelButton.setEnabled(true);
            SAXwindowSizeField.setText(String.valueOf(rpmHandler.getWindowSize()));
            SAXpaaSizeField.setText(String.valueOf(rpmHandler.getPaa()));
            SAXalphabetSizeField.setText(String.valueOf(rpmHandler.getAlphabet()));
            rpmIterationeField.setText(String.valueOf(rpmHandler.getNumberOfIterations()));
            rpmPanel.revalidate();
            rpmPanel.repaint();
            saxParametersPane.revalidate();
            saxParametersPane.repaint();
            rpmRepPanel.updateRPMRepPatterns();
            rpmRepPanel.resetPanel();
          }
        };
        SwingUtilities.invokeLater(updateParam);
      }
      // Return Results from RPM Classification and load them into the GUI
      else if (GrammarVizMessage.RPM_CLASS_RESULTS_UPDATE_MESSAGE.equalsIgnoreCase(message.getType())) {
        controller.getSession().rpmHandler = (RPMHandler) message.getPayload();


        Runnable updateTable = new Runnable() {
          @Override
          public void run() {
            rpmPanel.setClassificationResults(controller.getSession());
            rpmPanel.updateRPMStatistics();
            rpmPanel.resetPanel();
          }
        };
        SwingUtilities.invokeLater(updateTable);
      }
      // Show Error when file was not a model
      else if (GrammarVizMessage.LOAD_FILE_ERROR_UPDATE_MESSAGE.equalsIgnoreCase(message.getType())) {
        JOptionPane.showMessageDialog(frame, message.getPayload(), "Error Loading File", JOptionPane.ERROR_MESSAGE);
      }
      // Show Dialog box if training data could not be loaded when loading model
      else if (GrammarVizMessage.RPM_MISSING_TRAIN_DATA_UPDATE_MESSAGE.equalsIgnoreCase(message.getType())) {
        this.controller.getRPMLoadMissingTrainListener().actionPerformed(new ActionEvent(this, 0, null));
      }
    }
  }

  @Override
  public void actionPerformed(ActionEvent arg) {

    // get the action command code
    //
    String command = arg.getActionCommand();

    // treating options
    //
    if (OPTIONS_MENU_ITEM.equalsIgnoreCase(command)) {
      log(Level.INFO, "options menu action performed");

      GrammarvizOptionsPane parametersPanel = new GrammarvizOptionsPane(
          this.controller.getSession());

      GrammarvizOptionsDialog parametersDialog = new GrammarvizOptionsDialog(frame, parametersPanel,
          this.controller.getSession());

      parametersDialog.setVisible(true);
    }

    // showing up the about dialog
    //
    if (ABOUT_MENU_ITEM.equalsIgnoreCase(command)) {
      log(Level.INFO, "about menu action performed");
      AboutGrammarVizDialog dlg = new AboutGrammarVizDialog(frame);
      dlg.clearAndHide();
    }

    if (SELECT_FILE.equalsIgnoreCase(command)) {
      log(Level.INFO, "select file action performed");
      controller.getBrowseFilesListener().actionPerformed(null);
    }

    if (LOAD_MODEL.equalsIgnoreCase(command)) {
      log(Level.INFO, "load model action performed");
      this.controller.getRPMLoadListener().actionPerformed(new ActionEvent(this, 0, null));
    }

    if (TRAIN_MODEL.equalsIgnoreCase(command)) {
      log(Level.INFO, "train model action performed");
      //JOptionPane.showMessageDialog(null, "Implement me");
      this.controller.getSession().rpmNumberOfIterations = Integer.valueOf(this.rpmIterationeField.getText());
      this.controller.getRPMTrainListener().actionPerformed(new ActionEvent(this, 0, null));
      this.rpmRepPanel.clear();
    }

    if (TEST_MODEL.equalsIgnoreCase(command)) {
      log(Level.INFO, "test model action performed");
      //JOptionPane.showMessageDialog(null, "Implement me");
      this.controller.getRPMTestListener().actionPerformed(new ActionEvent(this, 0, null));
      this.rpmPanel.clear();
    }

    if (SAVE_MODEL.equalsIgnoreCase(command)) {
      log(Level.INFO, "save model action performed");
      this.controller.getRPMSaveListener().actionPerformed(new ActionEvent(this, 0, null));
    }

    if (LOAD_DATA.equalsIgnoreCase(command)) {
      log(Level.INFO, "load data action performed");
      this.isTimeSeriesLoaded = false;
      if (this.dataFilePathField.getText().isEmpty()) {
        raiseValidationError("The file is not yet selected.");
      }
      else {
        String loadLimit = this.dataRowsLimitTextField.getText();
        this.controller.getLoadFileListener().actionPerformed(new ActionEvent(this, 1, loadLimit));
      }
    }

    else if (PROCESS_DATA.equalsIgnoreCase(command)) {
      log(Level.INFO, "process data action performed");
      if (this.isTimeSeriesLoaded) {
        // check the values for window/paa/alphabet, etc.
        this.controller.getSession().saxWindow = Integer.valueOf(this.SAXwindowSizeField.getText());
        this.controller.getSession().saxPAA = Integer.valueOf(this.SAXpaaSizeField.getText());
        this.controller.getSession().saxAlphabet = Integer
            .valueOf(this.SAXalphabetSizeField.getText());
        this.controller.getProcessDataListener().actionPerformed(new ActionEvent(this, 0, null)); // only
                                                                                                  // one
                                                                                                  // handler
                                                                                                  // over
                                                                                                  // there
      }
      else {
        raiseValidationError("The timeseries is not loaded yet.");
      }
    }

    else if (DISPLAY_CHART.equalsIgnoreCase(command)) {
      log(Level.INFO, "display chart action performed");
      if (null == this.controller.getSession().chartData) {
        raiseValidationError("No chart data recieved yet.");
      }
      else {
        dataChartPane.resetChartPanel();
        sequiturRulesPane.resetSelection();
        ruleChartPane.clear();
      }
    }

    else if (DISPLAY_DENSITY_DATA.equalsIgnoreCase(command)) {
      log(Level.INFO, "display density plot action performed");
      if (null == this.controller.getSession().chartData) {
        raiseValidationError("No chart data recieved yet.");
      }
      else {
        ruleChartPane.clear();
        this.dataChartPane.actionPerformed(new ActionEvent(this, 0, DISPLAY_DENSITY_DATA));
      }
    }

    else if (DISPLAY_LENGTH_HISTOGRAM.equalsIgnoreCase(command)) {
      log(Level.INFO, "display rule length histogram action performed");
      if (null == this.controller.getSession().chartData) {
        raiseValidationError("No chart data recieved yet.");
      }
      else {
        ruleChartPane.clear();
        this.dataChartPane.actionPerformed(new ActionEvent(this, 1, DISPLAY_LENGTH_HISTOGRAM));
      }
    }

    else if (DISPLAY_ANOMALIES_DATA.equalsIgnoreCase(command)) {
      log(Level.INFO, "find/display anomalies action performed");
      if (null == this.controller.getSession().chartData) {
        raiseValidationError("No chart data recieved yet.");
      }
      else {

        log(Level.INFO, "going to run anomalies search, this takes time, please wait... ");

        try {
          this.controller.getSession().chartData.addObserver(this);

          this.controller.getSession().chartData.findAnomalies();
          this.anomaliesPane.updateAnomalies();
          this.anomaliesPane.resetPanel();

          this.controller.getSession().chartData.deleteObserver(this);
        }
        catch (Exception e) {
          String errorTrace = StackTrace.toString(e);
          log(Level.ALL, errorTrace);
        }
      }
    }

    else if (SAVE_CHART.equalsIgnoreCase(command)) {
      log(Level.INFO, "save chart action performed");
      if (null == this.controller.getSession().chartData) {
        raiseValidationError("No chart data recieved yet.");
      }
      else {
        this.dataChartPane.actionPerformed(new ActionEvent(this, 2, SAVE_CHART));
      }
    }

    else if (GUESS_PARAMETERS.equalsIgnoreCase(command)) {
      log(Level.INFO, "starting the guessing params dialog");
      disableAllExceptSelectButton();
      this.dataLoadButton.setEnabled(true);
      this.modelLoadButton.setEnabled(true);
      this.guessParametersButton.setEnabled(true);
      this.guessParametersButton.removeActionListener(this);
      this.dataChartPane.actionPerformed(new ActionEvent(this, 2, GUESS_PARAMETERS));
    }

    else if (UserSession.PARAMS_CHANGED_EVENT.equalsIgnoreCase(command)) {
      this.SAXwindowSizeField.setText(String.valueOf(this.controller.getSession().saxWindow));
      this.SAXpaaSizeField.setText(String.valueOf(this.controller.getSession().saxPAA));
      this.SAXalphabetSizeField.setText(String.valueOf(this.controller.getSession().saxAlphabet));
      this.saxParametersPane.revalidate();
      this.saxParametersPane.repaint();
    }

    else if (RESET_GUESS_BUTTON_LISTENER.equalsIgnoreCase(command)) {
      this.guessParametersButton.setText("Guess");
      this.guessParametersButton.addActionListener(this);
      this.discretizeButton.setEnabled(true);
    }

    else if (FIND_PERIODICITY.equalsIgnoreCase(command)) {
      log(Level.INFO, "find periodicity action performed");
      this.dataChartPane.actionPerformed(new ActionEvent(this, 3, FIND_PERIODICITY));
    }

    else if (CLUSTER_RULES.equalsIgnoreCase(command)) {
      log(Level.INFO, "cluster/prune rules action performed");
      if (null == this.controller.getSession().chartData) {
        raiseValidationError("No chart data recieved yet.");
      }
      else {

        // fix the parameters
        JTextField lengthThreshold = new JTextField("0.1");
        JTextField overlapThreshold = new JTextField("0.5");

        // build a parameters panel
        JPanel parameterPanel = new JPanel();
        parameterPanel.add(new JLabel("threshold for length:"));
        parameterPanel.add(lengthThreshold);
        parameterPanel.add(Box.createHorizontalStrut(15)); // a spacer
        parameterPanel.add(new JLabel("threshold for overlap:"));
        parameterPanel.add(overlapThreshold);

        // wait for the user
        int result = JOptionPane.showConfirmDialog(null, parameterPanel, "Please Enter Parameter",
            JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {

          double thresholdLength = Double.parseDouble(lengthThreshold.getText());
          double thresholdCommon = Double.parseDouble(overlapThreshold.getText());

          dataChartPane.resetChartPanel();
          packedRulesPane.resetSelection();
          ruleChartPane.clear();

          this.controller.getSession().chartData.performRemoveOverlapping(thresholdLength,
              thresholdCommon);

          packedRulesPane.setChartData(this.controller.getSession().chartData);
        }

      }
    }
    else if (PRUNE_RULES.equalsIgnoreCase(command)) {
      log(Level.INFO, "prune rules action performed");
      if (null == this.controller.getSession().chartData) {
        raiseValidationError("No chart data recieved yet.");
      }
      else {

        this.controller.getSession().chartData.performRanking();

        // setting the chart first
        //
        dataChartPane.resetChartPanel();

        // and the rules pane second
        //
        sequiturRulesPane.resetPanel();

        // and the "snapshots panel"
        //
        ruleChartPane.clear();

        // and the rules periodicity panel
        //
        rulesPeriodicityPane.resetPanel();

        // and the anomalies panel
        //
        anomaliesPane.resetPanel();

        // dataChartPane.getChart().setNotify(true);
        frame.validate();
        frame.repaint();

      }
    }

    else if (USE_SLIDING_WINDOW_ACTION_KEY.equalsIgnoreCase(command)) {
      log(Level.INFO, "sliding window toggled");
      if (this.useSlidingWindowCheckBox.isSelected()) {
        this.controller.getSession().useSlidingWindow = true;
        this.paaSizeLabel.setText("  PAA size:");
        this.windowSizeLabel.setText("Window size:");
        this.windowSizeLabel.setEnabled(true);
        this.windowSizeLabel.setVisible(true);
        this.SAXwindowSizeField.setText(String.valueOf(this.controller.getSession().saxWindow));
        this.SAXwindowSizeField.setEnabled(true);
        this.SAXwindowSizeField.setVisible(true);
        this.useGlobalNormalizationCheckBox.setEnabled(true);
        this.useGlobalNormalizationCheckBox.setVisible(true);
        // this.globalNormalizatonLabel.setEnabled(true);
        // this.globalNormalizatonLabel.setVisible(true);
      }
      else {
        this.controller.getSession().useSlidingWindow = false;
        this.windowSizeLabel.setText("");
        this.windowSizeLabel.setEnabled(false);
        this.windowSizeLabel.setVisible(false);
        this.SAXwindowSizeField.setEnabled(false);
        this.SAXwindowSizeField.setVisible(false);
        this.useGlobalNormalizationCheckBox.setEnabled(false);
        this.useGlobalNormalizationCheckBox.setVisible(false);
        // this.globalNormalizatonLabel.setEnabled(false);
        // this.globalNormalizatonLabel.setVisible(false);
        this.paaSizeLabel.setText("  Segments number:");
      }
    }

    else if (USE_GLOBAL_NORMALIZATION_ACTION.equalsIgnoreCase(command)) {
      log(Level.INFO, "global normalization toggled");
      if (this.useGlobalNormalizationCheckBox.isSelected()) {
        this.controller.getSession().useGlobalNormalization = true;
      }
      else {
        this.controller.getSession().useGlobalNormalization = false;
      }
    }

    else if (NumerosityReductionStrategy.NONE.toString().equalsIgnoreCase(command)
        || NumerosityReductionStrategy.EXACT.toString().equalsIgnoreCase(command)
        || NumerosityReductionStrategy.MINDIST.toString().equalsIgnoreCase(command)) {
      log(Level.INFO, "numerosity reduction option toggled");
      this.controller.getSession().numerosityReductionStrategy = NumerosityReductionStrategy
          .fromString(command);
    }

    else if ("Exit".equalsIgnoreCase(command)) {
      log(Level.INFO, "Exit selected, shutting down, bye! ");
      shutdown();
    }
  }

  private void raiseValidationError(String message) {
    JOptionPane.showMessageDialog(frame, message, "Validation error", JOptionPane.ERROR_MESSAGE);
  }

  private void disableAllExceptSelectButton() {
    this.dataLoadButton.setEnabled(false);
    this.modelLoadButton.setEnabled(false);
    this.guessParametersButton.setEnabled(false);
    this.discretizeButton.setEnabled(false);
    this.trainButton.setEnabled(false);
    this.testButton.setEnabled(false);
    this.findAnomaliesButton.setEnabled(false);
    this.displayChartButton.setEnabled(false);
    this.clusterRulesButton.setEnabled(false);
    this.rankRulesButton.setEnabled(false);
    this.displayRulesDensityButton.setEnabled(false);
    this.displayRulesLenHistogramButton.setEnabled(false);
    this.saveChartButton.setEnabled(false);
    this.saveModelButton.setEnabled(false);
  }

  private void enableAllButtons() {
    this.dataLoadButton.setEnabled(true);
    this.modelLoadButton.setEnabled(true);
    this.guessParametersButton.setEnabled(true);
    this.discretizeButton.setEnabled(true);
    this.findAnomaliesButton.setEnabled(true);
    this.displayChartButton.setEnabled(true);
    this.clusterRulesButton.setEnabled(true);
    this.rankRulesButton.setEnabled(true);
    this.displayRulesDensityButton.setEnabled(true);
    this.displayRulesLenHistogramButton.setEnabled(true);
    this.saveChartButton.setEnabled(true);
  }

}
