package com.example.game.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.game.R;
import com.example.game.activities.LoginActivity;
import com.example.game.databinding.FragmentSignupBinding;
import com.example.game.helpers.NavigationUtil;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupFragment extends Fragment {
    private static final String TAG = "SignupActivity";

    private FragmentSignupBinding binding;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etName;
    private EditText etRepeatedPassword;
    private Button btnSignup;
    private TextView tvLoginMessage;

    public SignupFragment(){
        // Required empty public constructor
    }

    public static SignupFragment newInstance() {
        SignupFragment fragment = new SignupFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentSignupBinding.bind(view);
        etEmail = binding.etMail;
        etPassword = binding.etPassword;
        etName = binding.etName;
        etRepeatedPassword = binding.etRepeatPassword;
        btnSignup = binding.btnSignUp;
        tvLoginMessage = binding.tvLoginMessage;

        //set a listener on the btnSignup
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = etEmail.getText().toString();
                String name = etName.getText().toString();
                final String password = etPassword.getText().toString();
                String repeatedPassword = etRepeatedPassword.getText().toString();
                if (password.equals(repeatedPassword)) {
                    ParseUser user = new ParseUser();
                    user.put("name", name);
                    user.setUsername(email);
                    user.setPassword(password);
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error Signup: " + e);
                            } else {
                                login(email, password);
                            }
                        }
                    });
                } else {
                    Snackbar.make(btnSignup, "Password and Repeated Password should match",
                            BaseTransientBottomBar.LENGTH_SHORT).show();
                    etPassword.setBackground(getActivity().getDrawable(R.drawable.edit_text_border_danger));
                    etRepeatedPassword.setBackground(getActivity().getDrawable(R.drawable.edit_text_border_danger));
                }
            }
        });

        //set a listener on the tvLoginMessage
        tvLoginMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationUtil.goToActivity(getActivity(), LoginActivity.class);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    private void login(String email, String password) {
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error Logging in: " + e);
                } else {
                    Toast.makeText(getContext(), "Successful logged in", Toast.LENGTH_SHORT).show();
                    Fragment fragment = new TakePictureFragment();
                    getFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
                }
            }
        });
    }
}