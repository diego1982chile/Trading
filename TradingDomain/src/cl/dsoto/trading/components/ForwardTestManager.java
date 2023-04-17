package cl.dsoto.trading.components;

import cl.dsoto.trading.model.ForwardTest;
import org.ta4j.core.TimeSeries;

import javax.ejb.Remote;

/**
 * Created by des01c7 on 25-03-19.
 */
public interface ForwardTestManager {

    public ForwardTest persist(ForwardTest forwardTest) throws Exception;

    public ForwardTest createFromFile(String name) throws Exception;

    public ForwardTest createFromSeries(TimeSeries series) throws Exception;

    //public void delete(ForwardTest forwardTest) throws Exception;

}
