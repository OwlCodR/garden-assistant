package com.garden_assistant.gardenassistant;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.garden_assistant.gardenassistant.MainActivity.user;

public class LoginFragment extends Fragment implements
        View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPasswordField;
    private View myInflatedView;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextInputLayout textInputLayoutEmail, textInputLayoutPassword;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_login, container,false);

        mEmailField = myInflatedView.findViewById(R.id.field_email_login);
        mPasswordField = myInflatedView.findViewById(R.id.field_password_login);

        textInputLayoutEmail = (TextInputLayout) myInflatedView.findViewById(R.id.textInputLayoutEmailLogin);
        textInputLayoutPassword = (TextInputLayout) myInflatedView.findViewById(R.id.textInputLayoutPasswordLogin);

        myInflatedView.findViewById(R.id.button_login).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        return myInflatedView;
    }



    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();

                            Log.d(TAG, "signInWithEmail:success");
                            Fragment fragment = new GreenhouseTabsFragment();
                            FragmentTransaction ft = getFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, fragment)
                                    .addToBackStack(null);
                            ft.commit();
                            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Моя Теплица");
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Не удалось войти.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            textInputLayoutEmail.setErrorEnabled(true);
            textInputLayoutEmail.setError("Заполните поле");
            valid = false;
        } else {
            textInputLayoutEmail.setErrorEnabled(false);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            textInputLayoutPassword.setErrorEnabled(true);
            textInputLayoutPassword.setError("Заполните поле");
            valid = false;
        } else {
            textInputLayoutPassword.setErrorEnabled(false);
        }

        if (password.length() < 6) {
            textInputLayoutPassword.setErrorEnabled(true);
            textInputLayoutPassword.setError("Пароль должен содержать не менее 6 символов");
            valid = false;
        } else {
            textInputLayoutPassword.setErrorEnabled(false);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
    }

    @VisibleForTesting
    public ProgressDialog mProgressDialog;
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
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
}

