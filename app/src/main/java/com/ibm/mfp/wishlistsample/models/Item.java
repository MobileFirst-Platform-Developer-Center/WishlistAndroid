/**
 * Copyright 2015 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.mfp.wishlistsample.models;

import android.util.Log;

import com.cloudant.toolkit.mapper.DataObject;
import com.cloudant.toolkit.mapper.Metadata;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

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
        this.imgURL = imageUrl;
        this.productId = productId;
    }

    public Item(JsonObject json){
        this.title = json.get("title").toString().replace("\"","");
        this.store = json.get("store").toString().replace("\"", "");
        this.price =Integer.parseInt(String.valueOf(json.get("price")));
        this.imgURL = json.get("image").toString().replace("\"", "");
        this.productId = json.get("productId").toString().replace("\"","");
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
        this.imgURL = imgURL;
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
        return "{title: '"+getTitle()+"', store : '"+getStore()+"', price :"
                +getPrice()+", image :'"+getImgURL()+"', productId :'"+getProductId()+"'}";
    }
}
