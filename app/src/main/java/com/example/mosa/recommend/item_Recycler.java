package com.example.mosa.recommend;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mosa.R;

import java.util.ArrayList;

public class item_Recycler extends RecyclerView.Adapter<item_Recycler.ViewHolder_item>{

    ArrayList<Bitmap> itemfile;
    ArrayList<String> itemfile_ex;


    @Override
    public ViewHolder_item onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent,false);
        ViewHolder_item myView=new ViewHolder_item(view);
        return myView;
    }

    @Override
    public void onBindViewHolder(ViewHolder_item holder, int position) {
        Bitmap item=itemfile.get(position);
        String item_ex=itemfile_ex.get(position);

        holder.item_img.setImageBitmap(item);
        holder.item_ex.setText(item_ex);
    }

    @Override
    public int getItemCount() {
        return itemfile.size();
    }

    public class ViewHolder_item extends RecyclerView.ViewHolder{
        ImageView item_img;
        TextView item_ex;

        public ViewHolder_item(View itemView) {
            super(itemView);
            item_img=itemView.findViewById(R.id.prodouct_id);
            item_ex=itemView.findViewById(R.id.prodict_ex_id);
        }
    }

    public item_Recycler(ArrayList<Bitmap> itemfile_list, ArrayList<String> itemfile_ex_list){
        this.itemfile=itemfile_list;
        this.itemfile_ex=itemfile_ex_list;
    }


}
