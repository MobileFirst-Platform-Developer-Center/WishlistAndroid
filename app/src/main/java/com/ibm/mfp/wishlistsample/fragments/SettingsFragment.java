package com.ibm.mfp.wishlistsample.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.ibm.mfp.wishlistsample.datamanagers.WishListChallengeHandler;
import com.ibm.mfp.wishlistsample.datamanagers.WishListDataManager;
import com.worklight.wlclient.api.WLClient;

import java.net.MalformedURLException;
import java.net.URL;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.alexrs.prefs.lib.Prefs;
import timber.log.Timber;

/**
 * Created by chethan on 19/05/15.
 */
public class SettingsFragment extends Fragment implements Constants{

    @InjectView(R.id.settings_mfp_url)
    EditText mfpURL ;

    @InjectView(R.id.settings_dataproxy_url)
    EditText dataproxyURL;

    @InjectView(R.id.custom_server_switch)
    Switch customServerSwitch;

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
        Timber.d("Settings Fragment onViewCreated");

        WLClient.createInstance(getActivity().getApplicationContext());

        Boolean shouldUseCustomServer = Prefs.with(getActivity().getApplicationContext()).getBoolean(USE_CUSTOM_SERVER, false);
        customServerSwitch.setChecked(shouldUseCustomServer);

        mfpURL.setText(Prefs.with(getActivity().getApplicationContext()).getString(MFP_SERVER_URL, ""));
        dataproxyURL.setText(Prefs.with(getActivity().getApplicationContext()).getString(MFP_DATAPROXY_URL, ""));

        customServerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(getActivity(), "server url " + WLClient.getInstance().getServerUrl(), Toast.LENGTH_SHORT).show();
                mfpURL.setEnabled(isChecked);
                dataproxyURL.setEnabled(isChecked);
                save.setEnabled(isChecked);
                if (isChecked){
                    clickSave();
                }else{
                    try {
                        WLClient.getInstance().setServerUrl(new URL("http://129.41.233.140:9080/wishlist"));
                        Prefs.with(getActivity().getApplicationContext()).save(USE_CUSTOM_SERVER,isChecked);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    @OnClick(R.id.settings_save)
    public void clickSave(){
        Prefs.with(getActivity().getApplicationContext()).save(USE_CUSTOM_SERVER,customServerSwitch.isChecked());
        if(customServerSwitch.isChecked()){
            Prefs.with(getActivity().getApplicationContext()).save(MFP_SERVER_URL,mfpURL.getText().toString());
            Prefs.with(getActivity().getApplicationContext()).save(MFP_DATAPROXY_URL,dataproxyURL.getText().toString());
            try {
                WLClient.getInstance().setServerUrl(new URL(mfpURL.getText().toString()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
}
