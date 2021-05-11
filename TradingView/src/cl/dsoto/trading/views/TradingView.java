package cl.dsoto.trading.views;

import cl.dsoto.trading.controllers.BackTestController;
import cl.dsoto.trading.controllers.ForwardTestController;
import cl.dsoto.trading.model.ForwardTest;
import cl.dsoto.trading.model.Period;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

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
    private JButton newForwardTest;
    private JComboBox timeFrames;
    private JButton newBackTest;
    private JComboBox comboBox2;
    private JLabel loaderLabel;
    private JButton deleteBackTest;
    private JButton deleteForwardTest;

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

        newForwardTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NewForwardTestView newForwardTestView = new NewForwardTestView(panel1,(Period) list1.getSelectedValue());
                newForwardTestView.setVisible(true);
            }
        });

        timeFrames.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                try {
                    if(backTestController != null) {
                        backTestController.changeTimeFrame(list1, timeFrames);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                panel1.updateUI();
            }
        });

        list2.addListSelectionListener(new ListSelectionListener() {

            ForwardTestView forwardTestView;

            @Override
            public void valueChanged(ListSelectionEvent e) {

                if(e.getValueIsAdjusting()) {
                    return;
                }

                ForwardTest forwardTest = (ForwardTest) list2.getSelectedValue();

                if(forwardTest == null) {
                    return;
                }

                loaderLabel.setVisible(true);

                new SwingWorker<Void, String>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        // Worken hard or hardly worken...
                        forwardTestView = new ForwardTestView(panel1, forwardTest);
                        return null;
                    }

                    @Override
                    protected void done() {
                        if(forwardTestView != null) {
                            forwardTestView.setVisible(true);
                        }
                        loaderLabel.setVisible(false);
                    }
                }.execute();

                //loaderLabel.setVisible(false);
                //panel1.updateUI();
            }
        });
    }

    public static void main(String[] args) throws Exception {

        JFrame jFrame = new JFrame("Trading View");

        double offset=0.8;
        jFrame.setSize(new Dimension((int)(0.67 * Toolkit.getDefaultToolkit().getScreenSize().getWidth()),
                (int)(offset* Toolkit.getDefaultToolkit().getScreenSize().getHeight())));
        jFrame.setMinimumSize(new Dimension((int)(0.67 * Toolkit.getDefaultToolkit().getScreenSize().getWidth()),
                (int)(offset* Toolkit.getDefaultToolkit().getScreenSize().getHeight())));
        jFrame.setMaximumSize(new Dimension((int)(0.67 * Toolkit.getDefaultToolkit().getScreenSize().getWidth()),
                (int)(offset* Toolkit.getDefaultToolkit().getScreenSize().getHeight())));

        TradingView tradingView = new TradingView();

        tradingView.backTestController = new BackTestController(tradingView.list1, tradingView.timeFrames, tradingView.list2);

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

    private void createUIComponents() throws IOException {
        ImageIcon loading = new ImageIcon("images/ajax-loader.gif");
        loaderLabel = new JLabel(loading);
        loaderLabel.setVisible(false);

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
