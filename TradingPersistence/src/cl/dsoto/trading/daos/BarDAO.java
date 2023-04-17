package cl.dsoto.trading.daos;

import cl.dsoto.trading.model.*;
import org.ta4j.core.Bar;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by des01c7 on 25-03-19.
 */
@Local
public interface BarDAO {

    ForwardTestBar persist(ForwardTestBar bar) throws Exception;

    PeriodBar persist(PeriodBar bar) throws Exception;

    List<PeriodBar> getBars(Period period) throws Exception;

    List<ForwardTestBar> getBars(ForwardTest forwardTest) throws Exception;
}
