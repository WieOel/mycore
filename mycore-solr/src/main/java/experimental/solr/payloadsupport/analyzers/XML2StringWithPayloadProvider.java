/**
 * 
 */
package experimental.solr.payloadsupport.analyzers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

/**
 * @author shermann
 *
 */
public class XML2StringWithPayloadProvider {

    private Document xmlSource;

    private List<Element> tokenList;

    /**
     * @param source
     * @throws FileNotFoundException
     */
    public XML2StringWithPayloadProvider(File source) throws FileNotFoundException {
        this(new FileInputStream(source));
    }

    /**
     * @param source
     */
    public XML2StringWithPayloadProvider(InputStream source) {
        try {
            createDocument(source);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param inputStream the InputStream to build the document from, stream is closed after method call 
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private void createDocument(InputStream inputStream) throws IOException {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            xmlSource = saxBuilder.build(inputStream);
            XPath xp = XPath.newInstance("//Word");
            tokenList = xp.selectNodes(xmlSource);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            inputStream.close();
        }
    }

    /**
     * Method returns a {@link Document} that contains all the words with their payloads.
     * 
     * @return a {@link Document}
     */
    public Document getWordPayloadDocument() {
        Element payloads = new Element("payloads");
        Document payloadDocument = new Document(payloads);

        Iterator<Element> iterator = tokenList.iterator();

        while (iterator.hasNext()) {
            Element w = iterator.next();
            String tokenWithPayload = this.getTokenWithPayload(w);
            payloads.addContent(new Element("payload").setText(tokenWithPayload));
        }

        return payloadDocument;
    }

    /**
     * Method returns a String where each word is followed a payload information (separated by a |)
     * 
     * @return {@link String}
     * @throws IOException
     */
    public String getFlatDocument() throws IOException {
        StringBuilder b = new StringBuilder();
        Iterator<Element> iterator = tokenList.iterator();

        while (iterator.hasNext()) {
            Element w = iterator.next();
            String tokenWithPayload = this.getTokenWithPayload(w);
            b.append(tokenWithPayload);
        }

        return b.toString().trim();
    }

    private String getTokenWithPayload(Element w) {
        String text = w.getText().replaceAll("\"", "").replaceAll("'", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll(",", "")
                .replaceAll("[0-9]*", "").replaceAll("\\.", "");

        if (text.trim().length() == 0) {
            return "";
        }

        String x = w.getAttributeValue("x");
        String y = w.getAttributeValue("y");
        String data = "x" + x + "y" + y;
        return (text + "|" + data + "\n");
    }
}