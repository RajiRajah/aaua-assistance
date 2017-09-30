package com.aauaforum.aauaassistance.helper;

import android.support.annotation.NonNull;

import com.aauaforum.aauaassistance.model.User;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

/**
 * Created by Rajah on 9/29/2017.
 */

public class RealmHelper {

    private Realm realm;
    private User users;
    private Boolean emailFetch = null;
    private Boolean saved = null;
    private int nextId;


    public RealmHelper(Realm realm) {
        this.realm = realm;
    }

    public int getUserId() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Number currentId = realm.where(User.class).max("id");
                if (currentId == null) {
                    nextId = 1;
                } else {
                    nextId = currentId.intValue() + 1;
                }
            }
        });
        return nextId;
    }

    //    getting email
    public boolean getUserEmailExist(final String email) {
        if (email == null) {
            emailFetch = false;
        } else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    users = realm.where(User.class).equalTo("email", email).findFirst();
                    emailFetch = (users != null);
                }
            });
        }
        return emailFetch;
    }

    public boolean saveUser(final User user) {
        if (user == null) {
            saved = false;
        } else {
            try {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealm(user);
                        saved = true;
                    }
                });
            } catch (RealmException e) {
                e.printStackTrace();
                saved = false;
            }
        }
        return saved;
    }

    public boolean fetchUser() {
        users = realm.where(User.class).findFirst();
        return true;
    }

    public User getUserById(final int id) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                users = realm.where(User.class).equalTo("id", id).findFirst();
            }
        });

        return users;
    }
}
