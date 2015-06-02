package com.ibm.mfp.wishlistsample;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;

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
import timber.log.Timber;

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
                    return new URL(getCloudantUrlFromProperties(context));
                else
                    return new URL(Prefs.with(context).getString(MFP_DATAPROXY_URL,""));
            }
            return new URL(getCloudantUrlFromProperties(context));
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

    public static void pingCloudant(Context context) {
            URL url = getDataProxyUrl(context);
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(url.toString(), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Timber.d("is cloudant available - success  status code : "+statusCode + "Response "+responseBody.toString());
                    EventBus.getDefault().post(true);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Timber.d("is cloudant available - failure  status code : "+statusCode);
                    EventBus.getDefault().post(false);
                }
            });
    }

    public static String getCloudantUrlFromProperties(Context context){
        try {
            Properties prop = new Properties();
            InputStream itemData = context.getAssets().open("wlclient.properties");
            prop.load(itemData);
            itemData.close();
            return prop.getProperty("wlServerProtocol")+"://"+prop.getProperty("wlServerHost")
                    +":"+prop.getProperty("wlServerPort")+"/"+prop.getProperty("dataproxy");
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
            return prop.getProperty("wlServerProtocol")+"://"+prop.getProperty("wlServerHost")
                    +":"+prop.getProperty("wlServerPort")+prop.getProperty("wlServerContext");
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return "http://129.41.226.173:9080/MobileFirstStarter";
    }

    public static Typeface getRegularTypeface(Context context) {
        return Typeface.createFromAsset(context.getApplicationContext().getAssets(), "fonts/Lato-Regular.ttf");
    }

}
