package cl.dsoto.trading.model;

import org.ta4j.core.Bar;
import org.ta4j.core.TimeSeries;

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

    @Override
    public String toString() {
        return name;
    }
}
