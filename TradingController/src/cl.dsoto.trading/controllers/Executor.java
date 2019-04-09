package cl.dsoto.trading.controllers;

import cl.dsoto.trading.clients.ServiceLocator;
import cl.dsoto.trading.components.OptimizationManager;
import cl.dsoto.trading.components.PeriodManager;
import cl.dsoto.trading.components.StrategyManager;
import cl.dsoto.trading.model.*;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.TimeSeries;
import org.uma.jmetal.runner.singleobjective.GenerationalGeneticAlgorithmStockMarketIntegerRunner;
import org.uma.jmetal.runner.singleobjective.GenerationalGeneticAlgorithmStockMarketRunner;
import ta4jexamples.loaders.CsvTicksLoader;
import ta4jexamples.strategies.*;

import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by des01c7 on 28-03-19.
 */
public class Executor {

    static PeriodManager periodManager = (PeriodManager) ServiceLocator.getInstance().getService(PeriodManager.class);
    static StrategyManager strategyManager = (StrategyManager) ServiceLocator.getInstance().getService(StrategyManager.class);

    static List<String> files = Arrays.asList("2010_D.csv","2011_D.csv","2012_D.csv","2013_D.csv","2014_D.csv","2015_D.csv","2016_D.csv","2017_D.csv","2018_D.csv");

    public static void main(String[] args) throws Exception {

        for (String file : files) {

            Period period = createFromFile(file);

            List<Strategy> strategies = strategyManager.getIntegerProblemTypeStrategies();

            for (Strategy strategy : strategies) {
                GenerationalGeneticAlgorithmStockMarketIntegerRunner runner =
                    new GenerationalGeneticAlgorithmStockMarketIntegerRunner(strategy.getName(), file, strategy.getVariables());
                Optimization optimization = runner.run();
                optimization.setPeriod(period);
                period.getOptimizations().add(optimization);
                updateStrategy(optimization, strategy.getName());
            }

            strategies = strategyManager.getBinaryProblemTypeStrategies();

            for (Strategy strategy : strategies) {
                GenerationalGeneticAlgorithmStockMarketRunner runner =
                        new GenerationalGeneticAlgorithmStockMarketRunner(strategy.getName(), file, strategy.getVariables());
                Optimization optimization = runner.run();
                optimization.setPeriod(period);
                period.getOptimizations().add(optimization);
            }

            periodManager.persist(period);
        }
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

    public static Period createFromFile(String file) throws Exception {
        TimeSeries timeSeries = CsvTicksLoader.load(file);
        String name = file;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        java.sql.Date start = java.sql.Date.valueOf(timeSeries.getFirstBar().getBeginTime().toLocalDate());
        java.sql.Date end = java.sql.Date.valueOf(timeSeries.getLastBar().getBeginTime().toLocalDate());

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

}
