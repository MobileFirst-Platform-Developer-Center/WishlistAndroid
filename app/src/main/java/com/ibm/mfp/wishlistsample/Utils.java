package com.ibm.mfp.wishlistsample;

import android.content.Context;

import com.worklight.wlclient.api.WLClient;

import java.net.MalformedURLException;
import java.net.URL;

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
}
