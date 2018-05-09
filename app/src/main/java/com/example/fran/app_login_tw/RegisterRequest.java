package com.example.fran.app_login_tw;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fran on 24/04/2018.
 */

public class RegisterRequest extends  StringRequest {

    private static  final String REGISTER_REQUEST_URL="http://173.6.0.228:8000/Register.php";
    private Map<String,String> params;
    public RegisterRequest(String nombre, String apepaterno, String apematerno, String nomusuario, String password, Response.Listener<String> listener){
        super(Method.POST, REGISTER_REQUEST_URL,listener,null);
        params=new HashMap<>();
        params.put("nombre",nombre);
        params.put("apepaterno",apepaterno);
        params.put("apematerno",apematerno);
        params.put("nomusuario",nomusuario);
        params.put("password",password);
    }
    @Override
    public Map<String, String> getParams(){
        return params;
    }

}
