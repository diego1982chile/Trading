package cl.dsoto.trading.daos;

import cl.dsoto.trading.factories.DataSourceFactory;
import cl.dsoto.trading.model.ProblemType;
import cl.dsoto.trading.model.Solution;
import cl.dsoto.trading.model.Strategy;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by des01c7 on 25-03-19.
 */
@Stateless
public class ValueDAOImpl<T extends Comparable> implements ValueDAO {

    static private final Logger logger = Logger.getLogger(ValueDAOImpl.class.getName());

    @Resource(lookup = "java:jboss/TradingDS")
    private DataSource dataSource;

    @Override
    public Solution persistBinaryValues(Solution solution) throws Exception {

        String sql = "{call trd.create_boolean_value(?,?)}";

        try (Connection connect = dataSource.getConnection();
             CallableStatement call = connect.prepareCall(sql)) {

            call.setLong(1, solution.getOptimization().getId());

            for (int i = 0; i < solution.getValues().size(); i++) {

                BitSet bitset = (BitSet) solution.getValues().get(i);

                if(bitset.isEmpty()) {
                    call.setLong(1, solution.getId());
                    call.setBoolean(2, false);
                    call.execute();
                }
                else {
                    for (int j = 0; j < bitset.length(); j++) {
                        call.setLong(1, solution.getId());
                        call.setBoolean(2, bitset.get(j));
                        call.execute();
                    }
                }

            }

            ResultSet rs = call.getResultSet();

            if (rs.next()) {
                //solution.setId(rs.getLong(1));
            } else {
                String errorMsg = "El registro no fue creado. Contacte a Desarrollo";
                logger.log(Level.SEVERE, errorMsg);
                throw new Exception(errorMsg);
            }
            //rs.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new Exception(e);
        }

        return solution;
    }

    @Override
    public Solution persistIntegerValues(Solution solution) throws Exception  {

        String sql = "{call trd.create_integer_value(?,?)}";

        try (Connection connect = dataSource.getConnection();
             CallableStatement call = connect.prepareCall(sql)) {

            call.setLong(1, solution.getOptimization().getId());

            Strategy problem = solution.getOptimization().getStrategy();

            int cont = 0;

            while (cont < problem.getVariables()) {
                call.setLong(1, solution.getId());
                call.setInt(2, (Integer) solution.getValues().get(cont));
                call.execute();
                cont++;
            }

            ResultSet rs = call.getResultSet();

            if (rs.next()) {
                //solution.setId(rs.getLong(1));
            } else {
                String errorMsg = "El registro no fue creado. Contacte a Desarrollo";
                logger.log(Level.SEVERE, errorMsg);
                throw new Exception(errorMsg);
            }
            //rs.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new Exception(e);
        }

        return solution;
    }

    @Override
    public Solution persistRealValues(Solution solution) throws Exception {

        String sql = "{call trd.create_real_value(?,?)}";

        try (Connection connect = dataSource.getConnection();
             CallableStatement call = connect.prepareCall(sql)) {

            call.setLong(1, solution.getOptimization().getId());

            Strategy problem = solution.getOptimization().getStrategy();

            int cont = 0;

            while (cont < problem.getVariables()) {
                call.setLong(1, solution.getId());
                call.setDouble(2, (Double) solution.getValues().get(cont));
                call.execute();
                cont++;
            }

            call.execute();

            ResultSet rs = call.getResultSet();

            if (rs.next()) {
                //solution.setId(rs.getLong(1));
            } else {
                String errorMsg = "El registro no fue creado. Contacte a Desarrollo";
                logger.log(Level.SEVERE, errorMsg);
                throw new Exception(errorMsg);
            }
            //rs.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new Exception(e);
        }

        return solution;
    }

    @Override
    public List<Boolean> getBinaryValues(Solution solution) throws Exception {

        List<Boolean> binaryValues = new ArrayList<>();

        String sql = "{call trd.get_boolean_values_by_solution(?)}";

        try (Connection connect = dataSource.getConnection();
             CallableStatement call = connect.prepareCall(sql)) {

            call.setLong(1, solution.getId());

            call.execute();

            ResultSet rs = call.getResultSet();

            while (rs.next()) {
                binaryValues.add(rs.getBoolean("boolean_value"));
            }
            //rs.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new Exception(e);
        }

        return binaryValues;
    }

    @Override
    public List<Integer> getIntegerValues(Solution solution) throws Exception {

        List<Integer> intValues = new ArrayList<>();

        String sql = "{call trd.get_int_values_by_solution(?)}";

        try (Connection connect = dataSource.getConnection();
             CallableStatement call = connect.prepareCall(sql)) {

            call.setLong(1, solution.getId());

            call.execute();

            ResultSet rs = call.getResultSet();

            while (rs.next()) {
                intValues.add(rs.getInt("int_value"));
            }
            //rs.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new Exception(e);
        }

        return intValues;

    }

    @Override
    public List<Float> getRealValues(Solution solution) throws Exception {

        List<Float> floatValues = new ArrayList<>();

        String sql = "{call trd.get_real_values_by_solution(?)}";

        try (Connection connect = dataSource.getConnection();
             CallableStatement call = connect.prepareCall(sql)) {

            call.setLong(1, solution.getId());

            call.execute();

            ResultSet rs = call.getResultSet();

            while (rs.next()) {
                floatValues.add(rs.getFloat("float_value"));
            }
            //rs.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new Exception(e);
        }

        return floatValues;

    }
}
