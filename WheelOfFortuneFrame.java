/**
 * EECS 285 Project 3 - Wheel of Fortune main GUI.
 *
 * See https://eecs285.github.io/p3-wheel/ for the specification.
 *
 * Project UID 7a0233619c14f47d99d949048c3b9c3319411051
 *
 * @author Andrew M. Morgan
 * @author Amir Kamil
 */

package eecs285.proj3.hankchau; // replace with your uniqname

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;
import java.io.File;
import java.io.FileFilter;
import java.net.URISyntaxException;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;


public class WheelOfFortuneFrame extends JFrame {
  /** Uniqname used for package and file paths. */
  public static final String UNIQNAME = "hankchau";
  // Replace the string above with your uniqname.

  /** Number of wheel spaces in the game. */
  public static final int NUM_WHEEL_SPACES = 24;

  /** Path to images folder. */
  public static final String IMAGES_PATH =
    "eecs285/proj3/" + UNIQNAME + "/images";

  /** File extension for images. */
  public static final String IMAGE_EXTENSION = "jpg";

  /**
   * Loades wheel-space images from the images/ directory.
   *
   * Looks for files that follow the naming pattern
   * <spaceNumber>_<value>.jpg. Ignores all other files in the
   * directory. Assumes that there are exactly NUM_WHEEL_SPACES
   * images, numbered from 1 to NUM_WHEEL_SPACES.
   *
   * @return  array of WheelSpace objects representing the images
   */
  static WheelSpace[] loadImages() {
    File[] fileList;
    File myDir = null;

    // Allocate array for number of spaces, which is set to a constant
    WheelSpace[] wheelSpaces = new WheelSpace[NUM_WHEEL_SPACES];

    // Get a File object for the directory containing the images
    try {
      myDir = new File(WheelOfFortuneFrame.class.getClassLoader()
                       .getResource(IMAGES_PATH).toURI());
    } catch (URISyntaxException uriExcep) {
      System.out.println("Caught a URI syntax exception");
      System.exit(4); // Just bail for simplicity in this project
    }

    // Loop from 1 to the number of spaces expected, so we can look
    // for files named <spaceNumber>_<value>.jpg. Note: Space numbers
    // in image filenames are 1-based, NOT 0-based.
    for (int i = 1; i <= NUM_WHEEL_SPACES; i++) {
      // Get a listing of files named appropriately for an image for
      // wheel space #i. There should only be one, and this will be
      // checked below.
      fileList = myDir.listFiles(new WheelSpaceImageFilter(i));

      if (fileList.length == 1) {
        // Index starts at 0, space numbers start at 1: hence the - 1
        wheelSpaces[i - 1] =
          new WheelSpace(WheelSpaceImageFilter.getSpaceValue(fileList[0]),
                         new ImageIcon(fileList[0].toString()));
      } else {
        System.out.println("ERROR: Invalid number of images for space: " + i);
        System.out.println("       Expected 1, but found " + fileList.length);
      }
    }

    return wheelSpaces;
  }

  // Helper nested class to filter images used for wheel spaces, based
  // on specifically expected filename format.
  private static class WheelSpaceImageFilter implements FileFilter {
    /** Prefix of the requested filename. */
    private String prefix;  // The prefix of the filename we're looking
                            // for - what comes before the first underscore

    /**
     * Constructs a filter with the given prefix.
     *
     * @param inPref  integer corresponding to the prefix
     */
    WheelSpaceImageFilter(int inPref) {
      // Sets the prefix member to string version of space number
      prefix = new Integer(inPref).toString();
    }

    /**
     * Tests whether the file provided should be accepted by our file
     * filter. In the FileFilter interface.
     */
    @Override
    public boolean accept(File imageFile) {
      boolean isAccepted = false;

      // Accepted if matched "<prefix>_<...>.jpg" where
      // IMAGE_EXTENSION is assumed to be "jpg" for this example
      if (imageFile.getName().startsWith(prefix + "_") &&
          imageFile.getName().endsWith("." + IMAGE_EXTENSION)) {
        isAccepted = true;
      }

      return isAccepted;
    }

    /**
     * Parses a wheel space image's filename to determine the dollar
     * value associated with it.
     *
     * @param imageFile  the wheel space image
     * @return  the dollar value associated with the image
     */
    public static int getSpaceValue(File imageFile) {
      String filename = imageFile.getName();
      int indx1 = filename.indexOf("_");
      int indx2 = filename.indexOf(".");

      String dollar_value = filename.substring(indx1 + 1, indx2);

      // if bankrupt
      if (dollar_value.equals("bankrupt")) {
        return -1;
      }
      // if lose a turn
      else if (dollar_value.equals("loseATurn")) {
        return -2;
      }

      return Integer.parseInt(dollar_value);
    }
  }

  /**
   * Create and start a game of Wheel of Fortune.
   *
   * @param generator  the random-number generator to use
   */
  public WheelOfFortuneFrame(Random generator) {
    this.generator = generator;

    // Load Images
    wheelSpaces = loadImages();

    // Set Up Game
    SetUpGame();

    // Get Main Frame
    GetMainFrame();

  }

  private void SetUpGame() {
    makeEnterNumPlayers();
    makeEnterPlayerNames();
    makeEnterPuzzle();
  }

  private void makeEnterNumPlayers() {
    String title = "Number of Players Input";
    String prompt = "    Enter number of players (must be at least 1)";
    GameSetupDialog numPlayersDialog = new GameSetupDialog(this, title, prompt);

    // add OK Button
    JButton submit = new JButton("OK");
    submit.addActionListener(e -> {
      // check if text is valid
      String input = numPlayersDialog.GetInputText();

      if (input.equals("")) {
        return;
      }

      try {
        num_players = Integer.parseInt(input);
        assert(num_players > 0);
        // close dialog
        numPlayersDialog.dispose();
      } catch (NumberFormatException | AssertionError error) {
        num_players = 0;
        curr_player = 0;
        // show input error message
        JOptionPane.showMessageDialog(
            numPlayersDialog,
            "Input must be a positive integer",
            "Input Error",
            JOptionPane.ERROR_MESSAGE
        );
      }
    });

    numPlayersDialog.makeBottomPanel(submit);
    numPlayersDialog.pack();
    numPlayersDialog.setVisible(true);
  }

  private void makeEnterPlayerNames() {
    // initialize players array
    players = new ArrayList<>();

    for (int i = 0; i < num_players; i++) {
      String title = "Player Name Input";
      String prompt = "   Enter name of player #" + i;
      GameSetupDialog playerNameDialog = new GameSetupDialog(this, title, prompt);

      int player_index = i;

      JButton submit = new JButton("OK");
      submit.addActionListener(e -> {
        String input = playerNameDialog.GetInputText();
        // check if input is empty
        if (input.equals("")) {
          return;
        }
        players.add(new Player(player_index, input));

        playerNameDialog.dispose();
      });

      playerNameDialog.makeBottomPanel(submit);
      playerNameDialog.pack();
      playerNameDialog.setVisible(true);
    }
  }

  private void makeEnterPuzzle() {
    String title = "Puzzle Input";
    String prompt = "Ask a non-player to enter a puzzle";
    GameSetupDialog puzzleDialog = new GameSetupDialog(this, title, prompt);

    // add OK button
    JButton submit = new JButton("OK");
    submit.addActionListener(e -> {
      if (puzzleDialog.GetInputText().equals("")) {
        return;
      }
      puzzle = (puzzleDialog.GetInputText()).toUpperCase();
      puzzleDialog.dispose();
    });

    puzzleDialog.makeBottomPanel(submit);
    puzzleDialog.pack();
    puzzleDialog.setVisible(true);
  }

  private void GetMainFrame() {
    // set Main Frame Layout
    setLayout(new BorderLayout());

    // add puzzle panel
    add(makeBottomPanel(), BorderLayout.SOUTH);
    // add name panel
    add(makeNamesPanel(), BorderLayout.NORTH);
    // add middle
    add(makeCenterPanel(), BorderLayout.CENTER);

    pack();
    setVisible(true);
  }

  private JPanel makeNamesPanel() {
    JPanel namesPanel = new JPanel();
    namesPanel.setLayout(new GridLayout(1, num_players));

    for (Player player : players) {
      JPanel playerPanel = new JPanel();

      // add money Label
      int money = player.GetMoney();
      JLabel moneyLabel = new JLabel(Integer.toString(money));
      playerPanel.add(moneyLabel);

      // set border
      String name = player.GetName();
      TitledBorder playerInfoBorder = BorderFactory.createTitledBorder(name);
      playerInfoBorder.setTitleJustification(TitledBorder.LEFT);
      playerPanel.setBorder(playerInfoBorder);

      walletLabels.add(moneyLabel);
      nameBorders.add(playerInfoBorder);
      namesPanel.add(playerPanel);
    }
    // set first border to red
    nameBorders.get(curr_player).setBorder(BorderFactory.createLineBorder(Color.RED));

    return namesPanel;
  }

  private JPanel makeBottomPanel() {
    JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new BorderLayout());

    // add alphabets panel
    bottomPanel.add(makeAlphaPanel(), BorderLayout.NORTH);
    // add puzzle panel
    bottomPanel.add(makePuzzlePanel(), BorderLayout.SOUTH);

    return bottomPanel;
  }

  private JPanel makeCenterPanel() {
    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new GridLayout(1, 3));

    // empty dummy panel
    centerPanel.add(new JPanel());

    // add Action Panel
    centerPanel.add(makeActionPanel());

    // empty dummy panel
    centerPanel.add(new JPanel());

    return centerPanel;
  }

  private JPanel makeActionPanel() {
    JPanel actionPanel = new JPanel();
    actionPanel.setLayout(new FlowLayout());

    // add Buttons Panel
    actionPanel.add(makeActionButtonPanel());
    // add Wheel Panel

    wheelImageLabel = makeWheelPanel();
    actionPanel.add(wheelImageLabel);

    return actionPanel;
  }

  private JPanel makeActionButtonPanel() {
    // Buy Vowel Button
    buyVowel = new JButton("Buy a Vowel");
    buyVowel.addActionListener(e -> {
      BuyVowelListener();
    });
    buyVowel.setEnabled(false);

    // Spin Wheel Button
    spinWheel = new JButton("Spin the Wheel");
    spinWheel.addActionListener(e ->
        SpinWheelListener()
    );

    // Solve Puzzle Button
    solvePuzzle = new JButton("Solve the Puzzle");
    solvePuzzle.addActionListener(e ->
      SolvePuzzleListener()
    );

    JPanel actionButtonPanel = new JPanel();
    actionButtonPanel.setLayout(new GridLayout(7, 1));

    actionButtonPanel.add(new JLabel());
    actionButtonPanel.add(buyVowel);

    actionButtonPanel.add(new JLabel());
    actionButtonPanel.add(spinWheel);

    actionButtonPanel.add(new JLabel());
    actionButtonPanel.add(solvePuzzle);
    actionButtonPanel.add(new JLabel());

    return actionButtonPanel;
  }

  private void BuyVowelListener() {
    // Disable Action Buttons
    DisableActionButtons();
    // spend money
    Player player = players.get(curr_player);
    player.SpendMoney(vowel_price);
    walletLabels.get(curr_player).setText(Integer.toString(player.GetMoney()));

    EnableAlphaButtons("vowel");

    repaint();
  }

  private void SpinWheelListener() {
    // Disable Action Buttons
    DisableActionButtons();

    // get new wheel_value;
    int index = generator.nextInt(NUM_WHEEL_SPACES);
    // update Wheel Image Icon
    ImageIcon wheelImage = wheelSpaces[index].GetImageIcon();
    wheelImageLabel.setIcon(wheelImage);

    // update wheel value
    wheelValue = wheelSpaces[index].GetSpaceValue();

    if (wheelValue == -1) {
      Player player = players.get(curr_player);
      // go bankrupt
      player.GoBankrupt();
      // update wallet Label
      walletLabels.get(curr_player).setText("0");
      // Next Player Chooses Action
      SetupNextPlayersTurn();

    } else if (wheelValue == -2) {
      // lose a turn
      // Next Player Chooses Action
      SetupNextPlayersTurn();

    } else {
      EnableAlphaButtons("consonants");
    }

    // re-render
    repaint();
  }

  private void SolvePuzzleListener() {
    // Disable Action Buttons
    DisableActionButtons();

    String title = "Solve Puzzle";
    String prompt = "   Enter complete puzzle exactly as displayed";
    GameSetupDialog solvePuzzleDialog = new GameSetupDialog(this, title, prompt);

    JButton submit = new JButton("OK");
    submit.addActionListener(e -> CheckSolvePuzzle(solvePuzzleDialog));

    // add submit button
    solvePuzzleDialog.makeBottomPanel(submit);
    solvePuzzleDialog.pack();
    solvePuzzleDialog.setVisible(true);
  }

  private void CheckSolvePuzzle(GameSetupDialog solvePuzzleDialog) {
    String input = solvePuzzleDialog.GetInputText();
    String name = players.get(curr_player).GetName();

    // check answer
    if (input.toUpperCase().equals(puzzle)) {
      // right answer
      int money = players.get(curr_player).GetMoney();
      JOptionPane.showMessageDialog(
          solvePuzzleDialog,
          name + " wins $" + money,
          "Game Over",
          JOptionPane.PLAIN_MESSAGE
      );
      dispose();

    } else {
      // wrong answer
      JOptionPane.showMessageDialog(
          solvePuzzleDialog,
          "Guess by " + name + " was incorrect!",
          "Wrong Answer",
          JOptionPane.ERROR_MESSAGE
      );
      // Next Player Chooses Action
      solvePuzzleDialog.dispose();
      SetupNextPlayersTurn();
      repaint();
    }
  }

  private void DisableActionButtons() {
    buyVowel.setEnabled(false);
    spinWheel.setEnabled(false);
    solvePuzzle.setEnabled(false);
  }

  private void EnableAlphaButtons(String mode) {
    if (mode.equals("vowel")) {
      for (JButton v : vowelButtons) {
        v.setEnabled(true);
      }
    } else if (mode.equals("consonants")) {
      for (JButton c : consonantButtons) {
        c.setEnabled(true);
      }
    }
  }

  private JLabel makeWheelPanel() {
    JPanel wheelPanel = new JPanel();

    // initial image
    ImageIcon wheelImage = wheelSpaces[0].GetImageIcon();
    JLabel wheelLabel = new JLabel(wheelImage);

    // add to JPanel
    wheelPanel.add(wheelLabel);

    return wheelLabel;
  }

  private JPanel makeAlphaPanel() {
    JPanel alphaPanel = new JPanel();
    alphaPanel.setLayout(new FlowLayout());

    // add vowels
    alphaPanel.add(makeVowelsPanel());
    // add consonants
    alphaPanel.add(makeConsonantsPanel());

    return alphaPanel;
  }

  private JPanel makeVowelsPanel() {
    String[] vowels_text = {"A", "E", "I", "O", "U"};

    JPanel vowelsPanel = new JPanel();
    vowelsPanel.setLayout(new GridLayout(3, 2));

    TitledBorder vowelsBorder = BorderFactory.createTitledBorder("Vowels");
    vowelsBorder.setTitleJustification(TitledBorder.LEFT);
    vowelsPanel.setBorder(vowelsBorder);

    for (String v : vowels_text) {
      JButton vowelButton = new JButton(v);
      // add Action Listener
      vowelButton.addActionListener(e ->
          AlphaButtonListener(vowelButton, "vowel")
      );
      vowelButton.setEnabled(false);
      vowelButtons.add(vowelButton);
      vowelsPanel.add(vowelButton);
    }

    return vowelsPanel;
  }

  private JPanel makeConsonantsPanel() {
      String[] consonants = {"B", "C", "D", "F", "G", "H", "J",
                            "K", "L", "M", "N", "P", "Q", "R",
                            "S", "T", "V", "W", "X", "Y", "Z"};
    JPanel consonantsPanel = new JPanel();
    consonantsPanel.setLayout(new GridLayout(3, 7));

    TitledBorder consonantsBorder = BorderFactory.createTitledBorder("Consonants");
    consonantsBorder.setTitleJustification(TitledBorder.LEFT);
    consonantsPanel.setBorder(consonantsBorder);

    for (String c : consonants) {
      JButton consonantButton = new JButton(c);
      // add Action Listener
      consonantButton.addActionListener(e ->
          AlphaButtonListener(consonantButton, "consonant")
      );

      consonantButton.setEnabled(false);
      consonantButtons.add(consonantButton);
      consonantsPanel.add(consonantButton);
    }

    return consonantsPanel;
  }

  private void AlphaButtonListener(JButton alphaButton, String mode) {
    // Disable all alpha buttons;
    DisableAlphaButtons();

    // check if letter is correct
    String letter = alphaButton.getText();
    int letter_counter = UpdatePuzzle(letter);

    // remove alphaButton from list
    if (mode.equals("vowel")) {
      vowelButtons.remove(alphaButton);
    } else if (mode.equals("consonant")) {
      consonantButtons.remove(alphaButton);
      // update player wallet
      int amount = wheelValue * letter_counter;

      Player player = players.get(curr_player);
      player.AddMoney(amount);
      // update wallet Label
      walletLabels.get(curr_player).setText(Integer.toString(player.GetMoney()));
    }

    // Change curr player border color for name panels
    if (letter_counter == 0) {
      // next player's turn
      SetupNextPlayersTurn();
    } else {
      SetActionButtons();
    }

    //re-render
    repaint();
  }

  private void DisableAlphaButtons() {
    // disable all alphabet buttons
    for (JButton cons : consonantButtons) {
      cons.setEnabled(false);
    }
    for (JButton v : vowelButtons) {
      v.setEnabled(false);
    }
  }

  private int UpdatePuzzle(String letter) {
    int letter_counter = 0;
    // updates puzzle if it contains letter
    if (puzzle.contains(letter)) {
      StringBuilder new_displayed = new StringBuilder();
      // reveal letters
      for (int j = 0; j < puzzle.length(); j++) {
        char puzzle_letter = puzzle.charAt(j);
        if (puzzle_letter == letter.charAt((0))) {
          letter_counter++;
          new_displayed.append(letter);
        } else {
          new_displayed.append(displayed_puzzle.charAt(j));
        }
      }
      // update displayed puzzle
      displayed_puzzle = new_displayed.toString();
      puzzleLabel.setText(displayed_puzzle);
    }

    return letter_counter;
  }

  private void SetupNextPlayersTurn() {
    ChangePlayerBorderColor();
    SetActionButtons();
  }

  private void ChangePlayerBorderColor() {
    nameBorders.get(curr_player).setBorder(
        BorderFactory.createLineBorder(Color.BLACK)
    );
    // if no players left
    if (curr_player == num_players - 1) {
      // start with first player
      curr_player = 0;
    } else {
      curr_player++;
    }
    // set new player border color to red
    nameBorders.get(curr_player).setBorder(
        BorderFactory.createLineBorder(Color.RED)
    );
  }

  private void SetActionButtons() {
    // set action buttons
    // check if can buy vowel
    int money = players.get(curr_player).GetMoney();
    if (money >= vowel_price && !vowelButtons.isEmpty()) {
      buyVowel.setEnabled(true);
    } else {
      buyVowel.setEnabled(false);
    }
    // check if can spin wheel
    if (!consonantButtons.isEmpty()) {
      spinWheel.setEnabled(true);
    } else {
      spinWheel.setEnabled(false);
    }

    solvePuzzle.setEnabled(true);
  }

  private JPanel makePuzzlePanel() {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < puzzle.length(); i++) {
      // check if char is a letter
      char c = puzzle.charAt(i);
      if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
        sb.append("-");
      } else {
        sb.append(c);
      }
    }
    displayed_puzzle = sb.toString();

    puzzleLabel = new JLabel(displayed_puzzle);
    puzzleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    puzzleLabel.setVerticalAlignment(SwingConstants.CENTER);

    JPanel puzzlePanel = new JPanel();
    puzzlePanel.add(puzzleLabel);

    // add Action Listener

    return puzzlePanel;
  }



  private int vowel_price = 250;
  private int num_players;
  private int curr_player;
  private String puzzle;
  private String displayed_puzzle;
  private WheelSpace[] wheelSpaces;
  private ArrayList<Player> players;
  private Random generator;

  // Name Panels
  private ArrayList<JLabel> walletLabels = new ArrayList<>();
  private ArrayList<TitledBorder> nameBorders = new ArrayList<>();

  // Action Buttons
  private JButton buyVowel;
  private JButton spinWheel;
  private JButton solvePuzzle;

  // wheel Panel
  private JLabel wheelImageLabel;
  private int wheelValue;

  // Alphabet Buttons
  private ArrayList<JButton> vowelButtons = new ArrayList<>();
  private ArrayList<JButton> consonantButtons = new ArrayList<>();

  // Puzzle Button
  private JLabel puzzleLabel;
}
