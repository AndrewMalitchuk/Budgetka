package com.example.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anychart.anychart.AnyChart;
import com.anychart.anychart.AnyChartView;
import com.anychart.anychart.DataEntry;
import com.anychart.anychart.Pie;
import com.anychart.anychart.ValueDataEntry;
import com.example.budgetka.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Андрей on 12.05.2018.
 */

public class PieChartFragment extends Fragment {


    private Button button1;

    private Button button2;

    private EditText editText1;
    private EditText editText2;

    public static PieChartFragment newInstance() {


        PieChartFragment fragment = new PieChartFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.piechartboard, container, false);


        button1 = rootView.findViewById(R.id.button);
        button2 = rootView.findViewById(R.id.button3);

        editText1 = rootView.findViewById(R.id.editText);
        editText1.setEnabled(false);
        editText2 = rootView.findViewById(R.id.editText2);
        editText2.setEnabled(false);

        Pie pie = AnyChart.pie();


        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("то", 200));
        data.add(new ValueDataEntry("сє", 300));
        data.add(new ValueDataEntry("пятое", 400));
        data.add(new ValueDataEntry("десятое", 500));
//
        AnyChartView anyChartView = (AnyChartView) rootView.findViewById(R.id.any_chart_view);

        anyChartView.setChart(pie);

//
        pie.setData(data);


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
                                                                                         editText1.setText(months + "/" + days + "/" + years);
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
                                                                                         editText2.setText(months + "/" + days + "/" + years);
//
                                                                                     }
                                                                                 }, now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
//                datePickerDialog.setMinDate(now);

                datePickerDialog.show(getActivity().getFragmentManager(), "Datepicker");
            }
        });


        return rootView;
    }


}