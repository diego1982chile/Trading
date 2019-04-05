package cl.dsoto.trading.daos;

import cl.dsoto.trading.model.Solution;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by des01c7 on 25-03-19.
 */
@Local
public interface ValueDAO<T extends Comparable> {

    Solution persistBinaryValues(Solution solution) throws Exception;

    Solution persistIntegerValues(Solution solution) throws Exception;

    Solution persistRealValues(Solution solution) throws Exception;

    List<T> getBinaryValues(Solution solution) throws Exception;

    List<T> getIntegerValues(Solution solution) throws Exception;

    List<T> getRealValues(Solution solution) throws Exception;

}
