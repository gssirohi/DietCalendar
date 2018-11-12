package com.techticz.auth.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techticz.auth.LoginActivity;
import com.techticz.auth.R;
import com.techticz.auth.constant.AuthProviders;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLoginFragInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private OnLoginFragInteractionListener mListener;
    private LinearLayout ll_phone;
    private LinearLayout ll_otp;
    private TextInputLayout tiv_phone;
    private TextInputLayout tiv_otp;
    private TextView tv_resent;
    private TextView b_login;
    private TextView b_submit_otp;
    private SignInButton b_google_sign_in;
    private LoginButton b_fb_sign_in;


    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.frag_login, container, false);
        initUiFields(view);
        return view;
    }

    private void initUiFields(ViewGroup view) {
        ll_phone = (LinearLayout)view.findViewById(R.id.ll_phone_number);
        ll_otp = (LinearLayout)view.findViewById(R.id.ll_otp);
        tiv_phone = (TextInputLayout)view.findViewById(R.id.tiv_phone);
        tiv_otp = (TextInputLayout)view.findViewById(R.id.tiv_otp);
        tv_resent = (TextView)view.findViewById(R.id.tv_resend);

        b_login = (TextView)view.findViewById(R.id.b_login);
        b_submit_otp = (TextView)view.findViewById(R.id.b_submit_otp);

        b_google_sign_in = (SignInButton)view.findViewById(R.id.sign_in_button);
        b_fb_sign_in = (LoginButton)view.findViewById(R.id.button_facebook_login);

        registerFBCallBack();
        b_google_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onLoginClick(AuthProviders.GOOGLE);
            }
        });
    }

    private void registerFBCallBack() {
        b_fb_sign_in.setReadPermissions("email", "public_profile");
        b_fb_sign_in.registerCallback(((LoginActivity)getActivity()).getFBCallBackManager()
                ,(LoginActivity)getActivity());
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragInteractionListener) {
            mListener = (OnLoginFragInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginFragInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnLoginFragInteractionListener {
        // TODO: Update argument type and name
        void onLoginClick(AuthProviders google);
        void onSubmitOtpClick();
        void onUserLoggedIn(FirebaseUser user, AuthProviders provider);
    }
}
