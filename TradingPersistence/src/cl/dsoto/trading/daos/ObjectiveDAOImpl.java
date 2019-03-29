package cl.dsoto.trading.daos;

import cl.dsoto.trading.cdi.ServiceLocator;
import cl.dsoto.trading.factories.DataSourceFactory;
import cl.dsoto.trading.model.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by des01c7 on 25-03-19.
 */
public class ObjectiveDAOImpl implements ObjectiveDAO {

    //@EJB
    private ValueDAO valueDAO = (ValueDAO) ServiceLocator.getInstance().getService(ValueDAO.class);

    static private final Logger logger = Logger.getLogger(ObjectiveDAOImpl.class.getName());

    public Objective persist(Objective objective) throws Exception {

        String sql = "{call trd.create_objective(?,?)}";

        try (Connection connect = DataSourceFactory.getInstance().getConnection();
             CallableStatement call = connect.prepareCall(sql)) {

            call.setLong(1, objective.getOptimization().getId());
            call.setDouble(2, objective.getObjective());

            call.execute();

            ResultSet rs = call.getResultSet();

            if (rs.next()) {
                objective.setId(rs.getLong(1));
            } else {
                connect.rollback();
                String errorMsg = "El registro no fue creado. Contacte a Desarrollo";
                logger.log(Level.SEVERE, errorMsg);
                throw new Exception(errorMsg);
            }
            rs.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new Exception(e);
        }

        return objective;
    }

    @Override
    public List<Objective> getObjectivesByOptimization(Optimization optimization) throws Exception {

        List<Objective> objectives = new ArrayList<>();

        String sql = "{call trd.get_objectives_by_optimization(?)}";

        try (Connection connect = DataSourceFactory.getInstance().getConnection();
             CallableStatement call = connect.prepareCall(sql)) {

            call.setLong(1, optimization.getId());

            call.execute();

            ResultSet rs = call.getResultSet();

            while (rs.next()) {
                objectives.add(createObjectiveFromResultSet(rs, optimization));
            }

            rs.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new Exception(e);
        }

        return objectives;
    }

    public Objective createObjectiveFromResultSet(ResultSet rs, Optimization optimization) throws Exception {

        Objective objective = null;

        try {

            long id = rs.getLong("id");

            double value = rs.getDouble("objective");

            objective = new Objective(id, optimization, value);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return objective;
    }

}

