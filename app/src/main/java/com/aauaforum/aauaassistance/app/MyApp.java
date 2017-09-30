package com.aauaforum.aauaassistance.app;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Rajah on 9/29/2017.
 */

public class MyApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        //        Realm Initialization
        Realm.init(this);

//        RealmConfiguration
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
