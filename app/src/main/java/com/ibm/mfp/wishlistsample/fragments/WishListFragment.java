package com.ibm.mfp.wishlistsample.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cloudant.toolkit.Store;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.ibm.imf.data.DataManager;
import com.ibm.mfp.wishlistsample.CatalogListViewAdapter;
import com.ibm.mfp.wishlistsample.R;
import com.ibm.mfp.wishlistsample.Utils;
import com.ibm.mfp.wishlistsample.datamanagers.JsonStoreDataManager;
import com.ibm.mfp.wishlistsample.datamanagers.WishListChallengeHandler;
import com.ibm.mfp.wishlistsample.datamanagers.WishListDataManager;
import com.ibm.mfp.wishlistsample.models.Item;
import com.squareup.picasso.Picasso;
import com.worklight.wlclient.api.WLClient;

import java.net.MalformedURLException;
import java.net.URL;

import bolts.Continuation;
import bolts.Task;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by chethan on 17/05/15.
 */
public class WishListFragment extends Fragment {

    Boolean usingCloudant = false;

    @InjectView(R.id.wishlistListView)
    ListView wishlistListView;

    @InjectView(R.id.wishlist_add)
    FloatingActionButton add;

    EditText itemName = null, storeName = null, itemPrice = null;
    ImageView itemImage = null;

    public static WishListFragment newInstance()
    {
        return new WishListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wishlist, container, false);
        ButterKnife.inject(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.d("WishList Fragment onViewCreated");

        wishlistListView.setAdapter(new CatalogListViewAdapter(null));

        WLClient.createInstance(getActivity().getApplicationContext());
        Utils.setCustomServerURLs(getActivity().getApplicationContext());

        //Register challenge handler
        WLClient.getInstance().registerChallengeHandler(new WishListChallengeHandler(getActivity()));

        if (Utils.isOnline(getActivity())){
            if(Utils.isCloudantAvailable(getActivity())){
                WishListDataManager.getInstance(getActivity()).setUpDB();
                usingCloudant = true;
            }else{
                usingCloudant = false;
                JsonStoreDataManager.getInstance(getActivity()).setUpLocalStore();
                JsonStoreDataManager.getInstance(getActivity()).getAllItemsFromAdapter();
            }
        }else{
            usingCloudant = false;
            JsonStoreDataManager.getInstance(getActivity()).setUpLocalStore();
            JsonStoreDataManager.getInstance(getActivity()).getLocalListItems();
        }
        //enable the add new item button
        add.setEnabled(true);
    }

    @OnClick(R.id.wishlist_add)
    public void addItem(){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                MaterialDialog newItemDialog = new MaterialDialog.Builder(getActivity())
                        .title("Wish List")
                        .customView(R.layout.new_item, true)
                        .positiveText("ADD")
                        .negativeText("CANCEL")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                Item item = new Item(itemName.getText().toString(),
                                        storeName.getText().toString(),
                                        Integer.parseInt(itemPrice.getText().toString()),
                                        "/images/gs6edge.png",
                                        "00006");
                                if (usingCloudant) {
                                    WishListDataManager.getInstance(getActivity()).addItem(item);
                                } else {
                                    JsonStoreDataManager.getInstance(getActivity()).addLocalItem(item);
                                }
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {

                            }
                        })
                        .build();

                itemName = (EditText) newItemDialog.getCustomView().findViewById(R.id.new_item_name);
                storeName = (EditText) newItemDialog.getCustomView().findViewById(R.id.new_item_store);
                itemPrice = (EditText) newItemDialog.getCustomView().findViewById(R.id.new_item_price);
                itemImage = (ImageView) newItemDialog.getCustomView().findViewById(R.id.new_item_image);
                Picasso.with(getActivity()).load("http://boxstore-catalog.mybluemix.net/MFPSampleWebService/images/gs6edge.png")
                        .into(itemImage);
                newItemDialog.show();
            }
        });
    }
}
