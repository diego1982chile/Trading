package cl.dsoto.trading.components;

import cl.dsoto.trading.model.Period;
import cl.dsoto.trading.model.Strategy;

import java.util.List;

/**
 * Created by des01c7 on 25-03-19.
 */
public interface PeriodManager {

    public Period persist(Period period) throws Exception;
    public List<Period> getLast(int periods) throws Exception;
    public Period createFromFile(String file) throws Exception;

}
