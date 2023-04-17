package cl.dsoto.trading.components;

import cl.dsoto.trading.model.ForwardTest;
import cl.dsoto.trading.model.Period;
import cl.dsoto.trading.model.TimeFrame;
import org.ta4j.core.TimeSeries;

import javax.ejb.Remote;
import java.util.List;

/**
 * Created by des01c7 on 25-03-19.
 */
@Remote
public interface ForwardTestManager {

    public ForwardTest persist(ForwardTest forwardTest) throws Exception;

    public ForwardTest createFromFile(String file) throws Exception;

    public ForwardTest createFromSeries(TimeSeries series) throws Exception;

    //public void delete(ForwardTest forwardTest) throws Exception;

}
