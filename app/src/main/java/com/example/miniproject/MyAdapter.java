package com.example.miniproject;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.collection.LLRBNode;

import org.w3c.dom.Text;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<listTournamentCls> listTournament;
    private Context context;

    public MyAdapter(List<listTournamentCls> listTournament, Context context) {
        this.listTournament = listTournament;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listtournament_layout,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            listTournamentCls listItem = listTournament.get(position);

            holder.tvTourName.setText(listItem.getName());
            holder.tvTourDate.setText("Date range : " + listItem.getStartdate() + " - " + listItem.getEnddate());
            holder.tvTourCat.setText("Category : " + listItem.getCategory());
            holder.tvTourDiff.setText("Difficulty : " + listItem.getDifficulty());
            holder.tvTourLike.setText("Like : " + listItem.getLike().toString());
            holder.tvStatus.setText(listItem.getStatus().toString());

            if (listItem.getStatus().toString().equals("Closed")) {
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FF0000"));
            } else if (listItem.getStatus().toString().equals("Upcoming")) {
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FAC900"));
            } else if (listItem.getStatus().toString().equals("Completed")) {
                holder.tvStatus.setBackgroundColor(Color.parseColor("#0090FA"));
            }
        } catch (Exception e) {
            Toast.makeText(context.getApplicationContext(), "Something went wrong! " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return listTournament.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTourName;
        public TextView tvTourDate;
        public TextView tvTourCat;
        public TextView tvTourDiff;
        public TextView tvTourLike;
        public TextView tvStatus;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTourName = (TextView)  itemView.findViewById(R.id.idTVTournamentName);
            tvTourDate = (TextView)  itemView.findViewById(R.id.idTVTourDate);
            tvTourCat = (TextView)  itemView.findViewById(R.id.idTVTourCat);
            tvTourDiff = (TextView)  itemView.findViewById(R.id.idTVTourDiff);
            tvTourLike = (TextView)  itemView.findViewById(R.id.idTVTourLike);
            tvStatus = (TextView)  itemView.findViewById(R.id.tvStatus);

            //CardView cv = (CardView) itemView.findViewById(R.id.cardview);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(Config.user.getStatus().equals("user")) {
                        if(tvStatus.getText().toString().equals("Available")) {
                            Intent intent = new Intent(view.getContext(), QuizActivity.class);
                            intent.putExtra("tournament_name", tvTourName.getText());
                            context.startActivity(intent);
                        }
                    } else if(Config.user.getStatus().equals("admin")) {

                            Intent intent = new Intent(view.getContext(), UpdateActivity.class);
                            intent.putExtra("tournament_name", tvTourName.getText());
                            context.startActivity(intent);
                    }

                    //cv.setCardBackgroundColor(Color.parseColor("#E4E4E4E4"));




                     //Toast.makeText(view.getContext(), "Clicked Laugh Vote" + Config.UserStatus, Toast.LENGTH_SHORT).show();
                }
            });




        }
    }
}
