package eecs285.proj3.hankchau;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class WheelSpace {

  /*
   * Constructor for WheelSpace class
   */
  public WheelSpace(int spaceValue, ImageIcon imageIcon) {
    this.spaceValue = spaceValue;
    this.imageIcon = imageIcon;
  }

  int GetSpaceValue() {
    return spaceValue;
  }

  public ImageIcon GetImageIcon() {
    return imageIcon;
  }


  private ImageIcon imageIcon;
  private int spaceValue;

}
