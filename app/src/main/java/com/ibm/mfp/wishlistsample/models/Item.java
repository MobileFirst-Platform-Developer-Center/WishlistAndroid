package com.ibm.mfp.wishlistsample.models;

import android.util.Log;

import com.cloudant.toolkit.mapper.DataObject;
import com.cloudant.toolkit.mapper.Metadata;

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

    public Item(JSONObject json){
        try {
            this.title = json.getString("title");
            this.store = json.getString("store");
            this.price =json.getInt("price");
            this.imgURL = json.getString("image");
            this.productId = json.getString("productId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getItemJsonObject(){
        String json = "{title: '"+getTitle()+"', store : '"+getStore()+"', price :'"
                +getPrice()+"', image :'"+getImgURL()+"', productId :'"+getProductId()+"'}";

        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
