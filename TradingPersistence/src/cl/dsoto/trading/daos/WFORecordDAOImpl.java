package cl.dsoto.trading.daos;

import cl.dsoto.trading.model.*;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by des01c7 on 25-03-19.
 */
@Stateless
public class WFORecordDAOImpl implements WFORecordDAO {

    static private final Logger logger = Logger.getLogger(WFORecordDAOImpl.class.getName());

    @Resource(lookup = "java:jboss/TradingDS")
    private DataSource dataSource;

    @Override
    public WFORecord persist(WFORecord wfoRecord) throws Exception {

        String sql = "{call trd.create_wfo_record(?,?,?,?,?,?,?,?,?,?,?,?)}";

        try (Connection connect = dataSource.getConnection();
             CallableStatement call = connect.prepareCall(sql)) {

            call.setString(1, wfoRecord.getId());
            call.setInt(2, wfoRecord.getIteration());
            call.setString(3, wfoRecord.getStrategy());
            call.setString(4, wfoRecord.getPeriod());
            call.setString(5, wfoRecord.getStage());
            call.setInt(6, wfoRecord.getNumberOfTrades());
            call.setDouble(7, wfoRecord.getProfitableTradesRatio());
            call.setDouble(8, wfoRecord.getRewardRiskRatio());
            call.setDouble(9, wfoRecord.getVsBuyAndHoldRatio());
            call.setDouble(10, wfoRecord.getCashflow());
            call.setDouble(11, wfoRecord.getEfficiencyIndex());
            call.setString(12, wfoRecord.getParameters());

            call.execute();

            ResultSet rs = call.getResultSet();

            if (rs.next()) {
                wfoRecord.setId(rs.getString(1));
            } else {
                connect.rollback();
                String errorMsg = "El registro no fue creado. Contacte a Desarrollo";
                logger.log(Level.SEVERE, errorMsg);
                throw new Exception(errorMsg);
            }

            //rs.close();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new Exception(e);
        }

        return wfoRecord;
    }

    @Override
    public List<WFORecord> getWFORecords() throws Exception {

        List<WFORecord> wfoRecords = new ArrayList<>();

        String sql = "{call trd.get_wfo_records()}";

        try (Connection connect = dataSource.getConnection();
             CallableStatement call = connect.prepareCall(sql)) {

            call.execute();

            logger.log(Level.INFO, "Registros recuperadas:");

            ResultSet rs = call.getResultSet();

            while (rs.next()) {
                wfoRecords.add(createWFORecordFromResultSet(rs));
            }

        } catch (SQLException e) {
            String errorMsg = "Error al recuperar la descripción de la BDD.";
            logger.log(Level.SEVERE, e.getMessage());
            throw new Exception(e.getMessage());
        }

        return wfoRecords;
    }


    private WFORecord createWFORecordFromResultSet(ResultSet resultSet) throws Exception {

        String id = resultSet.getString("id");
        int iteration = resultSet.getInt("iteration");
        String strategy = resultSet.getString("strategy");
        String period = resultSet.getString("period");
        String stage = resultSet.getString("stage");
        int numberOfTrades = resultSet.getInt("number_of_trades");
        double profitableTradesRatio = resultSet.getInt("profitable_trades_ratio");
        double rewardRiskRatio = resultSet.getInt("reward_risk_ratio");
        double vsBuyAndHoldRatio = resultSet.getInt("vs_buy_and_hold_ratio");
        double cashFlow = resultSet.getInt("cash_flow");
        double efficiencyIndex = resultSet.getInt("efficiency_index");
        String parameters = resultSet.getString("parameters");

        WFORecord wfoRecord = new WFORecord(id, iteration, strategy, period, stage, numberOfTrades, profitableTradesRatio,
                rewardRiskRatio, vsBuyAndHoldRatio, cashFlow, efficiencyIndex, parameters);

        return wfoRecord;
    }


}

