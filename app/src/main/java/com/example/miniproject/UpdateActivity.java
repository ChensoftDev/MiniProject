package com.example.miniproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class UpdateActivity extends AppCompatActivity {

    String selected_tournament;

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Config.TOURNAMENT_REF);
    TextView tvTourname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        selected_tournament = getIntent().getStringExtra("tournament_name");

        tvTourname = findViewById(R.id.etTourname);

        tvTourname.setText(selected_tournament.toString());

        EditText etStartdate = findViewById(R.id.etStartDateUp);
        EditText etEnddate = findViewById(R.id.etEndDateUp);

        Button btnUpdate = findViewById(R.id.buttonUpdate);
        Button btnDelete = findViewById(R.id.buttonDelete);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateTournament(tvTourname.getText().toString(),etStartdate.getText().toString(),etEnddate.getText().toString());
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog alertDialog = new AlertDialog.Builder(UpdateActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Tournament deletion")
                        .setMessage("Are you sure to delete the tournament?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DeleteTournament(selected_tournament);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //set what should happen when negative button is clicked
                                //Toast.makeText(getApplicationContext(),"Nothing Happened",Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();

            }
        });

        etStartdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateOnEditText(etStartdate);
            }
        });

        etEnddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateOnEditText(etEnddate);
            }
        });


    }


    private void setDateOnEditText(EditText et) {
        // on below line we are getting
        // the instance of our calendar.
        final Calendar c = Calendar.getInstance();

        // on below line we are getting
        // our day, month and year.
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // on below line we are creating a variable for date picker dialog.
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                // on below line we are passing context.
                UpdateActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // on below line we are setting date to our edit text.
                        et.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                    }
                },
                // on below line we are passing year,
                // month and day for selected date in our date picker.
                year, month, day);
        // at last we are calling show to
        // display our date picker dialog.
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
        datePickerDialog.setCanceledOnTouchOutside(false);
        datePickerDialog.show();
    }

    private void DeleteTournament(String tournamentname) {
        try {
            reference.child(tournamentname).removeValue();
            finishAffinity();
            startActivity(new Intent(UpdateActivity.this, UserActivity.class));
        }catch (Exception e) {
            Toast.makeText(UpdateActivity.this, "Something went wrong! " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


    private void UpdateTournament(String newname,String stdate,String enddate) {

        try {

            String zstDate = stdate.replaceAll("-", "");
            String zedDate = enddate.replaceAll("-", "");

            if(zstDate.isEmpty() || zedDate.isEmpty() || newname.isEmpty()) {
                Toast.makeText(UpdateActivity.this, "Please input data", Toast.LENGTH_LONG).show();
                return;
            }

            if (newname.equals(selected_tournament)) {
                reference.child(selected_tournament).child("startdate").setValue(zstDate.toString());
                reference.child(selected_tournament).child("enddate").setValue(zedDate.toString());

            } else {
                reference.child(selected_tournament).get().addOnSuccessListener(dataSnapshot -> {
                    reference.child(newname).setValue(dataSnapshot.getValue());
                    reference.child(newname).child("startdate").setValue(zstDate.toString());
                    reference.child(newname).child("enddate").setValue(zedDate.toString());
                    reference.child(selected_tournament).removeValue();
                });
            }


            finishAffinity();
            startActivity(new Intent(UpdateActivity.this, UserActivity.class));

        }catch (Exception e) {
            Toast.makeText(UpdateActivity.this, "Something went wrong! " + e.getMessage(), Toast.LENGTH_LONG).show();
        }



    }
}