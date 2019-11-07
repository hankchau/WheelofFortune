package eecs285.proj3.hankchau;

import java.awt.*;
import javax.swing.*;

class GameSetupDialog extends JDialog {

  GameSetupDialog(JFrame parent, String title, String prompt_text) {
    super(parent, title, true);
    // declare new dialog
    // set layout
    setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

    // add Top Panel
    add(makeTopPanel(prompt_text));

    // prevent users from closing window
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
  }

  private JPanel makeTopPanel(String prompt) {
    JPanel topPanel = new JPanel();
    topPanel.setLayout(new BorderLayout());

    topPanel.add(makeTextField(), BorderLayout.SOUTH);
    topPanel.add(makePromptLabel(prompt), BorderLayout.NORTH);

    return topPanel;
  }

  private JTextField makeTextField() {
    inputField = new JTextField(7);

    return inputField;
  }

  private JLabel makePromptLabel(String prompt) {
    return new JLabel(prompt, SwingConstants.LEFT);
  }

  void makeBottomPanel(JButton submit) {
    JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new BorderLayout());

    JPanel gridPanel = new JPanel();
    gridPanel.setLayout(new GridLayout(1, 7));

    // empty dummy panels
    gridPanel.add(new JPanel());
    gridPanel.add(new JPanel());
    gridPanel.add(new JPanel());

    // add submit button
    gridPanel.add(submit);

    // empty dummy panels
    gridPanel.add(new JPanel());
    gridPanel.add(new JPanel());
    gridPanel.add(new JPanel());

    bottomPanel.add(gridPanel, BorderLayout.CENTER);

    add(bottomPanel);
  }

  String GetInputText() {
    return inputField.getText();
  }

  private JTextField inputField;
}
