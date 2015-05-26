package com.ibm.mfp.wishlistsample.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ibm.mfp.wishlistsample.CatalogListViewAdapter;
import com.ibm.mfp.wishlistsample.R;
import com.ibm.mfp.wishlistsample.Utils;
import com.ibm.mfp.wishlistsample.datamanagers.CatalogDataManager;
import com.mikepenz.materialdrawer.Drawer;
import com.worklight.wlclient.api.WLClient;

import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by chethan on 21/05/15.
 */
public class HomeFragment extends Fragment {

    Drawer.Result drawer;

    public static HomeFragment newInstance(Drawer.Result drawer)
    {
        return new HomeFragment(drawer);
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
