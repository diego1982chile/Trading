package cl.dsoto.trading.model;

import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;
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
public class ForwardTest implements Serializable {

    /** El identificador único de la entidad, inicialmente fijado en <code>NON_PERSISTED_ID</code>. */
    private long id = NON_PERSISTED_ID;

    String name;
    Timestamp timestamp;
    Date start;
    Date end;

    TimeFrame timeFrame;

    Period period;

    List<ForwardTestBar> bars = new ArrayList<>();

    public List<ForwardTestBar> getBars() {
        return bars;
    }

    public void setBars(List<ForwardTestBar> bars) {
        this.bars = bars;
    }

    public ForwardTest(String name, Timestamp timestamp, Date start, Date end, TimeFrame timeFrame) {
        this.name = name;
        this.timestamp = timestamp;
        this.start = start;
        this.end = end;
        this.timeFrame = timeFrame;
    }

    public ForwardTest(long id, String name, Timestamp timestamp, Date start, Date end, TimeFrame timeFrame) {
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

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return name;
    }
}
