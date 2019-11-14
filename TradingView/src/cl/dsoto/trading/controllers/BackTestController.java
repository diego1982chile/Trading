package cl.dsoto.trading.controllers;

import cl.dsoto.trading.clients.ServiceLocator;
import cl.dsoto.trading.components.PeriodManager;
import cl.dsoto.trading.model.*;
import cl.dsoto.trading.strategies.StrategyHelper;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.ta4j.core.*;
import org.ta4j.core.analysis.CashFlow;
import org.ta4j.core.analysis.criteria.AverageProfitableTradesCriterion;
import org.ta4j.core.analysis.criteria.RewardRiskRatioCriterion;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.analysis.criteria.VersusBuyAndHoldCriterion;
import ta4jexamples.analysis.BuyAndSellSignalsToChart;
import ta4jexamples.research.MultipleStrategy;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by des01c7 on 12-04-19.
 */
public class BackTestController {

    //@EJB
    private PeriodManager periodManager = (PeriodManager) ServiceLocator.getInstance().getService(PeriodManager.class);

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

    List<Period> periods;

    Period selected;


    public List<Period> getLast() throws Exception {
        try {
            return periodManager.getLast(100);
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public BackTestController(JList periods) throws Exception {
        try {
            this.periods = getLast();
            periods.setModel(new javax.swing.AbstractListModel() {
                List<Period> periods = getLast();
                public int getSize() { return periods.size(); }
                public Object getElementAt(int i) { return periods.get(i); }
            });
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Period getSelected() {
        return selected;
    }

    public void setSelected(Period selected) {
        if(this.selected != selected) {
            this.selected = selected;
            computeResults();
        }
    }

    public PeriodManager getPeriodManager() {
        return periodManager;
    }

    public void setPeriodManager(PeriodManager periodManager) {
        this.periodManager = periodManager;
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

    private void computeResults() {

        try {
            TimeSeries series = new BaseTimeSeries(selected.getName());

            for (PeriodBar periodBar : selected.getBars()) {
                ZonedDateTime time = periodBar.getEndTime();
                double open = periodBar.getOpenPrice().doubleValue();
                double close = periodBar.getClosePrice().doubleValue();
                double max = periodBar.getMaxPrice().doubleValue();
                double min = periodBar.getMinPrice().doubleValue();
                double vol = periodBar.getVolume().doubleValue();

                series.addBar(new BaseBar(time, open, max, min, close, vol));
            }

            org.slf4j.LoggerFactory.getLogger(this.getClass());

            getStart().setText(DateTimeFormatter.ISO_LOCAL_DATE.format(series.getFirstBar().getEndTime()));
            getEnd().setText(DateTimeFormatter.ISO_LOCAL_DATE.format(series.getLastBar().getEndTime()));

            TimeSeriesManager seriesManager = new TimeSeriesManager(series);

            List<org.ta4j.core.Strategy> strategies = StrategyHelper.mapFrom(selected);

            MultipleStrategy multipleStrategy = new MultipleStrategy(strategies);

            TradingRecord tradingRecord = seriesManager.run(multipleStrategy.buildStrategy(series), Order.OrderType.BUY);

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

            for (int i = 0; i < cashFlow.getSize(); ++i) {
                System.out.println("CashFlow["+ i +"]: " + cashFlow.getValue(i));
                //getCashFlowDetailView().append("CashFlow["+ i +"]: " + cashFlow.getValue(i));
                //getCashFlowDetailView().append(newline);
            }

            getCashFlowView().setText(String.valueOf(decimalFormat.format(cashFlow.getValue(cashFlow.getSize()-1))));

            //Chart
            JFreeChart jfreechart = BuyAndSellSignalsToChart.buildCandleStickChart(series, multipleStrategy.buildStrategy(series));
            ChartPanel panel = new ChartPanel(jfreechart);
            panel.setFillZoomRectangle(true);
            panel.setMouseWheelEnabled(true);
            panel.setPreferredSize(new Dimension(1024, 400));

            getPlotView().setLayout(new BorderLayout());
            getPlotView().removeAll();
            getPlotView().add(panel);
            getPlotView().validate();

            //BuyAndSellSignalsToChart.buildCandleStickChart(series, multipleStrategy.buildStrategy(series));
        }
        catch(Exception e) {

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
