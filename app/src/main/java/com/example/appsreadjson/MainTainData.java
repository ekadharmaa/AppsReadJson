package com.example.appsreadjson;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;

public class MainTainData extends AppCompatActivity {

    ImageView imvMaintain;
    Button btPilih, btSave;
    Ormawa ormawa;
    boolean gantiImage;
    String pathImage, ModeMaintain, tempFile, myMessage;
    RequestQueue mRequestQueue, mRequestQueueImage;
    ProgressBar pgs;
    EditText etId, etNama, etJml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tain_data);

        imvMaintain = (ImageView) findViewById(R.id.imageViewMaintain);
        etId = (EditText) findViewById(R.id.editTextIdMaintain);
        etNama = (EditText) findViewById(R.id.editTextNamaMaintain);
        etJml = (EditText) findViewById(R.id.editTextJmlMaintain);

        pgs = (ProgressBar) findViewById(R.id.progressBarMaintain);
        btPilih = (Button) findViewById(R.id.buttonPilihFotoMaintain);
        btSave = (Button) findViewById(R.id.buttonSaveMaintain);
        pgs.setVisibility(View.GONE);
        imvMaintain.setImageResource(R.drawable.ic_launcher_background);
        gantiImage = false;

        etId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                searchData(etId.getText().toString());
            }
        });
        btPilih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gantiImage = true;
                String path = Environment.getExternalStorageDirectory() + "/" + "Pictures" + "/";
                Uri uri = Uri.parse(path);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setDataAndType(uri, "image/*");
                activityResultLauncher.launch(intent);
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModeMaintain = "insert";
                pgs.setVisibility(View.VISIBLE);
                ExeMaintain();
            }
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    Uri uri = o.getData().getData();
                    imvMaintain.setImageURI(uri);
                    pathImage = getRealPathFromURI(getBaseContext(), uri);
                }
            }
    );

    private StringRequest createRequestVolley() {
        String myURLs = "";
        switch (ModeMaintain) {
            case "insert":
                myMessage = "Insert Data";
                myURLs = UrlConnect.URL_INSERT_DATA;
                break;
            case "update":
                myMessage = "Update Data";
                myURLs = UrlConnect.URL_UPDATE_DATA;
                break;
            case "delete":
                myMessage = "Delete Data";
                myURLs = UrlConnect.URL_DELETE_DATA;
                break;
        }
        StringRequest stringRequest=new StringRequest(Request.Method.POST, myURLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pgs.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(MainTainData.this, myMessage + " " +
                                    jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            clearActivity();
                        } catch (Exception e) {
                            Toast.makeText(MainTainData.this,e.getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainTainData.this,error.getMessage()
                        , Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("IDUkm", etId.getText().toString());
                params.put("Nama", etNama.getText().toString());
                params.put("Jml_Anggota", etJml.getText().toString());
                switch (ModeMaintain){
                    case "insert" :
                        File file=new File(pathImage);
                        String namaFile=file.getName();
                        params.put("MyFoto1","Kosong");
                        params.put("MyFoto2",namaFile);
                        break;
                    case "update" :
                        if (gantiImage){
                            File file2=new File(pathImage);
                            String namaFile2=file2.getName();
                            params.put("MyFoto1", tempFile);
                            params.put("MyFoto2", namaFile2);
                        }else {
                            params.put("MyFoto1", tempFile);
                            params.put("MyFoto2", tempFile);
                        }
                        break;
                    case "delete" :
                        params.put("MyFoto1",tempFile);
                        params.put("MyFoto2","Kosong");
                        break;
                }
                return params;
            }
        };
        return stringRequest;
    }
    private void clearActivity(){
        etId.setText("");
        etNama.setText("");
        etJml.setText("");
        etId.setEnabled(true);
        imvMaintain.setImageResource(R.drawable.ic_launcher_background);
    }
    private void ExeMaintain(){
        mRequestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest=createRequestVolley();
        if (gantiImage){
            File file=new File(pathImage);
            RequestBody requestBody=RequestBody.create(MediaType.parse("image/*"),file);
            MultipartBody.Part FiletoUpload= MultipartBody.Part.createFormData("file", file.getName(),requestBody);
            RequestBody filename=RequestBody.create(MediaType.parse("text/plain"),file.getName());
            ApiConfig getResponse=AppConfig.getRetrofit().create(ApiConfig.class);
            Call<ServerResponse> call=getResponse.uploadFile(FiletoUpload,filename);
            call.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(retrofit2.Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                    ServerResponse serverResponse=response.body();
                    Toast.makeText(MainTainData.this, "Masuk",
                            Toast.LENGTH_LONG).show();
                    if (serverResponse!=null){
                        if(serverResponse.getSuccess()){
                            mRequestQueue.add(stringRequest);
                        }else{
                            Toast.makeText(MainTainData.this, serverResponse.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Log.e("Response : ",serverResponse.toString());
                    }
                }
                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    Log.e("Response : ",t.getMessage());
                }
            });
            gantiImage=false;
        }else{
            mRequestQueue.add(stringRequest);
        }
    }
    private void searchData(String key){
        mRequestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,
                UrlConnect.URL_LOAD_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj=new JSONObject(response);
                    if (!obj.getBoolean("error")){
                        JSONArray dataOrmawaArray=obj.getJSONArray("data");
                        for (int i=0;i<dataOrmawaArray.length();i++){
                            JSONObject dtObjOrmawa=dataOrmawaArray.getJSONObject(i);
                            ormawa=new Ormawa(
                                    dtObjOrmawa.getString("Id"),
                                    dtObjOrmawa.getString("Nama"),
                                    dtObjOrmawa.getInt("Jml_Anggota"),
                                    dtObjOrmawa.getString("MyFoto")
                            );
                        }
                        ViewData();
                    }else{

                    }
                }catch (Exception e){
                    Toast.makeText(MainTainData.this,
                            e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainTainData.this,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String,String>();
                params.put("IDUkm",key);
                return params;
            }
        };
        mRequestQueue.add(stringRequest);
    }

    private void ViewData(){
        mRequestQueueImage = Volley.newRequestQueue(this);
        pgs.setVisibility(View.VISIBLE);
        ImageRequest imageRequest=new ImageRequest(
                UrlConnect.ROOT_LOADIMAGE + ormawa.getPath(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                pgs.setVisibility(View.GONE);
                etId.setEnabled(false);
                etId.setText(ormawa.getId());
                etNama.setText(ormawa.getNama());
                etJml.setText(String.valueOf(ormawa.getJumlahAnggota()));
                imvMaintain.setImageBitmap(response);
                btSave.setEnabled(false);
                tempFile=ormawa.getPath();
            }
        }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pgs.setVisibility(View.GONE);
                        Toast.makeText(MainTainData.this,
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        mRequestQueueImage.add(imageRequest);
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] data_media_uri = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, data_media_uri, null,
                    null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}