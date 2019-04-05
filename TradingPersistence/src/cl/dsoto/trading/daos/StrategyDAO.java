package cl.dsoto.trading.daos;

import cl.dsoto.trading.model.Strategy;
import cl.dsoto.trading.model.Solution;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by des01c7 on 25-03-19.
 */
@Local
public interface StrategyDAO {

    List<Strategy> getStrategies() throws Exception;

    Strategy getStrategyById(long id) throws Exception;
}
