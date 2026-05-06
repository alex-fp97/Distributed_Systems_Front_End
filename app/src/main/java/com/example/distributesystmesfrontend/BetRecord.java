package MiscPackage;
import java.io.Serializable;
import java.time.LocalDateTime;

public class BetRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String playerID;
    private final String gameName;
    private final double betAmount;
    private final double profits;
    private transient final LocalDateTime timeStamp;

    public BetRecord(String playerID, String gameName, double betAmount, double profits) {
        this.playerID = playerID;
        this.gameName = gameName;
        this.betAmount = betAmount;
        this.profits = profits;
        this.timeStamp = LocalDateTime.now();
    }

    public String getPlayerID() {return playerID;}
    public String getGameName() {return gameName;}
    public double getBetAmount() {return betAmount;}
    public double getProfits() {return profits;}
    public LocalDateTime getTimeStamp() {return timeStamp;}

    @Override
    public String toString() {
        return "BetRecord" +
                "playerID='" + playerID + '\'' +
                ", gameName='" + gameName + '\'' +
                ", betAmount=" + betAmount +
                ", profits=" + profits +
                ", timeStamp=" + timeStamp;
    }
}
