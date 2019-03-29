package cl.dsoto.trading.components;

import cl.dsoto.trading.cdi.ServiceLocator;
import cl.dsoto.trading.daos.OptimizationDAO;
import cl.dsoto.trading.daos.StrategyDAO;
import cl.dsoto.trading.model.Optimization;
import cl.dsoto.trading.model.Period;
import cl.dsoto.trading.model.Strategy;

import java.util.List;

/**
 * Created by des01c7 on 25-03-19.
 */
public class OptimizationManagerImpl implements OptimizationManager {

    //@EJB
    private OptimizationDAO optimizationDAO = (OptimizationDAO) ServiceLocator.getInstance().getService(OptimizationDAO.class);

    @Override
    public Optimization persist(Optimization optimization) throws Exception {
        optimization = optimizationDAO.persist(optimization);
        return optimization;
    }

    @Override
    public List<Optimization> getOptimizationsByPeriod(Period period) throws Exception {
        List<Optimization> optimizationList = optimizationDAO.getOptimizationsByPeriod(period);
        return optimizationList;
    }
}
