package cl.dsoto.trading.strategies;

import cl.dsoto.trading.model.*;
import javafx.util.Pair;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;
import ta4jexamples.strategies.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by des01c7 on 17-04-19.
 */
public class StrategyHelper {

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

    public static List<org.ta4j.core.Strategy> mapFrom(Period period, TimeSeries series) throws Exception {

        List<org.ta4j.core.Strategy> strategies = new ArrayList<>();

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

    public static Map<String, List<Pair<String, Integer>>> mapStrategiesFrom(Period period) {

        Map<String, List<Pair<String, Integer>>> parameters = new HashMap<>();

        for (Optimization optimization : period.getOptimizationsOfType(ProblemType.BINARY)) {
            for (Solution solution : optimization.getSolutions()) {

                for (int i = 0; i < solution.getValues().size(); i++) {
                    boolean value = (Boolean) solution.getValues().get(i);

                    if (value) {

                        switch (i) {
                            case 0:
                                parameters.put("CCICorrectionStrategy", CCICorrectionStrategy.getParameters());
                                break;
                            case 1:
                                parameters.put("GlobalExtremaStrategy", GlobalExtremaStrategy.getParameters());
                                break;
                            case 2:
                                parameters.put("MovingMomentumStrategy", MovingMomentumStrategy.getParameters());
                                break;
                            case 3:
                                parameters.put("RSI2Strategy", RSI2Strategy.getParameters());
                                break;
                            case 4:
                                parameters.put("MACDStrategy", MACDStrategy.getParameters());
                                break;
                            case 5:
                                parameters.put("StochasticStrategy", StochasticStrategy.getParameters());
                                break;
                            case 6:
                                parameters.put("ParabolicSARStrategy", ParabolicSARStrategy.getParameters());
                                break;
                            case 7:
                                parameters.put("MovingAveragesStrategy", MovingAveragesStrategy.getParameters());
                                break;
                            case 8:
                                parameters.put("BagovinoStrategy", BagovinoStrategy.getParameters());
                                break;
                            case 9:
                                parameters.put("FXBootCampStrategy", FXBootCampStrategy.getParameters());
                                break;
                            case 10:
                                parameters.put("TunnelStrategy", TunnelStrategy.getParameters());
                                break;
                            case 11:
                                parameters.put("WinslowStrategy", WinslowStrategy.getParameters());
                                break;
                        }
                    }
                }
            }
        }

        return parameters;
    }

}
