package com.example.appsreadjson;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class MainActivitySearchData extends AppCompatActivity {
    ImageView imv;
    TextView etId;
    Button btSearch;
    Ormawa ormawa;
    TextView tvNama, tvJumlahAnggota;

    RequestQueue mRequestQueue;
    RequestQueue mRequestQueueImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_search_data);

        imv = (ImageView) findViewById(R.id.ivData);
        etId = (TextView) findViewById(R.id.etId);
        btSearch = (Button) findViewById(R.id.btSearch);
        tvNama = (TextView) findViewById(R.id.tvNama);
        tvJumlahAnggota = (TextView) findViewById(R.id.tvJumlahAnggota);

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchData(etId.getText().toString());
            }
        });
    }

    private void viewData(){
        tvNama.setText("Nama\t: " + ormawa.getNama().toString());
        tvJumlahAnggota.setText("Jumlah Anggota\t: " + String.valueOf(ormawa.getJumlahAnggota()));
        ImageRequest requestImage = new ImageRequest(UrlConnect.ROOT_LOADIMAGE + ormawa.getMyFoto(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imv.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivitySearchData.this, "Error Load Image", Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueueImage.add(requestImage);
    }
    private void searchData(String id){
        mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueueImage = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlConnect.URL_LOAD_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) {
                        JSONArray dataOrmawa = obj.getJSONArray("data");

                        for (int i = 0; i < dataOrmawa.length(); i++) {
                            JSONObject dtObjOrmawa = dataOrmawa.getJSONObject(i);
                            ormawa = new Ormawa(
                                    dtObjOrmawa.getString("Id"),
                                    dtObjOrmawa.getString("Nama"),
                                    dtObjOrmawa.getString("MyFoto"),
                                    dtObjOrmawa.getInt("Jml_Anggota")
                            );
                        }
                        viewData();
                    } else {
                        Toast.makeText(MainActivitySearchData.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivitySearchData.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivitySearchData.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("IDUkm", id);
                return params;
            }
        };
        mRequestQueueImage.add(stringRequest);
    }
}

