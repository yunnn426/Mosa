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

import com.example.mosa.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class item_Recycler extends RecyclerView.Adapter<item_Recycler.ViewHolder_item>{

    ArrayList<Bitmap> itemfile=new ArrayList<Bitmap>();
    ArrayList<String> itemname=new ArrayList<String>();

    @Override
    public ViewHolder_item onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent,false);
        ViewHolder_item myView=new ViewHolder_item(view);
        return myView;
    }
    @Override
    public void onBindViewHolder(ViewHolder_item holder, int position) {
        Bitmap item=itemfile.get(position);
        String item_detail=itemname.get(position);
        holder.item_img.setImageBitmap(item);
    }
    public class ViewHolder_item extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView item_img;
        public ViewHolder_item(View itemView) {
            super(itemView);
            item_img=itemView.findViewById(R.id.prodouct_id);
            item_img.setOnClickListener(this);
        }
        @Override
        public void onClick(View view){
            int position=getAdapterPosition();
            Intent intent=new Intent(itemView.getContext(),FullitemImage.class);
            Bitmap bitmap=itemfile.get(position);
            String item_detail=itemname.get(position);
            File file=BmpToFile(bitmap,"item_img");
            intent.putExtra("item_detail",item_detail);
            intent.putExtra("bitmap_url",file.getAbsolutePath());
            itemView.getContext().startActivity(intent);
        }
        //비트맵 이미지를 파일 형태로 변환
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

    public item_Recycler(ArrayList<Bitmap> itemfile_list){
        this.itemfile=itemfile_list;
    }
    public  item_Recycler(){

    }

    public void setrecycler(ArrayList<Bitmap> itemfile_list,ArrayList<String> itemname){
        this.itemfile=itemfile_list;
        this.itemname=itemname;
    }
    @Override
    public int getItemCount() {
        return itemfile.size();
    }

}
