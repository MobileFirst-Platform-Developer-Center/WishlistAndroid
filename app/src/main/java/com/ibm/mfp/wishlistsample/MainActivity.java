package com.ibm.mfp.wishlistsample;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.mfp.wishlistsample.datamanagers.JsonStoreDataManager;
import com.ibm.mfp.wishlistsample.fragments.CatalogFragment;
import com.ibm.mfp.wishlistsample.fragments.HomeFragment;
import com.ibm.mfp.wishlistsample.fragments.SettingsFragment;
import com.ibm.mfp.wishlistsample.fragments.WishListFragment;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.worklight.wlclient.WLRequestListener;
import com.worklight.wlclient.api.WLClient;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLOnReadyToSubscribeListener;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    Drawer.Result drawer;

    @InjectView(R.id.main_navigation)
    TextView navigation;

    @InjectView(R.id.main_title)
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        Timber.plant(new Timber.DebugTree());
        title.setTypeface(Utils.getRegularTypeface(this));

       drawer  = new Drawer()
                .withActivity(this)
                .withActionBarDrawerToggle(true)
                .withCloseOnClick(true)
                .withTranslucentStatusBar(true)
                .withDisplayBelowToolbar(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Home").withTypeface(Utils.getRegularTypeface(this)),

                        new PrimaryDrawerItem().withName("Catalog").withTypeface(Utils.getRegularTypeface(this)),
                        new PrimaryDrawerItem().withName("Wish List").withTypeface(Utils.getRegularTypeface(this)),

                        new PrimaryDrawerItem().withName("Settings").withTypeface(Utils.getRegularTypeface(this)),
                        new PrimaryDrawerItem().withName("Logout").withTypeface(Utils.getRegularTypeface(this))

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l, IDrawerItem iDrawerItem) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        switch (position){
                            case 0:
                                //home
                                title.setText("WISHLIST");
                                fragmentManager.beginTransaction()
                                        .replace(R.id.container, HomeFragment.newInstance(drawer))
                                        .commit();
                                break;
                            case 1:
                                title.setText("Catalog");
                                fragmentManager.beginTransaction()
                                        .replace(R.id.container, CatalogFragment.newInstance())
                                        .commit();
                                break;
                            case 2:
                                title.setText("Wish List");
                                fragmentManager.beginTransaction()
                                        .replace(R.id.container, WishListFragment.newInstance())
                                        .commit();
                                break;
                            case 3:
                                title.setText("Settings");
                                fragmentManager.beginTransaction()
                                        .replace(R.id.container, SettingsFragment.newInstance())
                                        .commit();
                                break;
                            case 4:
                                title.setText("Wish List");
                                //logout and show home
                                fragmentManager.beginTransaction()
                                        .replace(R.id.container, HomeFragment.newInstance(drawer))
                                        .commit();
                                WLClient.createInstance(getApplicationContext());
                                WLClient.getInstance().logout("cloudant", new WLRequestListener() {
                                    @Override
                                    public void onSuccess(WLResponse wlResponse) {
                                        toast("Successfully logged out");
                                    }

                                    @Override
                                    public void onFailure(WLFailResponse wlFailResponse) {
                                        Timber.d("Couldn't log out "+wlFailResponse.getErrorMsg());
                                        toast("Couldn't  log out");
                                    }
                                });
                                break;
                        }
                    }
                })
                .withSelectedItem(1)
                .build();
                drawer.setSelection(0);
//                drawer.openDrawer();

        setUpPush();
//        JsonStoreDataManager.getInstance(this).setUpLocalStore();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void toast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

@OnClick(R.id.main_navigation)
    public void navigationClick(){
    drawer.openDrawer();
}


    private void setUpPush(){
        WLClient.createInstance(this);
        WLClient.getInstance().getPush().setOnReadyToSubscribeListener(new WLOnReadyToSubscribeListener() {
            @Override
            public void onReadyToSubscribe() {
                Timber.d("onReadyToSubscribe");
                //subscribe to a tag
                WLClient.getInstance().getPush().subscribeTag("wishlist", null, new WLResponseListener() {
                    @Override
                    public void onSuccess(WLResponse wlResponse) {
                        Timber.d("Successfully subscribed to push tag wishlist");
                    }

                    @Override
                    public void onFailure(WLFailResponse wlFailResponse) {
                        Timber.d("An error occured while subscribing to manager push tag");
                    }
                });
            }
        });

        WLClient.getInstance().connect(new WLResponseListener() {
            @Override
            public void onSuccess(WLResponse wlResponse) {
                Timber.d("Successfully connected \n"+wlResponse.getResponseJSON());
            }

            @Override
            public void onFailure(WLFailResponse wlFailResponse) {
                Timber.d("An error occured while connecting to server");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (WLClient.getInstance().getPush() != null)
            WLClient.getInstance().getPush().setForeground(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (WLClient.getInstance().getPush() != null)
            WLClient.getInstance().getPush().setForeground(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (WLClient.getInstance().getPush() != null)
            WLClient.getInstance().getPush().unregisterReceivers();
    }

}
