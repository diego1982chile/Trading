package cl.dsoto.trading.components;

import cl.dsoto.trading.model.Test;
import cl.dsoto.trading.model.TimeFrame;

import javax.ejb.Remote;
import java.util.List;

/**
 * Created by des01c7 on 25-03-19.
 */
@Remote
public interface TestManager {

    public Test getTestById(long id) throws Exception;

    public Test persist(Test test) throws Exception;

    public void delete(Test test) throws Exception;

    public List<Test> getLast(int periods) throws Exception;

    public List<Test> getLast(TimeFrame timeFrame, int periods) throws Exception;

}
