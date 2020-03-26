package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView lista;
    private ListView dispo;
    private ArrayList<String> Listcapteurs;
    private ArrayList<String> listeDispo;
    private ArrayAdapter<String> adapter;
    private TextView direction;
    private ArrayAdapter<String> adapter2;
    private SensorManager capteurs;
    private Sensor acel, proxi;
    private SensorEventListener sensorEvent;
    private CameraManager camera;
    private String idCamera;
    private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //nous initialisons les variables et les capteurs
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        capteurs = (SensorManager) getSystemService(SENSOR_SERVICE);
        lista = (ListView) findViewById(R.id.capteursList);
        dispo = (ListView) findViewById(R.id.listDispo);
        direction = (TextView) findViewById(R.id.direction);
        img = (ImageView) findViewById(R.id.imageView);
        acel = capteurs.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        proxi = capteurs.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        camera = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            idCamera = camera.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        if(acel == null)
            finish();
        boolean flash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);// Si le capteur existe on obtiens true et si non false
        CapteursList();
        DispoCapteurs();
    }
    @Override
    protected void onPause(){
        super.onPause();
        capteurs.unregisterListener(mSensorEvent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        capteurs.registerListener(mSensorEvent, acel, SensorManager.SENSOR_DELAY_NORMAL);
    }

// nous initialisons la méthode d'écoute des capteurs
    final SensorEventListener mSensorEvent = new SensorEventListener() {
        @Override
        //S'il y a des changements dans le capteur, ce bloc de code est exécuté
        public void onSensorChanged(SensorEvent event) {

            if (event.values[0] < proxi.getMaximumRange()){
                img.setImageResource(R.drawable.tierra);
            }else {
                img.setImageResource(R.drawable.lejos);
            }
            float x, y, z;
            ConstraintLayout container = findViewById(R.id.conteneteur);
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                if(z == 0) {
                    container.setBackgroundColor(Color.parseColor("#000000"));
                }
                if(0<z){
                    container.setBackgroundColor(Color.parseColor("#FF0000"));

                }
                if(z>1) {
                    container.setBackgroundColor(Color.parseColor("#4CAF50"));

                }
                if (z>1) {
                    direction.setText("Direaction: avant");
                }
                if (z>0) {
                    direction.setText("Direaction: arriere");
                }
                if (x<0) {
                    direction.setText("Direaction: droite");
                }
                if (x>0) {
                    direction.setText("Direaction: gauche");
                }
                if (y<0) {
                    direction.setText("Direaction: bas");
                    try {
                        camera.setTorchMode(idCamera, false);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                if (y>0) {
                    direction.setText("Direaction: haut");
                    try {
                        camera.setTorchMode(idCamera, true);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    public void CapteursList () {
        Listcapteurs = new ArrayList<String>(); // on crée une ArrayList
        List <Sensor> listCapteurs = capteurs.getSensorList(Sensor.TYPE_ALL); // on obtiens toutes sortes de capteurs sur l'appareil dans une List
        for(Sensor sensor: listCapteurs) { // On parcoure la List
            Listcapteurs.add(sensor.getName()); // on mets les valeurs dans une arrayList
        }
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, Listcapteurs); // On creer une adapter avec notre list
        lista.setAdapter(adapter); // On passe les donnes à notre List View
    }
    public void DispoCapteurs() {
        listeDispo = new ArrayList<String>();
        if(capteurs.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) { //on obtiens un objet qui corresponds au controlateur de ce capteur
            listeDispo.add("le capteur accelerometer existe");
        }else {
            listeDispo.add("le capteur accelerometer n'existe pas");
        }
        if(capteurs.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            listeDispo.add("le capteur AMBIENT_TEMPERATURE existe");
        }else {
            listeDispo.add("le capteur AMBIENT_TEMPERATURE n'existe pas");
        }
        if(capteurs.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
            listeDispo.add("le capteur GRAVITY existe");
        }else {
            listeDispo.add("le capteur GRAVITY n'existe pas");
        }
        /*
        on cree un adapter pour convertir
        le Array List et après l'ajouter dans le ListView
        */
        adapter2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, listeDispo);
        dispo.setAdapter(adapter2);
    }
}
