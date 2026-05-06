package PlayerPackage;

import MiscPackage.BetRecord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String playerName;
    private final int playerID;
    private static int nextPlayerID = 0;
    private double balance;
    private final List<BetRecord> records;

    public Player(String playerName, double balance){
        this.playerName = playerName;
        this.balance = balance;
        this.records = new ArrayList<>();
        this.playerID = nextPlayerID++;
    }

    public String getPlayerName() {return playerName;}
    public int getPlayerID() {return playerID;}
    public double getBalance() {return balance;}
    public void updateBalance(double balance) {this.balance = balance;}
    public void addBalance(double amount) {this.balance += amount;}
    public List<BetRecord> getRecords() {return records;}
    public void addRecord(BetRecord betRecord){records.add(betRecord);}

}
