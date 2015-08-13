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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ibm.mfp.wishlistsample.CatalogListViewAdapter;
import com.ibm.mfp.wishlistsample.R;
import com.ibm.mfp.wishlistsample.Utils;
import com.ibm.mfp.wishlistsample.datamanagers.CatalogDataManager;
import com.mikepenz.materialdrawer.Drawer;
import com.worklight.wlclient.api.WLClient;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeFragment extends Fragment {

    private Drawer.Result drawer;

    public static HomeFragment newInstance(Drawer.Result drawer)
    {
        return new HomeFragment(drawer);

//        return new HomeFragment();
    }

    private void setDrawer(Drawer.Result draw){
        this.drawer = draw;
    }

    public HomeFragment(){
    }

    public HomeFragment(Drawer.Result drawer){
        this.drawer = drawer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.home, container, false);
        ButterKnife.inject(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.catalog)
    public void clickCatalog(){
        drawer.setSelection(1);
    }

    @OnClick(R.id.wishlist)
    public void clickWishlist(){
        drawer.setSelection(2);
    }
}
