package cl.dsoto.trading.daos;

import cl.dsoto.trading.model.Optimization;
import cl.dsoto.trading.model.Period;
import cl.dsoto.trading.model.Solution;
import cl.dsoto.trading.model.Strategy;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by des01c7 on 25-03-19.
 */
@Local
public interface OptimizationDAO {

    Optimization persist(Optimization optimization) throws Exception;

    List<Optimization> getOptimizationsByPeriod(Period period) throws Exception;

}
