/**
 * EECS 285 Project 3 - command-line interface for Wheel of Fortune.
 *
 * See https://eecs285.github.io/p3-wheel/ for the specification.
 *
 * Project UID 7a0233619c14f47d99d949048c3b9c3319411051
 *
 * @author Andrew M. Morgan
 * @author Amir Kamil
 */

package eecs285.proj3.hankchau; // replace with your uniqname

import java.util.Random;
import javax.swing.JFrame;

public class WheelOfFortune {

  /**
   * Command-line interface for the Wheel of Fortune game.
   *
   * The seed for the random-number generator can be specified as a
   * command-line argument.
   */
  public static void main(String[] args) {
    // This will be the main frame that contains the game interface...
    WheelOfFortuneFrame gameFrame;

    // The seed value that should be used to construct a Random...
    long randomSeedVal = 100;
    if (args.length > 0) {
      try {
        randomSeedVal = Long.parseLong(args[0]);
      } catch (NumberFormatException e) {
        System.out.println("Random seed must be an integer");
        System.exit(1);
      }
    }

    gameFrame = new WheelOfFortuneFrame(new Random(randomSeedVal));
    gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    gameFrame.pack();

    // Show the game interface and start the game!
    gameFrame.setVisible(true);
  }
}
