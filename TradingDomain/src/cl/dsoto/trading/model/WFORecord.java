package cl.dsoto.trading.model;

/**
 * Created by root on 07-12-21.
 */
public class WFORecord {

    String id;
    int iteration;
    String strategy;
    String period;
    String stage;
    int numberOfTrades;
    double profitableTradesRatio;
    double rewardRiskRatio;
    double vsBuyAndHoldRatio;
    double cashflow;
    double efficiencyIndex;
    String parameters;

    public WFORecord(String id, int iteration, String strategy, String period, String stage, int numberOfTrades,
                     double profitableTradesRatio, double rewardRiskRatio, double vsBuyAndHoldRatio, double cashflow,
                     double efficiencyIndex, String parameters) {
        this.id = id;
        this.iteration = iteration;
        this.strategy = strategy;
        this.period = period;
        this.stage = stage;
        this.numberOfTrades = numberOfTrades;
        this.profitableTradesRatio = profitableTradesRatio;
        this.rewardRiskRatio = rewardRiskRatio;
        this.vsBuyAndHoldRatio = vsBuyAndHoldRatio;
        this.cashflow = cashflow;
        this.efficiencyIndex = efficiencyIndex;
        this.parameters = parameters;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public int getNumberOfTrades() {
        return numberOfTrades;
    }

    public void setNumberOfTrades(int numberOfTrades) {
        this.numberOfTrades = numberOfTrades;
    }

    public double getProfitableTradesRatio() {
        return profitableTradesRatio;
    }

    public void setProfitableTradesRatio(double profitableTradesRatio) {
        this.profitableTradesRatio = profitableTradesRatio;
    }

    public double getRewardRiskRatio() {
        return rewardRiskRatio;
    }

    public void setRewardRiskRatio(double rewardRiskRatio) {
        this.rewardRiskRatio = rewardRiskRatio;
    }

    public double getVsBuyAndHoldRatio() {
        return vsBuyAndHoldRatio;
    }

    public void setVsBuyAndHoldRatio(double vsBuyAndHoldRatio) {
        this.vsBuyAndHoldRatio = vsBuyAndHoldRatio;
    }

    public double getCashflow() {
        return cashflow;
    }

    public void setCashflow(double cashflow) {
        this.cashflow = cashflow;
    }

    public double getEfficiencyIndex() {
        return efficiencyIndex;
    }

    public void setEfficiencyIndex(double efficiencyIndex) {
        this.efficiencyIndex = efficiencyIndex;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }


}
