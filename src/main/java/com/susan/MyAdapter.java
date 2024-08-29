package com.susan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class MyAdapter extends BaseAdapter {

    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 1;
    private List<String> datas; // 图片的URL
    private LayoutInflater inflater;
    private OkHttpClient client;
    private Map<String, Bitmap> cache = Collections.synchronizedMap(new WeakHashMap<>());
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private Context context;

    public MyAdapter(List<String> datas, Context context) {
        this.datas = datas;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.client = new OkHttpClient();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_img, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.img = convertView.findViewById(R.id.item_img);

            viewHolder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 获取ImageView中的Bitmap对象
                    Bitmap bitmap = ((BitmapDrawable) viewHolder.img.getDrawable()).getBitmap();
                    // 创建BitmapDrawable对象
                    BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
                    new MyDialog(context, R.style.myDialog, drawable).show();
                }
            });

            viewHolder.bt = convertView.findViewById(R.id.item_bt);

            viewHolder.bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "待更新~", Toast.LENGTH_SHORT).show();
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String url = datas.get(position);
        Bitmap cachedBitmap = cache.get(url);
        if (cachedBitmap != null) {
            // 如果缓存中有图片，则直接使用
            mainThreadHandler.post(() -> viewHolder.img.setImageBitmap(cachedBitmap));
        } else {
            // 如果缓存中没有，则发起网络请求
            downloadImage(url, viewHolder);
        }
        return convertView;
    }

    private void downloadImage(String url, final ViewHolder viewHolder) {
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try (InputStream is = response.body().byteStream()) {
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        cache.put(url, bitmap);
                        mainThreadHandler.post(() -> viewHolder.img.setImageBitmap(bitmap));
                    }
                }
            }
        });
    }

    private class ViewHolder {
        ImageView img;
        Button bt;
    }
}