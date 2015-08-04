package com.ibm.mfp.wishlistsample;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.cloudant.toolkit.Store;
import com.ibm.imf.data.DataManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.OkHttpDownloader;
import com.worklight.wlclient.api.WLClient;

import net.steamcrafted.loadtoast.LoadToast;

import org.apache.http.Header;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import bolts.Task;
import de.greenrobot.event.EventBus;
import me.alexrs.prefs.lib.Prefs;

/**
 * Created by chethan on 20/05/15.
 */
public class Utils implements Constants {

    public static void setCustomServerURLs(Context context){
        try {

                WLClient.getInstance().setServerUrl(new URL(Prefs.with(context).getString(MFP_SERVER_URL, "")+"/"+Prefs.with(context).getString(MFP_RUNTIME_NAME, "")));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static URL getDataProxyUrl(Context context){
        try {
            return new URL(Prefs.with(context).getString(MFP_SERVER_URL, "")+"/"+"datastore");
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
    
    public static String getCloudantUrlFromProperties(Context context){
        try {
            Properties prop = new Properties();
            InputStream itemData = context.getAssets().open("wlclient.properties");
            prop.load(itemData);
            itemData.close();
            String urlString = prop.getProperty("wlServerProtocol")+"://"+prop.getProperty("wlServerHost")
                    +":"+prop.getProperty("wlServerPort")+"/"+prop.getProperty("dataproxy");
            return urlString.substring(0,urlString.length()-2);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return "http://129.41.226.173:9080/imfdata";
    }

    public static String getMFPUrlFromProperties(Context context){
        try {
            Properties prop = new Properties();
            InputStream itemData = context.getAssets().open("wlclient.properties");
            prop.load(itemData);
            itemData.close();
            String urlString = prop.getProperty("wlServerProtocol")+"://"+prop.getProperty("wlServerHost")
                    +":"+prop.getProperty("wlServerPort")+prop.getProperty("wlServerContext");
            return urlString.substring(0,urlString.length()-2);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return "http://129.41.226.173:9080/MobileFirstStarter";
    }

    public static Typeface getRegularTypeface(Context context) {
        return Typeface.createFromAsset(context.getApplicationContext().getAssets(), "fonts/Lato-Regular.ttf");
    }

    public static Typeface getProximaTypeface(Context context) {
        return Typeface.createFromAsset(context.getApplicationContext().getAssets(), "fonts/ProximaNova-Regular.otf");
    }

}
