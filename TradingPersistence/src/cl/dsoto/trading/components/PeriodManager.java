package cl.dsoto.trading.components;

import cl.dsoto.trading.model.Period;
import cl.dsoto.trading.model.Strategy;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.TimeSeries;

import javax.ejb.Remote;
import java.util.List;

/**
 * Created by des01c7 on 25-03-19.
 */
@Remote
public interface PeriodManager {

    public Period getPeriodById(long id) throws Exception;

    public Period persist(Period period) throws Exception;

    public List<Period> getLast(int periods) throws Exception;

    public Period createFromFile(String file) throws Exception;

    public void generateOptimizations(TimeSeries series) throws Exception;

    public List<org.ta4j.core.Strategy> mapFrom(Period period) throws Exception;

}
