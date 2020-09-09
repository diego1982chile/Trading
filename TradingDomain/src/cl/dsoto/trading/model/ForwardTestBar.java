package cl.dsoto.trading.model;

import org.ta4j.core.BaseBar;

import java.io.Serializable;
import java.time.ZonedDateTime;

import static cl.dsoto.trading.model.DAO.NON_PERSISTED_ID;

/**
 * Created by des01c7 on 29-03-19.
 */
public class ForwardTestBar extends BaseBar implements Serializable {

    /** El identificador único de la entidad, inicialmente fijado en <code>NON_PERSISTED_ID</code>. */
    private long id = NON_PERSISTED_ID;

    ForwardTest forwardTest;

    public ForwardTestBar(long id, ZonedDateTime endTime, double openPrice, double highPrice, double lowPrice, double closePrice, double volume, ForwardTest forwardTest) {
        super(endTime, openPrice, highPrice, lowPrice, closePrice, volume);
        this.id = id;
        this.forwardTest = forwardTest;
    }

    public ForwardTestBar(ZonedDateTime endTime, double openPrice, double highPrice, double lowPrice, double closePrice, double volume, ForwardTest forwardTest) {
        super(endTime, openPrice, highPrice, lowPrice, closePrice, volume);
        this.forwardTest = forwardTest;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ForwardTest getForwardTest() {
        return forwardTest;
    }

    public void setForwardTest(ForwardTest forwardTest) {
        this.forwardTest = forwardTest;
    }
}
