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
public class TestDAOImpl implements TestDAO {

    static private final Logger logger = Logger.getLogger(TestDAOImpl.class.getName());

    @EJB
    private OptimizationDAO optimizationDAO;

    @EJB
    private BarDAO barDAO;

    @EJB
    private ForwardTestDAO forwardTestDAO;

    @Resource(lookup = "java:jboss/TradingDS")
    private DataSource dataSource;

    public Test getTestById(long id) throws Exception {

        Test test = null;

        String sql = "{call trd.get_test_by_id(?)}";

        try (Connection connect = dataSource.getConnection();
             CallableStatement call = connect.prepareCall(sql)) {

            call.setLong(1, id);

            call.execute();

            logger.log(Level.INFO, "Registros recuperadas:");

            ResultSet rs = call.getResultSet();

            if (rs.next()) {
                test = createTestFromResultSet(rs);
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

        return test;
    }


    public Test persist(Test test) throws Exception {

        String sql = "{call trd.create_test(?,?,?,?,?)}";

        try (Connection connect = dataSource.getConnection();
             CallableStatement call = connect.prepareCall(sql);
        ) {

            call.setString(1, test.getName());
            call.setTimestamp(2, test.getTimestamp());
            call.setLong(3, test.getTimeFrame().getId());

            call.execute();

            ResultSet rs = call.getResultSet();

            if (rs.next()) {
                test.setId(rs.getLong(1));

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

        return test;
    }

    public List<Test> getLast(int periods) throws Exception {

        List<Test> testList = new ArrayList<>();

        String sql = "{call trd.get_last_tests(?)}";

        try (Connection connect = dataSource.getConnection();
             CallableStatement call = connect.prepareCall(sql)) {

            call.setInt(1, periods);

            call.execute();

            logger.log(Level.INFO, "Registros recuperadas:");

            ResultSet rs = call.getResultSet();

            while (rs.next()) {
                testList.add(createTestFromResultSet(rs));
            }

        } catch (SQLException e) {
            String errorMsg = "Error al recuperar la descripción de la BDD.";
            logger.log(Level.SEVERE, e.getMessage());
            throw new Exception(e.getMessage());
        }

        return testList;
    }

    @Override
    public void delete(Test test) {

        String sql = "{call trd.delete_test(?)}";

        try (Connection connection = dataSource.getConnection();
             CallableStatement call = connection.prepareCall(sql)) {

            call.setLong(1, test.getId());
            call.execute();

        } catch (SQLException e) {
            String errorMessage = "No se pudo eliminar el test: " + test.toString();
            throw new EJBException(errorMessage, e);
        }
    }

    private Test createTestFromResultSet(ResultSet resultSet) throws Exception {

        long id = resultSet.getLong("id");

        String name = resultSet.getString("name");
        Timestamp timestamp = resultSet.getTimestamp("timestamp");

        TimeFrame timeFrame = TimeFrame.valueOf(resultSet.getInt("id_time_frame"));

        Test test = new Test(id, name, timestamp, timeFrame);

        return test;
    }

}
