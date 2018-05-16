package com.example.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class DashboardFragment extends Fragment implements OnDateSelectedListener, OnMonthChangedListener {

    private SlidingUpPanelLayout slidingLayout;
    private TextView txtViewSelectedDate;
    private MaterialCalendarView widget;
    private FloatingActionButton floatingActionButton;
    private TextView txtViewTotalMonthSum;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDB;

    private RecyclerView recyclerView;
    private ArrayList<Item> itemSpinnerList = new ArrayList<>();
    private ModelAdapter spinnerAdapter;

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
        View rootView = inflater.inflate(R.layout.dashboard_fragment, container, false);

        slidingLayout = rootView.findViewById(R.id.slidingLayout);
        floatingActionButton = rootView.findViewById(R.id.floatingActionButton);

        txtViewSelectedDate = rootView.findViewById(R.id.selectedDate);
        widget = rootView.findViewById(R.id.calendarView);

        Calendar calendar = Calendar.getInstance();
        widget.setDateSelected(calendar.getTime(), true);

        widget.setOnDateChangedListener(this);
        widget.setOnMonthChangedListener(this);

        txtViewSelectedDate.setText(getSelectedDatesString());

        recyclerView = rootView.findViewById(R.id.recyclerView);

        txtViewTotalMonthSum = rootView.findViewById(R.id.txtViewTotalMonthSum);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(getSelectedDatesString());
            }
        });

        getTotalSumForMonth(CalendarDay.from(CalendarDay.today().getYear(), CalendarDay.today().getMonth(), 1));
        initSliderContent(getSelectedDatesString());
        return rootView;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        txtViewSelectedDate.setText(getSelectedDatesString());
        cleanSliderContent();
        initSliderContent(getSelectedDatesString());
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        txtViewTotalMonthSum.setText("0/0");
        getTotalSumForMonth(date);
    }

    private String getSelectedDatesString() {
        CalendarDay date = widget.getSelectedDate();
        date = widget.getSelectedDate();
        Format formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date.getDate());
    }

    public void showDialog(String date) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.costs_dialog,
                null, false);

        final Item item = new Item();
        item.setDate(date);

        final EditText inputCostsValue = formElementsView.findViewById(R.id.editText3);

        final RadioGroup inOutCostsRadioGroup = formElementsView
                .findViewById(R.id.inOutRadioGroup);

        final Spinner costsTypeSpinner = formElementsView.findViewById(R.id.spinner2);

        final RadioButton inCostsRadioButton = formElementsView.findViewById(R.id.inRadioButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.cost_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        costsTypeSpinner.setAdapter(adapter);
        inOutCostsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId = inOutCostsRadioGroup
                        .getCheckedRadioButtonId();
                RadioButton selectedRadioButton = formElementsView
                        .findViewById(selectedId);
                if (selectedRadioButton.equals(inCostsRadioButton)) {
                    item.setCosts(true);
                    item.setType("null");
                    costsTypeSpinner.setEnabled(false);
                } else {
                    item.setCosts(false);
                    item.setType(costsTypeSpinner.getSelectedItem().toString());
                    costsTypeSpinner.setEnabled(true);
                }
            }
        });

        new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).setView(formElementsView)
                .setTitle(date)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                        item.setPrice(Double.valueOf(String.valueOf(inputCostsValue.getText())));
                        final String key = FirebaseDatabase.getInstance().getReference().child("listItem").push().getKey();
                        Map<String, Object> listItemValues = item.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/" + user.getUid() + "/" + key, listItemValues);
                        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
                        dialog.cancel();
                    }
                }).show();
    }

    private void cleanSliderContent() {
        itemSpinnerList.clear();
        itemSpinnerList.removeAll(itemSpinnerList);

        recyclerView.getRecycledViewPool().clear();
        recyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());

        recyclerView.getRecycledViewPool().clear();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        spinnerAdapter = new ModelAdapter(itemSpinnerList);
        recyclerView.setAdapter(spinnerAdapter);
    }

    private void initSliderContent(final String date) {
        cleanSliderContent();

        mDB = FirebaseDatabase.getInstance().getReference().child(user.getUid());
        mDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Item post = postSnapshot.getValue(Item.class);
                    if (date.equals(post.getDate())) {
                        itemSpinnerList.add(post);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        spinnerAdapter = new ModelAdapter(itemSpinnerList);
                        recyclerView.setAdapter(spinnerAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getTotalSumForMonth(final CalendarDay date) {
        Calendar calendar = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDay());

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        final Date begin = new Date(date.getYear() - 1900, date.getMonth(), 1);
        final Date end = new Date(date.getYear() - 1900, date.getMonth(), daysInMonth);

        final double[] inCosts = {0};
        final double[] outCosts = {0};
        final String[] value = {null};

        if (begin.after(end)) {
            Toast.makeText(getContext(), "Помилка. Некоректний проміжок", Toast.LENGTH_SHORT).show();
        } else {
            try {
                mDB = FirebaseDatabase.getInstance().getReference().child(user.getUid());
                mDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Item post = postSnapshot.getValue(Item.class);
                            Date temp = null;
                            try {
                                temp = format.parse(post.getDate());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (temp.after(begin) || temp.equals(begin)) {
                                if (temp.before(end) || temp.equals(end)) {
                                    if (post.getType().equals("null")) {
                                        inCosts[0] += post.getPrice();
                                    } else {
                                        outCosts[0] += post.getPrice();
                                    }
                                    value[0] = Double.valueOf(inCosts[0]) + "/" + Double.valueOf(outCosts[0]);
                                    try {
                                        txtViewTotalMonthSum.setText(value[0]);
                                    } catch (Exception e) {
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
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
    }
}