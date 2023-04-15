package cl.dsoto.trading.controllers;

import cl.dsoto.trading.clients.ServiceLocator;
import cl.dsoto.trading.components.PeriodManager;
import cl.dsoto.trading.components.StrategyManager;
import cl.dsoto.trading.model.*;
import cl.dsoto.trading.model.Strategy;
import javafx.util.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ta4j.core.*;
import org.ta4j.core.analysis.CashFlow;
import org.ta4j.core.analysis.criteria.AverageProfitableTradesCriterion;
import org.ta4j.core.analysis.criteria.RewardRiskRatioCriterion;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.analysis.criteria.VersusBuyAndHoldCriterion;
import org.uma.jmetal.runner.multiobjective.MOCellStockMarketIntegerRunner;
import ta4jexamples.loaders.CsvTicksLoader;
import ta4jexamples.research.MultipleStrategy;
import ta4jexamples.strategies.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by des01c7 on 28-03-19.
 */
public class WalkForward {

    static PeriodManager periodManager = (PeriodManager) ServiceLocator.getInstance().getService(PeriodManager.class);
    static StrategyManager strategyManager = (StrategyManager) ServiceLocator.getInstance().getService(StrategyManager.class);

    //static List<String> files = Arrays.asList("2011_01.csv","2011_02.csv","2011_03.csv","2011_04.csv","2011_05.csv","2011_06.csv","2011_07.csv","2011_08.csv","2011_09.csv", "2011_10.csv", "2011_11.csv", "2011_12.csv");

    static List<String> files = Arrays.asList("DATA.csv");

    static Workbook workbook;

    static FileOutputStream outputStream;

    static Map<String, Double> efficiency = new HashMap<>();

    static int cont = 0;

    static int test = 0;

    static String id = UUID.randomUUID().toString();

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

    /** Logger para la clase */
    private static final Logger logger = Logger.getLogger(WalkForward.class.getName());

    public static void main(String[] args) throws Exception {

        for (String file : files) {

            TimeSeries data = CsvTicksLoader.load(file);

            initOutput();

            for (int k = 0; k < 3; ++k) {

                test = k + 1;

                List<Strategy> strategies = strategyManager.getIntegerProblemTypeStrategies();

                //strategies = strategies.stream().filter(strategy -> strategy.getName().equals(MACD)).collect(Collectors.toList());

                for (Strategy strategy : strategies) {

                    int offset = data.getBarCount()/11;

                    for(int i = 0; i < 6; ++i) {

                        String nameIn = String.valueOf(data.getBar(i*offset).getBeginTime().getYear());
                        nameIn = nameIn + "_" + String.valueOf(data.getBar((i+4)*offset).getBeginTime().getYear());

                        String nameOut = String.valueOf(data.getBar((i+4)*offset + 1).getBeginTime().getYear());

                        TimeSeries in = new BaseTimeSeries(nameIn,data.getBarData().subList(i*offset,(i+4)*offset));
                        TimeSeries out = new BaseTimeSeries(nameOut,data.getBarData().subList((i+4)*offset + 1,(i+5)*offset));

                        Period period = createFromSeries(in);

                        /*
                        GenerationalGeneticAlgorithmStockMarketIntegerRunner runner =
                                new GenerationalGeneticAlgorithmStockMarketIntegerRunner(strategy.getName(), in, strategy.getVariables());
                        */

                        MOCellStockMarketIntegerRunner runner =
                                new MOCellStockMarketIntegerRunner(strategy.getName(), in, strategy.getVariables());

                        Optimization optimization = runner.run(strategy);
                        optimization.setPeriod(period);
                        period.getOptimizations().add(optimization);
                        updateStrategy(optimization, strategy.getName());

                        //periodManager.persist(period);

                        computeResults(test, period, strategy, nameIn, null, "in");

                        period = createFromSeries(out);

                        optimization.setPeriod(period);
                        period.getOptimizations().add(optimization);
                        //updateStrategy(optimization, strategy.getName());

                        //periodManager.persist(period);

                        computeResults(test, period, strategy, nameOut, nameIn, "out");

                        writeFile();

                    }

                /*
                strategies = strategyManager.getBinaryProblemTypeStrategies();

                for (Strategy strategy : strategies) {

                    TimeSeries in = new BaseTimeSeries("",data.getBarData().subList(i*offset,(i+4)*offset));
                    TimeSeries out = new BaseTimeSeries("",data.getBarData().subList((i+4)*offset + 1,(i+5)*offset));

                    Period period = periodManager.createFromSeries(in);

                    GenerationalGeneticAlgorithmStockMarketRunner runner =
                            new GenerationalGeneticAlgorithmStockMarketRunner(strategy.getName(), file, strategy.getVariables());
                    Optimization optimization = runner.run(strategy);
                    optimization.setPeriod(period);
                    period.getOptimizations().add(optimization);
                }
                */

                    //periodManager.persist(period);
                }

            }

            closeFile();
        }
    }

    public static void updateStrategy(Optimization optimization, String strategy) throws Exception {
        try {
            switch (strategy) {
                case "GlobalExtremaStrategy":
                    GlobalExtremaStrategy.mapFrom(optimization);
                    break;
                case "TunnelStrategy":
                    TunnelStrategy.mapFrom(optimization);
                    break;
                case "CCICorrectionStrategy":
                    CCICorrectionStrategy.mapFrom(optimization);
                    break;
                case "BagovinoStrategy":
                    BagovinoStrategy.mapFrom(optimization);
                    break;
                case "MovingAveragesStrategy":
                    MovingAveragesStrategy.mapFrom(optimization);
                    break;
                case "RSI2Strategy":
                    RSI2Strategy.mapFrom(optimization);
                    break;
                case "ParabolicSARStrategy":
                    ParabolicSARStrategy.mapFrom(optimization);
                    break;
                case "MovingMomentumStrategy":
                    MovingMomentumStrategy.mapFrom(optimization);
                    break;
                case "StochasticStrategy":
                    StochasticStrategy.mapFrom(optimization);
                    break;
                case "MACDStrategy":
                    MACDStrategy.mapFrom(optimization);
                    break;
                case "FXBootCampStrategy":
                    FXBootCampStrategy.mapFrom(optimization);
                    break;
                case "WinslowStrategy":
                    WinslowStrategy.mapFrom(optimization);
                    break;
            }
        }
        catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage());
        }

    }


    public static Period createFromSeries(TimeSeries timeSeries) throws Exception {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        java.sql.Date start = java.sql.Date.valueOf(timeSeries.getFirstBar().getBeginTime().toLocalDate());
        java.sql.Date end = java.sql.Date.valueOf(timeSeries.getLastBar().getBeginTime().toLocalDate());

        //TODO: Dejar esto parametrico
        TimeFrame timeFrame;

        switch ((int) Duration.between(timeSeries.getBar(0).getBeginTime(), timeSeries.getBar(1).getBeginTime()).getSeconds()) {
            case 2700:
                timeFrame = TimeFrame.HOUR;
                break;
            case 86400:
            case 345600:
            case 259200:
                timeFrame = TimeFrame.DAY;
                break;
            default:
                throw new Exception("TimeFrame no soportado!");
        }

        Period period = new Period(timeSeries.getName(), timestamp, start, end, timeFrame);

        for (Bar bar : timeSeries.getBarData()) {
            double open = bar.getOpenPrice().doubleValue();
            double high = bar.getMaxPrice().doubleValue();
            double low = bar.getMinPrice().doubleValue();
            double close = bar.getClosePrice().doubleValue();
            double volume = bar.getVolume().doubleValue();

            period.getBars().add(new PeriodBar(bar.getEndTime(), open, high, low, close, volume, period));
        }

        return period;
    }

    private static void computeResults(int iteration, Period selected, Strategy strategy, String currentPeriod, String previousPeriod, String stage) {

        try {

            TimeSeries timeSeries = new BaseTimeSeries(selected.getName());

            for (PeriodBar periodBar : selected.getBars()) {
                ZonedDateTime time = periodBar.getEndTime();
                double open = periodBar.getOpenPrice().doubleValue();
                double close = periodBar.getClosePrice().doubleValue();
                double max = periodBar.getMaxPrice().doubleValue();
                double min = periodBar.getMinPrice().doubleValue();
                double vol = periodBar.getVolume().doubleValue();

                timeSeries.addBar(new BaseBar(time, open, max, min, close, vol));
            }

            String START = DateTimeFormatter.ISO_LOCAL_DATE.format(timeSeries.getFirstBar().getEndTime());
            String END = DateTimeFormatter.ISO_LOCAL_DATE.format(timeSeries.getLastBar().getEndTime());

            TimeSeriesManager seriesManager = new TimeSeriesManager(timeSeries);

            List<org.ta4j.core.Strategy> strategies = mapFrom(selected);

            MultipleStrategy multipleStrategy = new MultipleStrategy(strategies);

            TradingRecord tradingRecord = seriesManager.run(multipleStrategy.buildStrategy(timeSeries));

            System.out.println("Number of trades for our strategy: " + tradingRecord.getTradeCount());
            int NUMBER_OF_TRADES = tradingRecord.getTradeCount();

            // Analysis

            // Getting the cash flow of the resulting trades
            CashFlow cashFlow = new CashFlow(timeSeries, tradingRecord);

            // Getting the profitable trades ratio
            AnalysisCriterion profitTradesRatio = new AverageProfitableTradesCriterion();
            System.out.println("Profitable trades ratio: " + profitTradesRatio.calculate(timeSeries, tradingRecord));

            double PROFIT_TRADES_RATIO = profitTradesRatio.calculate(timeSeries, tradingRecord);

            // Getting the reward-risk ratio
            AnalysisCriterion rewardRiskRatio = new RewardRiskRatioCriterion();
            System.out.println("Reward-risk ratio: " + rewardRiskRatio.calculate(timeSeries, tradingRecord));

            double REWARD_RISK_RATIO = rewardRiskRatio.calculate(timeSeries, tradingRecord);

            // Total profit of our strategy
            // vs total profit of a buy-and-hold strategy
            AnalysisCriterion vsBuyAndHold = new VersusBuyAndHoldCriterion(new TotalProfitCriterion());
            System.out.println("Our profit vs buy-and-hold profit: " + vsBuyAndHold.calculate(timeSeries, tradingRecord));

            double VS_BUY_AND_HOLD_RATIO = vsBuyAndHold.calculate(timeSeries, tradingRecord);

            for (int i = 0; i < tradingRecord.getTrades().size(); ++i) {
                System.out.println("Trade[" + i + "]: " + tradingRecord.getTrades().get(i).toString());
            }

            for (int i = 0; i < cashFlow.getSize(); ++i) {
                System.out.println("CashFlow[" + i + "]: " + cashFlow.getValue(i));
                //getCashFlowDetailView().append("CashFlow["+ i +"]: " + cashFlow.getValue(i));
                //getCashFlowDetailView().append(newline);
            }

            String pattern = "###.#####";
            DecimalFormat decimalFormat = new DecimalFormat(pattern);

            double CASHFLOW = cashFlow.getValue(cashFlow.getSize()-1).doubleValue();

            selected.getTimestamp().toString();

            //Chart
            /*
            JFreeChart jfreechart = BuyAndSellSignalsToChart.buildCandleStickChart(timeSeries, multipleStrategy.buildStrategy(timeSeries));
            ChartPanel panel = new ChartPanel(jfreechart);
            panel.setFillZoomRectangle(true);
            panel.setMouseWheelEnabled(true);
            panel.setPreferredSize(new Dimension(800, 200));

            BuyAndSellSignalsToChart.buildCandleStickChart(timeSeries, multipleStrategy.buildStrategy(timeSeries));
            */

            if(stage.equals("in")) {
                efficiency.put(currentPeriod, (CASHFLOW - 1)*100);
                cont++;
                writeWFORecord(new WFORecord(id, iteration,  strategy.getName(), currentPeriod, stage, NUMBER_OF_TRADES,
                        PROFIT_TRADES_RATIO, REWARD_RISK_RATIO, VS_BUY_AND_HOLD_RATIO, CASHFLOW, -1,
                        mapStrategiesFrom(selected).toString()));
            }
            else {
                double efficiencyRatio = ((CASHFLOW -1)*100) /efficiency.get(previousPeriod);
                cont++;
                writeWFORecord(new WFORecord(id, iteration, strategy.getName(), currentPeriod, stage, NUMBER_OF_TRADES,
                        PROFIT_TRADES_RATIO, REWARD_RISK_RATIO,VS_BUY_AND_HOLD_RATIO, CASHFLOW, efficiencyRatio,
                        mapStrategiesFrom(selected).toString()));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    public static List<org.ta4j.core.Strategy> mapFrom(Period period) throws Exception {

        List<org.ta4j.core.Strategy> strategies = new ArrayList<>();

        TimeSeries series = new BaseTimeSeries(period.getName());

        for (PeriodBar periodBar : period.getBars()) {
            series.addBar(periodBar);
        }

        for (Optimization optimization : period.getOptimizationsOfType(ProblemType.INTEGER)) {
            switch (optimization.getStrategy().getName()) {
                case GLOBAL_EXTREMA:
                    GlobalExtremaStrategy.mapFrom(optimization);
                    strategies.add(GlobalExtremaStrategy.buildStrategy(series));
                    break;
                case TUNNEL:
                    TunnelStrategy.mapFrom(optimization);
                    strategies.add(TunnelStrategy.buildStrategy(series));
                    break;
                case CCI_CORRECTION:
                    CCICorrectionStrategy.mapFrom(optimization);
                    strategies.add(CCICorrectionStrategy.buildStrategy(series));
                    break;
                case BAGOVINO:
                    BagovinoStrategy.mapFrom(optimization);
                    strategies.add(BagovinoStrategy.buildStrategy(series));
                    break;
                case MOVING_AVERAGES:
                    MovingAveragesStrategy.mapFrom(optimization);
                    strategies.add(MovingAveragesStrategy.buildStrategy(series));
                    break;
                case RSI_2:
                    RSI2Strategy.mapFrom(optimization);
                    strategies.add(RSI2Strategy.buildStrategy(series));
                    break;
                case PARABOLIC_SAR:
                    ParabolicSARStrategy.mapFrom(optimization);
                    strategies.add(ParabolicSARStrategy.buildStrategy(series));
                    break;
                case MOVING_MOMENTUM:
                    MovingMomentumStrategy.mapFrom(optimization);
                    strategies.add(MovingMomentumStrategy.buildStrategy(series));
                    break;
                case STOCHASTIC:
                    StochasticStrategy.mapFrom(optimization);
                    strategies.add(StochasticStrategy.buildStrategy(series));
                    break;
                case MACD:
                    MACDStrategy.mapFrom(optimization);
                    strategies.add(MACDStrategy.buildStrategy(series));
                    break;
                case FX_BOOTCAMP:
                    FXBootCampStrategy.mapFrom(optimization);
                    strategies.add(FXBootCampStrategy.buildStrategy(series));
                    break;
                case WINSLOW:
                    WinslowStrategy.mapFrom(optimization);
                    strategies.add(WinslowStrategy.buildStrategy(series));
                    break;
            }
        }

        for (Optimization optimization : period.getOptimizationsOfType(ProblemType.BINARY)) {

            strategies.clear();

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

    private static void initOutput() throws IOException {

        workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("WFA");

        Row header = sheet.createRow(cont);

        CellStyle headerStyle = workbook.createCellStyle();
        //headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setBold(true);
        headerStyle.setFont(font);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Test");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Strategy");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Period");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Stage");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue("Number of trades");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(5);
        headerCell.setCellValue("Profitable trades ratio");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(6);
        headerCell.setCellValue("Reward-risk ratio");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(7);
        headerCell.setCellValue("Cashflow");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(8);
        headerCell.setCellValue("Efficiency Ratio");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(9);
        headerCell.setCellValue("Parameters");
        headerCell.setCellStyle(headerStyle);
    }

    private static void writeWFORecord(WFORecord wfoRecord) {

        Sheet sheet = workbook.getSheetAt(0);

        Row wfoRow = sheet.createRow(cont);

        wfoRow.createCell(0).setCellValue(test);
        wfoRow.createCell(1).setCellValue(wfoRecord.getStrategy());
        wfoRow.createCell(2).setCellValue(wfoRecord.getPeriod());
        wfoRow.createCell(3).setCellValue(wfoRecord.getStage());
        wfoRow.createCell(4).setCellValue(wfoRecord.getNumberOfTrades());
        wfoRow.createCell(5).setCellValue(wfoRecord.getProfitableTradesRatio());
        wfoRow.createCell(6).setCellValue(wfoRecord.getRewardRiskRatio());
        wfoRow.createCell(7).setCellValue(wfoRecord.getCashflow());

        if(wfoRecord.getStage().equals("out")) {
            wfoRow.createCell(8).setCellValue(wfoRecord.getEfficiencyIndex());
        }
        if(wfoRecord.getStage().equals("in")) {
            wfoRow.createCell(9).setCellValue(wfoRecord.getParameters());
        }
    }

    private static void writeFile() throws IOException {
        //outputStream.flush();
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();

        String fileLocation = path.substring(0, path.length() - 1) + "WFA.xlsx";

        outputStream = new FileOutputStream(fileLocation, true);
        workbook.write(outputStream);
    }

    private static void closeFile() throws IOException {
        workbook.close();
    }

    public static Map<String, List<Pair<String, Integer>>> mapStrategiesFrom(Period period) {

        Map<String, List<Pair<String, Integer>>> parameters = new HashMap<>();

        for (Optimization optimization : period.getOptimizationsOfType(ProblemType.INTEGER)) {
            switch (optimization.getStrategy().getName()) {
                case GLOBAL_EXTREMA:
                    parameters.put(GLOBAL_EXTREMA, GlobalExtremaStrategy.getParameters());
                    break;
                case TUNNEL:
                    parameters.put(TUNNEL, TunnelStrategy.getParameters());
                    break;
                case CCI_CORRECTION:
                    parameters.put(CCI_CORRECTION, CCICorrectionStrategy.getParameters());
                    break;
                case BAGOVINO:
                    parameters.put(BAGOVINO, BagovinoStrategy.getParameters());
                    break;
                case MOVING_AVERAGES:
                    parameters.put(MOVING_AVERAGES, MovingAveragesStrategy.getParameters());
                    break;
                case RSI_2:
                    parameters.put(RSI_2, RSI2Strategy.getParameters());
                    break;
                case PARABOLIC_SAR:
                    parameters.put(PARABOLIC_SAR, ParabolicSARStrategy.getParameters());
                    break;
                case MOVING_MOMENTUM:
                    parameters.put(MOVING_MOMENTUM, MovingMomentumStrategy.getParameters());
                    break;
                case STOCHASTIC:
                    parameters.put(STOCHASTIC, StochasticStrategy.getParameters());
                    break;
                case MACD:
                    parameters.put(MACD, MACDStrategy.getParameters());
                    break;
                case FX_BOOTCAMP:
                    parameters.put(FX_BOOTCAMP, FXBootCampStrategy.getParameters());
                    break;
                case WINSLOW:
                    parameters.put(WINSLOW, WinslowStrategy.getParameters());
                    break;
            }
        }

        return parameters;
    }

}
