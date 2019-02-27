package CurrencyExchanger;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import org.w3c.dom.*;

public class xmlParser implements Runnable, Model{
    private static final String BACKUP = "currency.xml";
    private static final String XML_PATH = "gui.xml";

    private Map<CurrencyPair, Double> exchangeRates;

    public xmlParser() {
        this.exchangeRates = new HashMap<>();
    }

    public Map<CurrencyPair, Double> getExchangeRates() {
        return exchangeRates;
    }

    public void updateHashMap(){
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

    @Override
    public void run() {
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
        //Fetch XML from server
        try {
            url = new URL("https://www.boi.org.il/currency.xml");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            is = con.getInputStream();
            factory = DocumentBuilderFactory.newDefaultInstance();
            builder = factory.newDocumentBuilder();
            doc = builder.parse(is);

        } catch (java.net.MalformedURLException e) {
            //e.printStackTrace();
        } catch (java.io.IOException e) {
//            If could not GET xml file, open it locally.
            try {
                File fXmlFile = new File(BACKUP);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                doc = dBuilder.parse(fXmlFile);
            }catch(javax.xml.parsers.ParserConfigurationException ex){
                ex.printStackTrace();
            }
            catch (org.xml.sax.SAXException ex){ ex.printStackTrace();}
            catch (IOException ex){ ex.printStackTrace();}
        }
        catch (javax.xml.parsers.ParserConfigurationException e) {
          //  e.printStackTrace();
        }
        catch (org.xml.sax.SAXException e) {
          //  e.printStackTrace();
        }
        NAME            = doc.getElementsByTagName("NAME");
        UNIT            = doc.getElementsByTagName("UNIT");
        CURRENCYCODE    = doc.getElementsByTagName("CURRENCYCODE");
        COUNTRY         = doc.getElementsByTagName("COUNTRY");
        RATE            = doc.getElementsByTagName("RATE");
        CHANGE          = doc.getElementsByTagName("CHANGE");
        LAST_UPDATE     = doc.getElementsByTagName("LAST_UPDATE");
        //Create new XML for local storage
        Document dom = null;
        Element e = null;
        Element currencyEle = null;
        String _LAST_UPDATE = null;
        String _NAME = null;
        String _UNIT = null;
        String _CURRENCYCODE = null;
        String _COUNTRY = null;
        String _RATE = null;
        String _CHANGE = null;
        Element rootEle = null;
        //
        // instance of a DocumentBuilderFactory
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // use factory to get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // create instance of DOM
            dom = db.newDocument();
            // create the root element
            rootEle = dom.createElement("CURRENCIES");
            // create data elements and place them under root
            e = dom.createElement("LAST_UPDATE");
            _LAST_UPDATE = LAST_UPDATE.item(0).getFirstChild().getNodeValue();
            e.appendChild(dom.createTextNode(_LAST_UPDATE));
            rootEle.appendChild(e);
        }catch(ParserConfigurationException ex){ex.printStackTrace();}
        //
        //Create all currencies
        int i = 0;
        for (Currency from : Currency.values()) {
            // create node currency
            currencyEle = dom.createElement("CURRENCY");
            rootEle.appendChild(currencyEle);
            //
            // add children to node currency
            e = dom.createElement("NAME");
            _NAME = NAME.item(i).getFirstChild().getNodeValue();
            e.appendChild(dom.createTextNode(_NAME));
            currencyEle.appendChild(e);

            e = dom.createElement("UNIT");
            _UNIT = UNIT.item(i).getFirstChild().getNodeValue();
            e.appendChild(dom.createTextNode(_UNIT));
            currencyEle.appendChild(e);

            e = dom.createElement("CURRENCYCODE");
            _CURRENCYCODE = CURRENCYCODE.item(i).getFirstChild().getNodeValue();
            e.appendChild(dom.createTextNode(_CURRENCYCODE));
            currencyEle.appendChild(e);

            e = dom.createElement("COUNTRY");
            _COUNTRY = COUNTRY.item(i).getFirstChild().getNodeValue();
            e.appendChild(dom.createTextNode(_COUNTRY));
            currencyEle.appendChild(e);

            e = dom.createElement("RATE");
            _RATE = RATE.item(i).getFirstChild().getNodeValue();
            e.appendChild(dom.createTextNode(_RATE));
            currencyEle.appendChild(e);

            e = dom.createElement("CHANGE");
            _CHANGE = CHANGE.item(i).getFirstChild().getNodeValue();
            e.appendChild(dom.createTextNode(_CHANGE));
            currencyEle.appendChild(e);
            ++i;
        }
        dom.appendChild(rootEle);
        // write the content into xml file
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(dom);
            StreamResult result = new StreamResult(new File("gui.xml"));
            transformer.transform(source, result);
            System.out.println("File saved!");
        } catch (TransformerException te) {
            System.out.println(te.getMessage());
        }

    }
}
