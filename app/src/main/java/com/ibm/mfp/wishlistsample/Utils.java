package com.ibm.mfp.wishlistsample;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;

import com.cloudant.toolkit.Store;
import com.ibm.imf.data.DataManager;
import com.worklight.wlclient.api.WLClient;

import net.steamcrafted.loadtoast.LoadToast;

import java.net.MalformedURLException;
import java.net.URL;

import bolts.Task;
import me.alexrs.prefs.lib.Prefs;

/**
 * Created by chethan on 20/05/15.
 */
public class Utils implements Constants {

    public static void setCustomServerURLs(Context context){
        try {
            if (Prefs.with(context).getBoolean(USE_CUSTOM_SERVER,false)) {
                WLClient.getInstance().setServerUrl(new URL(Prefs.with(context).getString(MFP_SERVER_URL, "")));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static URL getDataProxyUrl(Context context){
        try {
            if (Prefs.with(context).getBoolean(USE_CUSTOM_SERVER,false)){
                if (Prefs.with(context).getString(MFP_DATAPROXY_URL,"").equalsIgnoreCase(""))
                    return new URL("http://129.41.233.140:9080/imfdata");
                else
                    return new URL(Prefs.with(context).getString(MFP_DATAPROXY_URL,""));
            }
            return new URL("http://129.41.233.140:9080/imfdata");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni == null){
            return false;
        }
        return ni.isConnected();
    }

    public static boolean isCloudantAvailable(Context context) {
        try {
            LoadToast toast = new LoadToast(context);
            toast.setText("Cloudant or local");
            toast.setTranslationY(400);
            toast.show();
            DataManager.initialize(context, Utils.getDataProxyUrl(context));
            Task<Store> wishListTask = DataManager.getInstance().remoteStore("wishlist");
            wishListTask.waitForCompletion();
            if(wishListTask.isFaulted()){
                toast.error();
                return false;
            }else {
                toast.success();
                return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
