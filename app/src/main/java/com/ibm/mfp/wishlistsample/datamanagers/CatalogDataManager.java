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
package com.ibm.mfp.wishlistsample.datamanagers;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.ibm.mfp.wishlistsample.Constants;
import com.ibm.mfp.wishlistsample.models.Item;
import com.worklight.common.Logger;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class CatalogDataManager implements Constants {

    private static CatalogDataManager catalogDataManager = null;
    LoadToast toast;
    Context context;

    public static CatalogDataManager getInstance(Context context) {
        if (catalogDataManager == null) {
            catalogDataManager = new CatalogDataManager(context);
        }
        return catalogDataManager;
    }

    private CatalogDataManager() {

    }

    private CatalogDataManager(Context context) {
        this.context = context;
        toast = new LoadToast(context);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public ArrayList<Item> getCatalogData() {
        final ArrayList<Item> itemArrayList = new ArrayList<Item>();
        String adapterName = "CatalogAdapter";
        String procedureName = "getCatalog";

        WLResourceRequest resourceRequest = null;
        try {
            resourceRequest = new WLResourceRequest(
                    new URI("adapters/" + adapterName + "/" + procedureName), WLResourceRequest.GET);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (resourceRequest != null) {
            toast.setText("Fetching Catalog");
            toast.setTranslationY(400);
            toast.show();
            resourceRequest.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    showToast(true);
                    Logger.getInstance("CatalogDataManager").debug("Success" + wlResponse.getResponseJSON());
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(wlResponse.getResponseJSON()));
                        JSONArray responseArr = new JSONArray(String.valueOf(
                                jsonObject.get("getAllProductsDetailsReturn")));


                        for (int i = 0; i < responseArr.length(); i++) {
                            JSONObject responseItem = responseArr.getJSONObject(i);
                            itemArrayList.add(new Item(
                                    responseItem.getString(TITLE),
                                    responseItem.getString(STORE),
                                    responseItem.getInt(PRICE),
                                    "https://dl.dropboxusercontent.com/u/97674776" +
                                            responseItem.getString(PHOTO),
                                    responseItem.getString(PRODUCT_ID)
                            ));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    EventBus.getDefault().post(itemArrayList);
                    Logger.getInstance("CatalogDataManager").debug("posted itemarraylist from catalog data manager");
                    EventBus.getDefault().post("loadedItems");
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Logger.getInstance("CatalogDataManager").debug("Failure" +
                            wlFailResponse.getResponseText());
                    showToast(false);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Could not connect to MFP server", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        }

        return null;
    }

    private void showToast(final Boolean isSuccess) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSuccess)
                    toast.success();
                else
                    toast.error();
            }
        });
    }

}
