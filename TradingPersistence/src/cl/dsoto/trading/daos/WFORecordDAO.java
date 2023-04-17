package cl.dsoto.trading.daos;

import cl.dsoto.trading.model.*;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by des01c7 on 25-03-19.
 */
@Local
public interface WFORecordDAO {

    WFORecord persist(WFORecord wfoRecord) throws Exception;

    List<WFORecord> getWFORecords() throws Exception;

    //List<WFORecord> getWFORecordsById(String id) throws Exception;
}
