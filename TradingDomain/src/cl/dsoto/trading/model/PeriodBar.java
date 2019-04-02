package cl.dsoto.trading.model;

import org.ta4j.core.BaseBar;
import org.ta4j.core.TimeSeries;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static cl.dsoto.trading.model.DAO.NON_PERSISTED_ID;

/**
 * Created by des01c7 on 29-03-19.
 */
public class PeriodBar extends BaseBar {

    /** El identificador único de la entidad, inicialmente fijado en <code>NON_PERSISTED_ID</code>. */
    private long id = NON_PERSISTED_ID;

    Period period;

    public PeriodBar(long id, ZonedDateTime endTime, double openPrice, double highPrice, double lowPrice, double closePrice, double volume, Period period) {
        super(endTime, openPrice, highPrice, lowPrice, closePrice, volume);
        this.id = id;
        this.period = period;
    }

    public PeriodBar(ZonedDateTime endTime, double openPrice, double highPrice, double lowPrice, double closePrice, double volume, Period period) {
        super(endTime, openPrice, highPrice, lowPrice, closePrice, volume);
        this.period = period;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }
}
