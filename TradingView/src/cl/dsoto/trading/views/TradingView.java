package cl.dsoto.trading.views;

import cl.dsoto.trading.controllers.BackTestController;
import cl.dsoto.trading.model.Period;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

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
    private JButton button2;

    public BackTestController backTestController;


    public TradingView() {
        list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                backTestController.setSelected((Period)list1.getSelectedValue());
            }
        });
    }

    public static void main(String[] args) throws Exception {

        JFrame jFrame = new JFrame("Trading View");

        double offset=0.9;
        jFrame.setSize(new Dimension((int)(0.75 * Toolkit.getDefaultToolkit().getScreenSize().getWidth()),
                (int)(offset* Toolkit.getDefaultToolkit().getScreenSize().getHeight())));
        jFrame.setMinimumSize(new Dimension((int)(0.75 * Toolkit.getDefaultToolkit().getScreenSize().getWidth()),
                (int)(offset* Toolkit.getDefaultToolkit().getScreenSize().getHeight())));
        jFrame.setMaximumSize(new Dimension((int)(0.75 * Toolkit.getDefaultToolkit().getScreenSize().getWidth()),
                (int)(offset* Toolkit.getDefaultToolkit().getScreenSize().getHeight())));

        TradingView tradingView = new TradingView();

        tradingView.backTestController = new BackTestController(tradingView.list1);

        tradingView.backTestController.setStart(tradingView.textField1);
        tradingView.backTestController.setEnd(tradingView.textField2);
        tradingView.backTestController.setTradesView(tradingView.textField3);
        tradingView.backTestController.setProfitableTradesView(tradingView.textField4);
        tradingView.backTestController.setRewardRiskView(tradingView.textField5);
        tradingView.backTestController.setVsBuyAndHoldView(tradingView.textField6);
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


}
