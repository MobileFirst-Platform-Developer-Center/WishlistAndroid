package com.ibm.mfp.wishlistsample.models;

import android.util.Log;

import com.cloudant.toolkit.mapper.DataObject;
import com.cloudant.toolkit.mapper.Metadata;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chethan on 13/05/15.
 */
public class Item implements DataObject {

    private String title;
    private String store;
    private Integer price;
    private String imgURL;
    private String productId;

    private Metadata metadata;

    public Item(){

    }

    public Item(String title, String store, Integer price, String imageUrl, String productId) {
        this.title = title;
        this.store = store;
        this.price = price;
        this.imgURL = "http://boxstore-catalog.mybluemix.net/MFPSampleWebService"+imageUrl;
        this.productId = productId;
    }

    public Item(JsonObject json){
        this.title = json.get("title").toString();
        this.store = json.get("store").toString();
        this.price =Integer.parseInt(String.valueOf(json.get("price")));
        this.imgURL = json.get("image").toString();
        this.productId = json.get("productId").toString();
    }

    @Override
    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public void prettyPrint(){
        Log.d("Item"," Title : "+getTitle() +" Store : "+ getStore()+" Price : "+getPrice() + " Product ID : "+getProductId() + " Image URL : "+ getImgURL());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL ="http://boxstore-catalog.mybluemix.net/MFPSampleWebService"+ imgURL;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public JSONObject getItemJsonObject(){
        String json = getItemJsonAsString();

        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getItemJsonAsString(){
        return "{title: '"+getTitle()+"', store : '"+getStore()+"', price :'"
                +getPrice()+"', image :'"+getImgURL()+"', productId :'"+getProductId()+"'}";
    }
}
