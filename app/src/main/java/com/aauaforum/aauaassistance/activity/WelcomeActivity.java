package com.aauaforum.aauaassistance.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aauaforum.aauaassistance.Constant;
import com.aauaforum.aauaassistance.R;
import com.aauaforum.aauaassistance.helper.RealmHelper;
import com.aauaforum.aauaassistance.model.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = WelcomeActivity.class.getSimpleName();

    @BindView(R.id.btn_sign_in)
    SignInButton btnSignIn;
    @BindView(R.id.version_info)
    TextView versionInfo;
    @BindView(R.id.welcome_layout)
    RelativeLayout welcomeLayout;

    private User user;
    private Realm realm;
    private RealmHelper realmHelper;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private static final int RC_SIGN_IN = 007;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        welcomeScreen();
//        initializing Realm
        realm = Realm.getDefaultInstance();
        realmHelper = new RealmHelper(realm);

//        if (realmHelper.fetchUser()){
//            updateUI(true);
//        }

        btnSignIn.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customizing G+ button
        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setScopes(gso.getScopeArray());
        updateAppVersionInfo();
    }

    private void welcomeScreen() {
        Random randNum = new Random();
        int switchValue = randNum.nextInt(3) + 1;

        switch (switchValue){
            case 1:
                break;
            case 2:
                welcomeLayout.setBackground(getResources().getDrawable(R.drawable.aa_logo_1));
                versionInfo.setTextColor(getResources().getColor(R.color.lightColorPrimary));
                break;
            case 3:
                welcomeLayout.setBackground(getResources().getDrawable(R.drawable.aa_logo_2));
//                versionInfo.setTextColor(getResources().getColor(R.color.whiteColorPrimary));
                break;
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_sign_in:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.e(TAG, "display name: " + acct.getDisplayName());

            user = new User();
            user.setId(realmHelper.getUserId());
            user.setName(acct.getDisplayName());
            user.setEmail(acct.getEmail());
            user.setProfile_pic(acct.getPhotoUrl().toString());

            if (!realmHelper.getUserEmailExist(acct.getEmail())) {
                if (realmHelper.saveUser(user)) {
                    updateUI(true);
                }
            } else {
                updateUI(true);
            }

        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean isSignIn) {
        if (isSignIn) {
            Log.e(TAG, "realmResult: " + user.getId());

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constant.USER_ID, user.getId());
            startActivity(intent);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.signin_notify), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 50);
            toast.show();
        }

    }

    private void updateAppVersionInfo() {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionInfo.setText(getString(R.string.version_name) + " " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
