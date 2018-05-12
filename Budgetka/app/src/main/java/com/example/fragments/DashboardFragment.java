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
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetka.MainActivity;
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

    private FloatingActionButton floatingActionButton;

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

        floatingActionButton=rootView.findViewById(R.id.floatingActionButton);


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


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("ввв");
            }
        });

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



    public void showDialog(String date) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.moneydialog,
                null, false);


        final Switch mySwitch=formElementsView.findViewById(R.id.switch1);



        // the alert dialog
        new AlertDialog.Builder(getActivity()).setView(formElementsView)
                .setTitle(date)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {

                        String toastString = "";

                        if(mySwitch.isSelected()){
                            mySwitch.setText("Витрати");
                        }else{
                            mySwitch.setText("Надходження");
                        }


/

                        dialog.cancel();
                    }

                }).setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                Toast.makeText(getActivity(), "keklol", Toast.LENGTH_SHORT).show();
                return true;
            }
        }).show();
    }


}
