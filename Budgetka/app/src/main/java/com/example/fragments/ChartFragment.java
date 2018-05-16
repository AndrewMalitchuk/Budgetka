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
import com.example.models.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChartFragment extends Fragment {

    private Button btnBegDateChoose;
    private Button btnEndDateChoose;
    private Button btnExecute;
    private Button btnClean;

    private EditText inputBegDate;
    private EditText inputEndDate;

    private AnyChartView anyChartView;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDB;

    public static ChartFragment newInstance() {
        ChartFragment fragment = new ChartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chart_fragment, container, false);

        btnBegDateChoose = rootView.findViewById(R.id.btnBegDateChoose);
        btnEndDateChoose = rootView.findViewById(R.id.btnEndDateChoose);
        btnExecute = rootView.findViewById(R.id.btnExecute);
        btnClean = rootView.findViewById(R.id.btnClean);

        inputBegDate = rootView.findViewById(R.id.inputBegDate);
        inputBegDate.setEnabled(false);
        inputEndDate = rootView.findViewById(R.id.inputEndDate);
        inputEndDate.setEnabled(false);

        anyChartView = rootView.findViewById(R.id.chartView);

        btnBegDateChoose.setOnClickListener(new View.OnClickListener() {
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
                                                                                         inputBegDate.setText(months + "/" + days + "/" + years);
                                                                                     }
                                                                                 }, now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show(getActivity().getFragmentManager(), "Datepicker");
            }
        });

        btnEndDateChoose.setOnClickListener(new View.OnClickListener() {
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
                                                                                         inputEndDate.setText(months + "/" + days + "/" + years);
                                                                                     }
                                                                                 }, now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show(getActivity().getFragmentManager(), "Datepicker");
            }
        });

        btnExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildPie();
            }
        });

        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clean();
            }
        });

        return rootView;
    }

    public void buildPie() {
        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        Date begin = new Date();
        Date end = new Date();

        try {
            begin = format.parse(String.valueOf(inputBegDate.getText()));
            end = format.parse(String.valueOf(inputEndDate.getText()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mDB = FirebaseDatabase.getInstance().getReference().child(user.getUid());

        final Date finalBegin = begin;
        final Date finalEnd = end;

        final double[] in = {0};
        final double[] out = {0};

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
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (temp.after(finalBegin) || temp.equals(finalBegin)) {
                            if (temp.before(finalEnd) || temp.equals(finalEnd)) {
                                if (post.getType().equals("null")) {
                                    in[0] += post.getPrice();
                                } else {
                                    out[0] += post.getPrice();
                                }
                                Pie pie = AnyChart.pie();

                                List<DataEntry> data = new ArrayList<>();
                                data.add(new ValueDataEntry("Витрати", out[0]));
                                data.add(new ValueDataEntry("Надходження", in[0]));

                                anyChartView.setChart(pie);

                                pie.setData(data);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    public void clean() {
        inputBegDate.setText("");
        inputEndDate.setText("");

        Pie pie = AnyChart.pie();
        anyChartView.setChart(pie);
    }
}