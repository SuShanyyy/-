package com.susan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private GridView gridView;
    private EditText ed;
    private Button bt;

    private String url = "https://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592&cl=2&lm=&st=-1&fm=index&fr=&hs=0&xthttps=111110&sf=1&fmq=&pv=&ic=0&nc=1&z=&se=&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8";
    private List<String> list; // 存储图片URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void parse(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36 Edg/128.0.0.0")
                .addHeader("Connection", "keep-alive")
                .addHeader("Host", "image.baidu.com")
                .addHeader("Cookie", "BIDUPSID=AF8C873F7BD7C40384CB0EB2EC7F8465; PSTM=1716612477; BAIDUID=4D384D2CA8D6595658D2CF1EFC640F17:FG=1; newlogin=1; BDUSS=EJ2VE1mQ2V3NFJydzYyMkl1TTRSUm5XZFVkcTJ2cjVSVUtBSHNIQlZCNk15ZWRtRVFBQUFBJCQAAAAAAQAAAAEAAAD5oQBUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIw8wGaMPMBmW; BDUSS_BFESS=EJ2VE1mQ2V3NFJydzYyMkl1TTRSUm5XZFVkcTJ2cjVSVUtBSHNIQlZCNk15ZWRtRVFBQUFBJCQAAAAAAQAAAAEAAAD5oQBUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIw8wGaMPMBmW; BAIDUID_BFESS=4D384D2CA8D6595658D2CF1EFC640F17:FG=1; ZFY=UqLpW17qxuqDFO9:A3ylrhdLTmZ0cAJFSTrMuJaEhgeM:C; H_PS_PSSID=60275_60360_60621_60630_60664_60677; H_WISE_SIDS=60275_60360_60621_60630_60664_60677; H_WISE_SIDS_BFESS=60275_60360_60621_60630_60664_60677; BA_HECTOR=212k252h81802h8424210k2l9atdso1jco2t41u; BDORZ=FFFB88E999055A3F8A630C64834BD6D0; indexPageSugList=%5B%22%E6%96%97%E7%A0%B41%22%2C%22%E9%BB%84%E9%87%91%E5%88%86%E5%89%B2%E7%8E%87%E6%B5%B7%E6%8A%A5%22%2C%22%E8%AF%B8%E8%91%9B%E4%BA%AE%E7%AB%96%E5%B1%8F%E6%B5%B7%E6%8A%A5%22%2C%22%E4%BD%9B%E6%80%92%E7%81%AB%E8%8E%B2%22%2C%22%E8%90%A7%E7%82%8E%22%5D; BDRCVFR[dG2JNJb_ajR]=mk3SLVN4HKm; userFrom=www.hao123.com; BDRCVFR[-pGxjrCMryR]=mk3SLVN4HKm; ab_sr=1.0.1_YmY3ZWExYjcwZTFkYjQ4MDcyNDY3ZGVlN2FjNTk0NjM5MDk0MzFiOTE4OWZjZmZkOTIzMzI0N2Q4YTRmZDBmYTg3MjU0ZDY3NjA0YWM2ZWExYzAzZjQ5ZGFkMWRlYzFmMzE4M2ZmMTNiYjk3YzMxZjlhOGFmOTEwOWYyZWZlMTkwNTc4MjliOWNjYTU1YmY3ZTMyNjhmOTE3YzBkYTk4YzM4MTMwNWQzZGQ0MTdlMDJjOTQ1Y2I1ZTZmYzI1ZGM3YTZjY2VmZGU5OWMyYjI3MzZiZmJmNGQwMWYxOTk5NzU=")
                .addHeader("Upgrade-Insecure-Requests", "1") // 核心
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String regex = "\"ObjUrl\":\"(.*?)\"";
                                Pattern p = Pattern.compile(regex, Pattern.DOTALL);
                                Matcher m = p.matcher(responseBody);
                                while (m.find())
                                    list.add(m.group().replace("\"ObjUrl\":", "").replace("\"", "").replace("\\", ""));
                            }
                        });
                    }
                } finally {
                    response.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (list.size() == 0) {
                                Toast.makeText(MainActivity.this, "无搜索结果", Toast.LENGTH_SHORT).show();
                            } else {
                                MyAdapter myAdapter = new MyAdapter(list, MainActivity.this);
                                myAdapter.notifyDataSetChanged();
                                gridView.setAdapter(myAdapter);
                            }

                        }
                    });
                }
            }
        });
    }

    // 获得搜索图片后的URL
    private String getUrl(String word) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("word", word);
        return urlBuilder.toString();
    }

    private void init() {
        list = new ArrayList<>();

        gridView = findViewById(R.id.grid);
        ed = findViewById(R.id.ed);
        bt = findViewById(R.id.bt);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!list.isEmpty()) list.clear();
                url = getUrl(ed.getText().toString());
                // 解析源码
                parse(url);
            }
        });
    }
}