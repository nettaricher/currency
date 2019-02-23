import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;

import java.awt.event.*;

import java.net.*;

public class GUI {

        private JFrame frame;
        private JButton btSend;
        private JTextField tf1, tf2;
        private JComboBox currency1, currency2;
        private String[] val = { "USD", "GBP", "JPY", "EUR", "AUD",
                                        "CAD", "DKK","NOK", "ZAR",
                                        "SEK", "CHF", "JOD", "LBP", "EGP"};
        public GUI() {
            //creating GUI components
            frame = new JFrame("Final Project");
            frame.setLayout(new BorderLayout());
            tf1 = new JTextField(10);
            btSend = new JButton("send!");
            currency1 = new JComboBox (val);
            currency1.setSelectedIndex(0);
            currency2 = new JComboBox (val);
            currency2.setSelectedIndex(0);

            frame.add(tf1);
            frame.add(currency1);
            frame.add(btSend);

            Container container = frame.getContentPane();
            container.setLayout(new BorderLayout());
            container.add("Center", currency2);



//            //handling frame closing event
//            frame.addWindowListener(new WindowAdapter() {
//                                        public void windowClosing(WindowEvent event) {
//                                            frame.setVisible(false);
//                                            frame.dispose();
//                                            //connection.Stop();
//                                            //connection.removeConsumer(null);
//                                            System.exit(0);
//                                        }
//                                    }
//            );

        }


        public void go() {
            frame.setSize(400, 300);
            frame.setVisible(true);
        }

        public static void main(String args[]) {
            xmlThread t = new xmlThread();
            Thread t1 = new Thread(t);
            t1.start();

            GUI UI = new GUI();
            UI.go();
            /**Client GUI start*/
        }
}
