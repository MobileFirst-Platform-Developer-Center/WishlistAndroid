package com.ibm.mfp.wishlistsample.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.ibm.mfp.wishlistsample.CatalogListViewAdapter;
import com.ibm.mfp.wishlistsample.R;
import com.ibm.mfp.wishlistsample.Utils;
import com.ibm.mfp.wishlistsample.datamanagers.JsonStoreDataManager;
import com.ibm.mfp.wishlistsample.datamanagers.WishListChallengeHandler;
import com.ibm.mfp.wishlistsample.datamanagers.WishListDataManager;
import com.ibm.mfp.wishlistsample.models.Item;
import com.squareup.picasso.Picasso;
import com.worklight.wlclient.api.WLClient;

import net.steamcrafted.loadtoast.LoadToast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by chethan on 17/05/15.
 */
public class WishListFragment extends Fragment {

    Boolean usingCloudant = false;
    LoadToast toast;

    @InjectView(R.id.wishlistListView)
    ListView wishlistListView;

    @InjectView(R.id.wishlist_add)
    FloatingActionButton add;

    EditText itemName = null, storeName = null, itemPrice = null;
    ImageView itemImage = null;
    String placeholderImageUrlString = "https://dl.dropboxusercontent.com/u/97674776/images/gs6edge.png";

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
        Log.d("WishListFragment","WishList Fragment onViewCreated");
        EventBus.getDefault().register(this);
        toast = new LoadToast(getActivity());
        toast.setTranslationY(400);

        wishlistListView.setAdapter(new CatalogListViewAdapter(null));

        WLClient.createInstance(getActivity().getApplicationContext());
        Utils.setCustomServerURLs(getActivity().getApplicationContext());

        //Register challenge handler
        WLClient.getInstance().registerChallengeHandler(new WishListChallengeHandler(getActivity()));

        if (Utils.isOnline(getActivity())){
            Utils.pingCloudant(getActivity());
            toast.setText("Cloudant or local");
            toast.show();
        }else{
            usingCloudant = false;
            JsonStoreDataManager.getInstance(getActivity()).setUpLocalStore();
        }

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
                                        placeholderImageUrlString,
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
                itemImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        placeholderImageUrlString = getDummyImage();
                        Picasso.with(getActivity()).load(placeholderImageUrlString).into(itemImage);
                    }
                });
                Picasso.with(getActivity()).load(placeholderImageUrlString).into(itemImage);
                newItemDialog.show();
            }
        });
    }

    public void onEventMainThread(Boolean isCloudantAvailable){
        Log.d("WishListFragment","is cloudant available "+isCloudantAvailable);
        if (isCloudantAvailable){
            toast.success();
            WishListDataManager.getInstance(getActivity()).setUpDB();
            usingCloudant = true;
        }else{
            toast.error();
            usingCloudant = false;
            JsonStoreDataManager.getInstance(getActivity()).setUpLocalStore();
//            JsonStoreDataManager.getInstance(getActivity()).getLocalListItems();
        }
        //enable the add new item button
        add.setEnabled(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private String getDummyImage(){
        String imageString;
        int val = ((int) (Math.random()*10)) % 6;
        Log.d("WishListFragment","Random val" + val);
        switch (val){
            case 1:
                return "https://dl.dropboxusercontent.com/u/97674776/images/iPadAir2.jpg";
            case 2:
                return "https://dl.dropboxusercontent.com/u/97674776/images/gs6edge.png";
            case 3:
                return "https://dl.dropboxusercontent.com/u/97674776/images/samsung65tv.png";
            case 4:
                return "https://dl.dropboxusercontent.com/u/97674776/images/macbook_pro.png";
            default:
                return "https://dl.dropboxusercontent.com/u/97674776/images/gs6edge.png";

        }

    }
}
