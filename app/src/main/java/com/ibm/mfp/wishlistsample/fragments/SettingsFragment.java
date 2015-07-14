package com.ibm.mfp.wishlistsample.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.ibm.mfp.wishlistsample.CatalogListViewAdapter;
import com.ibm.mfp.wishlistsample.Constants;
import com.ibm.mfp.wishlistsample.R;
import com.ibm.mfp.wishlistsample.Utils;
import com.ibm.mfp.wishlistsample.datamanagers.WishListChallengeHandler;
import com.ibm.mfp.wishlistsample.datamanagers.WishListDataManager;
import com.worklight.wlclient.api.WLClient;

import java.net.MalformedURLException;
import java.net.URL;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.alexrs.prefs.lib.Prefs;

/**
 * Created by chethan on 19/05/15.
 */
public class SettingsFragment extends Fragment implements Constants{

    @InjectView(R.id.settings_mfp_url)
    EditText mfpURL ;

    @InjectView(R.id.settings_mfp_runtime)
    EditText mfpRuntimeName;

    @InjectView(R.id.settings_save)
    Button save;

    public static SettingsFragment newInstance()
    {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);
        ButterKnife.inject(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Settings fragment", "Settings Fragment onViewCreated");

        WLClient.createInstance(getActivity().getApplicationContext());

        mfpURL.setText(Prefs.with(getActivity().getApplicationContext()).getString(MFP_SERVER_URL, ""));
        mfpRuntimeName.setText(Prefs.with(getActivity().getApplicationContext()).getString(MFP_RUNTIME_NAME,""));


    }

    @OnClick(R.id.settings_save)
    public void clickSave(){
        Prefs.with(getActivity().getApplicationContext()).save(MFP_SERVER_URL,mfpURL.getText().toString());
        Prefs.with(getActivity().getApplicationContext()).save(MFP_RUNTIME_NAME,mfpRuntimeName.getText().toString());
            try {
                WLClient.getInstance().setServerUrl(new URL(mfpURL.getText().toString()+"/"+mfpRuntimeName.getText().toString()));
                Toast.makeText(getActivity(),"Successfully saved the custom server properties",Toast.LENGTH_SHORT).show();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

    }
}
