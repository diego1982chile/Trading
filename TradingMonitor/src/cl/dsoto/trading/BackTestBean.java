package cl.dsoto.trading;

import cl.dsoto.trading.clients.ServiceLocator;
import cl.dsoto.trading.components.GuestPreferences;
import cl.dsoto.trading.components.PeriodManager;
import cl.dsoto.trading.daos.OptimizationDAO;
import cl.dsoto.trading.daos.PeriodDAO;
import cl.dsoto.trading.model.Period;
import cl.dsoto.trading.model.PeriodBar;
import org.primefaces.model.DefaultTreeNode;
import org.ta4j.core.*;
import org.ta4j.core.analysis.CashFlow;
import org.ta4j.core.analysis.criteria.AverageProfitableTradesCriterion;
import org.ta4j.core.analysis.criteria.RewardRiskRatioCriterion;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.analysis.criteria.VersusBuyAndHoldCriterion;
import ta4jexamples.analysis.BuyAndSellSignalsToChart;
import ta4jexamples.research.MultipleStrategy;
import ta4jexamples.strategies.*;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by des01c7 on 03-04-19.
 */
@ManagedBean
@ViewScoped
public class BackTestBean {

    /**
     * Lista de categorías para el despliegue del filtro por categorías
     */
    private List<Period> periods = new ArrayList<>();

    private Period selectedPeriod;

    //@EJB
    private PeriodManager periodManager;

    private int trades;
    private double profitableTrades;
    private double rewardRisk;
    private double vsBuyAndHold;

    @ManagedProperty(value = "#{guestPreferences}")
    GuestPreferences guestPreferences;

    public GuestPreferences getGuestPreferences() {
        return guestPreferences;
    }

    public void setGuestPreferences(GuestPreferences guestPreferences) {
        this.guestPreferences = guestPreferences;
    }

    @PostConstruct
    protected void initialize() {

        periodManager = (PeriodManager) ServiceLocator.getInstance().getService(PeriodManager.class);

        guestPreferences.setTheme("indigo");

        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        try {
            periods = periodManager.getLast(100);
        }
        catch (Exception e) {

        }

    }

    public List<Period> getPeriods() {
        return periods;
    }

    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }

    public Period getSelectedPeriod() {
        return selectedPeriod;
    }

    public void setSelectedPeriod(Period selectedPeriod) {
        this.selectedPeriod = selectedPeriod;
    }

    public int getTrades() {
        return trades;
    }

    public void setTrades(int trades) {
        this.trades = trades;
    }

    public double getProfitableTrades() {
        return profitableTrades;
    }

    public void setProfitableTrades(double profitableTrades) {
        this.profitableTrades = profitableTrades;
    }

    public double getRewardRisk() {
        return rewardRisk;
    }

    public void setRewardRisk(double rewardRisk) {
        this.rewardRisk = rewardRisk;
    }

    public double getVsBuyAndHold() {
        return vsBuyAndHold;
    }

    public void setVsBuyAndHold(double vsBuyAndHold) {
        this.vsBuyAndHold = vsBuyAndHold;
    }

    private void computeResults() {

        try {
            TimeSeries series = new BaseTimeSeries(selectedPeriod.getName());

            for (PeriodBar periodBar : selectedPeriod.getBars()) {
                series.addBar(periodBar);
            }

            TimeSeriesManager seriesManager = new TimeSeriesManager(series);

            List<Strategy> strategies = periodManager.mapFrom(selectedPeriod);

            MultipleStrategy multipleStrategy = new MultipleStrategy(strategies);

            TradingRecord tradingRecord = seriesManager.run(multipleStrategy.buildStrategy(series), Order.OrderType.BUY);

            System.out.println("Number of trades for our strategy: " + tradingRecord.getTradeCount());

            // Analysis

            // Getting the cash flow of the resulting trades
            CashFlow cashFlow = new CashFlow(series, tradingRecord);

            // Getting the profitable trades ratio
            AnalysisCriterion profitTradesRatio = new AverageProfitableTradesCriterion();
            //System.out.println("Profitable trades ratio: " + profitTradesRatio.calculate(series, tradingRecord));
            setProfitableTrades(profitTradesRatio.calculate(series, tradingRecord));

            // Getting the reward-risk ratio
            AnalysisCriterion rewardRiskRatio = new RewardRiskRatioCriterion();
            //System.out.println("Reward-risk ratio: " + rewardRiskRatio.calculate(series, tradingRecord));
            setRewardRisk(rewardRiskRatio.calculate(series, tradingRecord));

            // Total profit of our strategy
            // vs total profit of a buy-and-hold strategy
            AnalysisCriterion vsBuyAndHold = new VersusBuyAndHoldCriterion(new TotalProfitCriterion());
            //System.out.println("Our profit vs buy-and-hold profit: " + vsBuyAndHold.calculate(series, tradingRecord));
            setVsBuyAndHold(vsBuyAndHold.calculate(series, tradingRecord));

            for (int i = 0; i < tradingRecord.getTrades().size(); ++i) {
                System.out.println("Trade["+ i +"]: " + tradingRecord.getTrades().get(i).toString());
            }

            for (int i = 0; i < cashFlow.getSize(); ++i) {
                System.out.println("CashFlow["+ i +"]: " + cashFlow.getValue(i));
            }

            //BuyAndSellSignalsToChart.buildCandleStickChart(series, multipleStrategy.buildStrategy(series));
        }
        catch(Exception e) {

        }

    }

}
