package com.example.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetka.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;

/**
 * Created by Андрей on 10.05.2018.
 */

public class DashboardFragment extends Fragment implements OnDateSelectedListener, OnMonthChangedListener {


    private SlidingUpPanelLayout slidingLayout;

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    private TextView selectedDate;

    private MaterialCalendarView widget;

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

        selectedDate=rootView.findViewById(R.id.selectedDate);
        widget=rootView.findViewById(R.id.calendarView);

        Calendar calendar = Calendar.getInstance();
        widget.setDateSelected(calendar.getTime(), true);

        widget.setOnDateChangedListener(this);
        widget.setOnMonthChangedListener(this);

        selectedDate.setText(getSelectedDatesString());


        if(selectedDate==null)
            Toast.makeText(getContext(), "selectedDate==null", Toast.LENGTH_SHORT).show();
        if(widget==null)
            Toast.makeText(getContext(), "widget==null", Toast.LENGTH_SHORT).show();

            Toast.makeText(getContext(), getSelectedDatesString(), Toast.LENGTH_SHORT).show();

        return rootView;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        selectedDate.setText(getSelectedDatesString());
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

        Toast.makeText(getContext(), "onMonthChanged", Toast.LENGTH_SHORT).show();

        //noinspection ConstantConditions
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(FORMATTER.format(date.getDate()));
    }

    private String getSelectedDatesString() {

        if(widget==null)
            Toast.makeText(getContext(), "getSelectedDatesString widget==null", Toast.LENGTH_SHORT).show();

        CalendarDay date = widget.getSelectedDate();
        date=widget.getSelectedDate();

        if(widget.getSelectedDate()==null)
            Toast.makeText(getContext(), "getSelectedDatesString widget.getSelectedDate==null", Toast.LENGTH_SHORT).show();

        if (date == null) {
            return "No Selection";
        }
        return FORMATTER.format(date.getDate());
    }

}
