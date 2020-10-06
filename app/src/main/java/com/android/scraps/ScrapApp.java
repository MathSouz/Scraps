package com.android.scraps;

import android.app.Application;

import androidx.room.Room;

import java.io.FileNotFoundException;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

public class ScrapApp extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Realm.init(this);
        // TODO: CUIDADO COM ISTO!
        RealmConfiguration configuration = new RealmConfiguration.Builder().migration(new RealmMigration() {
            @Override
            public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

            }
        }).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(configuration);
        try {
            Realm.migrateRealm(configuration);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
