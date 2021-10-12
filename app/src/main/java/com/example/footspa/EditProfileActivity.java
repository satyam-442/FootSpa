package com.example.footspa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    Button uploadImageBtn;
    TextView selectImageTV,updateDetailsBtn;
    CircleImageView profile_image_edit;

    DatabaseReference usersRef;
    FirebaseAuth mAuth;

    Uri imageUri;
    String myUri = "";
    StorageTask uploadTask;
    StorageReference profileRef;
    ProgressDialog loadingBar;
    TextInputLayout firstNameEdit, lastNameEdit, emailEdit, phoneEdit, addressEdit, genderEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        profileRef = FirebaseStorage.getInstance().getReference().child("Profile Pics");

        loadingBar = new ProgressDialog(this);

        firstNameEdit = findViewById(R.id.firstNameEdit);
        lastNameEdit = findViewById(R.id.lastNameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        addressEdit = findViewById(R.id.addressEditET);
        genderEdit = findViewById(R.id.genderEditET);

        updateDetailsBtn = findViewById(R.id.updateDetailsBtn);

        uploadImageBtn = findViewById(R.id.uploadImageBtn);
        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadProfileImage();
            }
        });

        selectImageTV = findViewById(R.id.selectImageTV);
        selectImageTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1, 1).start(EditProfileActivity.this);
            }
        });

        profile_image_edit = findViewById(R.id.profile_image_edit);

        loadingBar.show();
        getUserInfo();
        updateDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDetails();
            }
        });
    }

    private void updateUserDetails() {
        String fname = firstNameEdit.getEditText().getText().toString();
        String lname = lastNameEdit.getEditText().getText().toString();
        String email = emailEdit.getEditText().getText().toString();
        String phone = phoneEdit.getEditText().getText().toString();
        String address = addressEdit.getEditText().getText().toString();
        String gender = genderEdit.getEditText().getText().toString();
        if (TextUtils.isEmpty(fname) || TextUtils.isEmpty(lname) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)){
            Toast.makeText(getApplicationContext(), "Fields are empty", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setMessage("processing...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            String uid = FirebaseAuth.getInstance().getUid();
            HashMap usersMap = new HashMap();
            usersMap.put("FirstName",fname.trim());
            usersMap.put("LastName",lname.trim());
            usersMap.put("Email",email.trim());
            usersMap.put("Phone",phone);
            usersMap.put("Gender",gender.trim());
            usersMap.put("Address",address.trim());
            usersRef.child(uid).updateChildren(usersMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        loadingBar.dismiss();
                        Toast.makeText(getApplicationContext(), "Data Updated", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void getUserInfo() {
        loadingBar.show();
        usersRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fname = snapshot.child("FirstName").getValue().toString();
                    String lname = snapshot.child("LastName").getValue().toString();
                    String email = snapshot.child("Email").getValue().toString();
                    String gender = snapshot.child("Gender").getValue().toString();
                    String phone = snapshot.child("Phone").getValue().toString();
                    String address = snapshot.child("Address").getValue().toString();

                    firstNameEdit.getEditText().setText(fname);
                    lastNameEdit.getEditText().setText(lname);
                    emailEdit.getEditText().setText(email);
                    phoneEdit.getEditText().setText(phone);
                    addressEdit.getEditText().setText(address);
                    genderEdit.getEditText().setText(gender);

                    final String image = snapshot.child("image").getValue().toString();
                    if (!image.equals("default")) {
                        selectImageTV.setText("Change Img");

                        Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.profile).into(profile_image_edit);
                        Picasso.with(getApplicationContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(profile_image_edit, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.profile).into(profile_image_edit);
                            }
                        });
                    }
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profile_image_edit.setImageURI(imageUri);
        } else {
            Toast.makeText(getApplicationContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }

    private void UploadProfileImage() {
        loadingBar.setMessage("please wait");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        if (imageUri != null) {
            final StorageReference fileRef = profileRef.child(mAuth.getCurrentUser().getUid() + ".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        myUri = downloadUrl.toString();
                        HashMap imageMap = new HashMap();
                        imageMap.put("image", myUri);
                        usersRef.child(mAuth.getCurrentUser().getUid()).updateChildren(imageMap);
                        loadingBar.dismiss();
                    }
                }
            });
        } else {
            loadingBar.dismiss();
            Toast.makeText(getApplicationContext(), "Image not selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.male:
                if (checked)
                    genderEdit.getEditText().setText("Male");
                break;
            case R.id.female:
                if (checked)
                    genderEdit.getEditText().setText("Female");
                break;
            case R.id.others:
                if (checked)
                    genderEdit.getEditText().setText("other");
                break;
        }
    }
}