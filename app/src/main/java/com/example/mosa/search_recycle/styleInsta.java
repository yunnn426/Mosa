package com.example.mosa.search_recycle;

import android.os.AsyncTask;
import android.telecom.Call;


import com.google.gson.JsonObject;

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
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class styleInsta extends AsyncTask<String, Void, List<String>> {


    @Override
    protected List<String> doInBackground(String...params) {
        String tag=params[0];
        String urlStr = "https://www.instagram.com/explore/tags/" + tag + "/";
        List<String> imgurl = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(urlStr).get();
            Elements elements = doc.select("script[type='text/javascript']").eq(3);
            String script = elements.html().replace("window._sharedData = ", "").replace(";", "");

           /*
           jSONObject 데이터를 가져오려는 나의 노력
            String jsonData=null;
            Element script;
            for (int i=0;i<elements.size();i++) {
                script=elements.get(i);
                if (script.html().contains("window._sharedData")) {
                    jsonData = script.html().replaceFirst("^.*window\\._sharedData =", "");
                    jsonData = jsonData.replaceFirst(";</script>$", "");
                    break;
                }
            }


            Retrofit retrofit=new Retrofit.Builder().baseUrl(urlStr).addConverterFactory(GsonConverterFactory.create()).build();
            InstaJsonInterface instajson=retrofit.create(InstaJsonInterface.class);
            Call<JsonObject> call=instajson.getTagData(tag);
            */
            JSONObject jsonObject = new JSONObject(script);
            JSONObject entry_data = jsonObject.getJSONObject("entry_data");
            JSONArray tagPage = entry_data.getJSONArray("TagPage");
            JSONObject tagPageObj = tagPage.getJSONObject(0);
            JSONObject graphql = tagPageObj.getJSONObject("graphql");
            JSONObject hashtag = graphql.getJSONObject("hashtag");
            JSONObject edge_hashtag_to_media = hashtag.getJSONObject("edge_hashtag_to_media");
            JSONArray edges = edge_hashtag_to_media.getJSONArray("edges");
            for (int i = 0; i < edges.length(); i++) {
                JSONObject node = edges.getJSONObject(i).getJSONObject("node");
                String imageUrl = node.getString("thumbnail_src");
                imgurl.add(imageUrl);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return imgurl;
    }
}
