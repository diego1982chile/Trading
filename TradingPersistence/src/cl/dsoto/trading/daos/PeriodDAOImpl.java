package cl.dsoto.trading.daos;

import cl.dsoto.trading.cdi.ServiceLocator;
import cl.dsoto.trading.factories.DataSourceFactory;
import cl.dsoto.trading.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by des01c7 on 25-03-19.
 */
public class PeriodDAOImpl implements PeriodDAO {

    static private final Logger logger = Logger.getLogger(PeriodDAOImpl.class.getName());

    Map<Long, Strategy> strategyMap = new ConcurrentHashMap<>();

    //@EJB
    private OptimizationDAO optimizationDAO = (OptimizationDAO) ServiceLocator.getInstance().getService(OptimizationDAO.class);

    public Period persist(Period period) throws Exception {

        String sql = "{call trd.create_period(?,?,?,?,?)}";

        try (Connection connect = DataSourceFactory.getInstance().getConnection();
             CallableStatement call = connect.prepareCall(sql);
        ) {

            call.setString(1, period.getName());
            call.setTimestamp(2, period.getTimestamp());
            call.setDate(3, period.getStart());
            call.setDate(4, period.getEnd());
            call.setLong(5, period.getTimeFrame().getId());

            call.execute();

            ResultSet rs = call.getResultSet();

            if (rs.next()) {
                period.setId(rs.getLong(1));

                for (Optimization optimization : period.getOptimizations()) {
                    optimizationDAO.persist(optimization);
                }

            } else {
                connect.rollback();
                String errorMsg = "El registro no fue creado. Contacte a Desarrollo";
                logger.log(Level.SEVERE, errorMsg);
                throw new Exception(errorMsg);
            }
            rs.close();
            //connect.commit();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new Exception(e);
        }

        return period;
    }

    public List<Period> getLast(int periods) throws Exception {

        List<Period> periodList = new ArrayList<>();

        String sql = "{call trd.get_last_periods(?)}";

        try (Connection connect = DataSourceFactory.getInstance().getConnection();
             CallableStatement call = connect.prepareCall(sql)) {

            call.execute();

            logger.log(Level.INFO, "Registros recuperadas:");

            ResultSet rs = call.getResultSet();

            while (rs.next()) {
                periodList.add(createPeriodFromResultSet(rs));
            }

        } catch (SQLException e) {
            String errorMsg = "Error al recuperar la descripción de la BDD.";
            logger.log(Level.SEVERE, e.getMessage());
            throw new Exception(e.getMessage());
        }

        return periodList;
    }

    private Period createPeriodFromResultSet(ResultSet resultSet) throws Exception {

        long id = resultSet.getLong("id");

        String name = resultSet.getString("name");
        Timestamp timestamp = resultSet.getTimestamp("timestamp");
        Date start = resultSet.getDate("start");
        Date end = resultSet.getDate("end");

        TimeFrame timeFrame = TimeFrame.valueOf(resultSet.getInt("id_time_frame"));

        Period period = new Period(id, name, timestamp, start, end, timeFrame);

        period.setOptimizations(optimizationDAO.getOptimizationsByPeriod(period));

        return period;
    }

}
