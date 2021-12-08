package cl.dsoto.trading.components;

import cl.dsoto.trading.daos.PeriodDAO;
import cl.dsoto.trading.daos.TestDAO;
import cl.dsoto.trading.model.*;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.Strategy;
import org.ta4j.core.TimeSeries;
import org.uma.jmetal.runner.singleobjective.GenerationalGeneticAlgorithmStockMarketIntegerRunner;
import org.uma.jmetal.runner.singleobjective.GenerationalGeneticAlgorithmStockMarketRunner;
import ta4jexamples.loaders.CsvTicksLoader;
import ta4jexamples.strategies.*;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by des01c7 on 25-03-19.
 */
@Stateless
public class TestManagerImpl implements TestManager {

    @EJB
    private StrategyManager strategyManager;

    @EJB
    private TestDAO testDAO;

    @Override
    public Test getTestById(long id) throws Exception {
        return testDAO.getTestById(id);
    }

    @Override
    public Test persist(Test test) throws Exception {
        return testDAO.persist(test);
    }

    @Override
    public void delete(Test test) throws Exception {
        testDAO.delete(test);
    }

    @Override
    public List<Test> getLast(int periods) throws Exception {
        return testDAO.getLast(periods);
    }

    @Override
    public List<Test> getLast(TimeFrame timeFrame, int periods) throws Exception {
        List<Test> testList = new ArrayList<>();

        for (Test test : testDAO.getLast(periods)) {
            if(test.getTimeFrame().equals(timeFrame)) {
                testList.add(test);
            }
        }

        return testList;
    }

}
