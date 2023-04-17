package cl.dsoto.trading.daos;

import cl.dsoto.trading.model.Period;
import cl.dsoto.trading.model.Test;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by des01c7 on 25-03-19.
 */
@Local
public interface TestDAO {

    Test getTestById(long id) throws Exception;

    Test persist(Test test) throws Exception;

    void delete(Test test) throws Exception;

    List<Test> getLast(int tests) throws Exception;
}
