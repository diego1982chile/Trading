package cl.dsoto.trading.daos;

import cl.dsoto.trading.model.*;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.sql.DataSource;
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
@Stateless
public class ForwardTestDAOImpl implements ForwardTestDAO {

    static private final Logger logger = Logger.getLogger(ForwardTestDAOImpl.class.getName());

    Map<Long, Strategy> strategyMap = new ConcurrentHashMap<>();

    @EJB
    private BarDAO barDAO;

    @Resource(lookup = "java:jboss/TradingDS")
    private DataSource dataSource;

    public ForwardTest getForwardTestById(long id) throws Exception {

        ForwardTest forwardTest = null;

        String sql = "{call trd.get_forward_test_by_id(?)}";

        try (Connection connect = dataSource.getConnection();
             CallableStatement call = connect.prepareCall(sql)) {

            call.setLong(1, id);

            call.execute();

            logger.log(Level.INFO, "Registros recuperadas:");

            ResultSet rs = call.getResultSet();

            if (rs.next()) {
                forwardTest = createForwardTestFromResultSet(rs, null);
            }
            else {
                String errorMsg = "Error al recuperar la descripción de la BDD.";
                logger.log(Level.SEVERE, errorMsg);
                throw new Exception(errorMsg);
            }

        } catch (SQLException e) {
            String errorMsg = "Error al recuperar la descripción de la BDD.";
            logger.log(Level.SEVERE, e.getMessage());
            throw new Exception(e.getMessage());
        }

        return forwardTest;
    }


    public ForwardTest persist(ForwardTest forwardTest) throws Exception {

        String sql = "{call trd.create_forward_test(?,?,?,?,?,?)}";

        try (Connection connect = dataSource.getConnection();
             CallableStatement call = connect.prepareCall(sql);
        ) {

            call.setLong(1, forwardTest.getPeriod().getId());
            call.setString(2, forwardTest.getName());
            call.setTimestamp(3, forwardTest.getTimestamp());
            call.setDate(4, forwardTest.getStart());
            call.setDate(5, forwardTest.getEnd());
            call.setLong(6, forwardTest.getTimeFrame().getId());

            call.execute();

            ResultSet rs = call.getResultSet();

            if (rs.next()) {
                forwardTest.setId(rs.getLong(1));

                for (ForwardTestBar forwardTestBar : forwardTest.getBars()) {
                    barDAO.persist(forwardTestBar);
                }

            } else {
                connect.rollback();
                String errorMsg = "El registro no fue creado. Contacte a Desarrollo";
                logger.log(Level.SEVERE, errorMsg);
                throw new Exception(errorMsg);
            }
            //rs.close();
            //connect.commit();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new Exception(e);
        }

        return forwardTest;
    }

    public List<ForwardTest> getForwardTestsByPeriod(Period period) throws Exception {

        List<ForwardTest> forwardTests = new ArrayList<>();

        String sql = "{call trd.get_forward_tests_by_period(?)}";

        try (Connection connect = dataSource.getConnection();
             CallableStatement call = connect.prepareCall(sql)) {

            call.setLong(1, period.getId());

            call.execute();

            logger.log(Level.INFO, "Registros recuperadas:");

            ResultSet rs = call.getResultSet();

            while (rs.next()) {
                forwardTests.add(createForwardTestFromResultSet(rs, period));
            }

        } catch (SQLException e) {
            String errorMsg = "Error al recuperar la descripción de la BDD.";
            logger.log(Level.SEVERE, e.getMessage());
            throw new Exception(e.getMessage());
        }

        return forwardTests;
    }

    @Override
    public void delete(ForwardTest forwardTest) {

        String sql = "{call trd.delete_forward_test(?)}";

        try (Connection connection = dataSource.getConnection();
             CallableStatement call = connection.prepareCall(sql)) {

            call.setLong(1, forwardTest.getId());
            call.execute();

        } catch (SQLException e) {
            String errorMessage = "No se pudo eliminar el period: " + forwardTest.toString();
            throw new EJBException(errorMessage, e);
        }
    }

    private ForwardTest createForwardTestFromResultSet(ResultSet resultSet, Period period) throws Exception {

        long id = resultSet.getLong("id");

        String name = resultSet.getString("name");
        Timestamp timestamp = resultSet.getTimestamp("timestamp");
        Date start = resultSet.getDate("start");
        Date end = resultSet.getDate("end");

        TimeFrame timeFrame = TimeFrame.valueOf(resultSet.getInt("id_time_frame"));

        ForwardTest forwardTest = new ForwardTest(id, name, timestamp, start, end, timeFrame);

        forwardTest.setPeriod(period);

        forwardTest.setBars(barDAO.getBars(forwardTest));

        return forwardTest;
    }

}
