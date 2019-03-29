package cl.dsoto.trading.daos;

import cl.dsoto.trading.model.Objective;
import cl.dsoto.trading.model.Optimization;
import cl.dsoto.trading.model.Solution;

import java.util.List;

/**
 * Created by des01c7 on 25-03-19.
 */
public interface ObjectiveDAO {

    Objective persist(Objective objective) throws Exception;

    List<Objective> getObjectivesByOptimization(Optimization optimization) throws Exception;
}
