package com.example.fran.app_login_tw;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class plog extends AppCompatActivity {
    EditText etusuario, etpassvord;
    Button  btnguardar;

    String inputPassword="tespassword";
    String AES ="AES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plog);

        etusuario=(EditText) findViewById(R.id.edinomusu);
        etpassvord=(EditText) findViewById(R.id.edipas);
        btnguardar=(Button) findViewById(R.id.btnguardar);

    }

    public void ocl(View view) throws Exception {

        final String nomusuario = etusuario.getText().toString();
        final String password = etpassvord.getText().toString();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                     boolean success = jsonResponse.getBoolean("success");

                     if (success){

                         String nombre = jsonResponse.getString("nombre");
                         String apepaterno = jsonResponse.getString("apepaterno");
                         String apematerno = jsonResponse.getString("apematerno");

                         Intent intent = new Intent(plog.this, principal.class);
                         intent.putExtra("nombre",nombre);
                         intent.putExtra("apepaterno",apepaterno);
                         intent.putExtra("apematerno", apematerno);
                         intent.putExtra("nomusuario",nomusuario);
                         intent.putExtra("password",password);

                        plog.this.startActivity(intent);
                     }
                    else{
                         AlertDialog.Builder builder = new AlertDialog.Builder(plog.this);
                         builder.setMessage("Error De Login")
                                 .setNegativeButton("Retry",null)
                                 .create().show();
                     }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        LoginRequest loginRequest = new LoginRequest(encrypt(nomusuario,inputPassword), encrypt(password,inputPassword), responseListener);
        RequestQueue queue = Volley.newRequestQueue(plog.this);
        queue.add(loginRequest);
    }

    private String encrypt(String dato, String password) throws Exception{
        SecretKeySpec key = generarkey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] encVal= c.doFinal(dato.getBytes());
        String encrypto= Base64.encodeToString(encVal,Base64.DEFAULT);
        return encrypto;
    }

    private SecretKeySpec generarkey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes,0,bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }
}
