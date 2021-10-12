package com.example.footspa.ui.home;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.footspa.R;
import com.example.footspa.databinding.FragmentHomeBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class HomeFragment extends Fragment {

    FirebaseAuth mAuth;
    String uid;
    DatabaseReference usersRef;
    ProgressDialog loadingBar;
    TextInputLayout phoneInput;
    Button updatePhoneBtn;
    TextView userNameText, hairCareTextView;
    boolean isTextViewClicked = false;

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        loadingBar = new ProgressDialog(getContext());

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        /*final TextView textView = binding.textHome;*/
        userNameText = binding.userNameText;
        hairCareTextView = binding.hairCareTextView;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                /*textView.setText(s);*/
                usersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String phone = snapshot.child("Phone").getValue().toString();
                            String name = snapshot.child("FirstName").getValue().toString();
                            userNameText.setText(name);
                            if (phone.equals("NV")){
                                Snackbar snackbar = Snackbar.make(container,"Update the phone no.", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Do It", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                                                bottomSheetDialog.setContentView(R.layout.bottom_layout);
                                                bottomSheetDialog.setCanceledOnTouchOutside(false);

                                                phoneInput = bottomSheetDialog.findViewById(R.id.userPhoneLay);
                                                updatePhoneBtn = bottomSheetDialog.findViewById(R.id.updatePhone);

                                                updatePhoneBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        loadingBar.setMessage("updating...");
                                                        loadingBar.setCanceledOnTouchOutside(false);
                                                        loadingBar.show();
                                                        String phone = phoneInput.getEditText().getText().toString();
                                                        HashMap phoneMap = new HashMap();
                                                        phoneMap.put("Phone",phone);
                                                        usersRef.updateChildren(phoneMap);
                                                        loadingBar.dismiss();
                                                        bottomSheetDialog.dismiss();
                                                        Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                                bottomSheetDialog.show();
                                            }
                                        });
                                snackbar.show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                /*hairCareTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isTextViewClicked){
                            //This will shrink textview to 2 lines if it is expanded.
                            hairCareTextView.setMaxLines(1);
                            isTextViewClicked = false;
                        } else {
                            //This will expand the textview if it is of 2 lines
                            hairCareTextView.setMaxLines(Integer.MAX_VALUE);
                            //hairCareTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
                            isTextViewClicked = true;
                        }
                    }
                });*/
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
