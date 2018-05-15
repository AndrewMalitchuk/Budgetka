package com.example.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.budgetka.MainActivity;
import com.example.budgetka.R;
import com.example.models.Item;
import com.example.models.ModelAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
//import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

/**
 * Created by Андрей on 11.05.2018.
 */

public class ColumnChartFragment extends Fragment {


    private Button button1;
    private Button button2;
    private Button button3;

    private Spinner spinner;

    private EditText editText1;
    private EditText editText2;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mDB;

    private RecyclerView recyclerView;
    private ArrayList<Item> item_list = new ArrayList<>();
    private ModelAdapter mAdapter;

    public static ColumnChartFragment newInstance() {


        ColumnChartFragment fragment = new ColumnChartFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.columnchartboard, container, false);


        button1 = rootView.findViewById(R.id.button);
        button2 = rootView.findViewById(R.id.button3);
        button3 = rootView.findViewById(R.id.button4);

        editText1 = rootView.findViewById(R.id.editText);
        editText1.setEnabled(false);
        editText2 = rootView.findViewById(R.id.editText2);
        editText2.setEnabled(false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                                                                                     @Override
                                                                                     public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                                                                         String years = "" + year;
                                                                                         String months = "" + (monthOfYear + 1);
                                                                                         String days = "" + dayOfMonth;
                                                                                         if (monthOfYear >= 0 && monthOfYear < 9) {
                                                                                             months = "0" + (monthOfYear + 1);
                                                                                         }
                                                                                         if (dayOfMonth > 0 && dayOfMonth < 10) {
                                                                                             days = "0" + dayOfMonth;

                                                                                         }
                                                                                         editText1.setText(days + "/" + months + "/" + years);
//
                                                                                     }
                                                                                 }, now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
//                datePickerDialog.setMinDate(now);

                datePickerDialog.show(getActivity().getFragmentManager(), "Datepicker");
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                                                                                     @Override
                                                                                     public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                                                                         String years = "" + year;
                                                                                         String months = "" + (monthOfYear + 1);
                                                                                         String days = "" + dayOfMonth;
                                                                                         if (monthOfYear >= 0 && monthOfYear < 9) {
                                                                                             months = "0" + (monthOfYear + 1);
                                                                                         }
                                                                                         if (dayOfMonth > 0 && dayOfMonth < 10) {
                                                                                             days = "0" + dayOfMonth;

                                                                                         }
                                                                                         editText2.setText(days + "/" + months + "/" + years);
//
                                                                                     }
                                                                                 }, now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
//                datePickerDialog.setMinDate(now);

                datePickerDialog.show(getActivity().getFragmentManager(), "Datepicker");
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                item_list.clear();
                item_list.removeAll(item_list);

                recyclerView.getRecycledViewPool().clear();
                recyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());


                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mAdapter = new ModelAdapter(item_list);
                recyclerView.setAdapter(mAdapter);

                initList();
            }
        });


        spinner = rootView.findViewById(R.id.spinner3);

        try {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.planets_array, R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        return rootView;
    }

    public void clean() {
        item_list.clear();
        item_list.removeAll(item_list);

        recyclerView.getRecycledViewPool().clear();
        recyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ModelAdapter(item_list);
        recyclerView.setAdapter(mAdapter);
    }

    public void initList() {


        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        Date begin = new Date();
        Date end = new Date();

        try {
            begin = format.parse(String.valueOf(editText1.getText()));
            end = format.parse(String.valueOf(editText2.getText()));
        } catch (ParseException e) {
            e.printStackTrace();
        }


//        Toast.makeText(getContext(), "@"+date, Toast.LENGTH_SHORT).show();

        mDB = FirebaseDatabase.getInstance().getReference().child(user.getUid());


        final Date finalBegin = begin;
        final Date finalEnd = end;

        if (begin.after(end)) {
            Toast.makeText(getContext(), "Помилка. Некоректний проміжок", Toast.LENGTH_SHORT).show();
        } else {

            mDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Item post = postSnapshot.getValue(Item.class);


                        Date temp = null;
                        try {


                            temp = format.parse(post.getDate());

//                            Toast.makeText(getContext(),"temp="+temp,Toast.LENGTH_LONG).show();


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        if (temp.after(finalBegin) || temp.equals(finalBegin)) {

                            if (temp.before(finalEnd) || temp.equals(finalEnd)) {


                                if (post.getType().equals(spinner.getSelectedItem().toString())) {

                                    item_list.add(post);


                                    recyclerView.setHasFixedSize(true);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                    mAdapter = new ModelAdapter(item_list);
                                    recyclerView.setAdapter(mAdapter);
                                }

                            }
                        }


////                        Toast.makeText(getContext(), Double.toString(post.getPrice()), Toast.LENGTH_SHORT).show();
//                        item_list.add(post);
//                        recyclerView.setHasFixedSize(true);
//                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                        mAdapter = new ModelAdapter(item_list);
//                        recyclerView.setAdapter(mAdapter);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });
        }

    }


}

