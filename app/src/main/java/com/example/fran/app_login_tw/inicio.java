package com.example.fran.app_login_tw;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class inicio extends AppCompatActivity {
    EditText etnombre, etappater, etapmater, etusuario, etpassvord;
    String inputPassword="tespassword";
    String AES ="AES";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        etnombre=(EditText) findViewById(R.id.edinombre);
        etappater=(EditText) findViewById(R.id.ediappate);
        etapmater=(EditText) findViewById(R.id.ediapmate);
        etusuario=(EditText) findViewById(R.id.edinomusu);
        etpassvord=(EditText) findViewById(R.id.edipas);

        Intent intent =getIntent();
        String nombre = intent.getStringExtra("nombre");
        String apepaterno = intent.getStringExtra("apepaterno");
        String apematerno = intent.getStringExtra("apematerno");
        String nomusuario = intent.getStringExtra("nomusuario");
        String password = intent.getStringExtra("password");

        etnombre.setText(nombre);
        etappater.setText(apepaterno);
        etapmater.setText(apematerno);
        etusuario.setText(nomusuario);
        etpassvord.setText(password);

    }

    public void ocl(View view) throws Exception {

        String nombre = etnombre.getText().toString();
        String apepater =  etappater.getText().toString();
        String apemater = etapmater.getText().toString();
        String usu =  etusuario.getText().toString();
        String pass = etpassvord.getText().toString();

       String desnom = descryp(nombre,inputPassword);
       etnombre.setText(desnom);

        String desapa = descryp(apepater,inputPassword);
        etnombre.setText(desapa);

        String desapm = descryp(apemater,inputPassword);
        etnombre.setText(desapm);

        String desus = descryp(usu,inputPassword);
        etnombre.setText(desus);

        String despas = descryp(pass,inputPassword);
        etnombre.setText(despas);


    }

    private String descryp(String dato, String Password) throws Exception {
        SecretKeySpec key = generateKey(Password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE,key);
        byte[] decodedValue = Base64.decode(dato,Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);
        String descryp = new String(decValue);
        return  descryp;

    }

    private SecretKeySpec generateKey(String password) throws Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes,0,bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }
}
