package com.example.mosa.customer_history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mosa.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Recycler_history extends RecyclerView.Adapter<Recycler_history.ViewHolder> {

    /*
    파이어베이스에는 '유저의 계정 아이디', '유저의 진단 종류', '유저의 진단 결과', '유저의 진단 일자' 이런 형식으로 저장됩니다.
    이 데이터는 파이어베이스에서 직접 가지고 와서 진단 기록 탭을 불러올 때 직접 리사이클러 뷰로 불러와서 보여주겠습니다.
    */
    ArrayList<String> Mail=new ArrayList<String>();
    ArrayList<String> Diagnosis=new ArrayList<String>();
    ArrayList<String> Diagnosis_result=new ArrayList<String>();
    ArrayList<String> Dia_date=new ArrayList<String>();

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView his_mail;
        TextView his_date;
        TextView his_diag;
        TextView his_diag_result;
        public ViewHolder(View itemView) {
            super(itemView);
            his_mail=itemView.findViewById(R.id.history_mail);
            his_date=itemView.findViewById(R.id.history_date);
            his_diag=itemView.findViewById(R.id.history_diag);
            his_diag_result=itemView.findViewById(R.id.history_diag_result);
        }

    }
    public Recycler_history(ArrayList<String> mail, ArrayList<String> diagnosis, ArrayList<String> diagnosis_result, ArrayList<String> dia_date){
        this.Mail=mail;
        this.Diagnosis=diagnosis;
        this.Diagnosis_result=diagnosis_result;
        this.Dia_date=dia_date;
    }

    @Override
    public Recycler_history.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item,parent,false);
        Recycler_history.ViewHolder myview=new Recycler_history.ViewHolder(view);
        return myview;
    }

    @Override
    public void onBindViewHolder(Recycler_history.ViewHolder holder, int position) {
        String mail= Mail.get(position);
        String Diag=Diagnosis.get(position);
        String Diag_rst=Diagnosis_result.get(position);
        String Dia_da=Dia_date.get(position);

        holder.his_mail.setText(mail);
        holder.his_diag.setText(Diag);
        holder.his_diag_result.setText(Diag_rst);
        holder.his_date.setText(Dia_da);

    }

    @Override
    public int getItemCount() {
        return Mail.size();
    }
}
