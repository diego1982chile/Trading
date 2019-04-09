package cl.dsoto.trading.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import static cl.dsoto.trading.model.DAO.NON_PERSISTED_ID;

/**
 * Created by des01c7 on 22-03-19.
 */
public class Optimization implements Serializable {
    /** El identificador único de la entidad, inicialmente fijado en <code>NON_PERSISTED_ID</code>. */
    private long id = NON_PERSISTED_ID;

    private Period period;
    private Strategy strategy;
    private Timestamp timestamp;

    private List<Objective> objectives;
    private List<Solution> solutions;

    public Optimization(Period period, Strategy strategy, Timestamp timestamp, List<Objective> objectives, List<Solution> solutions) {
        this.period = period;
        this.strategy = strategy;
        this.timestamp = timestamp;
        this.objectives = objectives;
        this.solutions = solutions;
    }

    public Optimization(long id, Period period, Strategy strategy, Timestamp timestamp, List<Objective> objectives, List<Solution> solutions) {
        this.id = id;
        this.period = period;
        this.strategy = strategy;
        this.timestamp = timestamp;
        this.objectives = objectives;
        this.solutions = solutions;
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

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public List<Objective> getObjectives() {
        return objectives;
    }

    public void setObjectives(List<Objective> objectives) {
        this.objectives = objectives;
    }

    public List<Solution> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<Solution> solutions) {
        this.solutions = solutions;
    }
}
