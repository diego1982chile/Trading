package cl.dsoto.trading.model;

import java.io.Serializable;
import java.util.List;

import static cl.dsoto.trading.model.DAO.NON_PERSISTED_ID;


/**
 * Created by des01c7 on 22-03-19.
 */
public class Solution<T extends Comparable> implements Serializable {
    /**
     * El identificador único de la entidad, inicialmente fijado en <code>NON_PERSISTED_ID</code>.
     */
    private long id = NON_PERSISTED_ID;

    Optimization optimization;

    List<T> values;

    public Solution(long id, Optimization optimization, List<T> values) {
        this.id = id;
        this.optimization = optimization;
        this.values = values;
    }

    public Solution(long id, Optimization optimization) {
        this.id = id;
        this.optimization = optimization;
    }

    public Solution(Optimization optimization, List<T> values) {

        this.optimization = optimization;
        this.values = values;
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

    public List<T> getValues() {
        return values;
    }

    public void setSolution(List<T> values) {
        this.values = values;
    }
}
