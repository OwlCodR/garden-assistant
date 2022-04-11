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
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.garden_assistant.gardenassistant.MainActivity.user;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private FirebaseAuth mAuth;
    private View myInflatedView;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mRepeatPasswordField;
    private DatabaseReference mDatabase;
    private TextInputLayout textInputLayoutEmail, textInputLayoutPassword, textInputLayoutRepeatPassword;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_register, container,false);

        mEmailField = myInflatedView.findViewById(R.id.field_email_register);
        mPasswordField = myInflatedView.findViewById(R.id.field_password_register);
        mRepeatPasswordField = myInflatedView.findViewById(R.id.field_repeat_password_register);

        textInputLayoutEmail = (TextInputLayout) myInflatedView.findViewById(R.id.textInputLayoutEmailRegister);
        textInputLayoutPassword = (TextInputLayout) myInflatedView.findViewById(R.id.textInputLayoutPasswordRegister);
        textInputLayoutRepeatPassword = (TextInputLayout) myInflatedView.findViewById(R.id.textInputLayoutRepeatPasswordRegister);

        myInflatedView.findViewById(R.id.button_register).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        return myInflatedView;
    }

    private void createAccount(String email, final String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                            user = mAuth.getCurrentUser();

                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            mDatabase.child("users").child(user.getUid()).child("countGreenhouse").setValue(0);
                            mDatabase.child("users").child(user.getUid()).child("countVegetables").setValue(0);
                            mDatabase.child("users").child(user.getUid()).child("Vegetables");

                            Fragment fragment = new GreenhouseTabsFragment();
                            FragmentTransaction ft = getFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, fragment)
                                    .addToBackStack(null);
                            ft.commit();
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Моя Теплица");
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

        String repeatPassword = mRepeatPasswordField.getText().toString();
        if (!repeatPassword.equals(password)) {
            textInputLayoutRepeatPassword.setErrorEnabled(true);
            textInputLayoutRepeatPassword.setError("Пароли не совпадают");
            valid = false;
        } else {
            textInputLayoutRepeatPassword.setErrorEnabled(false);
        }
        return valid;
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
    public void onClick(View view) {
        createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
    }
}
