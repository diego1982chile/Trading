package cl.dsoto.trading.components;

import cl.dsoto.trading.daos.OptimizationDAO;
import cl.dsoto.trading.daos.PeriodDAO;
import cl.dsoto.trading.daos.StrategyDAO;
import cl.dsoto.trading.model.*;
import org.ta4j.core.*;
import org.ta4j.core.Strategy;
import org.uma.jmetal.runner.singleobjective.GenerationalGeneticAlgorithmStockMarketIntegerRunner;
import org.uma.jmetal.runner.singleobjective.GenerationalGeneticAlgorithmStockMarketRunner;
import ta4jexamples.loaders.CsvTicksLoader;
import ta4jexamples.strategies.*;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by des01c7 on 25-03-19.
 */
@Stateless
public class PeriodManagerImpl implements PeriodManager {

    @EJB
    private StrategyManager strategyManager;

    @EJB
    private PeriodDAO periodDAO;

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

    @Override
    public Period getPeriodById(long id) throws Exception {
        return periodDAO.getPeriodById(id);
    }

    @Override
    public Period persist(Period period) throws Exception {
        return periodDAO.persist(period);
    }

    @Override
    public void delete(Period period) throws Exception {
        periodDAO.delete(period);
    }

    @Override
    public List<Period> getLast(int periods) throws Exception {
        return periodDAO.getLast(periods);
    }

    @Override
    public List<Period> getLast(TimeFrame timeFrame, int periods) throws Exception {
        List<Period> periodList = new ArrayList<>();

        for (Period period : periodDAO.getLast(periods)) {
            if(period.getTimeFrame().equals(timeFrame)) {
                periodList.add(period);
            }
        }

        return periodList;
    }

    @Override
    public Period createFromFile(String file) throws Exception {
        TimeSeries timeSeries = CsvTicksLoader.load(file);
        String name = file;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Date start = Date.valueOf(timeSeries.getFirstBar().getBeginTime().toLocalDate());
        Date end = Date.valueOf(timeSeries.getLastBar().getBeginTime().toLocalDate());

        //TODO: Dejar esto parametrico
        TimeFrame timeFrame = TimeFrame.DAY;

        Period period = new Period(name, timestamp, start, end, timeFrame);

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

    public Period createFromSeries(TimeSeries series) throws Exception {

        String name = series.getName();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Date start = Date.valueOf(series.getFirstBar().getBeginTime().toLocalDate());
        Date end = Date.valueOf(series.getLastBar().getBeginTime().toLocalDate());

        //TODO: Dejar esto parametrico
        TimeFrame timeFrame = TimeFrame.DAY;

        Period period = new Period(name, timestamp, start, end, timeFrame);

        for (Bar bar : series.getBarData()) {
            double open = bar.getOpenPrice().doubleValue();
            double high = bar.getMaxPrice().doubleValue();
            double low = bar.getMinPrice().doubleValue();
            double close = bar.getClosePrice().doubleValue();
            double volume = bar.getVolume().doubleValue();

            period.getBars().add(new PeriodBar(bar.getEndTime(), open, high, low, close, volume, period));
        }

        return period;
    }

    @Override
    public List<Strategy> mapFrom(Period period) throws Exception {

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

    @Asynchronous
    public void generateOptimizations(TimeSeries timeSeries) throws Exception {

        Period period = createFromSeries(timeSeries);

        List<cl.dsoto.trading.model.Strategy> strategies = strategyManager.getIntegerProblemTypeStrategies();

        for (cl.dsoto.trading.model.Strategy strategy : strategies) {
            GenerationalGeneticAlgorithmStockMarketIntegerRunner runner =
                    new GenerationalGeneticAlgorithmStockMarketIntegerRunner(strategy.getName(), timeSeries, strategy.getVariables());
            Optimization optimization = runner.run(strategy);
            optimization.setPeriod(period);
            period.getOptimizations().add(optimization);
            updateStrategy(optimization, strategy.getName());
        }

        strategies = strategyManager.getBinaryProblemTypeStrategies();

        for (cl.dsoto.trading.model.Strategy strategy : strategies) {
            GenerationalGeneticAlgorithmStockMarketRunner runner =
                    new GenerationalGeneticAlgorithmStockMarketRunner(strategy.getName(), timeSeries, strategy.getVariables());
            Optimization optimization = runner.run(strategy);
            optimization.setPeriod(period);
            period.getOptimizations().add(optimization);
        }

        persist(period);

    }

    public static void updateStrategy(Optimization optimization, String strategy) throws Exception {
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

}
