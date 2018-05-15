package com.example.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.models.ModelAdapter;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Андрей on 12.05.2018.
 */

public class PieChartFragment extends Fragment {


    private Button button1;

    private Button button2;

    private Button button3;

    private Button button4;

    private EditText editText1;
    private EditText editText2;

    ////
    AnyChartView anyChartView;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mDB;


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
        button3 = rootView.findViewById(R.id.button5);
        button4 = rootView.findViewById(R.id.button6);

        editText1 = rootView.findViewById(R.id.editText1);
        editText1.setEnabled(false);
        editText2 = rootView.findViewById(R.id.editText2);
        editText2.setEnabled(false);



        ////

//

        anyChartView = (AnyChartView)rootView.findViewById(R.id.any_chart_view);

        ////



//        anyChartView = (AnyChartView) rootView.findViewById(R.id.any_chart_view);


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

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildPie();
            }
        });


        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clean();

            }
        });


        return rootView;
    }

    public  void buildPie(){


        final SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");

        Date begin=new Date();
        Date end=new Date();

        try {
            begin=format.parse(String.valueOf(editText1.getText()));
            end=format.parse(String.valueOf(editText2.getText()));
        } catch (ParseException e) {
            e.printStackTrace();
        }




//        Toast.makeText(getContext(), "@"+date, Toast.LENGTH_SHORT).show();

        mDB = FirebaseDatabase.getInstance().getReference().child(user.getUid());


        final Date finalBegin = begin;
        final Date finalEnd = end;


        final double[] in = {0};
        final double[] out = {0};


        if(begin.after(end)){
            Toast.makeText(getContext(), "Помилка. Некоректний проміжок", Toast.LENGTH_SHORT).show();
        }else {

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

                                //                        Toast.makeText(getContext(), Double.toString(post.getPrice()), Toast.LENGTH_SHORT).show();

                                if(post.getType().equals("null")){
                                    in[0] +=post.getPrice();
                                }else {
                                    out[0] +=post.getPrice();
                                }

                                Pie pie = AnyChart.pie();


                                List<DataEntry> data = new ArrayList<>();
                                data.add(new ValueDataEntry("Витрати", out[0]));
                                data.add(new ValueDataEntry("Надходження", in[0] ));

                                anyChartView.setChart(pie);

//
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


//        Toast.makeText(getContext(),"Строю ежи",Toast.LENGTH_LONG).show();


//        Pie pie = AnyChart.pie();
//
//
//        List<DataEntry> data = new ArrayList<>();
//        data.add(new ValueDataEntry("Витрати", in[0]));
//        data.add(new ValueDataEntry("Надходження", out[0]));
//
//        anyChartView.setChart(pie);
//
////
//        pie.setData(data);


    }

    public void clean(){

        editText1.setText("");
        editText2.setText("");

        Pie pie = AnyChart.pie();
        anyChartView.setChart(pie);

    }


}