package com.ibm.mfp.wishlistsample.datamanagers;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.cloudant.toolkit.IndexField;
import com.cloudant.toolkit.Store;
import com.cloudant.toolkit.mapper.DataObjectMapper;
import com.cloudant.toolkit.query.CloudantQuery;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.imf.data.DataManager;
import com.ibm.mfp.wishlistsample.Utils;
import com.ibm.mfp.wishlistsample.models.Item;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;
import net.steamcrafted.loadtoast.LoadToast;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import bolts.Continuation;
import bolts.Task;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * Created by chethan on 25/05/15.
 */
public class JsonStoreDataManager {

    private static JsonStoreDataManager instance = null;
    private Context context;
    private ArrayList<Item> allItemListFromAdapter = new ArrayList<Item>();
    private ArrayList<Item> allItemListFromLocalStore = new ArrayList<Item>();
    private LoadToast toast;
    private Store localStore;

    String adapterName = "LocalStoreAdapter";
    String procedureName = "localstore/getAllItems";

    private JsonStoreDataManager(Context context){
        this.context = context;
        toast = new LoadToast(context);
    }

    public static JsonStoreDataManager getInstance(Context context){
        if (instance == null) {
            instance =  new JsonStoreDataManager(context);
        }
        return instance;
    }

    public ArrayList<Item> getAllItemListFromAdapter() {
        return allItemListFromAdapter;
    }

    public void setAllItemListFromAdapter(ArrayList<Item> allItemListFromAdapter) {
        this.allItemListFromAdapter = allItemListFromAdapter;
    }

    /**
     * Forces network copy on local discarding any local changes
     * Clears local data and updates localstore with the data from adapter
     * If there is an error, it will retry if there is n/w, else will work on local copy
     * Displays the local store data
     */
    private void syncAdapterDataToLocalStore() {
        try {
            Boolean wasThereAnError = false;
            getLocalListItems();
            //delete all local data
            for (Item localItem : allItemListFromLocalStore){
                Task<String> deleteTask = localStore.delete(localItem);
                deleteTask.waitForCompletion();
                if (deleteTask.isFaulted()) {
                    Timber.d("There was an error while deleting data from local store "
                            + deleteTask.getError().getLocalizedMessage());
                    wasThereAnError = true;
                }
            }

            //push data from adapter into local store
            for(Item remoteData : allItemListFromAdapter){
                Task<Object> addTask = localStore.save(remoteData);
                addTask.waitForCompletion();
                if (addTask.isFaulted()){
                    Timber.d("There was an error while saving data from adapter  to local store"
                        + addTask.getError().getLocalizedMessage());
                    wasThereAnError = true;
                }
            }

            if (wasThereAnError){
                Timber.d("There was an error while trying to clear local data and push adapter data to local store. We will need to retry");
                // if online, retry  else work on local copy
                if(Utils.isOnline(context)){
                    getAllItemsFromAdapter();
                }else{
                    getLocalListItems();
                }
            }else{
                getLocalListItems();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getLocalListItems() {
//        toast.setText("Fetching local list");
//        toast.setTranslationY(400);
//        toast.show();

        if (localStore == null){
            setUpLocalStore();
        }else{
            try {
                Map<String, Object> queryJSON = new HashMap<String, Object>();
                Map<String, Object> selector = new HashMap<String, Object>();
                Map<String, Object> equalityOp = new HashMap<String, Object>();
                equalityOp.put("$eq", "Item");
                selector.put("@datatype", equalityOp);
                queryJSON.put("selector", selector);

                CloudantQuery query = new CloudantQuery(queryJSON);

                Task localFetchTask = localStore.performQuery(query);
                localFetchTask.waitForCompletion();
                localFetchTask.continueWith(new Continuation<List, Object>() {
                    @Override
                    public Object then(Task<List> task) throws Exception {
                        if (task.isFaulted()) {
                            dismissToast(false);
                            Timber.d("An error occurred while retrieving all the items from local store" +
                                    task.getError().getLocalizedMessage());
                            task.getError().printStackTrace();
                        } else {
                            dismissToast(true);
                            allItemListFromLocalStore.clear();
                            List itemsList = task.getResult();
                            for (Object item : itemsList) {
                                if (item instanceof Item) {
                                    Timber.d("The returned object  from List is Item");
                                    ((Item) item).prettyPrint();
                                    allItemListFromLocalStore.add((Item) item);
                                }
                            }
                            Timber.d("Item list sending from jsonstore "+allItemListFromLocalStore.toString());
                            EventBus.getDefault().post(allItemListFromLocalStore);
                        }
                        return null;
                    }
                });
            }catch (InterruptedException ie){
                ie.printStackTrace();
            }
        }
    }

    public void setUpLocalStore(){
        try {
            DataManager.initialize(context, new URL("http:localhost:9080/data"));

            final Task<Store> localstoreTask = DataManager.getInstance().localStore("ItemStore");
            localstoreTask.waitForCompletion();//important

            localstoreTask.continueWith(new Continuation<Store, Object>() {
                @Override
                public Object then(Task<Store> task) throws Exception {
                    if (task.isFaulted()) {
                        Timber.d("An error occurred while trying to set up a local cloudant store"
                                +task.getError().getLocalizedMessage());
                    } else {
                        Timber.d("local store created");
                        localStore = localstoreTask.getResult();
                        localStore.setMapper(new DataObjectMapper());
                        localStore.getMapper().setDataTypeForClassName("Item", Item.class.getCanonicalName());
//                        localStore.deleteIndexWithDataType("Item");
                        List <IndexField> list = new ArrayList<IndexField>();
                        list.add(new IndexField("price"));
                        localStore.createIndexWithDataType("Item", list);

                        if (Utils.isOnline(context)){
                            getAllItemsFromAdapter();
                        }else{
                            getLocalListItems();
                        }
                 }
                    return null;
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void addLocalItem(final Item item){
        toast.setText("Adding item");
        toast.show();
        localStore.save(item).continueWith(new Continuation<Object, Object>() {
            @Override
            public Object then(Task<Object> task) throws Exception {
                if (task.isFaulted()) {
                    dismissToast(false);
                    Timber.d("An error occurred while saving item to local store");
                } else {
                    getLocalListItems();
                    dismissToast(true);
                    Timber.d("After successfully adding item to local wishlist");
                    syncLocalDataToAdapter(item);
                }
                return null;
            }
        });
    }

    public void syncLocalDataToAdapter(Item item){

        toast.setText("Pushing data");
        toast.show();
        WLResourceRequest pushDataToAdapterRequest = null;
        try{
            pushDataToAdapterRequest = new WLResourceRequest(
                    new URI("adapters/"+adapterName+"/"+"localstore/addItem"),WLResourceRequest.PUT );
            Timber.d("item json to be pushed to adapter" + item.getItemJsonAsString());
            pushDataToAdapterRequest.send(item.getItemJsonAsString(),new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    dismissToast(true);
                    Timber.d("successfully pushed local data to adapter");
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    dismissToast(false);
                    Timber.d("An error occurred while pushing the local store data to adapter");
                }
            });
        }catch (URISyntaxException ue){
            ue.printStackTrace();
        }
    }

    private JsonArray getJsonOfLocalData() {
        JsonArray jsonArr = new JsonArray();
        JsonParser parser = new JsonParser();
        for (Item item : allItemListFromLocalStore){
            jsonArr.add(parser.parse(item.getItemJsonAsString()));
        }
        return jsonArr;
    }

    /**
     * Fetch data from adapter. sync it with local store and display from local store
     */
    public void getAllItemsFromAdapter(){
        toast.setText("Fetching wishlist");
        toast.setTranslationY(400);
        toast.show();
        WLResourceRequest getAllItemRequest = null;
        try {
            getAllItemRequest = new WLResourceRequest(
                    new URI("adapters/"+adapterName+"/"+procedureName),WLResourceRequest.GET);

            getAllItemRequest.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    dismissToast(true);
                    allItemListFromAdapter.clear();
                    String response = wlResponse.getResponseText();
                    Timber.d("****Successfully got data from LocalStoreAdapter\n"+response);
                    JsonParser parser = new JsonParser();
                    JsonArray responseArray = parser.parse(response).getAsJsonArray();
                    for(JsonElement obj:responseArray){
                        Timber.d("Item :: "+obj.toString());
                        allItemListFromAdapter.add(new Item(obj.getAsJsonObject()));
                    }
                    syncAdapterDataToLocalStore();

                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    dismissToast(false);
                    Timber.d("****An error occurred while fetching all the items from LocalStore Adapter "
                            +wlFailResponse.getErrorMsg());
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void dismissToast(final Boolean isSuccess){
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
