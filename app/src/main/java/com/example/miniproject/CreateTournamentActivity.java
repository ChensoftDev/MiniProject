package com.example.miniproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateTournamentActivity extends AppCompatActivity {

    private ArrayList<String> category;
    private RequestQueue mQueue;

    private String Difficulty = "Easy";
    private Map<String, Object> questions = new HashMap<>();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Config.TOURNAMENT_REF);

    //JSON Array
    private JSONArray result;

    private Spinner spin;


    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tournament);



        category = new ArrayList<String>();

        //Initializing Spinner
        spin = (Spinner) findViewById(R.id.spinner);

        EditText etStartdate = findViewById(R.id.etStartDate);
        EditText etEnddate = findViewById(R.id.etEndDate);
        Button btnCreatetournament = (Button) findViewById(R.id.btnCreate);
        EditText etName = findViewById(R.id.tournament_name);

        RadioButton rdEasy = findViewById(R.id.radioButton);
        RadioButton rdMeduim = findViewById(R.id.radioButton2);
        RadioButton rdHard = findViewById(R.id.radioButton3);

        // This will get the radiogroup
        RadioGroup rGroup = (RadioGroup)findViewById(R.id.radioGroup);
        // This will get the radiobutton in the radiogroup that is checked
        RadioButton checkedRadioButton = (RadioButton)rGroup.findViewById(rGroup.getCheckedRadioButtonId());

        // This overrides the radiogroup onCheckListener
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked)
                {
                    Difficulty = checkedRadioButton.getText().toString();
                }
            }
        });

        pd = new ProgressDialog(CreateTournamentActivity.this);

        // Set progress dialog style spinner
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // Set the progress dialog title and message
        pd.setTitle("Please wait");
        pd.setMessage("Loading.........");
        pd.setCancelable(false);

        // Set the progress dialog background color
        //pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));

        pd.setIndeterminate(false);

        // Finally, show the progress dialog
        pd.show();




        getCategory();


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

        btnCreatetournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateTournament(etName.getText().toString(),Difficulty,spin.getSelectedItem().toString(),etStartdate.getText().toString(),etEnddate.getText().toString());
                //GenerateQuestion();
            }
        });


    }


    private void CreateTournament(String TournamentName,String Difficulty,String Category,String StartDate,String EndDate){
        try {

            String stDate = StartDate.replaceAll("-","");
            String edDate = EndDate.replaceAll("-","");

            pd.show();


            //Creating a string request
            String APIRequest = Config.DATA_API_URL + "?amount=" + Config.Q_AMOUNT + "&category=" + getCategoryID(spin.getSelectedItemPosition()).intValue() +
                    "&difficulty=" + Difficulty.toLowerCase() + "&type=multiple";

            StringRequest stringRequest = new StringRequest(APIRequest,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject j = null;
                            try {
                                //Parsing the fetched Json String to JSON Object
                                j = new JSONObject(response);

                                if(j.getString("response_code").equals("0")) {

                                    //Toast.makeText(CreateTournamentActivity.this, APIRequest, Toast.LENGTH_LONG).show();

                                    j.put("startdate", stDate);
                                    j.put("enddate", edDate);
                                    j.put("like", 0);
                                    j.put("category", Category);
                                    j.put("difficulty", Difficulty);

                                    Map<String, Object> jsonMap = new Gson().fromJson(j.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());

                                    reference.child(TournamentName)
                                            .updateChildren(jsonMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        finishAffinity();
                                                        startActivity(new Intent(CreateTournamentActivity.this, UserActivity.class));
                                                        //String id = getCategoryID(spin.getSelectedItemPosition()).toString();
                                                        Toast.makeText(CreateTournamentActivity.this, "Create tournament successfully", Toast.LENGTH_LONG).show();finish();
                                                    } else {
                                                        Toast.makeText(CreateTournamentActivity.this, "Error : " + task.getException(), Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            });

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            pd.hide();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

            //Creating a request queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            //Adding request to the queue
            requestQueue.add(stringRequest);


        } catch (Exception e) {
            Toast.makeText(CreateTournamentActivity.this, "Something went wrong! " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
                CreateTournamentActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // on below line we are setting date to our edit text.

                        String Month,Day;

                        if((monthOfYear+1) < 10) {
                            Month = "0" + (monthOfYear + 1);
                        } else {
                            Month =  "" + (monthOfYear + 1);
                        }

                        if(dayOfMonth < 10) {
                            Day = "0" + dayOfMonth;
                        } else {
                            Day = "" + dayOfMonth;
                        }

                        et.setText(year + "-" + Month + "-" + Day);

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



    private Integer getCategoryID(int position){
        Integer id = 0;
        try {
            //Getting object of given index
            JSONObject json = result.getJSONObject(position);
            //Fetching name from that object
            id = json.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the id
        return id;
    }

    private void getCategory(){
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Config.DATA_CATEGORY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            result = j.getJSONArray("trivia_categories");

                            //Calling method getStudents to get the students from the JSON Array
                            for(int i = 0; i < result.length(); i++) {
                                JSONObject json = result.getJSONObject(i);
                                category.add(json.getString("name"));
                            }

                            spin.setAdapter(new ArrayAdapter<String>(CreateTournamentActivity.this, android.R.layout.simple_spinner_dropdown_item, category));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        pd.hide();

                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
}