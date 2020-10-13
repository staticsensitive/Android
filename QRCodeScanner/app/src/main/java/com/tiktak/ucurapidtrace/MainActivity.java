package com.tiktak.ucurapidtrace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    public static final int CAMERA_PERMISSION_CODE = 100;
    private CodeScanner mCodeScanner;
    RequestQueue requestQueue;

    String HttpUrl = "http://cityhall.ucu.edu.ph:8085/rapid/insert.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        final TextView txtresult = findViewById(R.id.txtresult);
        requestQueue = Volley.newRequestQueue(MainActivity.this);

        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);

        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                        txtresult.setText(result.getText());

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String ServerResponse) {


                                        // Showing response message coming from server.
                                        Toast.makeText(MainActivity.this, ServerResponse, Toast.LENGTH_LONG).show();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {


                                        // Showing error message if something goes wrong.
                                        Toast.makeText(MainActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() {

                                // Creating Map String Params.
                                Map<String, String> params = new HashMap<String, String>();

                                // Adding All values to Params.
                                params.put("qrcontents", result.getText());
                                params.put("officeorigin", "UCUMISPlus");

                                return params;
                            }

                        };

                        // Creating RequestQueue.
                        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

                        // Adding the StringRequest object into requestQueue.
                        requestQueue.add(stringRequest);

                    }//public void run
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    public void checkPermission(String permission, int requestCode){
        if(ContextCompat.checkSelfPermission(MainActivity.this, permission)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {permission},
                    requestCode);
        }
        else{
            Toast.makeText(this, "Permission Already Granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}