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
package com.ibm.mfp.wishlistsample.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.ibm.mfp.wishlistsample.CatalogListViewAdapter;
import com.ibm.mfp.wishlistsample.R;
import com.ibm.mfp.wishlistsample.Utils;
import com.ibm.mfp.wishlistsample.datamanagers.CatalogDataManager;
import com.worklight.wlclient.api.WLClient;

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
        Log.d("Catalog Fragment", "Catalog Fragment onViewCreated");

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
