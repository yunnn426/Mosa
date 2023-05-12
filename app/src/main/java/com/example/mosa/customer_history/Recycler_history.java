package com.example.mosa.customer_history;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mosa.PersonalActivity;
import com.example.mosa.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Recycler_history extends RecyclerView.Adapter<Recycler_history.ViewHolder> {

    /*
    파이어베이스에는 '유저의 계정 아이디', '유저의 진단 종류', '유저의 진단 결과', '유저의 진단 일자' 이런 형식으로 저장됩니다.
    이 데이터는 파이어베이스에서 직접 가지고 와서 진단 기록 탭을 불러올 때 직접 리사이클러 뷰로 불러와서 보여주겠습니다.
    */
    ArrayList<Bitmap> user_img=new ArrayList<Bitmap>();
    ArrayList<String> Mail=new ArrayList<String>();
    ArrayList<String> Diagnosis=new ArrayList<String>();
    ArrayList<String> Diagnosis_result_color=new ArrayList<String>();

    ArrayList<String> Diagnosis_result_face=new ArrayList<String>();
    ArrayList<String> Dia_date=new ArrayList<String>();

    public class ViewHolder extends RecyclerView.ViewHolder{
        Button review_btn;
        ImageView his_record;
        TextView his_mail;
        TextView his_date;
        TextView his_diag;
        TextView his_diag_result_1;

        TextView his_diag_result_2;
        public ViewHolder(View itemView) {
            super(itemView);
            review_btn=itemView.findViewById(R.id.record_view);
            his_record=itemView.findViewById(R.id.user_history_img);
            his_mail=itemView.findViewById(R.id.history_mail);
            his_date=itemView.findViewById(R.id.history_date);
            his_diag=itemView.findViewById(R.id.history_diag);
            his_diag_result_1=itemView.findViewById(R.id.history_diag_result_1);
            his_diag_result_2=itemView.findViewById(R.id.history_diag_result_2);
        }
    }

    public Recycler_history(ArrayList<String> mail, ArrayList<String> diagnosis, ArrayList<String> diagnosis_result_color, ArrayList<String> diagnosis_result_face,
                            ArrayList<String> dia_date, ArrayList<Bitmap> user_img ){
        this.Mail=mail;
        this.Diagnosis=diagnosis;
        this.Diagnosis_result_color=diagnosis_result_color;
        this.Diagnosis_result_face=diagnosis_result_face;
        this.Dia_date=dia_date;
        this.user_img=user_img;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item,parent,false);
        ViewHolder myview=new ViewHolder(view);
        return myview;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bitmap img=user_img.get(position);
        String mail= Mail.get(position);
        String Diag=Diagnosis.get(position);
        String Diag_rst_1=Diagnosis_result_color.get(position);
        String Diag_rst_2=Diagnosis_result_face.get(position);
        String Dia_da=Dia_date.get(position);

        holder.his_record.setImageBitmap(img);
        holder.his_mail.setText(mail);
        holder.his_diag.setText(Diag);
        holder.his_diag_result_1.setText(Diag_rst_1);
        holder.his_diag_result_2.setText(Diag_rst_2);
        holder.his_date.setText(Dia_da);

        holder.review_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(holder.itemView.getContext(), PersonalActivity.class);
                intent.putExtra("result_color",Diag_rst_1);
                intent.putExtra("result_face",Diag_rst_2);
                intent.putExtra("img","is_record");
                intent.putExtra("img_name","is_record");
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return Mail.size();
    }
}
