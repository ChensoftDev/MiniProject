package com.example.miniproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListTournamentActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    MyAdapter adapter;

    List<listTournamentCls> listTournament;

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Config.TOURNAMENT_REF);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tournament);

        //listTournament = new ArrayList<>();
        getTournamentList();



    }


    public void init() {
        recyclerView = (RecyclerView)  findViewById(R.id.rcvTourList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter adapter = new MyAdapter(listTournament,this);
        recyclerView.setAdapter(adapter);
    }

    private void getTournamentList() {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.getChildrenCount() > 0) {

                        listTournament = new ArrayList<>();
                        listTournament.clear();

                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            try {

                                String tournamentname = snap.getKey();
                                String category = (String) snap.child("category").getValue(String.class);
                                String difficulty = (String) snap.child("difficulty").getValue(String.class);
                                String enddate = (String) snap.child("enddate").getValue(String.class);
                                String startdate = (String) snap.child("startdate").getValue(String.class);
                                Integer like = (Integer) snap.child("like").getValue(Integer.class);

                                listTournament.add(new listTournamentCls(tournamentname,category,difficulty,startdate,enddate,like,"OKF"));

                            }
                            catch ( Exception e )  {
                                Toast.makeText(ListTournamentActivity.this, "Something went wrong! Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            finally {
                                init();
                            }
                        }
                    }
                } else {
                    RecyclerView recyclerView = findViewById(R.id.rcvTourList);
                    recyclerView.removeAllViewsInLayout();
                    recyclerView.forceLayout();
                    Toast.makeText(ListTournamentActivity.this, "Sorry no tournament found", Toast.LENGTH_LONG).show();


                }
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}