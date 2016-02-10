package dealRetriever;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/*
 * This class shows how to make a simple authenticated call to the
 * Amazon Product Advertising API.
 *
 * See the README.html that came with this sample for instructions on
 * configuring and running the sample.
 */

//(as you can see)...bulk of class written by amazon. I've written methods retrieveAmazonPrice and retrieveProductUrl on top to pass back to HUKDRetriever
public class AmazonPriceGrabber {

    /*
     * Your AWS Access Key ID, as taken from the AWS Your Account page.
     */
    private static final String AWS_ACCESS_KEY_ID = "AKIAILN2TPM667MBMJAQ";

    /*
     * Your AWS Secret Key corresponding to the above ID, as taken from the AWS
     * Your Account page.
     */
    private static final String AWS_SECRET_KEY = "IfKbXFLQj3t2MbPBLOv3ZcOY6WXO6nbJXkhhta62";

    /*
     * Use the end-point according to the region you are interested in.
     */
    private static final String ENDPOINT = "webservices.amazon.co.uk";

    private static final String ASSOCIATE_TAG = "githubcomthis-21";
    

    public static String getRequestUrl(String ean) throws org.xml.sax.SAXException, IOException, SAXException {

        /*
         * Set up the signed requests helper.
         */
        SignedRequestsHelper helper = null;

        try {
            helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY, ASSOCIATE_TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String requestUrl = null;
        Map<String, String> params = new HashMap<String, String>();

        params.put("Service", "AWSECommerceService");
        params.put("Operation", "ItemLookup");
        params.put("AWSAccessKeyId", "AKIAILN2TPM667MBMJAQ");
        params.put("AssociateTag", ASSOCIATE_TAG);
        params.put("ItemId", ean);
        params.put("IdType", "EAN");
        params.put("ResponseGroup", "Images,ItemAttributes,OfferFull,Reviews");
        params.put("SearchIndex", "All");
        requestUrl = helper.sign(params);
        return requestUrl;
    }

    //Code duplication!! Further improvements would allow a single method to recieve JSON + XML
    public static String retrieveAmazonPrice(String awsUrl) throws IOException {
        String price = "";
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new URL(awsUrl).openStream());
            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "/ItemLookupResponse/Items/Item/OfferSummary/LowestNewPrice/FormattedPrice";
            price = xPath.compile(expression).evaluate(doc);
            if (price.equals("") || price == null) {
                price = "£N/A";
            }
        } catch (ParserConfigurationException | SAXException | XPathExpressionException ex) {
            Logger.getLogger(AmazonPriceGrabber.class.getName()).log(Level.SEVERE, null, ex);
        }
        return price;
    }

    public static String retrieveProductUrl(String awsUrl) throws IOException {
        String amzUrl = "";
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new URL(awsUrl).openStream());
            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "/ItemLookupResponse/Items/Item/DetailPageURL";
            amzUrl = xPath.compile(expression).evaluate(doc);
            if (amzUrl.equals("") || amzUrl == null) {
                amzUrl = "Not Found";
            }
        } catch (ParserConfigurationException | SAXException | XPathExpressionException ex) {
            Logger.getLogger(AmazonPriceGrabber.class.getName()).log(Level.SEVERE, null, ex);
        }
        return amzUrl;
    }

}
