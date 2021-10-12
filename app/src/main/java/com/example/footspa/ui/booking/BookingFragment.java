package com.example.footspa.ui.booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.footspa.R;
import com.example.footspa.databinding.FragmentBookingBinding;

public class BookingFragment extends Fragment {

    private BookingViewModel bookingViewModel;
    private FragmentBookingBinding binding;
    ImageView addBookingImg, closeBookingImg;
    LinearLayout bookingLayout;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        binding = FragmentBookingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textGallery;

        bookingLayout = binding.bookingLayout;
        addBookingImg = binding.addBooking;
        addBookingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookingImg.setVisibility(View.GONE);
                closeBookingImg.setVisibility(View.VISIBLE);
                bookingLayout.setVisibility(View.VISIBLE);
                //remarkTxt.setText("Write remarks below");
            }
        });

        closeBookingImg = binding.closeBooking;
        closeBookingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookingImg.setVisibility(View.VISIBLE);
                closeBookingImg.setVisibility(View.GONE);
                bookingLayout.setVisibility(View.GONE);
                //remarkTxt.setText("Remarks");
            }
        });

        bookingViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
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