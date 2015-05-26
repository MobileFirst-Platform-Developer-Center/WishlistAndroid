package com.ibm.mfp.wishlistsample.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.ibm.mfp.wishlistsample.CatalogListViewAdapter;
import com.ibm.mfp.wishlistsample.R;
import com.ibm.mfp.wishlistsample.Utils;
import com.ibm.mfp.wishlistsample.datamanagers.CatalogDataManager;
import com.worklight.wlclient.api.WLClient;
import timber.log.Timber;

/**
 * Created by chethan on 13/05/15.
 */
public class CatalogFragment extends Fragment {

    public static CatalogFragment newInstance()
    {
        return new CatalogFragment();
    }

    ListView catalogListView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.catalog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WLClient.createInstance(getActivity().getApplicationContext());
        Utils.setCustomServerURLs(getActivity().getApplicationContext());

        CatalogDataManager.getInstance(getActivity()).getCatalogData();
        Timber.d("Catalog Fragment onViewCreated");

        catalogListView = (ListView)view.findViewById(R.id.catalogListView);
        catalogListView.setAdapter(new CatalogListViewAdapter(null)); // optionally you can pass the items here
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

//    public  void onEventMainThread(ArrayList<Item> itemList){
//        Timber.d("Got catalog item list fragment "+itemList.size());
//        for(Item item : itemList){
//            item.prettyPrint();
//        }
//    }
}
