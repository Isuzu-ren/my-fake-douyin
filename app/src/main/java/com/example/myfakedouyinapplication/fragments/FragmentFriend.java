package com.example.myfakedouyinapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myfakedouyinapplication.R;

public class FragmentFriend extends Fragment {
    public FragmentFriend() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, container, false);
        TextView textView = view.findViewById(R.id.fragment_text);
        textView.setText("暂无朋友");
        // view.setBackgroundColor(0xFFFF9800); // 不同背景色区分
        return view;
    }
}
