package cl.dsoto.trading.controllers;

import cl.dsoto.trading.clients.ServiceLocator;
import cl.dsoto.trading.components.PeriodManager;
import cl.dsoto.trading.components.TestManager;
import cl.dsoto.trading.model.Period;
import cl.dsoto.trading.model.PeriodBar;
import cl.dsoto.trading.model.Test;
import cl.dsoto.trading.model.TimeFrame;
import cl.dsoto.trading.strategies.StrategyHelper;
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
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * Created by des01c7 on 12-04-19.
 */
public class TestController {

    //@EJB
    private TestManager testManager = (TestManager) ServiceLocator.getInstance().getService(TestManager.class);

    private static String newline = System.getProperty("line.separator");

    List<Test> tests;

    Test selected;

    TimeFrame selectedTimeFrame;

    public TimeFrame getSelectedTimeFrame() {
        return selectedTimeFrame;
    }

    public void setSelectedTimeFrame(TimeFrame selectedTimeFrame) {
        this.selectedTimeFrame = selectedTimeFrame;
    }

    public TestController(JComboBox tests, JComboBox timeFrames, JList forwardTests) throws Exception {
        try {
            timeFrames.setModel(new DefaultComboBoxModel(TimeFrame.values()));
            timeFrames.setSelectedItem(TimeFrame.DAY);
            selectedTimeFrame = (TimeFrame) timeFrames.getSelectedItem();


            this.tests = getLast();

            tests.setModel(new DefaultComboBoxModel() {
                List<Test> tests = getLast();
                public int getSize() { return tests.size(); }
                public Object getElementAt(int i) { return tests.get(i); }
            });

        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<Test> getLast() throws Exception {
        try {
            return testManager.getLast(100);
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void delete(JList periods) throws Exception {
        try {
            testManager.delete(selected);
            this.tests = getLast();
            periods.setModel(new AbstractListModel() {
                List<Period> periods = getLast();
                public int getSize() { return periods.size(); }
                public Object getElementAt(int i) { return periods.get(i); }
            });
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void changeTimeFrame(JList periods, JComboBox timeFrames) throws Exception {
        try {
                selectedTimeFrame = (TimeFrame) timeFrames.getSelectedItem();
                periods.setModel(new AbstractListModel() {
                List<Period> periods = getLast();
                public int getSize() { return periods.size(); }
                public Object getElementAt(int i) { return periods.get(i); }
            });
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Test getSelected() {
        return selected;
    }

    public void setSelected(Test selected) {
        if(this.selected != selected) {
            this.selected = selected;
        }
    }

}
