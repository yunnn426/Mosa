package com.example.mosa.recommend;



import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mosa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

//이 엑티비티는 화장품 추천정보를 보여준다.
public class reccosActivity extends AppCompatActivity {

    ArrayList<Bitmap> itemfile;
    ArrayList<String> itemfile_ex;
    item_Recycler recycler;
    RecyclerView itemlist;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommend_screen_cos);
        itemlist=findViewById(R.id.itemlist_view);
        int itemnum=10;
        //여기서 결과 값을 받고 이 결과값을 이용해서 추천아이템을 검색
        Intent intent=getIntent();
        //String result_value=intent.getStringExtra("result");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        itemfile=new ArrayList<Bitmap>();
        itemfile_ex=new ArrayList<String>();

        //나중에 폴더 이름으로 따로 분류하고 그러면 될듯
        //String filesname="test_com_";
        //String filesname=result_value;
        storage=FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("test/");
        StorageReference storageReference1 =storage.getReference().child("text_ex/");
        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    item.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            itemfile.add(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(reccosActivity.this,"저장된 이미지를 불러오는 것을 실패 했습니다.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(reccosActivity.this,"스토리지를 불러오는 것을 실패 했습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        storageReference1.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference item : listResult.getItems()){
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            try{
                                URL url=new URL(uri.toString());
                                BufferedReader in =new BufferedReader(new InputStreamReader(url.openStream()));
                                String inputline;
                                StringBuilder content=new StringBuilder();

                                while((inputline=in.readLine())!=null){
                                    content.append(inputline);
                                }
                                in.close();
                                String text_ex=content.toString();
                                itemfile_ex.add(text_ex);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(reccosActivity.this,"저장된 텍스트를 불러오는 것을 실패 했습니다.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(reccosActivity.this,"스토리지를 불러오는 것을 실패 했습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        recycler=new item_Recycler(itemfile,itemfile_ex);
        itemlist.setLayoutManager(new LinearLayoutManager(reccosActivity.this, RecyclerView.HORIZONTAL, false));
        itemlist.setAdapter(recycler);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.top_menu,menu);
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
}
