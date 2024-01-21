package com.example.appsreadjson;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainViewActivityAll extends AppCompatActivity {
    ImageView imv;
    TextView tvId, tvNama, tvJumlahAnggota;
    Button btPrev, btNext;
    private ArrayList<Ormawa> JOrmawa = new ArrayList<Ormawa>();
    int currpos;
    private RequestQueue mRequestQueue;
    private RequestQueue mRequestQueueImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view_all);

        imv = (ImageView) findViewById(R.id.ivData);
        tvId = (TextView) findViewById(R.id.tvId);
        tvNama = (TextView) findViewById(R.id.tvNama);
        tvJumlahAnggota = (TextView) findViewById(R.id.tvJumlahAnggota);
        btPrev = (Button) findViewById(R.id.btPrev);
        btNext = (Button) findViewById(R.id.btNext);
        currpos = 0;



        insertDataJSONtoList();



    }
    private void ViewData(){
        tvId.setText("Id\t: " + JOrmawa.get(currpos).getId());
        tvNama.setText("Nama\t: " + JOrmawa.get(currpos).getNama());
        tvJumlahAnggota.setText("Jumlah Anggota\t: " + JOrmawa.get(currpos).getJumlahAnggota());

        ImageRequest requestImage = new ImageRequest(UrlConnect.ROOT_LOADIMAGE + JOrmawa.get(currpos).getMyFoto(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imv.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainViewActivityAll.this, "Error load Image", Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueueImage.add(requestImage);
    }
    private void insertDataJSONtoList(){
        mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueueImage = Volley.newRequestQueue(this);
        String isi = UrlConnect.URL_LOAD_DATA;

        StringRequest StringRequest = new StringRequest(Request.Method.POST, UrlConnect.URL_LOAD_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) {
                        JSONArray dataOrmawa = obj.getJSONArray("data");
                        for (int i = 0; i < dataOrmawa.length(); i++) {
                            JSONObject dataObjOrmawa = dataOrmawa.getJSONObject(i);
                            JOrmawa.add(new Ormawa(
                                    dataObjOrmawa.getString("Id"),
                                    dataObjOrmawa.getString("Nama"),
                                    dataObjOrmawa.getInt("Jml_Anggota"),
                                    dataObjOrmawa.getString("MyFoto")
                            ));
                        }
                        ViewData();
                    } else {
                        Toast.makeText(MainViewActivityAll.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainViewActivityAll.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainViewActivityAll.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String > params = new HashMap<String, String>();
                params.put("ID", "Kosong");
                return params;
            }
        };
        mRequestQueue.add(StringRequest);
    }


}