package cl.dsoto.trading.model;

import org.ta4j.core.*;
import ta4jexamples.strategies.*;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static cl.dsoto.trading.model.DAO.NON_PERSISTED_ID;

/**
 * Created by des01c7 on 29-03-19.
 */
public class Period implements Serializable {

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

    /** El identificador único de la entidad, inicialmente fijado en <code>NON_PERSISTED_ID</code>. */
    private long id = NON_PERSISTED_ID;

    String name;
    Timestamp timestamp;
    Date start;
    Date end;

    TimeFrame timeFrame;

    List<Optimization> optimizations = new ArrayList<>();

    List<PeriodBar> bars = new ArrayList<>();

    public List<PeriodBar> getBars() {
        return bars;
    }

    public void setBars(List<PeriodBar> bars) {
        this.bars = bars;
    }

    public Period(String name, Timestamp timestamp, Date start, Date end, TimeFrame timeFrame) {
        this.name = name;
        this.timestamp = timestamp;
        this.start = start;
        this.end = end;
        this.timeFrame = timeFrame;
    }

    public Period(long id, String name, Timestamp timestamp, Date start, Date end, TimeFrame timeFrame) {
        this.id = id;
        this.name = name;
        this.timestamp = timestamp;
        this.start = start;
        this.end = end;
        this.timeFrame = timeFrame;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public TimeFrame getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(TimeFrame timeFrame) {
        this.timeFrame = timeFrame;
    }

    public List<Optimization> getOptimizations() {
        return optimizations;
    }

    public void setOptimizations(List<Optimization> optimizations) {
        this.optimizations = optimizations;
    }

    public List<Optimization> getOptimizationsOfType(ProblemType problemType) {
        List<Optimization> optimizations = new ArrayList<>();

        for (Optimization optimization : this.optimizations) {
            if(optimization.getStrategy().getType().equals(problemType)) {
                optimizations.add(optimization);
            }
        }

        return optimizations;
    }

    public static List<org.ta4j.core.Strategy> extractStrategy(Period period) throws Exception {

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

    @Override
    public String toString() {
        return name;
    }
}
