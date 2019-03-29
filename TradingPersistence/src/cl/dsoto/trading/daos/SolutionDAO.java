package cl.dsoto.trading.daos;

import cl.dsoto.trading.model.Optimization;
import cl.dsoto.trading.model.Solution;

import java.util.List;

/**
 * Created by des01c7 on 25-03-19.
 */
public interface SolutionDAO {

    Solution persist(Solution solution) throws Exception;

    List<Solution> getSolutionsByOptimization(Optimization optimization) throws Exception;
}
