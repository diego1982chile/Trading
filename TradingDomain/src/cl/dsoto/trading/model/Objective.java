package cl.dsoto.trading.model;

import java.io.Serializable;

import static cl.dsoto.trading.model.DAO.NON_PERSISTED_ID;

/**
 * Created by des01c7 on 22-03-19.
 */
public class Objective implements Serializable {
    /** El identificador único de la entidad, inicialmente fijado en <code>NON_PERSISTED_ID</code>. */
    private long id = NON_PERSISTED_ID;

    Optimization optimization;
    double objective;

    public Objective(Optimization optimization, double objective) {
        this.optimization = optimization;
        this.objective = objective;
    }

    public Objective(long id, Optimization optimization, double objective) {
        this.id = id;
        this.optimization = optimization;
        this.objective = objective;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Optimization getOptimization() {
        return optimization;
    }

    public void setOptimization(Optimization optimization) {
        this.optimization = optimization;
    }

    public double getObjective() {
        return objective;
    }

    public void setObjective(double objective) {
        this.objective = objective;
    }
}
