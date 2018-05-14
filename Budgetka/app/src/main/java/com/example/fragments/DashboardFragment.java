package com.example.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetka.MainActivity;
import com.example.budgetka.R;
import com.example.models.Item;
import com.example.models.ModelAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

import static android.R.layout.simple_spinner_item;

/**
 * Created by Андрей on 10.05.2018.
 */

public class DashboardFragment extends Fragment implements OnDateSelectedListener, OnMonthChangedListener {


    private SlidingUpPanelLayout slidingLayout;

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    private TextView selectedDate;

    private MaterialCalendarView widget;

    private FloatingActionButton floatingActionButton;

    private TextView textView4;

    private Spinner spinner2;

    ///
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mDB;
    DatabaseReference mListItemRef;
    //
    private ArrayList<Item> myListItems;

    //
    private RecyclerView recyclerView;
    private Button btn_delete_all;

    private ArrayList<Item> item_list = new ArrayList<>();
    private ModelAdapter mAdapter;


    //
    private TextView textView2;

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dashboard, container, false);

        slidingLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);

        floatingActionButton = rootView.findViewById(R.id.floatingActionButton);


        selectedDate = rootView.findViewById(R.id.selectedDate);
        widget = rootView.findViewById(R.id.calendarView);

        Calendar calendar = Calendar.getInstance();
        widget.setDateSelected(calendar.getTime(), true);

        widget.setOnDateChangedListener(this);
        widget.setOnMonthChangedListener(this);

        selectedDate.setText(getSelectedDatesString());
//
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        textView2=rootView.findViewById(R.id.textView2);



        if (selectedDate == null)
            Toast.makeText(getContext(), "selectedDate==null", Toast.LENGTH_SHORT).show();
        if (widget == null)
            Toast.makeText(getContext(), "widget==null", Toast.LENGTH_SHORT).show();

//        Toast.makeText(getContext(), getSelectedDatesString(), Toast.LENGTH_SHORT).show();


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(getSelectedDatesString());
            }
        });

        return rootView;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        selectedDate.setText(getSelectedDatesString());
//        initControls(getSelectedDatesString());


        item_list.clear();
        recyclerView.getRecycledViewPool().clear();
        recyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());




        initControls(getSelectedDatesString());


    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

//        Toast.makeText(getContext(), "onMonthChanged", Toast.LENGTH_SHORT).show();

        //noinspection ConstantConditions
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(FORMATTER.format(date.getDate()));

        textView2.setText("0/0");
        getInSum(date);


    }

    private String getSelectedDatesString() {

        if (widget == null)
            Toast.makeText(getContext(), "getSelectedDatesString widget==null", Toast.LENGTH_SHORT).show();

        CalendarDay date = widget.getSelectedDate();
        date = widget.getSelectedDate();

        if (widget.getSelectedDate() == null)
            Toast.makeText(getContext(), "getSelectedDatesString widget.getSelectedDate==null", Toast.LENGTH_SHORT).show();

        if (date == null) {
            return "No Selection";
        }

        Format formatter = new SimpleDateFormat("dd/MM/yyyy");

        return formatter.format(date.getDate());

    }


    public void showDialog(String date) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.moneydialog,
                null, false);


        final Item item = new Item();
        item.setDate(date);

        final EditText editText3 = formElementsView.findViewById(R.id.editText3);

        final RadioGroup inOutRadioGroup = (RadioGroup) formElementsView
                .findViewById(R.id.inOutRadioGroup);


        final Spinner spinner2 = formElementsView.findViewById(R.id.spinner2);


        final RadioButton in = (RadioButton) formElementsView.findViewById(R.id.inRadioButton);

        final RadioButton out = (RadioButton) formElementsView.findViewById(R.id.outRadioButton);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter);


        inOutRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                int selectedId = inOutRadioGroup
                        .getCheckedRadioButtonId();

                RadioButton selectedRadioButton = (RadioButton) formElementsView
                        .findViewById(selectedId);

                if (selectedRadioButton.equals(in)) {
                    Toast.makeText(getContext(), "in", Toast.LENGTH_SHORT).show();

                    item.setCosts(true);


                    item.setType("null");

                    spinner2.setEnabled(false);
                } else {
                    Toast.makeText(getContext(), "out", Toast.LENGTH_SHORT).show();

                    item.setCosts(false);

//                    Toast.makeText(getActivity(), spinner2.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

                    item.setType(spinner2.getSelectedItem().toString());

                    spinner2.setEnabled(true);

                }

            }
        });

        // the alert dialog
        new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).setView(formElementsView)
                .setTitle(date)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {


                        item.setPrice(Double.valueOf(String.valueOf(editText3.getText())));


                        final String key = FirebaseDatabase.getInstance().getReference().child("listItem").push().getKey();

                        Map<String, Object> listItemValues = item.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();

                        childUpdates.put("/" + user.getUid() + "/" + key, listItemValues);

                        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);


                        dialog.cancel();
                    }

                }).show();
    }

    private void initControls(final String date) {

        item_list.clear();
        item_list.removeAll(item_list);

        recyclerView.getRecycledViewPool().clear();
        recyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());

        recyclerView.getRecycledViewPool().clear();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ModelAdapter(item_list);
        recyclerView.setAdapter(mAdapter);

        mDB = FirebaseDatabase.getInstance().getReference().child(user.getUid());


        mDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Item post = postSnapshot.getValue(Item.class);

                    if(date.equals(post.getDate())) {

//                        Toast.makeText(getContext(), Double.toString(post.getPrice()), Toast.LENGTH_SHORT).show();
                        item_list.add(post);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        mAdapter = new ModelAdapter(item_list);
                        recyclerView.setAdapter(mAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

    }

    public String getInSum(final CalendarDay date){

        Calendar calendar=new GregorianCalendar(date.getYear(),date.getMonth(),date.getDay());

//        Toast.makeText(getContext(), "@@"+calendar.get, Toast.LENGTH_SHORT).show();



        int daysInMonth=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        final SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");

        final Date begin=new Date(date.getYear()-1900,date.getMonth(),1);
        final Date end=new Date(date.getYear()-1900,date.getMonth(),daysInMonth);

//        Toast.makeText(getContext(), "date="+begin+" | "+end, Toast.LENGTH_SHORT).show();



//        Toast.makeText(getContext(), "max="+daysInMonth, Toast.LENGTH_SHORT).show();

        final double[] in = {0};
        final double[] out = { 0 };
        final String[] value = {null};

//        Double in=new Double(0);
//        Double in=new Double(0);

        if(begin.after(end)){
            Toast.makeText(getContext(), "Помилка. Некоректний проміжок", Toast.LENGTH_SHORT).show();
        }else {

            try {

                mDB = FirebaseDatabase.getInstance().getReference().child(user.getUid());

                mDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Item post = postSnapshot.getValue(Item.class);

//                        Toast.makeText(getContext(), "!"+post.getDate(),Toast.LENGTH_LONG).show();


                            Date temp = null;
                            try {


                                temp = format.parse(post.getDate());

//                            Toast.makeText(getContext(),"temp="+temp,Toast.LENGTH_LONG).show();


                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (temp.after(begin) || temp.equals(begin)) {

                                if (temp.before(end) || temp.equals(end)) {


                                    if (post.getType().equals("null")) {
                                        in[0] += post.getPrice();
//                                    Toast.makeText(getContext(), "in "+post.getPrice(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        out[0] += post.getPrice();
//                                    Toast.makeText(getContext(), "out "+post.getPrice(), Toast.LENGTH_SHORT).show();
                                    }

                                    value[0] = Double.valueOf(in[0]) + "/" + Double.valueOf(out[0]);
//                                Toast.makeText(getContext(), "value="+value[0], Toast.LENGTH_SHORT).show();
                                    try {
                                        textView2.setText(value[0]);
                                    } catch (Exception e) {
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }


                                }

                            }
//
//
//                        if(date.equals(post.getDate())) {
//
//                            Toast.makeText(getContext(), Double.toString(post.getPrice()), Toast.LENGTH_SHORT).show();
//                            item_list.add(post);
//                            recyclerView.setHasFixedSize(true);
//                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                            mAdapter = new ModelAdapter(item_list);
//                            recyclerView.setAdapter(mAdapter);
//                        }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                });


            } catch (Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

//        Toast.makeText(getContext(), "value="+ value[0], Toast.LENGTH_SHORT).show();

        return  null;

    }


}
