package com.techticz.auth.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.techticz.auth.LoginActivity;
import com.techticz.auth.R;
import com.techticz.auth.constant.AuthProviders;
import com.techticz.auth.utils.LoginUtils;
import com.google.firebase.auth.FirebaseUser;
import com.techticz.powerkit.customview.RoundImageView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnUserFragInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDetailsFragment extends Fragment {

    private OnUserFragInteractionListener mListener;
    private RoundImageView riv_user;
    private TextView tv_name;
    private TextView tv_email;
    private TextView tv_phone;
    private TextView tv_firebase_user_id;
    private ImageView iv_auth_proovider;
    private Button b_logout;
    private Button b_proceed;

    public UserDetailsFragment() {
        // Required empty public constructor
    }

    public static UserDetailsFragment newInstance() {
        UserDetailsFragment fragment = new UserDetailsFragment();
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
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_logged_in_user_details, container, false);
        initUiFields(view);
        return view;
    }

    private void initUiFields(ViewGroup view) {
        riv_user = (RoundImageView)view.findViewById(R.id.iv_user);
        tv_name = (TextView)view.findViewById(R.id.tv_user_name);
        tv_email = (TextView)view.findViewById(R.id.tv_user_email);
        tv_phone = (TextView)view.findViewById(R.id.tv_user_phone);
        tv_firebase_user_id = (TextView)view.findViewById(R.id.tv_firebase_id);
        iv_auth_proovider  = (ImageView)view.findViewById(R.id.iv_provider);
        b_logout = (Button)view.findViewById(R.id.b_logout);
        b_proceed = (Button)view.findViewById(R.id.b_proceed);

        b_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onLogoutClick();
            }
        });

        b_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onProceedClick();
            }
        });

        fillDetails();
    }

    private void fillDetails() {
        FirebaseUser user = ((LoginActivity) getActivity()).getCurrentUser();
        if(user == null) return;

        riv_user.setUrl(user.getPhotoUrl().toString());
        tv_name.setText(user.getDisplayName());
        tv_email.setText(user.getEmail());
        tv_phone.setText(user.getPhoneNumber());
        tv_firebase_user_id.setText(user.getUid());

        AuthProviders ap = LoginUtils.getCurrentAuthProvider(getContext());
        if(ap != null){
            if(ap == AuthProviders.GOOGLE)
            iv_auth_proovider.setImageResource(R.drawable.googleg_standard_color_18);
            if(ap == AuthProviders.FACEBOOK)
                iv_auth_proovider.setImageResource(R.drawable.com_facebook_button_icon_white);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserFragInteractionListener) {
            mListener = (OnUserFragInteractionListener) context;
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
    public interface OnUserFragInteractionListener {
        // TODO: Update argument type and name
        void onLogoutClick();

        void onProceedClick();
    }
}
