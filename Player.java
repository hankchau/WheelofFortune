package eecs285.proj3.hankchau;

public class Player {
  // Ctor
  public Player(int playerIndex, String name) {
    this.playerIndex = playerIndex;
    this.name = name;
    money = 0;
    myTurn = false;
  }

  public String GetName() {
    return name;
  }

  public int GetMoney() {
    return money;
  }

  public int GetIndex() {
    return playerIndex;
  }

  public boolean GetTurn() {
    return myTurn;
  }

  public void SetTurn(boolean bool) {
    myTurn = bool;
  }

  public void GoBankrupt() {
    money = 0;
  }

  public void SkipTurn() {
    myTurn = false;
  }

  public void AddMoney(int amount) {
    money += amount;
  }

  public void SpendMoney(int amount) {
    money -= amount;
  }


  private String name;
  private int playerIndex;
  private int money;
  private boolean myTurn;
}
