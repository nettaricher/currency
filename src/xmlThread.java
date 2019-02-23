import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.Writer;

enum currency {
    USD(0), GBP(1),JPY(2), EUR(3),AUD(4),CAD(5),DKK(6),NOK(7),
    ZAR(8),SEK(9),CHF(10),JOD(11), LBP(12), EGP(13);

    private final int num;
    currency(int num) { this.num = num; }
    public int getValue() { return num; }
}

public class xmlThread implements Runnable{

    @Override
    public void run() {
        InputStream is = null;
        HttpURLConnection con = null;

        try{

            FileWriter fileWriter = new FileWriter("C:\\Users\\Netta Richer\\Desktop\\file.txt");


            URL url = new URL("https://www.boi.org.il/currency.xml");
            con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            is = con.getInputStream();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            NodeList list = doc.getElementsByTagName("NAME");
            NodeList list2 = doc.getElementsByTagName("RATE");
            int length = list.getLength();
            for (int i=0; i<length; i++)
            {
                fileWriter.write(list.item(i).getFirstChild().getNodeValue()+ " - " +
                        list2.item(i).getFirstChild().getNodeValue() + String.format("%n"));
                ;
//                System.out.println("\n\n"+
//                        list.item(i).getFirstChild().getNodeValue()+ " - " +
//                        list2.item(i).getFirstChild().getNodeValue());
            }
            fileWriter.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (org.xml.sax.SAXException e)
        {
            e.printStackTrace();
        }
        catch (javax.xml.parsers.ParserConfigurationException e)
        {
            e.printStackTrace();
        }
    }
}
