package cl.dsoto.trading.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import static cl.dsoto.trading.model.DAO.NON_PERSISTED_ID;

/**
 * Created by des01c7 on 29-03-19.
 */
public class Test implements Serializable {

    /** El identificador único de la entidad, inicialmente fijado en <code>NON_PERSISTED_ID</code>. */
    private long id = NON_PERSISTED_ID;

    String name;
    Timestamp timestamp;
    Date start;
    Date end;

    /* Periodo de optimización asociado*/
    Period period;

    int trades;
    double rewardRiskRatio;
    double vsBuyAndHold;
    double cashFlow;

    public Test(String name, Timestamp timestamp, Date start, Date end, Period period, int trades, double rewardRiskRatio, double vsBuyAndHold, double cashFlow) {
        this.name = name;
        this.timestamp = timestamp;
        this.start = start;
        this.end = end;
        this.period = period;
        this.trades = trades;
        this.rewardRiskRatio = rewardRiskRatio;
        this.vsBuyAndHold = vsBuyAndHold;
        this.cashFlow = cashFlow;
    }

    public Test(long id, String name, Timestamp timestamp, Date start, Date end, Period period, int trades, double rewardRiskRatio, double vsBuyAndHold, double cashFlow) {
        this.id = id;
        this.name = name;
        this.timestamp = timestamp;
        this.start = start;
        this.end = end;
        this.period = period;
        this.trades = trades;
        this.rewardRiskRatio = rewardRiskRatio;
        this.vsBuyAndHold = vsBuyAndHold;
        this.cashFlow = cashFlow;
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

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public int getTrades() {
        return trades;
    }

    public void setTrades(int trades) {
        this.trades = trades;
    }

    public double getRewardRiskRatio() {
        return rewardRiskRatio;
    }

    public void setRewardRiskRatio(double rewardRiskRatio) {
        this.rewardRiskRatio = rewardRiskRatio;
    }

    public double getVsBuyAndHold() {
        return vsBuyAndHold;
    }

    public void setVsBuyAndHold(double vsBuyAndHold) {
        this.vsBuyAndHold = vsBuyAndHold;
    }

    public double getCashFlow() {
        return cashFlow;
    }

    public void setCashFlow(double cashFlow) {
        this.cashFlow = cashFlow;
    }
}
