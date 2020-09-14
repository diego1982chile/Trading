package cl.dsoto.trading.views;

import cl.dsoto.trading.controllers.ForwardTestController;
import cl.dsoto.trading.model.ForwardTest;
import cl.dsoto.trading.model.Period;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ForwardTestView extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField6;
    private JTextField textField7;

    ForwardTest selected;

    ForwardTestController forwardTestController;

    public ForwardTestView(Component parent, ForwardTest selected) {
        super(SwingUtilities.getWindowAncestor(parent), "Forward Test", ModalityType.APPLICATION_MODAL);
        this.setSize(500, 500);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        this.setLocationRelativeTo(null);
        this.setMinimumSize(new Dimension(500, 500));

        this.selected = selected;

        try {
            forwardTestController = new ForwardTestController(selected);

            forwardTestController.setStart(textField1);
            forwardTestController.setEnd(textField2);
            forwardTestController.setTradesView(textField3);
            forwardTestController.setProfitableTradesView(textField4);
            forwardTestController.setRewardRiskView(textField5);
            forwardTestController.setVsBuyAndHoldView(textField6);
            forwardTestController.setCashFlowView(textField7);

            JPanel jTabbedPane = (JPanel) contentPane.getComponents()[1];
            JPanel jPanel = (JPanel) jTabbedPane.getComponents()[1];

            forwardTestController.setPlotView(jPanel);

            forwardTestController.computeResults(200);


        } catch (Exception e) {
            e.printStackTrace();
        }

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        /*
        ForwardTestView dialog = new ForwardTestView();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
        */
    }
}
