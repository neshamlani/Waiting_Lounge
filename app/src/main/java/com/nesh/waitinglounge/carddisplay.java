package com.nesh.waitinglounge;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class carddisplay extends RecyclerView.Adapter<carddisplay.cardViewHolder> {

    List<String> token=new ArrayList<>();

    public carddisplay(List<String> token) {
        this.token = token;
    }

    public carddisplay(){}
    @NonNull
    @Override
    public cardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.waitdisp,parent,false);
        cardViewHolder cvh=new cardViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull cardViewHolder holder, int position) {
        holder.tv.setText(token.get(position));
        holder.tv.setBackgroundColor(Color.parseColor("#ffff00"));
    }


    @Override
    public int getItemCount() {
        return token.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    public static class cardViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView tv;

        public cardViewHolder(@NonNull View itemView) {
            super(itemView);
            cv=itemView.findViewById(R.id.cardDisp);
            tv=itemView.findViewById(R.id.tokenListDisp);
        }
    }
}
