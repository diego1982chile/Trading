package cl.dsoto.trading.controllers;

import cl.dsoto.trading.clients.ServiceLocator;
import cl.dsoto.trading.components.ForwardTestManager;
import cl.dsoto.trading.components.PeriodManager;
import cl.dsoto.trading.model.*;
import cl.dsoto.trading.strategies.StrategyHelper;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.ta4j.core.*;
import org.ta4j.core.Strategy;
import org.ta4j.core.analysis.CashFlow;
import org.ta4j.core.analysis.criteria.AverageProfitableTradesCriterion;
import org.ta4j.core.analysis.criteria.RewardRiskRatioCriterion;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.analysis.criteria.VersusBuyAndHoldCriterion;
import ta4jexamples.analysis.BuyAndSellSignalsToChart;
import ta4jexamples.loaders.CsvTicksLoader;
import ta4jexamples.research.MultipleStrategy;
import ta4jexamples.strategies.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by des01c7 on 12-04-19.
 */
public class ForwardTestController  {

    //@EJB
    private ForwardTestManager forwardTestManager = (ForwardTestManager) ServiceLocator.getInstance().getService(ForwardTestManager.class);

    private JTextField start;
    private JTextField end;

    private int trades;
    private JTextField tradesView;

    private double profitableTrades;
    private JTextField profitableTradesView;

    private double rewardRisk;
    private JTextField rewardRiskView;

    private double vsBuyAndHold;
    private JTextField vsBuyAndHoldView;

    private CashFlow cashFlow;
    private JTextField cashFlowView;

    private JTextArea cashFlowDetailView;

    private JPanel plotView;

    private JLabel strategiesView;

    String pattern = "###.#####";
    DecimalFormat decimalFormat = new DecimalFormat(pattern);

    private static String newline = System.getProperty("line.separator");

    ForwardTest selected;

    /** Close price of the last bar */
    private static Decimal LAST_BAR_CLOSE_PRICE;

    private static TimeSeries live;


    public ForwardTestController(ForwardTest selected) throws Exception {
        this.selected = selected;
    }

    public ForwardTest getSelected() {
        return selected;
    }

    public void setSelected(ForwardTest selected) throws Exception {
        if(this.selected != selected) {
            this.selected = selected;
            computeResults(200);
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

    /**
     * Builds a moving time series (i.e. keeping only the maxBarCount last bars)
     * @param maxBarCount the number of bars to keep in the time series (at maximum)
     * @return a moving time series
     */
    private TimeSeries initMovingTimeSeries(int maxBarCount) {
        //TimeSeries series = CsvTradesLoader.loadBitstampSeries();
        //TimeSeries series = CsvTicksLoader.load("EURUSD_Daily_201701020000_201712290000.csv");
        //TimeSeries series = CsvTicksLoader.load("2019_D.csv");

        TimeSeries series = new BaseTimeSeries(selected.getPeriod().getName());

        for (PeriodBar periodBar : selected.getPeriod().getBars()) {
            ZonedDateTime time = periodBar.getEndTime();
            double open = periodBar.getOpenPrice().doubleValue();
            double close = periodBar.getClosePrice().doubleValue();
            double max = periodBar.getMaxPrice().doubleValue();
            double min = periodBar.getMinPrice().doubleValue();
            double vol = periodBar.getVolume().doubleValue();

            series.addBar(new BaseBar(time, open, max, min, close, vol));
        }

        System.out.print("Initial bar count: " + series.getBarCount());
        // Limitating the number of bars to maxBarCount
        series.setMaximumBarCount(maxBarCount);
        LAST_BAR_CLOSE_PRICE = series.getBar(series.getEndIndex()).getClosePrice();
        System.out.println(" (limited to " + maxBarCount + "), close price = " + LAST_BAR_CLOSE_PRICE);

        //live = CsvTicksLoader.load("2020_D.csv");
        //live = CsvTicksLoader.load("2020_D.csv");

        live = new BaseTimeSeries(selected.getName());

        for (ForwardTestBar forwardTestBar : selected.getBars()) {
            ZonedDateTime time = forwardTestBar.getEndTime();
            double open = forwardTestBar.getOpenPrice().doubleValue();
            double close = forwardTestBar.getClosePrice().doubleValue();
            double max = forwardTestBar.getMaxPrice().doubleValue();
            double min = forwardTestBar.getMinPrice().doubleValue();
            double vol = forwardTestBar.getVolume().doubleValue();

            live.addBar(new BaseBar(time, open, max, min, close, vol));
        }

        return series;
    }

    /**
     * @param series a time series
     * @return a dummy strategy
     */
    private Strategy buildStrategy(TimeSeries series) {

        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }

        List<Strategy> strategies = new ArrayList<>();

        try {
            strategies = StrategyHelper.mapFrom(selected.getPeriod(), series);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MultipleStrategy multipleStrategy = new MultipleStrategy(strategies);

        return multipleStrategy.buildStrategy(series);
    }

    /**
     * Generates a random bar.
     * @return a random bar
     */
    private Bar generateRandomBar(int i) {
        /*
        if(live == null || live.isEmpty()) {
            //live = CsvTicksLoader.load("EURUSD_Daily_201801020000_201812310000.csv");
            live = CsvTicksLoader.load("2020_D.csv");
        }
        */
        LAST_BAR_CLOSE_PRICE = live.getBar(i).getClosePrice();
        return live.getBar(i);
    }

    public void computeResults(int maxBarCount) {

        try {

            System.out.println("********************** Initialization **********************");
            // Getting the time series
            TimeSeries series = initMovingTimeSeries(maxBarCount);

            //TimeSeries series2 = initMovingTimeSeries(maxBarCount);

            // Building the trading strategy
            Strategy strategy = buildStrategy(series);

            //Strategy strategy2 = buildStrategy(series2);

            // Initializing the trading history
            TradingRecord tradingRecord = new BaseTradingRecord();
            System.out.println("************************************************************");

            int STEP = 13;
            int OFFSET = 20;

            boolean flag = false;

            Bar newBar = null;

            getStart().setText(DateTimeFormatter.ISO_LOCAL_DATE.format(live.getFirstBar().getEndTime()));
            getEnd().setText(DateTimeFormatter.ISO_LOCAL_DATE.format(live.getLastBar().getEndTime()));

            /*
              We run the strategy for the 50 next bars.
             */
            for (int i = 0; i < live.getBarCount(); i++) {

                while(!flag) {
                    try {
                        // New bar
                        Thread.sleep(30); // I know...
                        newBar = generateRandomBar(i);
                        System.out.println("------------------------------------------------------\n"
                                + "Bar "+i+" added, close price = " + newBar.getClosePrice().doubleValue());
                        series.addBar(newBar);
                        //series2.addBar(newBar);
                        flag = true;
                    }
                    catch(IllegalArgumentException e) {
                        i++;
                    }
                }

                flag = false;

                int endIndex = series.getEndIndex();

                if (strategy.shouldEnter(endIndex)) {
                    // Our strategy should enter
                    System.out.println("Strategy should ENTER on " + endIndex);
                    boolean entered = tradingRecord.enter(endIndex, newBar.getClosePrice(), Decimal.TEN);
                    if (entered) {
                        Order entry = tradingRecord.getLastEntry();
                        System.out.println("Entered on " + entry.getIndex()
                                + " (date=" + live.getFirstBar().getEndTime().plusDays(i)
                                + ", price=" + entry.getPrice().doubleValue()
                                + ", amount=" + entry.getAmount().doubleValue() + ")");
                    }
                } else if (strategy.shouldExit(endIndex)) {
                    // Our strategy should exit
                    System.out.println("Strategy should EXIT on " + endIndex);
                    boolean exited = tradingRecord.exit(endIndex, newBar.getClosePrice(), Decimal.TEN);
                    if (exited) {
                        Order exit = tradingRecord.getLastExit();
                        System.out.println("Exited on " + exit.getIndex()
                                + " Date=" + live.getFirstBar().getEndTime().plusDays(i)
                                + " (price=" + exit.getPrice().doubleValue()
                                + ", amount=" + exit.getAmount().doubleValue() + ")");
                    }
                }
            }

            System.out.println("Number of trades for our strategy: " + tradingRecord.getTradeCount());
            setTrades(tradingRecord.getTradeCount());
            getTradesView().setText(String.valueOf(tradingRecord.getTradeCount()));

            // Analysis

            // Getting the cash flow of the resulting trades
            cashFlow = new CashFlow(series, tradingRecord);

            // Getting the profitable trades ratio
            AnalysisCriterion profitTradesRatio = new AverageProfitableTradesCriterion();
            System.out.println("Profitable trades ratio: " + profitTradesRatio.calculate(series, tradingRecord));
            setProfitableTrades(profitTradesRatio.calculate(series, tradingRecord));
            getProfitableTradesView().setText(String.valueOf(profitTradesRatio.calculate(series, tradingRecord)));

            // Getting the reward-risk ratio
            AnalysisCriterion rewardRiskRatio = new RewardRiskRatioCriterion();
            System.out.println("Reward-risk ratio: " + rewardRiskRatio.calculate(series, tradingRecord));
            setRewardRisk(rewardRiskRatio.calculate(series, tradingRecord));
            getRewardRiskView().setText(String.valueOf(rewardRiskRatio.calculate(series, tradingRecord)));

            // Total profit of our strategy
            // vs total profit of a buy-and-hold strategy
            AnalysisCriterion vsBuyAndHold = new VersusBuyAndHoldCriterion(new TotalProfitCriterion());
            System.out.println("Our profit vs buy-and-hold profit: " + vsBuyAndHold.calculate(series, tradingRecord));
            setVsBuyAndHold(vsBuyAndHold.calculate(series, tradingRecord));
            getVsBuyAndHoldView().setText(String.valueOf(vsBuyAndHold.calculate(series, tradingRecord)));

            for (int i = 0; i < tradingRecord.getTrades().size(); ++i) {
                System.out.println("Trade["+ i +"]: " + tradingRecord.getTrades().get(i).toString());
            }

            // Getting the cash flow of the resulting trades
            CashFlow cashFlow = new CashFlow(series, tradingRecord);

            for (int i = 0; i < 10000; ++i) {
                try {
                    System.out.println("CashFlow["+ i +"]: " + cashFlow.getValue(i));
                }
                catch (IndexOutOfBoundsException e) {
                    getCashFlowView().setText(String.valueOf(decimalFormat.format(cashFlow.getValue(i-1))));
                    break;
                }
            }

            //Chart
            JFreeChart jfreechart = BuyAndSellSignalsToChart.buildCandleStickChart(series, buildStrategy(series));
            ChartPanel panel = new ChartPanel(jfreechart);
            panel.setFillZoomRectangle(true);
            panel.setMouseWheelEnabled(true);
            panel.setPreferredSize(new Dimension(800, 200));

            getPlotView().setLayout(new BorderLayout());
            getPlotView().removeAll();
            getPlotView().add(panel);
            getPlotView().validate();

        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    public JTextField getStart() {
        return start;
    }

    public void setStart(JTextField start) {
        this.start = start;
    }

    public JTextField getEnd() {
        return end;
    }

    public void setEnd(JTextField end) {
        this.end = end;
    }

    public JTextField getTradesView() {
        return tradesView;
    }

    public void setTradesView(JTextField tradesView) {
        this.tradesView = tradesView;
    }

    public JTextField getProfitableTradesView() {
        return profitableTradesView;
    }

    public void setProfitableTradesView(JTextField profitableTradesView) {
        this.profitableTradesView = profitableTradesView;
    }

    public JTextField getRewardRiskView() {
        return rewardRiskView;
    }

    public void setRewardRiskView(JTextField rewardRiskView) {
        this.rewardRiskView = rewardRiskView;
    }

    public JTextField getVsBuyAndHoldView() {
        return vsBuyAndHoldView;
    }

    public void setVsBuyAndHoldView(JTextField vsBuyAndHoldView) {
        this.vsBuyAndHoldView = vsBuyAndHoldView;
    }

    public JTextField getCashFlowView() {
        return cashFlowView;
    }

    public void setCashFlowView(JTextField cashFlowView) {
        this.cashFlowView = cashFlowView;
    }

    public JTextArea getCashFlowDetailView() {
        return cashFlowDetailView;
    }

    public void setCashFlowDetailView(JTextArea cashFlowDetailView) {
        this.cashFlowDetailView = cashFlowDetailView;
    }

    public JPanel getPlotView() {
        return plotView;
    }

    public void setPlotView(JPanel plotView) {
        this.plotView = plotView;
    }

    public JLabel getStrategiesView() {
        return strategiesView;
    }

    public void setStrategiesView(JLabel strategiesView) {
        this.strategiesView = strategiesView;
    }

}
