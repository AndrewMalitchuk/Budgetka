package com.example.models;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.budgetka.R;

import java.util.ArrayList;

public class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ViewHolder> {

    public ArrayList<Item> itemList;

    public ModelAdapter(ArrayList<Item> arrayList) {
        itemList = arrayList;
        notifyDataSetChanged();
    }

    @Override
    public ModelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ModelAdapter.ViewHolder holder, int position) {
        final int pos = position;
        if (!itemList.get(position).getType().equals("null")) {
            ImageView imageView = holder.itemView.findViewById(R.id.imageStatus);
            if (imageView != null) {
                imageView.setColorFilter(Color.rgb(244, 67, 54));
                imageView.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
            }
            holder.itemName.setText(Double.toString(itemList.get(position).getPrice()) + " (" + itemList.get(position).getType() + ")");
            TextView temp = holder.itemView.findViewById(R.id.textView4);
            temp.setText(itemList.get(position).getDate());
        } else {
            ImageView imageView = holder.itemView.findViewById(R.id.imageStatus);
            if (imageView != null) {
                imageView.setColorFilter(Color.rgb(76, 175, 80));
                imageView.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
            }
            holder.itemName.setText(Double.toString(itemList.get(position).getPrice()));
            TextView temp = holder.itemView.findViewById(R.id.textView4);
            temp.setText(itemList.get(position).getDate());
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemName;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemName = (TextView) itemLayoutView.findViewById(R.id.txt_Name);
        }
    }
}
