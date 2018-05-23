package com.example.fran.app_login_tw;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import java.io.File;
import java.security.MessageDigest;
import java.security.Principal;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class principal extends AppCompatActivity implements View.OnClickListener {

    private final String CARPETA_RAIZ="misImagenesPrueba/";
    private final String RUTA_IMAGEN=CARPETA_RAIZ+"misFotos";
    final int COD_SELECCIONA=10;
    final int COD_FOTO=20;

    String inputPassword="tespassword";
    String AES ="AES";

    EditText etnombre, etappater, etapmater, etusuario, etpassvord;
    Button botonCargar, btnguardar;
    ImageView imagen;
    String path="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        imagen = (ImageView) findViewById(R.id.imageView);
        botonCargar=(Button) findViewById(R.id.btncargar);

        btnguardar=(Button) findViewById(R.id.btnguardar);
        etnombre=(EditText) findViewById(R.id.edinombre);
        etappater=(EditText) findViewById(R.id.ediappate);
        etapmater=(EditText) findViewById(R.id.ediapmate);
        etusuario=(EditText) findViewById(R.id.edinomusu);
        etpassvord=(EditText) findViewById(R.id.edipas);

        btnguardar.setOnClickListener(this);

        if(validaPermisos()){
          botonCargar.setEnabled(true);
        }else{
          botonCargar.setEnabled(false);
        }
    }

    private boolean validaPermisos() {
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }
        if ((checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)) {

            return true;
        }
        if((shouldShowRequestPermissionRationale(CAMERA) || (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)))){
           cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults.length==2 && grantResults[0] ==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED) {
            botonCargar.setEnabled(true);
            }else{
                solicitarPermisoManual();
            }
            }
        }

    private void solicitarPermisoManual() {
        final CharSequence[] opciones={"Si","No"};
        final AlertDialog.Builder alertOpciones= new AlertDialog.Builder(principal.this);
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Si")){
                    Intent intent =new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri= Uri.fromParts("package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }
                else{
                  Toast.makeText(getApplicationContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                  dialogInterface.dismiss();
                }
            }
        });

        alertOpciones.show();
    }


    private void cargarDialogoRecomendacion() {
     AlertDialog.Builder dialogo = new AlertDialog.Builder(principal.this);
     dialogo.setTitle("Permisos Desactivados");
     dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

     dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {


         @TargetApi(Build.VERSION_CODES.M)
         @Override
         public void onClick(DialogInterface dialogInterface, int i) {
           requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);

         }
     });
      dialogo.show();


    }


    private void cargarImagen() {
        final CharSequence[] opciones={"Tomar Foto","Galeria","Cancelar"};
        final AlertDialog.Builder alertOpciones= new AlertDialog.Builder(principal.this);
        alertOpciones.setTitle("Seleccione una Opcion");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Tomar Foto")){
                    tomarFoto();
                }
                else{
                    if (opciones[i].equals("Galeria")){
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent,"seleccione la aplicacion"),COD_SELECCIONA);
                    }
                    else{
                       dialogInterface.dismiss();
                    }
                }
            }
        });

        alertOpciones.show();

    }

    private void tomarFoto(){

        File fileImagen=new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean isCreada=fileImagen.exists();
        String nombreImagen="";

        if(isCreada==false){
            isCreada=fileImagen.mkdirs();
        }

        if(isCreada==true){
            String nombre=(System.currentTimeMillis()/1000)+".jpg";
        }

        path=Environment.getExternalStorageDirectory()+File.separator+RUTA_IMAGEN+File.separator+nombreImagen;

        File imagen=new File(path);

        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(imagen));
        startActivityForResult(intent,COD_FOTO);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            switch (requestCode){
                case COD_SELECCIONA:
                    Uri miPath = data.getData();
                    imagen.setImageURI(miPath);
                    break;

                case COD_FOTO:
                    MediaScannerConnection.scanFile(this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String s, Uri uri) {
                      Log.i("Ruta de almacenamiento","Path:"+path);
                    }
                });
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    imagen.setImageBitmap(bitmap);

                    break;
            }

        }
    }

    public void oncli(View view) {
        cargarImagen();
    }

    @Override
    public void onClick(View view) {

        final String nombre = etnombre.getText().toString();
        final String apepaterno = etappater.getText().toString();
        final String apematerno = etapmater.getText().toString();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonReponse = new JSONObject(response);
                    boolean success= jsonReponse.getBoolean("success");

                    if (success){
                        Intent intent = new Intent(principal.this,plog.class);
                        principal.this.startActivity(intent);
                    } else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(principal.this);
                        builder.setMessage("Error De Registro")
                                .setNegativeButton("Retry",null)
                                .create().show();
                    }

                } catch (JSONException e){

                    e.printStackTrace();
                }
            }
        };
        RegisterRequest registerRequest = null;
        try {
            registerRequest = new RegisterRequest(encrypt(nombre,inputPassword),encrypt(apepaterno,inputPassword),encrypt(apematerno,inputPassword),encrypt(etusuario.getText().toString(),inputPassword),encrypt(etpassvord.getText().toString(),inputPassword),responseListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestQueue queue = Volley.newRequestQueue(principal.this);
        queue.add(registerRequest);
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
