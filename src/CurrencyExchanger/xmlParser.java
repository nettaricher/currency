package CurrencyExchanger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class xmlParser implements Runnable{
    public static final String XML_PATH = "/home/sagig/Documents/shenkar/third year/Java/0 - Homework/2-Currency/currency/src/CurrencyExchanger/currency.xml";
    private final Map<CurrencyPair, Double> exchangeRates = new HashMap<CurrencyPair, Double>();
    private Date date;
    public Date getDate() {
        return date;
    }

    @Override
    public void run() {
        InputStream is = null;
        HttpURLConnection con = null;
        //NodeList Code;
        NodeList Rate = null;
        URL url;
        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document doc = null;
            try {
                url = new URL("https://www.boi.org.il/currency.xml");
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                is = con.getInputStream();
                factory = DocumentBuilderFactory.newDefaultInstance();
                builder = factory.newDocumentBuilder();
                doc = builder.parse(is);
                //Code = doc.getElementsByTagName("CURRENCYCODE");
            } catch (java.net.MalformedURLException e) {
                //e.printStackTrace();
            } catch (java.io.IOException e) {
                //If could not GET xml file, open it locally.
                try {
                    File fXmlFile = new File(XML_PATH);
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    doc = dBuilder.parse(fXmlFile);
                }catch(javax.xml.parsers.ParserConfigurationException ex){
                    ex.printStackTrace();
                }
                catch (org.xml.sax.SAXException ex){ ex.printStackTrace();}
                catch (IOException ex){ ex.printStackTrace();}
            } catch (javax.xml.parsers.ParserConfigurationException e) {
              //  e.printStackTrace();
            } catch (org.xml.sax.SAXException e) {
              //  e.printStackTrace();
            }
            Rate = doc.getElementsByTagName("RATE");
            int i = 0, j;
            for (Currency from : Currency.values()) {
                Double toShekels = Double.parseDouble(Rate.item(i).getFirstChild().getNodeValue());
                j = 0;
                for (Currency to : Currency.values()) {
                    Double toCurr = Double.parseDouble(Rate.item(j).getFirstChild().getNodeValue());
                    if (to == from) {
                        exchangeRates.put(new CurrencyPair(from, to), 1.0);
                    } else {
                        Double newRate = toShekels / toCurr;
                        if (from == Currency.JPY){
                            newRate /= 100;
                        }
                        else if (from == Currency.LBP){
                            newRate /= 10;
                        }
                        else if (to == Currency.JPY){
                            newRate *= 100;
                        }
                        else if (to == Currency.LBP){
                            newRate *= 10;
                        }
                        exchangeRates.put(new CurrencyPair(from, to), newRate);
                    }
                    ++j;
                }
                ++i;
            }
            //Write to serialized file rates.ser
            try {
                FileOutputStream fileOut = new FileOutputStream("rates.ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(exchangeRates);
                out.close();
                fileOut.close();
                System.out.println("Serialized data is updated in rates.ser");
            } catch (IOException e) {
                e.printStackTrace();
            }
            date = Calendar.getInstance().getTime();

    }
}
