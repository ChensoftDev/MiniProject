package com.example.miniproject;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OngoingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OngoingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View view;

    RecyclerView recyclerView;

    MyAdapter adapter;

    List<listTournamentCls> listTournament = new ArrayList<>();;

     ProgressDialog pd;




    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Config.TOURNAMENT_REF);

    public OngoingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OngoingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OngoingFragment newInstance(String param1, String param2) {
        OngoingFragment fragment = new OngoingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        pd = new ProgressDialog(getContext());

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

        // Set the progress status zero on each button click




        //Toast.makeText(getActivity(),"Authentication failed.",Toast.LENGTH_LONG).show();


        //listTournament.clear();

        //init();
        //getTournamentList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this
            if(view == null) {
                view = inflater.inflate(R.layout.fragment_ongoing, container, false);

                recyclerView = (RecyclerView)  view.findViewById(R.id.rcvTourList1);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                listTournament.clear();
                getTournamentList();

                adapter = new MyAdapter(listTournament,getActivity());
                recyclerView.setAdapter(adapter);


            }
            return view;
    }




    private void getTournamentList() {

        Integer todaydate = Integer.parseInt(Config.currentDate.replaceAll("-","").replaceAll("[\\D]", ""));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    if (dataSnapshot.getChildrenCount() > 0) {

                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            try {

                                String tournamentname = snap.getKey();
                                String category = (String) snap.child("category").getValue(String.class);
                                String difficulty = (String) snap.child("difficulty").getValue(String.class);
                                String enddate = (String) snap.child("enddate").getValue(String.class);
                                String startdate = (String) snap.child("startdate").getValue(String.class);
                                Integer like = (Integer) snap.child("like").getValue(Integer.class);
                                String status = "Available";


                                for (DataSnapshot ds2 : snap.child("played").getChildren()) {

                                    if(ds2.getKey().equals(Config.user.getFirstName() + " " + Config.user.getLastName())) {
                                        status = "Completed";
                                        //Toast.makeText(getActivity(), ds2.getKey(), Toast.LENGTH_LONG).show();
                                    }

                                }


                                if(Integer.parseInt(startdate.replaceAll("[\\D]", "")) <= todaydate && Integer.parseInt(enddate.replaceAll("[\\D]", "")) >= todaydate)  {
                                    listTournament.add(new listTournamentCls(tournamentname,category,difficulty,startdate,enddate,like,status));
                                }


                            }
                            catch ( Exception e )  {
                                Toast.makeText(getActivity(), "Something went wrong! Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }

                        adapter.notifyDataSetChanged();
                        pd.hide();
                    }
                } else {
                    recyclerView = view.findViewById(R.id.rcvTourList1);
                    recyclerView.removeAllViewsInLayout();
                    recyclerView.forceLayout();
                    Toast.makeText(getActivity(), "Sorry no tournament found", Toast.LENGTH_LONG).show();

                }
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}