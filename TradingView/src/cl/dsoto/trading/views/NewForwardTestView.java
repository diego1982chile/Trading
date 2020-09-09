package cl.dsoto.trading.views;

import cl.dsoto.trading.clients.ServiceLocator;
import cl.dsoto.trading.components.ForwardTestManager;
import cl.dsoto.trading.controllers.NewForwardTestController;
import cl.dsoto.trading.model.ForwardTest;
import cl.dsoto.trading.model.Period;
import org.ta4j.core.TimeSeries;
import ta4jexamples.loaders.CsvTicksLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class NewForwardTestView extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JTextField textField3;
    private JTextField textField4;
    private JButton selectButton;
    private JTextField textField2;

    private ForwardTestManager forwardTestManager = (ForwardTestManager) ServiceLocator.getInstance().getService(ForwardTestManager.class);
    NewForwardTestController newForwardTestController;

    Period selected;
    ForwardTest forwardTest;

    public NewForwardTestView(Component parent, Period period) {
        super(SwingUtilities.getWindowAncestor(parent), "Nuevo Test Forward", ModalityType.APPLICATION_MODAL);
        this.setSize(400, 220);
        this.setLocationRelativeTo(null);
        this.setMinimumSize(new Dimension(400, 220));

        this.selected = period;

        textField1.setText(selected.getName());

        setContentPane(contentPane);
        setModal(true);
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
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.showOpenDialog(contentPane);

                File file = fc.getSelectedFile();
                String name = file.getName();

                TimeSeries timeSeries = CsvTicksLoader.load(file);

                try {
                    forwardTest = forwardTestManager.createFromSeries(timeSeries);
                    forwardTest.setPeriod(selected);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                textField2.setText(file.getName());
            }
        });
        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    forwardTestManager.persist(forwardTest);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
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
        NewForwardTestView dialog = new NewForwardTestView();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
        */
    }
}
