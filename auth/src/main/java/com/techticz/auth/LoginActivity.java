package com.techticz.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import timber.log.Timber;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.techticz.auth.constant.AuthProviders;
import com.techticz.auth.fragment.UserDetailsFragment;
import com.techticz.auth.fragment.LoginFragment;
import com.techticz.auth.utils.LoginUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.fragment.app.FragmentTransaction;
import timber.log.Timber;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,UserDetailsFragment.OnUserFragInteractionListener,LoginFragment.OnLoginFragInteractionListener, FacebookCallback<LoginResult> {

    private static final String TAG = "LoginActivity";
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private ProgressDialog mProgressDialog;
    private LoginFragment loginFrag;
    private UserDetailsFragment userFrag;
    private FirebaseUser currentUser;
    private SharedPreferences pref;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();
        initAuthenticators();
    }

    private void initAuthenticators() {

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mCallbackManager = CallbackManager.Factory.create();
        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
    }

    private void initUI() {
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            displayUserDetails(currentUser);
        } else {
            displayLoginScreen();
        }
    }

    private void displayUserDetails(FirebaseUser currentUser) {
        this.currentUser = currentUser;
        if(userFrag == null){
            userFrag = UserDetailsFragment.newInstance();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //TODO: put tablet and mobile logic here
        transaction.replace(R.id.frag_container, userFrag);
       // transaction.addToBackStack(null);
        transaction.commit();
    }

    private void displayLoginScreen() {
        if(loginFrag == null){
            loginFrag = LoginFragment.newInstance();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //TODO: put tablet and mobile logic here
        transaction.replace(R.id.frag_container, loginFrag);
       // transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Timber.d("Google Signin Status:"+result.getStatus().toString());
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                displayLoginScreen();
                // [END_EXCLUDE]
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Timber.d( "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.d( "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            LoginUtils.performAppLogin(LoginActivity.this,user,AuthProviders.GOOGLE);
                            displayUserDetails(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Timber.e( "signInWithCredential:failure: %s", task.getException());
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(LoginActivity.this, "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address",Toast.LENGTH_SHORT).show();
                            }

                            signOutWithGoogle();
                            displayLoginScreen();
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }


    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOutWithGoogle() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        displayLoginScreen();
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        displayLoginScreen();
                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Timber.d( "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    @Override
    public void onLogoutClick() {
        AuthProviders ap = LoginUtils.getCurrentAuthProvider(this);
        if(ap != null){
            if(ap == AuthProviders.GOOGLE){
                signOutWithGoogle();
            } else if (ap == AuthProviders.FACEBOOK){
                signOutWithFacebook();
            }
        }
        currentUser = null;
        LoginUtils.performAppLogout(this);
        displayLoginScreen();
    }

    @Override
    public void onProceedClick() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onLoginClick(AuthProviders provider) {
        if(provider == AuthProviders.GOOGLE){
            signInWithGoogle();
        }
    }

    @Override
    public void onSubmitOtpClick() {

    }

    @Override
    public void onUserLoggedIn(FirebaseUser user, AuthProviders provider) {

    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public CallbackManager getFBCallBackManager() {
        return mCallbackManager;
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        Timber.d( "facebook:onSuccess:" + loginResult);
        handleFacebookAccessToken(loginResult.getAccessToken());
    }

    @Override
    public void onCancel() {
        Timber.d( "facebook:onCancel");
        // [START_EXCLUDE]
        Toast.makeText(LoginActivity.this, "Facebook Login canceled.",
                Toast.LENGTH_SHORT).show();
        // [END_EXCLUDE]
    }

    @Override
    public void onError(FacebookException error) {
        Timber.d( "facebook:onError %s", error);
        // [START_EXCLUDE]
        Toast.makeText(LoginActivity.this, "Facebook Login failed.",
                Toast.LENGTH_SHORT).show();
        // [END_EXCLUDE]
    }

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Timber.d( "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.d( "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            LoginUtils.performAppLogin(LoginActivity.this,user,AuthProviders.FACEBOOK);
                            displayUserDetails(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Timber.e( "signInWithCredential:failure: %s", task.getException());
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(LoginActivity.this, "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address",Toast.LENGTH_SHORT).show();
                            }


                            signOutWithFacebook();
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_facebook]

    public void signOutWithFacebook() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        LoginUtils.performAppLogout(this);
        displayLoginScreen();
    }

}
