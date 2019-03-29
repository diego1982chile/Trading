package cl.dsoto.trading.daos;

import cl.dsoto.trading.model.Period;
import cl.dsoto.trading.model.Strategy;

import java.util.List;

/**
 * Created by des01c7 on 25-03-19.
 */
public interface PeriodDAO {

    Period persist(Period period) throws Exception;

    List<Period> getLast(int periods) throws Exception;
}
