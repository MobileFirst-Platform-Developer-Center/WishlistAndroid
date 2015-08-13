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
package com.ibm.mfp.wishlistsample.datamanagers;

import android.app.Activity;
import android.content.Context;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ibm.mfp.wishlistsample.R;
import com.worklight.common.Logger;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.challengehandler.ChallengeHandler;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;

import java.util.HashMap;

public class WishListChallengeHandler extends ChallengeHandler {

    private Context context;
    private String username;
    private String password;
    private EditText userName = null,pwd = null;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private  void setUsername(String username){
        this.username = username;
    }
    private  void setPassword(String password){
        this.password = password;
    }

    public WishListChallengeHandler(Context context) {
        super("CustomLoginModule");
        this.context = context;

    }

    @Override
    public boolean isCustomResponse(WLResponse wlResponse) {
        Logger.getInstance("WishListChallengeHandler").debug("is Custom Response " + wlResponse.getResponseJSON());
        if (wlResponse != null && wlResponse.getResponseJSON() != null){
            try {
                if (wlResponse.getResponseJSON().has("authStatus")){
                    String authRequired = wlResponse.getResponseJSON().getString("authStatus");
                    Logger.getInstance("WishListChallengeHandler").debug("auth status is : " + authRequired.equalsIgnoreCase("required"));
                    return authRequired.equalsIgnoreCase("required");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void handleChallenge(WLResponse wlResponse) {
        //need to run on Main UI thread
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MaterialDialog loginDialog = new MaterialDialog.Builder(context)
                        .title("Login")
                        .customView(R.layout.login, true)
                        .positiveText("LOGIN")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                submitLogin(userName.getText().toString(),pwd.getText().toString());
                            }
                        })
                        .build();

                userName =(EditText) loginDialog.getCustomView().findViewById(R.id.login_username);
                pwd =(EditText) loginDialog.getCustomView().findViewById(R.id.login_password);
                loginDialog.show();
            }
        });

    }

    public void submitLogin(String username, String password){
        HashMap<String,String> requestParam = new HashMap<>();
        requestParam.put("username",username);
        requestParam.put("password",password);
        submitLoginForm("/my_custom_auth_request_url", requestParam, null, 0,"POST");
    }

    @Override
    public void onSuccess(WLResponse wlResponse) {
        Logger.getInstance("WishListChallengeHandler").debug("Successfully logged in");
        //TODO dismiss login form
        submitSuccess(wlResponse);
    }

    @Override
    public void onFailure(WLFailResponse wlFailResponse) {
        //TODO show alert of invalid username and password
        Logger.getInstance("WishListChallengeHandler").debug("Failed to logged in");
        submitFailure(wlFailResponse);
    }
}
