/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dealRetriever;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Max Wootton
 */
public class HUKD {
    @SerializedName("title")
    public String title;
    @SerializedName("dealUrl")    
    public String dealUrl;
    @SerializedName("productUrl")    
    public String productUrl;
    @SerializedName("imgUrl")        
    public String imgUrl;
    @SerializedName("description")        
    public String description; 
    @SerializedName("temperature")        
    public String temperature;
    @SerializedName("ean")
    public String ean;
    @SerializedName("price")
    public String price;
    @SerializedName("amazonPrice")
    public String amazonPrice;
    @SerializedName("priceDifference")
    public String priceDifference;
    @SerializedName("amazonUrl")
    public String amazonUrl;

    public HUKD(String title, String dealUrl, String productUrl, String imgUrl, String description, String temperature, String ean, String price, String amazonPrice, String priceDifference, String amazonUrl) {
        this.title = title;
        this.dealUrl = dealUrl;
        this.productUrl = productUrl;
        this.imgUrl = imgUrl;
        this.description = description;
        this.temperature = temperature;
        this.ean = ean;
        this.price = price;
        this.amazonPrice = amazonPrice;
        this.priceDifference = priceDifference;
        this.amazonUrl = amazonUrl;
    }

public String returnJson(){
    Gson gson = new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
    String json = gson.toJson(this);
    return json;
}
}


