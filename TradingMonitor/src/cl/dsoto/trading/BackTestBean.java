package cl.dsoto.trading;

import cl.dsoto.trading.clients.ServiceLocator;
import cl.dsoto.trading.components.GuestPreferences;
import cl.dsoto.trading.components.PeriodManager;
import cl.dsoto.trading.daos.OptimizationDAO;
import cl.dsoto.trading.daos.PeriodDAO;
import cl.dsoto.trading.model.*;
import cl.dsoto.trading.model.Strategy;
import org.apache.poi.hssf.record.chart.TickRecord;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.OhlcChartModel;
import org.primefaces.model.chart.OhlcChartSeries;
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
import javax.imageio.ImageIO;
import javax.print.attribute.standard.Compression;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by des01c7 on 03-04-19.
 */
@ManagedBean
@ViewScoped
public class BackTestBean {


    private long idSelectedPeriod;

    private Period selectedPeriod;

    //@EJB
    private PeriodManager periodManager = (PeriodManager) ServiceLocator.getInstance().getService(PeriodManager.class);;

    private int trades;
    private double profitableTrades;
    private double rewardRisk;
    private double vsBuyAndHold;
    private CashFlow cashFlow;

    private StreamedContent chart;

    private static final String GLOBAL_EXTREMA = "GlobalExtremaStrategy";

    private static final String TUNNEL = "TunnelStrategy";

    private static final String CCI_CORRECTION = "CCICorrectionStrategy";

    private static final String BAGOVINO = "BagovinoStrategy";

    private static final String MOVING_AVERAGES = "MovingAveragesStrategy";

    private static final String RSI_2 = "RSI2Strategy";

    private static final String PARABOLIC_SAR = "ParabolicSARStrategy";

    private static final String MOVING_MOMENTUM = "MovingMomentumStrategy";

    private static final String STOCHASTIC = "StochasticStrategy";

    private static final String MACD = "MACDStrategy";

    private static final String FX_BOOTCAMP = "FXBootCampStrategy";

    private static final String WINSLOW = "WinslowStrategy";

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

        //guestPreferences.setTheme("indigo");

    }

    public Period getSelectedPeriod() {
        return selectedPeriod;
    }

    public void setSelectedPeriod(Period selectedPeriod) {
        this.selectedPeriod = selectedPeriod;
        computeResults();
    }


    public long getIdSelectedPeriod() {
        return idSelectedPeriod;
    }

    public void setIdSelectedPeriod(long idSelectedPeriod) {
        try {
            this.idSelectedPeriod = idSelectedPeriod;
            setSelectedPeriod(periodManager.getPeriodById(idSelectedPeriod));
        }
        catch(Exception e) {

        }
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

    public CashFlow getCashFlow() {
        return cashFlow;
    }

    public void setCashFlow(CashFlow cashFlow) {
        this.cashFlow = cashFlow;
    }

    public StreamedContent getChart() {
        return chart;
    }

    public void setChart(StreamedContent chart) {
        this.chart = chart;
    }

    private void computeResults() {

        try {
            TimeSeries series = new BaseTimeSeries(selectedPeriod.getName());

            for (PeriodBar periodBar : selectedPeriod.getBars()) {
                series.addBar(periodBar);
            }

            TimeSeriesManager seriesManager = new TimeSeriesManager(series);

            List<org.ta4j.core.Strategy> strategies = mapFrom(selectedPeriod);

            MultipleStrategy multipleStrategy = new MultipleStrategy(strategies);

            TradingRecord tradingRecord = seriesManager.run(multipleStrategy.buildStrategy(series), Order.OrderType.BUY);

            System.out.println("Number of trades for our strategy: " + tradingRecord.getTradeCount());
            setTrades(tradingRecord.getTradeCount());

            // Analysis

            // Getting the cash flow of the resulting trades
            cashFlow = new CashFlow(series, tradingRecord);

            // Getting the profitable trades ratio
            AnalysisCriterion profitTradesRatio = new AverageProfitableTradesCriterion();
            System.out.println("Profitable trades ratio: " + profitTradesRatio.calculate(series, tradingRecord));
            setProfitableTrades(profitTradesRatio.calculate(series, tradingRecord));

            // Getting the reward-risk ratio
            AnalysisCriterion rewardRiskRatio = new RewardRiskRatioCriterion();
            System.out.println("Reward-risk ratio: " + rewardRiskRatio.calculate(series, tradingRecord));
            setRewardRisk(rewardRiskRatio.calculate(series, tradingRecord));

            // Total profit of our strategy
            // vs total profit of a buy-and-hold strategy
            AnalysisCriterion vsBuyAndHold = new VersusBuyAndHoldCriterion(new TotalProfitCriterion());
            System.out.println("Our profit vs buy-and-hold profit: " + vsBuyAndHold.calculate(series, tradingRecord));
            setVsBuyAndHold(vsBuyAndHold.calculate(series, tradingRecord));

            for (int i = 0; i < tradingRecord.getTrades().size(); ++i) {
                System.out.println("Trade["+ i +"]: " + tradingRecord.getTrades().get(i).toString());
            }

            for (int i = 0; i < cashFlow.getSize(); ++i) {
                System.out.println("CashFlow["+ i +"]: " + cashFlow.getValue(i));
            }

            //Chart
            JFreeChart jfreechart = BuyAndSellSignalsToChart.buildCandleStickChart(series, multipleStrategy.buildStrategy(series));
            File chartFile = new File("dynamichart");
            ChartRenderingInfo info = new ChartRenderingInfo();
            ChartUtilities.saveChartAsPNG(chartFile, jfreechart, 1024, 400, info, true, Compression.NONE.getValue());
            chart = new DefaultStreamedContent(new FileInputStream(chartFile), "image/png");

            //BuyAndSellSignalsToChart.buildCandleStickChart(series, multipleStrategy.buildStrategy(series));
        }
        catch(Exception e) {

        }

    }

    public List<org.ta4j.core.Strategy> mapFrom(Period period) throws Exception {

        List<org.ta4j.core.Strategy> strategies = new ArrayList<>();

        TimeSeries series = new BaseTimeSeries(period.getName());

        for (PeriodBar periodBar : period.getBars()) {
            series.addBar(periodBar);
        }

        for (Optimization optimization : period.getOptimizationsOfType(ProblemType.INTEGER)) {
            switch (optimization.getStrategy().getName()) {
                case GLOBAL_EXTREMA:
                    GlobalExtremaStrategy.mapFrom(optimization);
                    break;
                case TUNNEL:
                    TunnelStrategy.mapFrom(optimization);
                    break;
                case CCI_CORRECTION:
                    CCICorrectionStrategy.mapFrom(optimization);
                    break;
                case BAGOVINO:
                    BagovinoStrategy.mapFrom(optimization);
                    break;
                case MOVING_AVERAGES:
                    MovingAveragesStrategy.mapFrom(optimization);
                    break;
                case RSI_2:
                    RSI2Strategy.mapFrom(optimization);
                    break;
                case PARABOLIC_SAR:
                    ParabolicSARStrategy.mapFrom(optimization);
                    break;
                case MOVING_MOMENTUM:
                    MovingMomentumStrategy.mapFrom(optimization);
                    break;
                case STOCHASTIC:
                    StochasticStrategy.mapFrom(optimization);
                    break;
                case MACD:
                    MACDStrategy.mapFrom(optimization);
                    break;
                case FX_BOOTCAMP:
                    FXBootCampStrategy.mapFrom(optimization);
                    break;
                case WINSLOW:
                    WinslowStrategy.mapFrom(optimization);
                    break;
            }
        }

        for (Optimization optimization : period.getOptimizationsOfType(ProblemType.BINARY)) {
            for (Solution solution : optimization.getSolutions()) {

                for (int i = 0; i < solution.getValues().size(); i++) {
                    boolean value = (Boolean) solution.getValues().get(i);

                    if (value) {

                        switch (i) {
                            case 0:
                                strategies.add(CCICorrectionStrategy.buildStrategy(series));
                                break;
                            case 1:
                                strategies.add(GlobalExtremaStrategy.buildStrategy(series));
                                break;
                            case 2:
                                strategies.add(MovingMomentumStrategy.buildStrategy(series));
                                break;
                            case 3:
                                strategies.add(RSI2Strategy.buildStrategy(series));
                                break;
                            case 4:
                                strategies.add(MACDStrategy.buildStrategy(series));
                                break;
                            case 5:
                                strategies.add(StochasticStrategy.buildStrategy(series));
                                break;
                            case 6:
                                strategies.add(ParabolicSARStrategy.buildStrategy(series));
                                break;
                            case 7:
                                strategies.add(MovingAveragesStrategy.buildStrategy(series));
                                break;
                            case 8:
                                strategies.add(BagovinoStrategy.buildStrategy(series));
                                break;
                            case 9:
                                strategies.add(FXBootCampStrategy.buildStrategy(series));
                                break;
                            case 10:
                                strategies.add(TunnelStrategy.buildStrategy(series));
                                break;
                            case 11:
                                strategies.add(WinslowStrategy.buildStrategy(series));
                                break;
                        }
                    }
                }
            }
        }

        return strategies;

    }


}
