package cl.dsoto.trading.daos;

import cl.dsoto.trading.model.ForwardTest;
import cl.dsoto.trading.model.Period;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by des01c7 on 25-03-19.
 */
@Local
public interface ForwardTestDAO {

    ForwardTest getForwardTestById(long id) throws Exception;

    ForwardTest persist(ForwardTest forwardTest) throws Exception;

    void delete(ForwardTest forwardTest) throws Exception;

    List<ForwardTest> getForwardTestsByPeriod(Period period) throws Exception;
}
