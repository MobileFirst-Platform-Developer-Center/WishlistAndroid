package com.ibm.mfp.wishlistsample.datamanagers;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.cloudant.sync.datastore.Datastore;
import com.cloudant.toolkit.Store;
import com.cloudant.toolkit.query.CloudantQuery;
import com.cloudant.toolkit.query.Query;
import com.ibm.imf.data.DataManager;
import com.ibm.mfp.wishlistsample.Utils;
import com.ibm.mfp.wishlistsample.models.Item;

import net.steamcrafted.loadtoast.LoadToast;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
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
 * Created by chethan on 19/05/15.
 */
public class WishListDataManager {

    private static WishListDataManager instance = null;
    private Context context = null;
    Store remoteDatastore = null;
    LoadToast toast;
    URL dataProxyURL = null;

    public static WishListDataManager getInstance(Context context){
        if (instance == null){
            instance = new WishListDataManager(context);
        }
        return instance;
    }

    private WishListDataManager(Context context){
        this.context = context;
        toast = new LoadToast(context);
    }

    private WishListDataManager(){
    }

    public void setUpDB(){

            toast.setText("Fetching wishlist");
            toast.setTranslationY(400);
            toast.show();
            dataProxyURL  = Utils.getDataProxyUrl(context);
            Timber.d("Data proxy url :: "+dataProxyURL);


            DataManager.initialize(context, dataProxyURL);
            Task<Store> wishListTask = DataManager.getInstance().remoteStore("wishlist");
            wishListTask.continueWith(new Continuation<Store, Object>() {
                @Override
                public Object then(Task<Store> task) throws Exception {
                    if (task.isFaulted()){
                        showToast(false);
                        Timber.d("An error occurred while creating a remote store "+task.getError().getLocalizedMessage());
                    }else{
                        Store store = task.getResult();
                        remoteDatastore = store;
                        Timber.d("created remote store : " + store.getName());
                        remoteDatastore.getMapper().setDataTypeForClassName("Item", Item.class.getCanonicalName());

                        Task<Boolean> permissionTask = DataManager.getInstance()
                                .setCurrentUserPermissions(DataManager.DB_ACCESS_GROUP_ADMINS, remoteDatastore.getName());

                        permissionTask.continueWith(new Continuation<Boolean, Object>() {
                            @Override
                            public Object then(Task<Boolean> task) throws Exception {
                                if (task.isFaulted()){
                                    showToast(false);
                                    Timber.d("An error occurred while setting permissions for remote data store"
                                            +task.getError().getLocalizedMessage());
                                }else {
                                    Timber.d("DB Permissions set for remote data store");
                                    getWishListItems();
                                }
                                return null;
                            }
                        });
                    }
                    return null;
                }
            });

    }

    private void getWishListItems() {
        if (remoteDatastore == null){
            setUpDB();
        }else{
            Map<String, Object> dataTypeEqualityOpMap = new HashMap<String, Object>();
            dataTypeEqualityOpMap.put("$eq", "Item");

            Map<String, Object> dataTypeSelectorMap = new HashMap<String, Object>();
            dataTypeSelectorMap.put("@datatype", dataTypeEqualityOpMap);

            List<Map<String, Object>> andPredicates = new ArrayList<Map<String, Object>>();
            andPredicates.add(dataTypeSelectorMap);

            Map<String, Object> andOpMap = new HashMap<String, Object>();
            andOpMap.put("$and", andPredicates);

            Map<String, Object> cloudantQueryMap = new HashMap<String, Object>();
            cloudantQueryMap.put("selector", andOpMap);

            Query itemsQuery = new CloudantQuery(cloudantQueryMap);

            Task<List> fetchAllItemsTask = remoteDatastore.performQuery(itemsQuery);
            fetchAllItemsTask.continueWith(new Continuation<List, Object>() {
                @Override
                public Object then(Task<List> task) throws Exception {
                    if (task.isFaulted()){
                        showToast(false);
                        Timber.d("An error occurred while retrieving all the items from remote store"+task.getError().getLocalizedMessage());
                    }else{
                        showToast(true);

                        ArrayList<Item> itemList = new ArrayList<Item>();
                        List itemsList = task.getResult();
                        for (Object item:itemsList){
                            if (item instanceof Item){
                                Timber.d("The returned object  from List is Item");
                                ((Item) item).prettyPrint();
                                itemList.add((Item) item);
                            }
                        }
                        EventBus.getDefault().post(itemList);
                    }
                    return null;
                }
            });
        }
    }

    public void addItem(Item item){
        toast.setText("Adding item");
        toast.show();
        Task<Object> addTask = remoteDatastore.save(item);
        addTask.continueWith(new Continuation<Object, Object>() {
            @Override
            public Object then(Task<Object> task) throws Exception {
                if (task.isFaulted()){
                    showToast(false);
                    Timber.d("An error occurred while saving item to remote store");
                }else{
                    getWishListItems();
                    showToast(true);
                    Timber.d("After successfully adding item to wishlist, refreshing the data from cloudant");
                }
                return null;
            }
        });
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
