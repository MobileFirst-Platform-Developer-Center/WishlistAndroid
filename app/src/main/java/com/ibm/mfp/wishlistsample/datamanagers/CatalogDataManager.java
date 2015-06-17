package com.ibm.mfp.wishlistsample.datamanagers;

import android.app.Activity;
import android.content.Context;

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

/**
 * Created by chethan on 14/05/15.
 */
public class CatalogDataManager implements Constants{

    private static CatalogDataManager catalogDataManager = null;
    LoadToast toast;
    Context context;

    public static CatalogDataManager getInstance(Context context){
        if (catalogDataManager==null){
            catalogDataManager = new CatalogDataManager(context);
        }
            return catalogDataManager;
    }

    private CatalogDataManager(){

    }

    private CatalogDataManager(Context context){
        this.context = context;
        toast = new LoadToast(context);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public ArrayList<Item> getCatalogData(){
        final ArrayList<Item> itemArrayList = new ArrayList<Item>();
        String adapterName = "CatalogAdapter";
        String procedureName = "getCatalog";

        WLResourceRequest resourceRequest = null;
        try {
            resourceRequest = new WLResourceRequest(
                    new URI("adapters/"+adapterName+"/"+procedureName),WLResourceRequest.GET);
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
                        JSONArray responseArr = new JSONArray(String.valueOf(jsonObject.getJSONObject("Envelope")
                                .getJSONObject("Body").get("getAllProductsDetailsReturn")));


                       for(int i=0;i<responseArr.length();i++){
                           JSONObject responseItem = responseArr.getJSONObject(i);
                            itemArrayList.add(new Item(
                                    responseItem.getString(TITLE),
                                    responseItem.getString(STORE),
                                    responseItem.getInt(PRICE),
                                    "http://boxstore-catalog.mybluemix.net/MFPSampleWebService"+responseItem.getString(PHOTO),
                                    responseItem.getString(PRODUCT_ID)
                            ));
                       }
                } catch (JSONException e) {
                        e.printStackTrace();
                    }

                EventBus.getDefault().post(itemArrayList);
                    Logger.getInstance("CatalogDataManager").debug("posted itemarraylist from catalog data manager");
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Logger.getInstance("CatalogDataManager").debug("Failure" + wlFailResponse.getResponseText());
                    showToast(false);
                }
            });
        }

        return null;
    }

    private void showToast(final Boolean isSuccess){
        ((Activity)context).runOnUiThread(new Runnable() {
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
