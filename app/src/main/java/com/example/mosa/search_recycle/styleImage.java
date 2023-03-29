package com.example.mosa.search_recycle;
//리사이클러 뷰를 구성하는 중
//차후구현

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mosa.R;

import java.util.ArrayList;
import java.util.List;

public class styleImage extends RecyclerView.Adapter<styleImage.ViewHolder>
{

    private final List<String> items=new ArrayList<>();
    private final Context context;

    public styleImage(Context context){
        this.context=context;
    }
    public void addItem(String imageUrl){
        items.add(imageUrl);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.style_search_screen,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(items.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public ViewHolder(View itemView){
            super(itemView);
            imageView=itemView.findViewById(R.id.style_image);
        }
    }

}
