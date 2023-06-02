package com.example.mosa.recommend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mosa.PersonalActivity;
import com.example.mosa.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class hair_recycler extends RecyclerView.Adapter<hair_recycler.ViewHolder_item>{

    ArrayList<Bitmap> itemfile=new ArrayList<Bitmap>();

    @NonNull
    @Override
    public ViewHolder_item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.hair_carouselview,parent,false);
        ViewHolder_item myView=new ViewHolder_item(view);
        return myView;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder_item holder, int position) {
        Bitmap img=itemfile.get(position);

        holder.item_img.setImageBitmap(img);

    }

    @Override
    public int getItemCount() {
        return itemfile.size();
    }

    public class ViewHolder_item extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView item_img;

        public ViewHolder_item(@NonNull View itemView) {
            super(itemView);
            item_img=itemView.findViewById(R.id.hair_item);
            item_img.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position=getAdapterPosition();
            Intent intent=new Intent(itemView.getContext(),FullitemImage.class);
            Bitmap bitmap=itemfile.get(position);
            File file=BmpToFile(bitmap,"item_img");
            intent.putExtra("bitmap_url",file.getAbsolutePath());
            itemView.getContext().startActivity(intent);
        }
        public File BmpToFile(Bitmap bmp, String filename)
        {
            File file=new File(itemView.getContext().getExternalFilesDir(null),filename);
            try{
                FileOutputStream fos=new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG,100,fos);
                fos.flush();
                fos.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            return file;
        }
    }
    public void setrecycler(ArrayList<Bitmap> itemfile_list){
        this.itemfile=itemfile_list;
    }
}
