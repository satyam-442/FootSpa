package com.example.footspa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    TextInputLayout firstNameInp, lastNameInp, emailInp, pwdInp, rePwdInp;
    EditText genderET;
    TextView registerBtn;
    FirebaseAuth mAuth;
    ProgressDialog loadingBar;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference();

        firstNameInp = findViewById(R.id.firstNameRegister);
        lastNameInp = findViewById(R.id.lastNameRegister);
        emailInp = findViewById(R.id.emailRegister);
        pwdInp = findViewById(R.id.passwordRegister);
        rePwdInp = findViewById(R.id.rePasswordRegister);

        loadingBar = new ProgressDialog(this);

        genderET = findViewById(R.id.genderText);
        registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname = firstNameInp.getEditText().getText().toString();
                String lname = lastNameInp.getEditText().getText().toString();
                String email = emailInp.getEditText().getText().toString();
                String pwd = pwdInp.getEditText().getText().toString();
                String rePwd = rePwdInp.getEditText().getText().toString();
                String gender = genderET.getText().toString();
                if (TextUtils.isEmpty(fname) || TextUtils.isEmpty(lname) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(rePwd)){
                    Snackbar.make(v, "Some field's are empty", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
                else if(!pwd.equals(rePwd))
                {
                    Snackbar.make(v, "Password Mismatched", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
                else {
                    loadingBar.setMessage("processing...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    mAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String uid = FirebaseAuth.getInstance().getUid();
                                HashMap usersMap = new HashMap();
                                usersMap.put("uid",uid);
                                usersMap.put("FirstName",fname);
                                usersMap.put("LastName",lname);
                                usersMap.put("Email",email);
                                usersMap.put("Password",pwd);
                                usersMap.put("Phone","NV");
                                usersMap.put("Gender",gender);
                                usersMap.put("image","default");
                                usersMap.put("Address","NP");
                                usersRef.child("Users").child(uid).updateChildren(usersMap);
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            else {
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "Error Occurred ;" + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
                }
            }
        });

    }

    /*public void goToDashBoard(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }*/

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.male:
                if (checked)
                    // Pirates are the best
                    genderET.setText("Male");
                    break;
            case R.id.female:
                if (checked)
                    genderET.setText("Female");
                    break;
            case R.id.others:
                if (checked)
                    genderET.setText("other");
                    break;
        }
    }
}