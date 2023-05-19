package com.example.mosa.recommend;

import android.graphics.Bitmap;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mosa.R;

import java.util.ArrayList;

public class cloth_item extends RecyclerView.Adapter<cloth_item.fashion_viewholder>{

    ArrayList<Bitmap> fashion_img=new ArrayList<Bitmap>();

    @Override
    public fashion_viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cloth_item_view,parent,false);
        fashion_viewholder fashion_view=new fashion_viewholder(view);
        return fashion_view;
    }

    @Override
    public void onBindViewHolder(fashion_viewholder holder, int position) {
        Bitmap fashion=fashion_img.get(position);
        holder.fashion_item_img.setImageBitmap(fashion);
    }

    @Override
    public int getItemCount() {
        return fashion_img.size();
    }

    public class fashion_viewholder extends RecyclerView.ViewHolder{
        ImageView fashion_item_img;
        public fashion_viewholder(View itemView) {
            super(itemView);
            fashion_item_img=itemView.findViewById(R.id.cloth_product);
        }
    }

    public cloth_item(ArrayList<Bitmap> img){
        this.fashion_img=img;
    }
    public cloth_item(){}
    public void setcloth_item(ArrayList<Bitmap> img){this.fashion_img=img;}
}
