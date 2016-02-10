/*
* @author: Max Wootton
 */
package dealRetriever;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

public class HUKDRetriever {

	public ArrayList<JSONObject> returnedDeals = new ArrayList<>();
	public ArrayList<HUKD> dealObjects = new ArrayList<>();
	public ArrayList<String> jsonDeals = new ArrayList<>();

	//initiates the data retrieval - sets up connection to HUKD API
	public static String startRetrieval() throws JSONException {
		System.out.println("[INFO] New API Call started");
		String jsonResponse = "Error - JSON unable to be retrieved";
		try {
			HUKDRetriever test = new HUKDRetriever();
			String rawJson = "";
			URL url = new URL(
					"http://api.hotukdeals.com/rest_api/v2/?key=748f05e164a1bdf72381b435b3f86174&merchant=Argos&output=json&results_per_page=10");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()),"UTF-8"));
			String output;
			// System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				rawJson = rawJson + output;
			}
			test.buildArray(rawJson);
			jsonResponse = test.printMainArray();

			conn.disconnect();

		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}
		return jsonResponse;
	}
	
	//takes the raw JSON string and builds a JSONArray
	public void buildArray(String rawJSON) throws JSONException, IOException {
		JSONObject jsonObj = new JSONObject(rawJSON);
		JSONObject dealsJsonObj = jsonObj.getJSONObject("deals");
		JSONArray jsonArray = dealsJsonObj.getJSONArray("items");
		array2Objects(jsonArray);
		buildMainArray();
	}
	//Pulls apart the JSON array and seperates it into JSON Objects
	public void array2Objects(JSONArray jsonArray) {
		try {
			int count = jsonArray.length(); // get totalCount of all jsonObjects
			for (int i = 0; i < count; i++) { // iterate through jsonArray
				JSONObject jsonObject = jsonArray.getJSONObject(i); // get
																	// jsonObject
																	// @ i
																	// position
				// System.out.println("\njsonObject " + i + ": " + jsonObject);
				returnedDeals.add(jsonObject);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	//Takes the JSON Objects and maps them to pure Java objects
	public void buildMainArray() throws IOException {
		try {
			int count = returnedDeals.size(); // get totalCount of all
												// jsonObjects in the arraylist
			for (int i = 0; i < count; i++) { // iterate through jsonArray
				String title, price, priceDifference, urlDeal, urlProduct, description, imgURL, temperature, ean,
						amazonPrice, amazonUrl;
				JSONObject objTemp = returnedDeals.get(i);
				title = objTemp.getString("title");
				price = "£" + objTemp.optString("price"); // gets the price of
															// the temp object
															// and assigns it to
															// price param
				urlDeal = objTemp.getString("deal_link");
				imgURL = objTemp.getString("deal_image");
				urlProduct = getProductURL(imgURL);
				description = objTemp.getString("description");
				temperature = objTemp.getInt("temperature") + "°";
				ean = eanRetriever(urlProduct);
				String amzResp = AmazonPriceGrabber.getRequestUrl(ean);
				amazonPrice = AmazonPriceGrabber.retrieveAmazonPrice(amzResp); // response
																				// from
																				// getPrice
																				// method
				amazonUrl = AmazonPriceGrabber.retrieveProductUrl(amzResp);
				priceDifference = comparePrices(price, amazonPrice);
				// printing deal for testing purposes
				// System.out.println("\n----New Product----\nPrice: " + price);
				// System.out.println("URL (HUKD): " + urlDeal);
				// System.out.println("URL (Product): " + urlProduct);
				// System.out.println("IMG: " + imgURL);
				// System.out.println("Description: " + description);
				// System.out.println("Temperature: " + temperature);
				// System.out.println("EAN: " + ean);
				// System.out.println("Amazon Price: " + amazonPrice);
				// System.out.println("Amazon Url: " + amazonUrl);
				// System.out.println(priceDifference);
				HUKD temp = new HUKD(title, urlDeal, urlProduct, imgURL, description, temperature, ean, price,
						amazonPrice, priceDifference, amazonUrl);
				dealObjects.add(temp);
				// temp.returnString();
				jsonDeals.add(temp.returnJson());
				// printMainArray();
			}
		} catch (JSONException e) {
		} catch (MalformedURLException | SAXException ex) {
			Logger.getLogger(HUKDRetriever.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	//build the URL for the argos page
	public String getProductURL(String img) {
		String hukdRedirect = "http://www.hotukdeals.com/visit?m=5&q=";
		String imgUrl = img;
		String hukdID = "";
		hukdID = imgUrl.substring(10);
		if (imgUrl.length() > 0) {
			int lastSlash = imgUrl.lastIndexOf("/");
			int endUnderscore = imgUrl.lastIndexOf("_");
			if (lastSlash != -1) {
				hukdID = imgUrl.substring(lastSlash + 1, endUnderscore); // not
																			// forgot
																			// to
																			// put
																			// check
																			// if(endIndex
																			// !=
																			// -1)
			}
		}
		hukdRedirect = hukdRedirect + hukdID;
		return hukdRedirect;
	}
	
	//takes the argos URL and extracts the EAN in the argos webpage
	public String eanRetriever(String url) throws MalformedURLException, IOException, SAXException {
		// retrieving the argos product page for the HUKD item
		URL argosURL = new URL(url);
		String fullHTML = "";
		String ean = "";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(argosURL.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				// System.out.println(inputLine);
				fullHTML = fullHTML + inputLine;
			}
			in.close();
			// searching through the argos webpage. Slightly inefficient since
			// we have to search through the entire html file. Will aim to
			// optimise later.
			int eanIndex = fullHTML.indexOf("EAN: ");
			if (eanIndex != -1) {
				ean = fullHTML.substring(fullHTML.indexOf("EAN: ") + 5);
				ean = ean.substring(0, ean.indexOf("."));
			} else {
				ean = "EAN not found";
			}
		} // In any exception's case, we're not going to be able to find EAN.
			// So....
		catch (Exception e) {
			ean = "EAN not found";
		}
		// Sense checking the EAN - is it in the right format? (i.e. 13
		// characters with trailing zero?)
		if (ean.length() == 12) {
			ean = checkSum(ean) + ean;
		}
		return ean;

	}

	// @param code = ean code passed from eanRetriever method
	public int checkSum(String code) {
		// takes the argos EAN's without check digits and adds them.
		int val = 0;
		for (int i = 0; i < code.length(); i++) {
			val += ((int) Integer.parseInt(code.charAt(i) + "")) * ((i % 2 == 0) ? 1 : 3);
		}

		int checksum_digit = 10 - (val % 10);
		if (checksum_digit == 10) {
			checksum_digit = 0; // check digits of value 10 are impossible,
								// resets to 0. Only case possible if it was 0
								// to begin with (10 - 0 = 0)
		}
		return checksum_digit;
	}
	//takes the argos price and amazon price and compares them
	public String comparePrices(String argosPrice, String amazonPrice) {
		String response = "";
		argosPrice = argosPrice.replaceAll("[^a-zA-Z0-9]", "");
		amazonPrice = amazonPrice.replaceAll("[^a-zA-Z0-9]", "");
		int argosInt = 0;
		int amazonInt = 0;
		// first round of if's to determine value and get numerics
		if (argosPrice.equals("NA") || argosPrice.equals("")) { // would be £N/A
																// if it hadn't
																// been stripped
																// out!
			response = "Price Comparison not available - No Argos price available";
		} else if (amazonPrice.equals("NA") || amazonPrice.equals("")) {
			response = "Price Comparison not available - No Amazon price available";
		} else {
			argosInt = Integer.parseInt(argosPrice);
			amazonInt = Integer.parseInt(amazonPrice);
			if (amazonInt > argosInt) {
				float priceDifference = ((float) amazonInt - (float) argosInt) / 100;
				String stringDif = "£" + String.format("%.2f", priceDifference); // round
																					// to
																					// 2dp
				response = "Argos is cheaper than Amazon by " + stringDif;
			} else if (argosInt > amazonInt) {
				float priceDifference = ((float) amazonInt - (float) argosInt) / 100;
				String stringDif = "£" + String.format("%.2f", priceDifference);
				response = "Amazon is cheaper than Argos by " + stringDif;
			} else {
				response = "Amazon and Argos are the same price";
			}
		}
		return response;

	}
	//generate a JSON string for all objects
	public String printMainArray() {
		// printing our array of json objects for testing purposes
		String json = "[";
		for (int n = 0; n < jsonDeals.size(); n++) {
			if (n != 0){
				json += ",";
			}
			json += jsonDeals.get(n);
		}
		json = (json + "]"); // hard coding the array name and encapsulating in json object
		json = json.replaceAll("[Â]", ""); //gets out invalid chars
		System.out.println(json);
		return json;
	}
}
