package cl.dsoto.trading.daos;

import cl.dsoto.trading.model.Optimization;
import cl.dsoto.trading.model.Period;
import cl.dsoto.trading.model.PeriodBar;
import cl.dsoto.trading.model.Solution;
import org.ta4j.core.Bar;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by des01c7 on 25-03-19.
 */
@Local
public interface BarDAO {

    PeriodBar persist(PeriodBar bar) throws Exception;

    List<PeriodBar> getBars(Period period) throws Exception;
}
