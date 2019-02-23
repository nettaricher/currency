import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class Main extends JPanel {
    enum Currency {
        USD("United States Dollar"),
        GBR("Great Britain Pound"),
        AUD("Australian Dollar"),
        EUR("Euro");

        private String description;

        Currency(String description) {
            this.description = description;
        }

        @Override public String toString() {
            return this.name() + " - " + this.description;
        }
    }

    class CurrencyPair {
        private final Currency from;
        private final Currency to;

        public CurrencyPair(Currency from, Currency to) {
            this.from = from;
            this.to = to;
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CurrencyPair that = (CurrencyPair) o;
            if (from != that.from) return false;
            return to == that.to;
        }

        @Override public int hashCode() {
            int result = from.hashCode();
            result = 31 * result + to.hashCode();
            return result;
        }
    }

    private final Map<CurrencyPair, BigDecimal> exchangeRates = new HashMap<CurrencyPair, BigDecimal>() {{
        put(new CurrencyPair(Main.Currency.USD, Main.Currency.USD), BigDecimal.valueOf(1));
        put(new CurrencyPair(Main.Currency.AUD, Main.Currency.AUD), BigDecimal.valueOf(1));
        put(new CurrencyPair(Main.Currency.EUR, Main.Currency.EUR), BigDecimal.valueOf(1));
        put(new CurrencyPair(Main.Currency.GBR, Main.Currency.GBR), BigDecimal.valueOf(1));

        put(new CurrencyPair(Main.Currency.USD, Main.Currency.GBR), BigDecimal.valueOf(0.75));
        put(new CurrencyPair(Main.Currency.USD, Main.Currency.AUD), BigDecimal.valueOf(1.33));
        put(new CurrencyPair(Main.Currency.USD, Main.Currency.EUR), BigDecimal.valueOf(0.89));

        put(new CurrencyPair(Main.Currency.EUR, Main.Currency.USD), BigDecimal.valueOf(1.12));
        put(new CurrencyPair(Main.Currency.EUR, Main.Currency.AUD), BigDecimal.valueOf(1.49));
        put(new CurrencyPair(Main.Currency.EUR, Main.Currency.GBR), BigDecimal.valueOf(0.85));

        put(new CurrencyPair(Main.Currency.AUD, Main.Currency.USD), BigDecimal.valueOf(0.74));
        put(new CurrencyPair(Main.Currency.AUD, Main.Currency.EUR), BigDecimal.valueOf(0.67));
        put(new CurrencyPair(Main.Currency.AUD, Main.Currency.GBR), BigDecimal.valueOf(0.57));

        put(new CurrencyPair(Main.Currency.GBR, Main.Currency.USD), BigDecimal.valueOf(1.33));
        put(new CurrencyPair(Main.Currency.GBR, Main.Currency.EUR), BigDecimal.valueOf(1.18));
        put(new CurrencyPair(Main.Currency.GBR, Main.Currency.AUD), BigDecimal.valueOf(1.76));

    }};

    public Main() {
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
        convertCmd.addActionListener(convertAction(amountInput, fromOptions, toOptions, convertText));
        JPanel convert = new JPanel();
        convert.add(convertCmd);
        convert.add(convertText);
        add(convert);
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
                BigDecimal conversion = convertCurrency(amountInputText);
                convertText.setText(NumberFormat
                        .getCurrencyInstance(Locale.US)
                        .format(conversion));
            }

            private BigDecimal convertCurrency(String amountInputText) {
                // TODO: Needs proper rounding and precision setting
                CurrencyPair currencyPair = new CurrencyPair(
                        (Currency) fromOptions.getSelectedItem(),
                        (Currency) toOptions.getSelectedItem());
                BigDecimal rate = exchangeRates.get(currencyPair);
                BigDecimal amount = new BigDecimal(amountInputText);
                return amount.multiply(rate);
            }
        };
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new Main());
        frame.setTitle("Currency Thing");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


}