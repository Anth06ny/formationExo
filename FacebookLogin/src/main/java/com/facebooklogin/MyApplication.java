package com.facebooklogin;

import android.app.Application;

import com.facebook.SessionDefaultAudience;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

/**
 * Created by Anthony on 15/10/2014.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //facebook
        final Permission[] permissions = new Permission[] { Permission.USER_PHOTOS, Permission.EMAIL, Permission.PUBLISH_ACTION };
        final SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(getResources().getString(R.string.facebook_app_id)).setNamespace(getApplicationInfo().name).setPermissions(permissions)
                .setDefaultAudience(SessionDefaultAudience.FRIENDS).setAskForAllPermissionsAtOnce(false).build();
        SimpleFacebook.setConfiguration(configuration);
    }
}
