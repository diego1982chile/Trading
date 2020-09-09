package cl.dsoto.trading.controllers;

import cl.dsoto.trading.clients.ServiceLocator;
import cl.dsoto.trading.components.ForwardTestManager;
import cl.dsoto.trading.components.PeriodManager;
import cl.dsoto.trading.model.ForwardTest;
import cl.dsoto.trading.model.Period;
import cl.dsoto.trading.model.PeriodBar;
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
public class NewForwardTestController {

    //@EJB
    private PeriodManager periodManager = (PeriodManager) ServiceLocator.getInstance().getService(PeriodManager.class);

    //@EJB
    private ForwardTestManager forwardTestManager = (ForwardTestManager) ServiceLocator.getInstance().getService(ForwardTestManager.class);

    private JTextField start;
    private JTextField end;


    private CashFlow cashFlow;
    private JTextField cashFlowView;

    private JTextArea cashFlowDetailView;

    private JPanel plotView;

    private JLabel strategiesView;

    String pattern = "###.#####";
    DecimalFormat decimalFormat = new DecimalFormat(pattern);

    private static String newline = System.getProperty("line.separator");

    List<Period> periods;

    Period selected;

    ForwardTest forwardTest;

    TimeFrame selectedTimeFrame;

    public TimeFrame getSelectedTimeFrame() {
        return selectedTimeFrame;
    }

    public void setSelectedTimeFrame(TimeFrame selectedTimeFrame) {
        this.selectedTimeFrame = selectedTimeFrame;
    }

    public NewForwardTestController(JList periods, JComboBox timeFrames) throws Exception {
        try {
            timeFrames.setModel(new DefaultComboBoxModel(TimeFrame.values()));
            timeFrames.setSelectedItem(TimeFrame.MINUTE);
            selectedTimeFrame = (TimeFrame) timeFrames.getSelectedItem();

            timeFrames.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent event) {
                    if (event.getStateChange() == ItemEvent.SELECTED) {
                        try {
                            List<Period> periods = getLast();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            this.periods = getLast();
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

    public void save(ForwardTest forwardTest) throws Exception {
        try {
            forwardTestManager.persist(forwardTest);
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<Period> getLast() throws Exception {
        try {
            return periodManager.getLast(selectedTimeFrame, 100);
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void delete(JList periods) throws Exception {
        try {
            periodManager.delete(selected);
            this.periods = getLast();
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


    public Period getSelected() {
        return selected;
    }



    public JTextField getStart() {
        return start;
    }

    public void setStart(JTextField start) {
        this.start = start;
    }

    public JTextField getEnd() {
        return end;
    }

    public void setEnd(JTextField end) {
        this.end = end;
    }


    public JTextField getCashFlowView() {
        return cashFlowView;
    }

    public void setCashFlowView(JTextField cashFlowView) {
        this.cashFlowView = cashFlowView;
    }

    public JTextArea getCashFlowDetailView() {
        return cashFlowDetailView;
    }

    public void setCashFlowDetailView(JTextArea cashFlowDetailView) {
        this.cashFlowDetailView = cashFlowDetailView;
    }

    public JPanel getPlotView() {
        return plotView;
    }

    public void setPlotView(JPanel plotView) {
        this.plotView = plotView;
    }

    public JLabel getStrategiesView() {
        return strategiesView;
    }

    public void setStrategiesView(JLabel strategiesView) {
        this.strategiesView = strategiesView;
    }


}
