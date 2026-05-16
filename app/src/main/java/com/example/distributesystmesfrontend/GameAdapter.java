package com.example.distributesystmesfrontend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    public interface OnGameClickListener {
        void onGameClick(String gameName);
    }

    private List<String> gameNames = new ArrayList<>();
    private final OnGameClickListener listener;

    public List<String> getCurrentGames(){
        return gameNames;
    }
    public GameAdapter(OnGameClickListener listener) {
        this.listener = listener;
    }

    public void setGames(List<String> games) {
        this.gameNames = games != null ? games : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = gameNames.get(position);
        holder.image.setImageResource(getDrawableForGame(name));
        holder.itemView.setOnClickListener(v -> listener.onGameClick(name));
    }

    @Override
    public int getItemCount() {
        return gameNames.size();
    }

    private int getDrawableForGame(String name) {
        switch (name) {
            case "Roulette":        return R.drawable.roulette_button;
            case "Slots":           return R.drawable.slots_button;
            case "Black Jack":      return R.drawable.blackjack_button;
            case "Tome of Sadness": return R.drawable.tome_of_sadness_img;
            case "Sweet Bonanza":   return R.drawable.sweet_bonanza_img;
            default:                return R.drawable.ic_launcher_foreground;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.gameImage);
        }
    }
}