package cl.dsoto.trading.components;

import cl.dsoto.trading.cdi.ServiceLocator;
import cl.dsoto.trading.daos.OptimizationDAO;
import cl.dsoto.trading.daos.PeriodDAO;
import cl.dsoto.trading.daos.StrategyDAO;
import cl.dsoto.trading.model.Period;
import cl.dsoto.trading.model.Strategy;
import cl.dsoto.trading.model.TimeFrame;
import org.ta4j.core.TimeSeries;
import ta4jexamples.loaders.CsvTicksLoader;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by des01c7 on 25-03-19.
 */
public class PeriodManagerImpl implements PeriodManager {

    //@EJB
    private PeriodDAO periodDAO = (PeriodDAO) ServiceLocator.getInstance().getService(PeriodDAO.class);

    @Override
    public Period persist(Period period) throws Exception {
        return periodDAO.persist(period);
    }

    @Override
    public List<Period> getLast(int periods) throws Exception {
        return periodDAO.getLast(periods);
    }

    @Override
    public Period createFromFile(String file) throws Exception {
        TimeSeries timeSeries = CsvTicksLoader.load(file);
        String name = file;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Date start = Date.valueOf(timeSeries.getFirstBar().getBeginTime().toLocalDate());
        Date end = Date.valueOf(timeSeries.getLastBar().getBeginTime().toLocalDate());

        //TODO: Dejar esto parametrico
        TimeFrame timeFrame = TimeFrame.DAY;

        Period period = new Period(name, timestamp, start, end, timeFrame);

        return period;
    }
}
