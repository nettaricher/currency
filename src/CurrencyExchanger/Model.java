package CurrencyExchanger;

import java.util.Map;

public interface Model {
    public Map<CurrencyPair, Double> getExchangeRates();
    public void updateHashMap();
}
