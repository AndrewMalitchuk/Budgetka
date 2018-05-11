package com.example.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.budgetka.R;

/**
 * Created by Андрей on 10.05.2018.
 */

public class TestFragment1 extends Fragment {




    public static TestFragment1 newInstance() {


        TestFragment1 t = new TestFragment1();

        return t;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.launch1, container, false);




        return rootView;
    }



}