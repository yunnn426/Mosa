package com.example.mosa;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


import com.bumptech.glide.Glide;
import com.example.mosa.search_recycle.styleImage;
import com.example.mosa.search_recycle.styleInsta;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class styleSearchActivity extends AppCompatActivity {

    EditText editText;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.style_search_screen);
        Intent intent=getIntent();

        editText=findViewById(R.id.style_str);
        button=findViewById(R.id.style_str_input);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Paint paint=new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);
        editText.setPadding(0,0,0,0);
        editText.setPaintFlags(editText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        //패션 이미지를 리사이클러 뷰 형식으로 불러와서 ppt에서의 내용을 구현
        //차후 구현

        RecyclerView recyclerView=findViewById(R.id.style_recycle);

        styleImage style_adepter=new styleImage(this);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        styleImage styleimge=new styleImage(this);
        recyclerView.setAdapter(styleimge);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=editText.getText().toString();
                List<String> Img_url;

                //크롤링을 통해서 인스타그램에서 검색 정보를 가지고 온다.
                try {
                    //Img_url=Insta_crawl(str);
                    styleInsta task=new styleInsta();
                    Img_url=task.execute(str).get();
                    for(int i=0;i<Img_url.size();i++){
                        styleimge.addItem(Img_url.get(i));
                    }


                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.top_menu,menu); //우상단 메뉴 활성화
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // 뒤로가기 버튼을 눌렀을 때 , 바로 이전 화면으로 이동
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //인스타그램에서 크롤링을 통해서 데이터를 가져오는 함수
    /*

    */
}