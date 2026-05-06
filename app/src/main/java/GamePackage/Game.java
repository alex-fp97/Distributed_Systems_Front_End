package GamePackage;

import MiscPackage.BetRecord;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;
    private int gameId;
    private static int nextGameId = 0;
    private String gameName;
    private String providerName;
    private final List<BetRecord> gameBetHistory = new ArrayList<>();
    private String riskLvl;
    private int noOfVotes;
    private double rating;
    private int hashKey;
    double min_bet;
    double max_bet;
    int betCategory;

    private boolean isActive = true;

    public Game (){this.gameId = nextGameId++;}

    //getters
    public String getGameName() { return gameName; }
    public String getProviderName(){return providerName;}
    public double getMinBet() {return min_bet;}
    public double getRating() { return rating; }
    public int getGameId() { return gameId; }
    public String getRiskLvl() { return riskLvl; }
    public int getVotes() { return noOfVotes; }
    public boolean isActive() { return isActive; }
    public List<BetRecord> getBetHistory(){return gameBetHistory;}
    public int hashKey(){return this.hashKey;}

    public double[] getPayTable() {
        switch (riskLvl.toLowerCase()) {
            case "low":    return new double[]{0.0, 0.0, 0.0, 0.1, 0.5, 1.0, 1.1, 1.3, 2.0, 2.5};
            case "medium": return new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.5, 1.0, 1.5, 2.5, 3.5};
            case "high":   return new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 2.0, 6.5};
            default: throw new IllegalArgumentException("Invalid risk level: " + riskLvl);
        }
    }
    public String getBetCategory() {
        if (min_bet >= 5.0) return "$$$";
        if (min_bet >= 1.0) return "$$";
        return "$";
    }
    public int getJackpot() {
        switch (riskLvl.toLowerCase()) {
            case "low":    return 10;
            case "medium": return 20;
            case "high":   return 40;
            default: throw new IllegalArgumentException("Invalid risk level");
        }
    }

    //setters
    public void setRating(int rating) { this.rating = rating; }
    public void setActive() { isActive = true; }
    public void setInactive() {isActive = false;}

    public void updateRisk(String newRisk) {this.riskLvl = newRisk;}
    public void rateGame(double rating){
        try {
            if (rating < 0 || rating > 5) throw new IllegalArgumentException();
            else{
                noOfVotes++;
                this.rating += rating / noOfVotes;
            }
        }
        catch (IllegalArgumentException e){
            throw new RuntimeException(e);
        }
    }
    public void addBetRecord(BetRecord br){gameBetHistory.add(br);}
    public double getTotalProfitLoss(){
        double profits = 0;
        double losses = 0;

        for (BetRecord br : gameBetHistory){
            profits += br.getBetAmount();
            losses += br.getProfits();
        }

        return profits-losses;
    }

    public String toString(){
        return "GamePackage.Game Name: " + gameName +
                "\nGamePackage.Game ID: " + gameId +
                "\nActive: " + isActive +
                "\nRisk Level: " + riskLvl +
                "\nRating: " + rating;
    }
}
