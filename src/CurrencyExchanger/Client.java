package CurrencyExchanger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import static javax.swing.JFrame.EXIT_ON_CLOSE;
public class Client extends JPanel{
    private Map<CurrencyPair, Double> exchangeRates = new HashMap<>();

    public Client() {
        super(new FlowLayout(FlowLayout.LEADING));

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
        JButton getRates = new JButton("Rates Table");
        convertCmd.addActionListener(convertAction(amountInput, fromOptions, toOptions, convertText));
        JPanel convert = new JPanel();
        convert.add(convertCmd);
        convert.add(convertText);
        add(convert);

        //Table
        Object rows[][] = new Object[Currency.values().length*Currency.values().length][];
        Object columns[] = { "From", "To","Rate" };
        DefaultTableModel model = new DefaultTableModel(rows, columns);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        getRates.addActionListener(showAllRates(model));
        convert.add(getRates);
    }
    private void updateRates(){
        try {
            FileInputStream fileIn = new FileInputStream("rates.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            exchangeRates = (HashMap<CurrencyPair, Double>) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("class not found");
            c.printStackTrace();
            return;
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
                updateRates();
                Double rate = exchangeRates.get(currencyPair);
                Double amount = Double.parseDouble(amountInputText);
                return amount*rate;
            }
        };
    }
    private ActionListener showAllRates(DefaultTableModel model) {

        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            //TODO: show all rates table.
                Iterator it = exchangeRates.entrySet().iterator();
                int i=0;
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    model.setValueAt(((CurrencyPair)pair.getKey()).getFrom(),i,0);
                    model.setValueAt(((CurrencyPair)pair.getKey()).getTo(),i,1);
                    BigDecimal val = new BigDecimal((Double)pair.getValue());
                    val = val.setScale(2, RoundingMode.CEILING);
                    model.setValueAt(val,i++,2);
                    it.remove(); // avoids a ConcurrentModificationException
                }

            }
        };
    }

    public static void main(String[] args) {
        xmlParser Rates = new xmlParser();
        Rates.run();
        System.out.println(Rates.getDate());
        JFrame frame = new JFrame();
        frame.getContentPane().add(new Client());
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
                System.out.println(Rates.getDate());
            }
        };
        new javax.swing.Timer(delay, taskPerformer).start();
        //
    }

}
