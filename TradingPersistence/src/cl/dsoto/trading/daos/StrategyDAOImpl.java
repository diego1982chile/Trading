package cl.dsoto.trading.daos;

import cl.dsoto.trading.factories.DataSourceFactory;
import cl.dsoto.trading.model.ProblemType;
import cl.dsoto.trading.model.Strategy;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class StrategyDAOImpl implements StrategyDAO {

    static private final Logger logger = Logger.getLogger(StrategyDAOImpl.class.getName());

    Map<Long, Strategy> strategyMap = new ConcurrentHashMap<>();

    @Resource(lookup = "java:jboss/TradingDS")
    private DataSource dataSource;

    public List<Strategy> getStrategies() throws Exception {

        List<Strategy> strategies = new ArrayList<>();

        String sql = "{call trd.get_strategies()}";

        try (Connection connect = dataSource.getConnection();
             CallableStatement call = connect.prepareCall(sql)) {

            call.execute();

            logger.log(Level.INFO, "Registros recuperadas:");

            ResultSet rs = call.getResultSet();

            while (rs.next()) {
                strategies.add(createStrategyFromResultSet(rs));
            }

        } catch (SQLException e) {
            String errorMsg = "Error al recuperar la descripción de la BDD.";
            logger.log(Level.SEVERE, e.getMessage());
            throw new Exception(e.getMessage());
        }

        return strategies;
    }

    @Override
    public Strategy getStrategyById(long id) throws Exception {
        if(strategyMap.isEmpty()) {
            try {
                for (Strategy strategy : getStrategies()) {
                    strategyMap.put(strategy.getId(), strategy);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return strategyMap.get(id);
    }

    private Strategy createStrategyFromResultSet(ResultSet resultSet) throws SQLException {

        long id = resultSet.getLong("id");

        String name = resultSet.getString("name");
        int variables = resultSet.getInt("variables");
        ProblemType problemType = ProblemType.valueOf(resultSet.getInt("id_problem_type"));

        Strategy strategy = new Strategy(id, name, variables, problemType);

        return strategy;
    }

}
