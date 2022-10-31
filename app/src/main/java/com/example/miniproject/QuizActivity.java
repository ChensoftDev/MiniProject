package com.example.miniproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class QuizActivity extends AppCompatActivity {

    private TextView tvQuestion,tvScore,tvQuestionNo;
    private RadioGroup radioGroup;
    private RadioButton rb1,rb2,rb3,rb4;

    private Button btnNext;

    int totalQuestion;
    int qCounter = 0;
    int score;

    boolean answsered;

    private QuestionModel currentQuestion;

    private List<QuestionModel> questionList;

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Config.TOURNAMENT_REF);

    String selected_tournament;

    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);




        tvQuestion = findViewById(R.id.tvQuestion);
        tvScore = findViewById(R.id.tvScore);
        tvQuestionNo = findViewById(R.id.tvQuestionNo);

        radioGroup = findViewById(R.id.radioGroupAns);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        rb3 = findViewById(R.id.rb3);
        rb4 = findViewById(R.id.rb4);

        btnNext = findViewById(R.id.btnNext);

        selected_tournament = getIntent().getStringExtra("tournament_name");

        pd = new ProgressDialog(QuizActivity.this);

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


        loadQuesion();


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answsered == false){
                    if(rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()) {
                        checkAnswer();
                    } else {

                            //no option selected
                    }
                } else {
                    showNextQuestion();
                }
            }
        });
    }

    private void checkAnswer() {
        answsered = true;
        RadioButton rbSelected = findViewById(radioGroup.getCheckedRadioButtonId());
        int answserNo = radioGroup.indexOfChild(rbSelected) + 1;

        if(answserNo == currentQuestion.getCorrectAnsNo()) {
            score++;
            tvScore.setText("Score : " + score);
        }

        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);
        rb4.setTextColor(Color.RED);

        switch (currentQuestion.getCorrectAnsNo()) {
            case 1:
                rb1.setTextColor(Color.GREEN);
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                break;
            case 4:
                rb4.setTextColor(Color.GREEN);
                break;
        }

        if (qCounter < totalQuestion) {
            btnNext.setText("Next");
        } else {
            btnNext.setText("Finish");
        }

    }

    private void showNextQuestion() {

        radioGroup.clearCheck();

        rb1.setTextColor(Color.BLACK);
        rb2.setTextColor(Color.BLACK);
        rb3.setTextColor(Color.BLACK);
        rb4.setTextColor(Color.BLACK);


        if(qCounter < totalQuestion) {
            currentQuestion = questionList.get(qCounter);
            tvQuestion.setText(Html.fromHtml(currentQuestion.getQuestion()));


            rb1.setText(Html.fromHtml(currentQuestion.getOption1()));
            rb2.setText(Html.fromHtml(currentQuestion.getOption2()));
            rb3.setText(Html.fromHtml(currentQuestion.getOption3()));
            rb4.setText(Html.fromHtml(currentQuestion.getOption4()));

            qCounter++;
            btnNext.setText("Submit");
            tvQuestionNo.setText("Question : " + qCounter + "/" + totalQuestion);
            answsered = false;

        } else {


            String msg;


            if(score > 5) {
                msg = "Excellent!";
            } else {
                msg = "Good!";
            }

            final SweetAlertDialog pDialog = new SweetAlertDialog(
                    QuizActivity.this, SweetAlertDialog.SUCCESS_TYPE);
            pDialog.setTitleText(msg);


            pDialog.setContentText("Your score is : " + score + "/" + totalQuestion + "   How do you like about this game?");
            pDialog.setConfirmText("Like");
            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    reference.child(selected_tournament).child("like").setValue(ServerValue.increment(1));
                    sDialog.dismissWithAnimation();
                    finishAffinity();
                    startActivity(new Intent(QuizActivity.this, UserActivity.class));

                }
            });
            pDialog.setCancelButton("Dislike", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            finishAffinity();
                            startActivity(new Intent(QuizActivity.this, UserActivity.class));

                        }
                    });

            pDialog.show();
            pDialog.setCancelable(false);


            completedPlayer();


        }
    }

    private void completedPlayer() {

        Map<String, Object> values = new HashMap<>();
        values.put(Config.user.getFirstName().toString() + " " + Config.user.getLastName().toString(), score);

        reference.child(selected_tournament).child("played")
                .updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(QuizActivity.this, "Tournament updated", Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(QuizActivity.this, "Booking Error : " + task.getException(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }





    private void loadQuesion(){

        reference.child(selected_tournament).child("results").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.getChildrenCount() > 0) {

                        questionList = new ArrayList<>();
                        questionList.clear();

                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            try {

                                String question;
                                String option1;
                                String option2;
                                String option3;
                                String option4;

                                question = (String) snap.child("question").getValue(String.class);
                                option1 = (String)  snap.child("incorrect_answers").child("0").getValue(String.class);
                                option2 = (String)  snap.child("incorrect_answers").child("1").getValue(String.class);
                                option3 = (String)  snap.child("incorrect_answers").child("2").getValue(String.class);
                                option4 = (String)  snap.child("correct_answer").getValue(String.class);

                                //SHUFFLE ANSWER
                                String[] answers = new String[] {option1,option2,option3,option4};
                                List<String> strList = Arrays.asList(answers);
                                Collections.shuffle(strList);
                                answers = strList.toArray(new String[strList.size()]);

                                Integer correctAnswerIndex = Arrays.asList(answers).indexOf(option4) + 1;

                                questionList.add(new QuestionModel(question,answers[0],answers[1],answers[2],answers[3],correctAnswerIndex));


                            }
                            catch ( Exception e )  {
                                Toast.makeText(QuizActivity.this, "Something went wrong! Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }

                        totalQuestion = questionList.size();
                        showNextQuestion();
                        pd.hide();


                    }
                } else {
                    Toast.makeText(QuizActivity.this, "Cannot retrieve question", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}