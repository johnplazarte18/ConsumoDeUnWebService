package com.example.consumedeunwebservice;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.consumedeunwebservice.WebService.*;

import org.json.*;

public class MainActivity extends AppCompatActivity implements Asynchtask {

    Map<String,String> datos;
    TextView txtCasosConfirmados,txtPacientesRecuperados,txtPacientesFallecidos,txtPais;
    Spinner cmbPaises;
    Boolean llenpais=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtCasosConfirmados=(TextView)findViewById(R.id.txtCasosConfirmados);
        txtPacientesRecuperados=(TextView)findViewById(R.id.txtPacientesRecuperados);
        txtPacientesFallecidos=(TextView)findViewById(R.id.txtPacientesFallecidos);
        txtPais=(TextView)findViewById(R.id.txtPais);
        cmbPaises=(Spinner)findViewById(R.id.cmbPaises);
        this.Paises();
    }
    public void Paises(){
        datos=new HashMap<String, String>();
        WebService ws= new WebService("https://api.covid19api.com/countries",datos,MainActivity.this,MainActivity.this);
        ws.execute("GET");
        llenpais=true;
    }
    public void click_Consultar(View view){
        if(cmbPaises.getSelectedItem()==null){
            Toast.makeText(this,"Seleccione el país",Toast.LENGTH_LONG).show();
        }else{
            datos=new HashMap<String, String>();
            WebService ws= new WebService("https://api.covid19api.com/total/country/"+cmbPaises.getSelectedItem().toString(),datos,MainActivity.this,MainActivity.this);
            ws.execute("GET");
            llenpais=false;
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void llenarPaises(String result) throws JSONException{
        ArrayList<String> lstDias = new ArrayList<String>();
        JSONArray JSONlista =  new JSONArray(result);
        for(int i=0; i< JSONlista.length();i++){
            JSONObject diaInfo=  JSONlista.getJSONObject(i);
            lstDias.add(diaInfo.getString("Slug").toString());
        }
        lstDias.sort(String::compareTo);
        cmbPaises.setAdapter(new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,lstDias));
    }
    public void mostrarInfo(String result) throws JSONException {
        ArrayList<ArrayList> lstDias = new ArrayList<ArrayList>();
        JSONArray JSONlista =  new JSONArray(result);


        for(int i=0; i< JSONlista.length();i++){
            JSONObject diaInfo=  JSONlista.getJSONObject(i);
            ArrayList<String> info = new ArrayList<String>();
            info.add(diaInfo.getString("Confirmed").toString());
            info.add(diaInfo.getString("Recovered").toString());
            info.add(diaInfo.getString("Deaths").toString());
            lstDias.add(info);
        }
        if(lstDias.size()==0){
            Toast.makeText(this,"No hay datos sobre "+cmbPaises.getSelectedItem().toString(),Toast.LENGTH_LONG).show();
        }else{
            ArrayList<String> dato = new ArrayList<String>();
            dato=lstDias.get(lstDias.size()-1);
            txtPais.setText(cmbPaises.getSelectedItem().toString());
            txtCasosConfirmados.setText(dato.get(0));
            txtPacientesRecuperados.setText(dato.get(1));
            txtPacientesFallecidos.setText(dato.get(2));
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void processFinish(String result) throws JSONException {
        if(llenpais){
            this.llenarPaises(result);
        }else{
            this.mostrarInfo(result);
        }
    }

}