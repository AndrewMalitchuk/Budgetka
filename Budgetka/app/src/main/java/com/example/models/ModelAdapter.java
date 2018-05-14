package com.example.models;

/**
 * Created by Андрей on 13.05.2018.
 */

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.anychart.anychart.Resource;
import com.example.budgetka.R;

import java.util.ArrayList;

import static com.example.budgetka.R.color.green;


public class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ViewHolder> {


    public ArrayList<Item> item_list;


    public ModelAdapter(ArrayList<Item> arrayList) {

        item_list = arrayList;
        notifyDataSetChanged();
    }

    @Override
    public ModelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowitem, null);

        // create ViewHolder
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ModelAdapter.ViewHolder holder, int position) {

        final int pos = position;

        if (!item_list.get(position).getType().equals("null")) {

            ImageView imageView = holder.itemView.findViewById(R.id.imageStatus);

            if(imageView!=null) {
                imageView.setColorFilter(Color.rgb(244, 67, 54));
                imageView.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
            }



            holder.item_name.setText(Double.toString(item_list.get(position).getPrice()) + " (" + item_list.get(position).getType() + ")");

            TextView temp=holder.itemView.findViewById(R.id.textView4);
            temp.setText(item_list.get(position).getDate());
        } else {

            ImageView imageView = holder.itemView.findViewById(R.id.imageStatus);

            if(imageView!=null) {
                imageView.setColorFilter(Color.rgb(76, 175, 80));

                imageView.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
            }


            holder.item_name.setText(Double.toString(item_list.get(position).getPrice()));

            TextView temp=holder.itemView.findViewById(R.id.textView4);
            temp.setText(item_list.get(position).getDate());
        }

//        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                deleteItemFromList(v, pos);
//
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return item_list.size();
    }


//    private void deleteItemFromList(View v, final int position) {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
//
//        builder.setMessage("Delete Item ?")
//                .setCancelable(false)
//                .setPositiveButton("CONFIRM",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//
//                                item_list.remove(position);
//                                notifyDataSetChanged();
//
//
//                            }
//                        })
//                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//
//
//                    }
//                });
//
//        builder.show();
//
//    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView item_name;
//        public ImageButton btn_delete;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            item_name = (TextView) itemLayoutView.findViewById(R.id.txt_Name);
//            btn_delete = (ImageButton) itemLayoutView.findViewById(R.id.btn_delete_unit);

        }
    }
}
