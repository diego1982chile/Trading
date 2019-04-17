package cl.dsoto.trading.views;

import cl.dsoto.trading.controllers.BackTestController;
import cl.dsoto.trading.controllers.StrategiesController;
import cl.dsoto.trading.model.Period;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class StrategiesView extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTree tree1;
    StrategiesController strategiesController;
    Period selected;

    public StrategiesView(Component parent, Period selected) {
        super(SwingUtilities.getWindowAncestor(parent), "Estrategias", ModalityType.APPLICATION_MODAL);
        this.setSize(400, 400);
        this.selected = selected;

        setContentPane(contentPane);
        //setModal(true);
        getRootPane().setDefaultButton(buttonOK);

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

        strategiesController = new StrategiesController(tree1, selected);

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
        StrategiesView dialog = new StrategiesView();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);*/
    }
}
