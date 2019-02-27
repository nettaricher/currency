package CurrencyExchanger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import static javax.swing.JFrame.EXIT_ON_CLOSE;
public class Client extends JPanel{
    private static final String XML_PATH = "gui.xml";
    private Map<CurrencyPair, Double> exchangeRates;
    public Client() {
        super(new FlowLayout(FlowLayout.LEADING));
        exchangeRates = new HashMap<>();
        // Amount
        JTextField amountInput = new JTextField(20);
        JPanel amount = new JPanel();
        amount.add(amountInput);
        amount.setBorder(BorderFactory.createTitledBorder("Enter Ammount"));
        add(amount, BorderLayout.CENTER);

        // From
        JPanel from = new JPanel();
        JComboBox fromOptions = new JComboBox(Currency.values());
        from.add(fromOptions);
        from.setBorder(BorderFactory.createTitledBorder("Select currency"));
        add(from, BorderLayout.CENTER);

        // To
        JComboBox toOptions = new JComboBox(Currency.values());
        JPanel to = new JPanel();
        to.add(toOptions);
        to.setBorder(BorderFactory.createTitledBorder("Convert to"));
        add(to, BorderLayout.CENTER);

        // Convert Action
        JLabel convertText = new JLabel();
        JButton convertCmd = new JButton("Convert");
        convertCmd.addActionListener(convertAction(amountInput, fromOptions, toOptions, convertText));
        JPanel convert = new JPanel();
        convert.add(convertCmd);
        convert.add(convertText);
        JButton getRates = new JButton("Rates Table");
        add(getRates);
        add(convert);

        //Table
        Object rows[][] = new Object[Currency.values().length*Currency.values().length][];
        Object columns[] = { "From", "To","Rate" };
        DefaultTableModel model = new DefaultTableModel(rows, columns);
        getRates.addActionListener(showAllRates(model));
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }
    private void updateHashMap(){
        InputStream is          = null;
        HttpURLConnection con   = null;
        NodeList LAST_UPDATE    = null;
        NodeList NAME           = null;
        NodeList UNIT           = null;
        NodeList CURRENCYCODE   = null;
        NodeList COUNTRY        = null;
        NodeList RATE           = null;
        NodeList CODE           = null;
        NodeList CHANGE         = null;
        URL url;
        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document doc = null;

        try {
            factory = DocumentBuilderFactory.newDefaultInstance();
            builder = factory.newDocumentBuilder();
            doc = builder.parse(new InputSource(XML_PATH));
//            NAME            = doc.getElementsByTagName("NAME");
//            UNIT            = doc.getElementsByTagName("UNIT");
//            CURRENCYCODE    = doc.getElementsByTagName("CURRENCYCODE");
//            COUNTRY         = doc.getElementsByTagName("COUNTRY");
            RATE            = doc.getElementsByTagName("RATE");
//            CHANGE          = doc.getElementsByTagName("CHANGE");
            LAST_UPDATE     = doc.getElementsByTagName("LAST_UPDATE");
            System.out.println(LAST_UPDATE.item(0).getFirstChild().getNodeValue());
        } catch (java.net.MalformedURLException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            e.printStackTrace();
        } catch (org.xml.sax.SAXException e) {
            e.printStackTrace();
        }

        int i = 0, j;
        for (Currency from : Currency.values()) {
            Double toShekels = Double.parseDouble(RATE.item(i).getFirstChild().getNodeValue());
            j = 0 ;
            for (Currency to : Currency.values()) {
                Double toCurr = Double.parseDouble(RATE.item(j).getFirstChild().getNodeValue());
                Double newRate = toShekels / toCurr;
                if (from == Currency.JPY) {
                    newRate /= 100;
                } else if (from == Currency.LBP) {
                    newRate /= 10;
                } else if (to == Currency.JPY) {
                    newRate *= 100;
                } else if (to == Currency.LBP) {
                    newRate *= 10;
                }
                exchangeRates.put(new CurrencyPair(from, to), newRate);
                ++j;
            }
            ++i;
        }
    }
    private ActionListener convertAction(
            final JTextField amountInput,
            final JComboBox fromOptions,
            final JComboBox toOptions,
            final JLabel convertText) {

        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO: Needs proper validation
                String amountInputText = amountInput.getText();
                if ("".equals(amountInputText)) { return; }

                // Convert
                Double conversion = convertCurrency(amountInputText);
                convertText.setText(NumberFormat
                        .getCurrencyInstance(Locale.US)
                        .format(conversion));
            }

            private Double convertCurrency(String amountInputText) {
                // TODO: Needs proper rounding and precision setting
                CurrencyPair currencyPair = new CurrencyPair(
                        (Currency) fromOptions.getSelectedItem(),
                        (Currency) toOptions.getSelectedItem());
                Double rate = exchangeRates.get(currencyPair);
                Double amount = Double.parseDouble(amountInputText);
                return amount*rate;
            }
        };

    }
    private ActionListener showAllRates(DefaultTableModel model) {

        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int i = 0;
                for (Currency from : Currency.values()) {
                    for (Currency to : Currency.values()) {
                        BigDecimal val = new BigDecimal(exchangeRates.get(new CurrencyPair(from, to)));
                        val = val.setScale(2, RoundingMode.CEILING);
                        model.setValueAt(from, i, 0);   //Set from currency
                        model.setValueAt(to, i, 1);     //Set to currency
                        model.setValueAt(val, i, 2);    //Set rate value
                        ++i;
                    }
                }

            }
        };
    }

    public static void main(String[] args) {
        //First, get the rates xml file and update hash table
        xmlParser Rates = new xmlParser();
        Rates.run();

        //Create and run GUI
        Client GUI = new Client();    //Send the c'tor and updated hashmap containing all data parsed
        GUI.updateHashMap();            //update map according to fetched data
        JFrame frame = new JFrame();
        frame.getContentPane().add(GUI);
        frame.setTitle("Currency Exchanger");
        frame.setSize(500, 620);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        //Fetch new data intervals
        int delay = 60000; //milliseconds
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Rates.run();
                GUI.updateHashMap();
            }
        };
        new javax.swing.Timer(delay, taskPerformer).start();
        //
    }

}
