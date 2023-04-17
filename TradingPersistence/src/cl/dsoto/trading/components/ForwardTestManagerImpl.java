package cl.dsoto.trading.components;

import cl.dsoto.trading.daos.ForwardTestDAO;
import cl.dsoto.trading.daos.PeriodDAO;
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
public class ForwardTestManagerImpl implements ForwardTestManager {

    @EJB
    private ForwardTestDAO forwardTestDAO;

    @Override
    public ForwardTest persist(ForwardTest forwardTest) throws Exception {
        return forwardTestDAO.persist(forwardTest);
    }

    @Override
    public ForwardTest createFromFile(String name) throws Exception {

        TimeSeries timeSeries = CsvTicksLoader.load(name);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Date start = Date.valueOf(timeSeries.getFirstBar().getBeginTime().toLocalDate());
        Date end = Date.valueOf(timeSeries.getLastBar().getBeginTime().toLocalDate());

        //TODO: Dejar esto parametrico
        TimeFrame timeFrame = TimeFrame.DAY;

        ForwardTest forwardTest = new ForwardTest(name, timestamp, start, end, timeFrame);

        for (Bar bar : timeSeries.getBarData()) {
            double open = bar.getOpenPrice().doubleValue();
            double high = bar.getMaxPrice().doubleValue();
            double low = bar.getMinPrice().doubleValue();
            double close = bar.getClosePrice().doubleValue();
            double volume = bar.getVolume().doubleValue();

            forwardTest.getBars().add(new ForwardTestBar(bar.getEndTime(), open, high, low, close, volume, forwardTest));
        }

        return forwardTest;
    }

    @Override
    public ForwardTest createFromSeries(TimeSeries series) throws Exception {

        String name = series.getName();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Date start = Date.valueOf(series.getFirstBar().getBeginTime().toLocalDate());
        Date end = Date.valueOf(series.getLastBar().getBeginTime().toLocalDate());

        //TODO: Dejar esto parametrico
        TimeFrame timeFrame = TimeFrame.DAY;

        ForwardTest forwardTest = new ForwardTest(name, timestamp, start, end, timeFrame);

        for (Bar bar : series.getBarData()) {
            double open = bar.getOpenPrice().doubleValue();
            double high = bar.getMaxPrice().doubleValue();
            double low = bar.getMinPrice().doubleValue();
            double close = bar.getClosePrice().doubleValue();
            double volume = bar.getVolume().doubleValue();

            forwardTest.getBars().add(new ForwardTestBar(bar.getEndTime(), open, high, low, close, volume, forwardTest));
        }

        return forwardTest;
    }


}
