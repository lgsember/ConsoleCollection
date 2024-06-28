package com.example.mycrew;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context context;
    private List<ItemClass> itemList;

    public MyAdapter(Context context, List<ItemClass> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(itemList.get(position).getPicture()).into(holder.recImage);
        holder.recTitle.setText(itemList.get(position).getNickname());
        holder.recSubtitle.setText(itemList.get(position).getName());
        holder.recText.setText(itemList.get(position).getBrand());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("Image", itemList.get(holder.getAdapterPosition()).getPicture());
                intent.putExtra("Nickname", itemList.get(holder.getAdapterPosition()).getNickname());
                intent.putExtra("Name", itemList.get(holder.getAdapterPosition()).getName());
                intent.putExtra("Brand", itemList.get(holder.getAdapterPosition()).getBrand());
                intent.putExtra("Model", itemList.get(holder.getAdapterPosition()).getModel());
                intent.putExtra("Status", itemList.get(holder.getAdapterPosition()).getStatus());
                intent.putExtra("Description", itemList.get(holder.getAdapterPosition()).getDescription());
                intent.putExtra("Owner", itemList.get(holder.getAdapterPosition()).getOwner());
                intent.putExtra("Key", itemList.get(holder.getAdapterPosition()).getKey());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView recImage;
    TextView recTitle, recSubtitle, recText;
    CardView recCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.recImage);
        recTitle = itemView.findViewById(R.id.recTitle);
        recSubtitle = itemView.findViewById(R.id.recSubtitle);
        recText = itemView.findViewById(R.id.recText);
        recCard = itemView.findViewById(R.id.recCard);

    }
}
