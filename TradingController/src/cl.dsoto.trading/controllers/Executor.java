package cl.dsoto.trading.controllers;

import cl.dsoto.trading.cdi.ServiceLocator;
import cl.dsoto.trading.components.OptimizationManager;
import cl.dsoto.trading.components.PeriodManager;
import cl.dsoto.trading.components.StrategyManager;
import cl.dsoto.trading.model.Optimization;
import cl.dsoto.trading.model.Period;
import cl.dsoto.trading.model.Strategy;
import org.ta4j.core.BaseStrategy;
import org.uma.jmetal.runner.singleobjective.GenerationalGeneticAlgorithmStockMarketIntegerRunner;
import org.uma.jmetal.runner.singleobjective.GenerationalGeneticAlgorithmStockMarketRunner;
import ta4jexamples.strategies.*;

import java.util.*;

/**
 * Created by des01c7 on 28-03-19.
 */
public class Executor {

    static PeriodManager periodManager = (PeriodManager) ServiceLocator.getInstance().getService(PeriodManager.class);
    static StrategyManager strategyManager = (StrategyManager) ServiceLocator.getInstance().getService(StrategyManager.class);

    static List<String> files = Arrays.asList("2010_D.csv","2011_D.csv","2013_D.csv","2014_D.csv","2015_D.csv","2016_D.csv","2017_D.csv","2018_D.csv");

    public static void main(String[] args) throws Exception {

        for (String file : files) {

            Period period = periodManager.createFromFile(file);

            List<Strategy> strategies = strategyManager.getIntegerProblemTypeStrategies();

            for (Strategy strategy : strategies) {
                GenerationalGeneticAlgorithmStockMarketIntegerRunner runner =
                    new GenerationalGeneticAlgorithmStockMarketIntegerRunner(strategy.getName(), file, strategy.getVariables());
                Optimization optimization = runner.run();
                period.getOptimizations().add(optimization);
                updateStrategy(optimization, strategy.getName());
            }

            strategies = strategyManager.getBinaryProblemTypeStrategies();

            for (Strategy strategy : strategies) {
                GenerationalGeneticAlgorithmStockMarketRunner runner =
                        new GenerationalGeneticAlgorithmStockMarketRunner(strategy.getName(), file, strategy.getVariables());
                Optimization optimization = runner.run();
                period.getOptimizations().add(optimization);
            }

            periodManager.persist(period);
        }
    }

    public static void updateStrategy(Optimization optimization, String strategy) throws Exception {
        switch (strategy) {
            case "GlobalExtremaStrategy":
                GlobalExtremaStrategy.mapFrom(optimization);
            case "TunnelStrategy":
                TunnelStrategy.mapFrom(optimization);
            case "CCICorrectionStrategy":
                CCICorrectionStrategy.mapFrom(optimization);
            case "BagovinoStrategy":
                BagovinoStrategy.mapFrom(optimization);
            case "MovingAveragesStrategy":
                MovingAveragesStrategy.mapFrom(optimization);
            case "RSI2Strategy":
                RSI2Strategy.mapFrom(optimization);
            case "ParabolicSARStrategy":
                ParabolicSARStrategy.mapFrom(optimization);
            case "MovingMomentumStrategy":
                MovingMomentumStrategy.mapFrom(optimization);
            case "StochasticStrategy":
                StochasticStrategy.mapFrom(optimization);
            case "MACDStrategy":
                MACDStrategy.mapFrom(optimization);
            case "FXBootCampStrategy":
                FXBootCampStrategy.mapFrom(optimization);
            case "WinslowStrategy":
                WinslowStrategy.mapFrom(optimization);
        }

    }

}
