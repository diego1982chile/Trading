package cl.dsoto.trading.daos;

import cl.dsoto.trading.model.Period;
import cl.dsoto.trading.model.Strategy;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by des01c7 on 25-03-19.
 */
@Local
public interface PeriodDAO {

    Period getPeriodById(long id) throws Exception;

    Period persist(Period period) throws Exception;

    void delete(Period period) throws Exception;

    List<Period> getLast(int periods) throws Exception;
}
