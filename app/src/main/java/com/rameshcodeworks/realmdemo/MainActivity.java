package com.rameshcodeworks.realmdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rameshcodeworks.realmdemo.model.SocialAccount;
import com.rameshcodeworks.realmdemo.model.User;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * 	Author: Sriyank Siddhartha
 *
 * 	Module 3: Setting up Realm
 *
 * 			"BEFORE" Project
 * */
public class MainActivity extends AppCompatActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private EditText etPersonName, etAge, etSocialAccountName, etStatus;

	private Realm myRealm;
	private RealmAsyncTask realmAsyncTask;
	RealmResults<User> userList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		etPersonName 		= (EditText) findViewById(R.id.etPersonName);
		etAge 				= (EditText) findViewById(R.id.etAge);
		etSocialAccountName = (EditText) findViewById(R.id.etSocialAccount);
		etStatus 			= (EditText) findViewById(R.id.etStatus);

		myRealm = Realm.getDefaultInstance();
		//Realm myAnotherRealm = MyApplication.getAnotherRealm();
        Log.i(TAG, "Current Version: " + myRealm.getVersion());
		
	}

	// Add data to Realm using Main UI Thread. Be Careful: As it may BLOCK the UI.
	public void addUserToRealm_Synchronously(View view) {
		final String id = UUID.randomUUID().toString();
		final String name = etPersonName.getText().toString();
		final int age = Integer.valueOf(etAge.getText().toString());
		final String socialAccountName = etSocialAccountName.getText().toString();
		final String status = etStatus.getText().toString();

		/*try {

			myRealm.beginTransaction();

			myRealm.commitTransaction();

		} catch (Exception e) {
			myRealm.cancelTransaction();
		}*/

		myRealm.executeTransaction(new Realm.Transaction() {
			@Override
			public void execute(Realm realm) {
				SocialAccount socialAccount = realm.createObject(SocialAccount.class);
				socialAccount.setName(socialAccountName);
				socialAccount.setStatus(status);

				User user = realm.createObject(User.class, id);
				user.setName(name);
				user.setAge(age);
				user.setSocialAccount(socialAccount);
				Toast.makeText(MainActivity.this, "Added Successfully", Toast.LENGTH_LONG).show();
			}
		});

	}

	// Add Data to Realm in the Background Thread.
	public void addUserToRealm_ASynchronously(View view) {

		final String id = UUID.randomUUID().toString();
		final String name = etPersonName.getText().toString();
		final int age = Integer.valueOf(etAge.getText().toString());
		final String socialAccountName = etSocialAccountName.getText().toString();
		final String status = etStatus.getText().toString();

		realmAsyncTask = myRealm.executeTransactionAsync(new Realm.Transaction() {
			@Override
			public void execute(Realm realm) {
				SocialAccount socialAccount = realm.createObject(SocialAccount.class);
				socialAccount.setName(socialAccountName);
				socialAccount.setStatus(status);

				User user = realm.createObject(User.class, id);
				user.setName(name);
				user.setAge(age);
				user.setSocialAccount(socialAccount);
			}
		}, new Realm.Transaction.OnSuccess() {
			@Override
			public void onSuccess() {

				Toast.makeText(MainActivity.this, "Added Successfully", Toast.LENGTH_LONG).show();
			}
		}, new Realm.Transaction.OnError() {
			@Override
			public void onError(Throwable error) {
				Toast.makeText(MainActivity.this, "Error occurred", Toast.LENGTH_LONG).show();
			}
		});
	}
    /*public void sampleQueryExample(View view) {

        RealmQuery<User> realmQuery = myRealm.where(User.class);
        realmQuery.greaterThan("age", 15);
        realmQuery.contains("name", "John");
        RealmResults<User> userList = realmQuery.findAll();
        displayQuiredUsers(userList);
        RealmResults<User> userList2 = myRealm.where(User.class)
                .greaterThan("age", 15)
                .contains("name", "John")
                .findAll();
        displayQuiredUsers(userList2);
    }*/

	public void openDisplayActivity(View view) {

		Intent intent = new Intent(this, DisplayActivity.class);
		startActivity(intent);
	}

	public void displayAllUsers(View view) {

		RealmResults<User> userList = myRealm.where(User.class).findAll();
		displayQuiredUsers(userList);

	}

	private void displayQuiredUsers(RealmResults<User> userList) {
        StringBuilder builder = new StringBuilder();

        for (User user : userList) {
            builder.append("ID: ").append(user.getId());
            builder.append("\nName: ").append(user.getName());
            builder.append(", Age: ").append(user.getAge());

            SocialAccount socialAccount = user.getSocialAccount();
            builder.append("\nS'Account: ").append(socialAccount.getName());
            builder.append(", Status: ").append(socialAccount.getStatus()).append(" .\n\n");
        }
        Log.i(TAG + " Lists", builder.toString());
    }

	public void exploreMiscConcepts(View view) {
		userList = myRealm.where(User.class).findAllAsync();
		userList.addChangeListener(userListListener);

//		if (userList.isLoaded()) {
//			userList.deleteFirstFromRealm();
//		}
	}
	RealmChangeListener<RealmResults<User>> userListListener = new RealmChangeListener<RealmResults<User>>() {
		@Override
		public void onChange(RealmResults<User> userList) {
			displayQuiredUsers(userList);
		}
	};

	@Override
	protected void onStop() {
		super.onStop();

		if (userList != null)
		userList.removeChangeListener(userListListener);  // Removes a particular listener
		//Or userList.removeAllChangeListeners();   // remove all register listener

		if (realmAsyncTask != null && !realmAsyncTask.isCancelled()) {
			realmAsyncTask.cancel();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		myRealm.close();
	}


}
