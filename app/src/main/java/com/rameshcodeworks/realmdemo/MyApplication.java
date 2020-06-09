package com.rameshcodeworks.realmdemo;


import android.app.Application;

import com.rameshcodeworks.realmdemo.model.Company;
import com.rameshcodeworks.realmdemo.model.SocialAccount;
import com.rameshcodeworks.realmdemo.model.User;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.annotations.RealmModule;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		Realm.init(this);

		RealmConfiguration configuration = new RealmConfiguration.Builder()
				.name("myFirstRealm.realm") // By default the name of db is "default.realm"
                .modules(new myCustomModule())
                .schemaVersion(2)
                .migration(new MyMigration())
				.build();

		Realm.setDefaultConfiguration(configuration);

	}

	@RealmModule (classes = {User.class, SocialAccount.class, Company.class})
	public class myCustomModule {

    }

//	public static Realm getAnotherRealm() {
//		RealmConfiguration myOtherConfiguration = new RealmConfiguration.Builder()
//				.name("mySecondRealm.realm")
//				.build();
//		Realm myAnotherRealm = Realm.getInstance(myOtherConfiguration);
//		return myAnotherRealm;
//	}
}
