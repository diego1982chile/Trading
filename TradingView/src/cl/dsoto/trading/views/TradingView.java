package cl.dsoto.trading.views;

import cl.dsoto.trading.controllers.BackTestController;
import cl.dsoto.trading.controllers.ForwardTestController;
import cl.dsoto.trading.controllers.StrategiesController;
import cl.dsoto.trading.model.ForwardTest;
import cl.dsoto.trading.model.Period;
import cl.dsoto.trading.model.TimeFrame;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;

/**
 * Created by des01c7 on 12-04-19.
 */
public class TradingView {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JList list1;
    private JTextField textField6;
    private JTextField textField7;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextArea textArea1;
    private JButton button1;
    private JLabel estrategiasLabel;
    private JButton button2;
    private JList list2;
    private JButton nuevoButton;
    private JComboBox comboBox1;

    public BackTestController backTestController;
    public ForwardTestController forwardTestController;

    public TradingView() {
        list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Period period = (Period) list1.getSelectedValue();
                list2.setModel(new javax.swing.AbstractListModel() {
                    java.util.List<ForwardTest> forwardTests = period.getForwardTests();
                    public int getSize() { return period.getForwardTests().size(); }
                    public Object getElementAt(int i) { return period.getForwardTests().get(i); }
                });
                backTestController.setSelected(period);
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    backTestController.delete(list1);
                    //list1.updateUI();
                    panel1.updateUI();
                }
                catch(Exception ex) {

                }
            }
        });
        nuevoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NewForwardTestView newForwardTestView = new NewForwardTestView(panel1,(Period) list1.getSelectedValue());
                newForwardTestView.setVisible(true);
            }
        });
        comboBox1.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                try {
                    backTestController.changeTimeFrame(list1, comboBox1);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                panel1.updateUI();
            }
        });
        list2.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ForwardTest forwardTest = (ForwardTest) list2.getSelectedValue();

                if(forwardTest == null) {
                    return;
                }

                ForwardTestView forwardTestView = new ForwardTestView(panel1, forwardTest);
                forwardTestView.setVisible(true);
            }
        });
    }

    public static void main(String[] args) throws Exception {

        JFrame jFrame = new JFrame("Trading View");

        double offset=0.8;
        jFrame.setSize(new Dimension((int)(0.65 * Toolkit.getDefaultToolkit().getScreenSize().getWidth()),
                (int)(offset* Toolkit.getDefaultToolkit().getScreenSize().getHeight())));
        jFrame.setMinimumSize(new Dimension((int)(0.65 * Toolkit.getDefaultToolkit().getScreenSize().getWidth()),
                (int)(offset* Toolkit.getDefaultToolkit().getScreenSize().getHeight())));
        jFrame.setMaximumSize(new Dimension((int)(0.65 * Toolkit.getDefaultToolkit().getScreenSize().getWidth()),
                (int)(offset* Toolkit.getDefaultToolkit().getScreenSize().getHeight())));

        TradingView tradingView = new TradingView();

        tradingView.backTestController = new BackTestController(tradingView.list1, tradingView.comboBox1, tradingView.list2);

        tradingView.backTestController.setStart(tradingView.textField1);
        tradingView.backTestController.setEnd(tradingView.textField2);
        tradingView.backTestController.setTradesView(tradingView.textField3);
        tradingView.backTestController.setProfitableTradesView(tradingView.textField4);
        tradingView.backTestController.setRewardRiskView(tradingView.textField5);
        tradingView.backTestController.setVsBuyAndHoldView(tradingView.textField6);
        tradingView.backTestController.setCashFlowView(tradingView.textField7);
        tradingView.backTestController.setCashFlowView(tradingView.textField7);
        //tradingView.backTestController.setCashFlowDetailView(tradingView.textArea1);

        JTabbedPane jTabbedPane = (JTabbedPane) tradingView.panel1.getComponents()[0];
        JPanel jPanel = (JPanel) jTabbedPane.getComponents()[2];
        jPanel = (JPanel) jPanel.getComponents()[0];
        jPanel = (JPanel) jPanel.getComponents()[1];

        tradingView.backTestController.setPlotView(jPanel);

        jFrame.setContentPane(tradingView.tabbedPane1);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        RefineryUtilities.centerFrameOnScreen(jFrame);
        jFrame.pack();
        jFrame.setVisible(true);
    }

    private void createUIComponents() {
        estrategiasLabel = new JLabel("Estrategias");
        estrategiasLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        estrategiasLabel.setForeground(new java.awt.Color(0, 0, 238));
        estrategiasLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                StrategiesView strategiesView = new StrategiesView(panel1, (Period) list1.getSelectedValue());
                strategiesView.setVisible(true);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                estrategiasLabel.setCursor(new java.awt.Cursor(Cursor.HAND_CURSOR));
                estrategiasLabel.setText("<html><a href=''>" + estrategiasLabel.getText() + "</a></html>");
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                estrategiasLabel.setCursor(new java.awt.Cursor(Cursor.DEFAULT_CURSOR));
                estrategiasLabel.setText(estrategiasLabel.getText());
            }
        });
        // TODO: place custom component creation code here
    }

}
