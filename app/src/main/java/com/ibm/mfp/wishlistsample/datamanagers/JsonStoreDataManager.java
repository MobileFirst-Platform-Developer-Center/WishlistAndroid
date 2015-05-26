package com.ibm.mfp.wishlistsample.datamanagers;

import android.app.Activity;
import android.content.Context;

import com.ibm.mfp.wishlistsample.models.Item;
import com.worklight.jsonstore.api.JSONStoreAddOptions;
import com.worklight.jsonstore.api.JSONStoreCollection;
import com.worklight.jsonstore.api.WLJSONStore;
import com.worklight.jsonstore.database.SearchFieldType;
import com.worklight.jsonstore.exceptions.JSONStoreAddException;
import com.worklight.jsonstore.exceptions.JSONStoreDatabaseClosedException;
import com.worklight.jsonstore.exceptions.JSONStoreException;
import com.worklight.jsonstore.exceptions.JSONStoreFindException;
import com.worklight.jsonstore.exceptions.JSONStoreInvalidSchemaException;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * Created by chethan on 25/05/15.
 */
public class JsonStoreDataManager {

    private static JsonStoreDataManager instance = null;
    private Context context;
    List<JSONStoreCollection> collections;
    JSONStoreCollection itemCollection;
    LoadToast toast;

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

    public void addItem(Item item){
        try {
            Timber.d("adding item");
            item.prettyPrint();
            toast.setText("Adding Item");
            toast.show();
            JSONObject newItem = item.getItemJsonObject();
            JSONStoreAddOptions addOptions = new JSONStoreAddOptions();
            addOptions.setMarkDirty(true);
            itemCollection.addData(newItem,addOptions);

        } catch (JSONStoreAddException e) {
            showToast(false);
            e.printStackTrace();
        } catch (JSONStoreDatabaseClosedException e) {
            showToast(false);
            e.printStackTrace();
        }
    }

    public void getAllItems(){
        //get all items and post it to adapter
        try {
            Timber.d("trying to retrieve all jsonstore items");
            ArrayList<Item> allItemsList = new ArrayList<Item>();
            List<JSONObject> itemList = itemCollection.findAllDocuments();
            for (JSONObject jsonObject:itemList){
                allItemsList.add(new Item(jsonObject));
            }
            postItemListToAdapter(allItemsList);
            showToast(true);

        } catch (JSONStoreFindException e) {
            showToast(false);
            e.printStackTrace();
        } catch (JSONStoreDatabaseClosedException e) {
            showToast(false);
            e.printStackTrace();
        }
    }

    private void postItemListToAdapter(ArrayList<Item> itemList){
        EventBus.getDefault().post(itemList);
    }

    public void setUpJsonStore(){
        try {
            toast.setText("Fetching wishlist");
            toast.setTranslationY(400);
            toast.show();

            collections = new LinkedList<JSONStoreCollection>();
            itemCollection = new JSONStoreCollection("Item");
            itemCollection.setSearchField("name", SearchFieldType.STRING);
            itemCollection.setSearchField("store", SearchFieldType.STRING);
            collections.add(itemCollection);

            WLJSONStore.getInstance(context).openCollections(collections);

            getAllItems();

        } catch (JSONStoreInvalidSchemaException e) {
            showToast(false);
            e.printStackTrace();
        }catch (JSONStoreException je){
            showToast(false);
            je.printStackTrace();
        }
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
