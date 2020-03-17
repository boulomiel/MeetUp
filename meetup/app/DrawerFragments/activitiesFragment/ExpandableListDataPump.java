package com.rubenmimoun.meetup.app.DrawerFragments.activitiesFragment;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExpandableListDataPump {


    public static HashMap<String, List<String>> getData(String title,List<String>list) {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        // TODO REPLACE BY GOOGLE API, PARSE A JSON INTO A STRING ARRAY, LOOP THROUGH STRING ARRAY, ADD EACH STRING TO MAP
        List<String>city_name_list = new ArrayList<>();
        String city_url = "https://andruxnet-world-cities-v1.p.rapidapi.com/?query=israel&searchby=city" ;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(city_url)
                .get()
                .addHeader("x-rapidapi-host", "andruxnet-world-cities-v1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "9dbfd29d4fmsh61a5cbaed8513fcp1ec144jsn29719c751d5e")
                .build();

       client.newCall(request).enqueue(new Callback() {
           @Override
           public void onFailure(@NotNull Call call, @NotNull IOException e) {
               e.printStackTrace();
           }

           @Override
           public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

               if ((response.isSuccessful())){

                   String result = response.body().string() ;
                   System.out.println(result);

               }

           }
       });



        expandableListDetail.put(title, list);
        return expandableListDetail;
    }
}
