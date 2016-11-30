package com.example.ti.final_mapa;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.TabHost;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Principal_Mapa_original extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private double latitude,longitude;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private TabHost tab;
    private TabHost.TabSpec tela1, tela2, tela3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_mapa);
        //
        tab = (TabHost) findViewById(R.id.IDtabLayout);
        tab.setup();

        tela1 = tab.newTabSpec("T 1");
        tela1.setContent(R.id.map);
        tela1.setIndicator("Minha primeira tela");
        tab.addTab(tela1);

        tela2 = tab.newTabSpec("T 2");
        tela2.setContent(R.id.tab2);
        tela2.setIndicator("Minha segunda tela", getResources().getDrawable(R.color.colorAccent));
        tab.addTab(tela2);

        tela3 = tab.newTabSpec("T 3");
        tela3.setContent(R.id.map);//tab3
        tela3.setIndicator("Minha terceira tela", ContextCompat.getDrawable(this, R.color.colorPrimary));
        tab.addTab(tela3);
        //
        conectar();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Mapa
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(latitude != 0.0 && longitude!=0.0) {
            // Add a marker in Sydney and move the camera
            LatLng sydney = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").snippet("test"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    }

    private synchronized void conectar() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this) // esta eh a referencia a entidade que implementa , no nosso caso eh a Listener ou seja, esta propria
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API) // depois adcicionei a api que estarei utilizando que eh a locationService
                .build(); // depois do build esta pronto , eu acabo de contruir meu objeto.
        mGoogleApiClient.connect();// agora eh só conectar.
    }

    // Listener
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //este metodo esta relacionado ao serviço do google que eu estou solicitando(no metodo anterior *conectar*), desta maneira ira pegar os dados que o serviço prover, com isso nós nao temos controle do que estará vindo no "bundle",possivelmente retonará vazio


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Location meuLocal = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);// o parametro de entrada eh ApiClient que eh o mGoogleApiClient
            // verifico se ele eh nulo. eh muito dificio acontecer de ele ser nulo, mas eh possivel
            if(meuLocal !=null ){
                latitude=meuLocal.getLatitude();
                longitude=meuLocal.getLongitude();
            }

            return;
        }
        // pegando a ultima localização conhecida.
        Location meuLocal = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);// o parametro de entrada eh ApiClient que eh o mGoogleApiClient
        // verifico se ele eh nulo. eh muito dificio acontecer de ele ser nulo, mas eh possivel
        if(meuLocal !=null){
            latitude=meuLocal.getLatitude();
            longitude=meuLocal.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
