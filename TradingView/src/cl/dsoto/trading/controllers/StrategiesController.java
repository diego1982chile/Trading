package cl.dsoto.trading.controllers;

import cl.dsoto.trading.clients.ServiceLocator;
import cl.dsoto.trading.components.PeriodManager;
import cl.dsoto.trading.model.Period;
import cl.dsoto.trading.model.PeriodBar;
import cl.dsoto.trading.strategies.StrategyHelper;
import javafx.util.Pair;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.ta4j.core.*;
import org.ta4j.core.analysis.CashFlow;
import org.ta4j.core.analysis.criteria.AverageProfitableTradesCriterion;
import org.ta4j.core.analysis.criteria.RewardRiskRatioCriterion;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;
import org.ta4j.core.analysis.criteria.VersusBuyAndHoldCriterion;
import ta4jexamples.analysis.BuyAndSellSignalsToChart;
import ta4jexamples.research.MultipleStrategy;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


/**
 * Created by des01c7 on 12-04-19.
 */
public class StrategiesController {

    DefaultMutableTreeNode root = new DefaultMutableTreeNode();

    public StrategiesController(JTree strategies, Period selected) {

        Map<String, List<Pair<String, Integer>>> strats = StrategyHelper.mapStrategiesFrom(selected);

        for (String s : strats.keySet()) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(s);
            for (Pair<String,Integer> stringIntegerPair : strats.get(s)) {
                child.add(new DefaultMutableTreeNode(stringIntegerPair.toString()));
            }
            root.add(child);
        }

        strategies.setModel(new javax.swing.tree.DefaultTreeModel(root));
    }

}
