package com.example.footspa.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.footspa.EditProfileActivity;
import com.example.footspa.R;
import com.example.footspa.databinding.FragmentProfileBinding;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentProfile extends Fragment {

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    TextView profileName, profilePhone, profileEmail, profileGender, profileAddress, profileImageTV;
    FirebaseAuth mAuth;
    String uid;
    DatabaseReference usersRef;
    CircleImageView setupProfileImage, profileImageEdit;
    Uri imageUri;
    StorageReference storagePicRef;
    String myUrl = "";
    String checker = "";
    StorageTask uploadTask;
    Button editDetailsBtn, selectImageBtn;
    LinearLayout profileDetails, editDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        storagePicRef = FirebaseStorage.getInstance().getReference().child("ProfilePictures");

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        profileName = binding.profileName;
        profilePhone = binding.profilePhone;
        profileEmail = binding.profileEmail;
        profileGender = binding.profileGender;
        profileAddress = binding.profileAddress;
        profileDetails = binding.profileLinearLayout;
        setupProfileImage = binding.profileImageProfile;

        editDetailsBtn = binding.editDetailsBtn;
        editDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(edit);
            }
        });

        /*profileImageTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start((Activity) getContext());
            }
        });*/

        profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                usersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String fname = snapshot.child("FirstName").getValue().toString();
                            String lname = snapshot.child("LastName").getValue().toString();
                            String phone = snapshot.child("Phone").getValue().toString();
                            String email = snapshot.child("Email").getValue().toString();
                            String gender = snapshot.child("Gender").getValue().toString();
                            String address = snapshot.child("Address").getValue().toString();
                            profileName.setText(fname+" "+lname);
                            profilePhone.setText(phone);
                            profileEmail.setText(email);
                            profileGender.setText(gender);
                            profileAddress.setText(address);
                            final String image = snapshot.child("image").getValue().toString();
                            if (!image.equals("default")) {
                                Picasso.with(getActivity()).load(image).placeholder(R.drawable.profile).into(setupProfileImage);
                                Picasso.with(getActivity()).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(setupProfileImage, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(getActivity()).load(image).placeholder(R.drawable.profile).into(setupProfileImage);
                                    }
                                });
                            }
                            /*if (phone.equals("NP")){
                                Snackbar snackbar = Snackbar.make(container,"Update the address", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Do It", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                                                bottomSheetDialog.setContentView(R.layout.bottom_layout);
                                                bottomSheetDialog.setCanceledOnTouchOutside(false);

                                                bottomSheetDialog.show();
                                            }
                                        });
                                snackbar.show();
                            }*/
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}