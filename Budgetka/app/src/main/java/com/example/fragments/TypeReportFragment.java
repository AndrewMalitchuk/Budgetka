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
import java.util.Date;

public class TypeReportFragment extends Fragment {

    private Button btnBegDateChoose;
    private Button btnEndDateChoose;
    private Button btnExecute;

    private Spinner spinnerCostsType;

    private EditText inputBegDate;
    private EditText inputEndDate;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDB;

    private RecyclerView recyclerView;
    private ArrayList<Item> itemList = new ArrayList<>();
    private ModelAdapter recyclerViewAdapter;

    public static TypeReportFragment newInstance() {
        TypeReportFragment fragment = new TypeReportFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.type_report_fragment, container, false);

        btnBegDateChoose = rootView.findViewById(R.id.btnBegDateChoose);
        btnEndDateChoose = rootView.findViewById(R.id.btnEndDateChoose);
        btnExecute = rootView.findViewById(R.id.btnExecute);

        inputBegDate = rootView.findViewById(R.id.inputBegDate);
        inputBegDate.setEnabled(false);
        inputEndDate = rootView.findViewById(R.id.inputEndDate);
        inputEndDate.setEnabled(false);

        recyclerView = rootView.findViewById(R.id.recyclerView);

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
                                                                                         inputBegDate.setText(days + "/" + months + "/" + years);
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
                                                                                         inputEndDate.setText(days + "/" + months + "/" + years);
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
                cleanRecyclerView();
                initRecyclerView();
            }
        });

        spinnerCostsType = rootView.findViewById(R.id.spinnerCostsType);
        try {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.cost_type_array, R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCostsType.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    public void cleanRecyclerView() {
        itemList.clear();
        itemList.removeAll(itemList);

        recyclerView.getRecycledViewPool().clear();
        recyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAdapter = new ModelAdapter(itemList);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    public void initRecyclerView() {
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
                                if (post.getType().equals(spinnerCostsType.getSelectedItem().toString())) {
                                    itemList.add(post);
                                    recyclerView.setHasFixedSize(true);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                    recyclerViewAdapter = new ModelAdapter(itemList);
                                    recyclerView.setAdapter(recyclerViewAdapter);
                                }
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
}

