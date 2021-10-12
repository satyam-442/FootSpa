package com.example.footspa.ui.contact_us;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.footspa.databinding.FragmentContactUsBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ContactUsFragment extends Fragment {

    private ContactUsViewModel contactUsViewModel;
    private FragmentContactUsBinding binding;
    FirebaseAuth mAuth;
    DatabaseReference usersRef;
    String uid;
    TextInputLayout nameET, phoneET, emailET;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contactUsViewModel = new ViewModelProvider(this).get(ContactUsViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        binding = FragmentContactUsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        nameET = binding.nameET;
        emailET = binding.emailET;
        phoneET = binding.phoneET;

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String fname = snapshot.child("FirstName").getValue().toString();
                    String lname = snapshot.child("LastName").getValue().toString();
                    String phone = snapshot.child("Phone").getValue().toString();
                    String email = snapshot.child("Email").getValue().toString();
                    nameET.getEditText().setText(fname+" "+lname);
                    phoneET.getEditText().setText(phone);
                    emailET.getEditText().setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        contactUsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {}
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}