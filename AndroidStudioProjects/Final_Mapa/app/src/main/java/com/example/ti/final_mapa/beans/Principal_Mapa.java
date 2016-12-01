package com.example.ti.final_mapa.beans;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.ti.final_mapa.AdaptadorDeUsuarios;
import com.example.ti.final_mapa.R;
import com.example.ti.final_mapa.modelo.Usuario;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Principal_Mapa extends FragmentActivity implements OnMapReadyCallback {

    //
    private DatabaseReference databaseReference,banco1,banco2;
    //
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    //
    private GoogleMap mMap;
    private TabHost tab;
    private TabHost.TabSpec tela1, tela2, tela3;
    //
    private ArrayList<Marker> mMarcadores = new ArrayList<Marker>();
    private ArrayList<Usuario> lstUsuario = new ArrayList<>();
    private List<Usuario> list = new ArrayList<>();

    //
    private RecyclerView mRecyclerView;
    //
    private String ID_usuarioCorrente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_mapa);
        //
        ID_usuarioCorrente = this.getIntent().getExtras().getString("nomeIdUsuario");
        //
        mRecyclerView = (RecyclerView) findViewById(R.id.ID_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(llm);
        //
        tab = (TabHost) findViewById(R.id.IDtabLayout);
        tab.setup();

        tela1 = tab.newTabSpec("T 1");
        tela1.setContent(R.id.tab1);
        tela1.setIndicator("Conversas");
        tab.addTab(tela1);

        tela2 = tab.newTabSpec("T 2");
        tela2.setContent(R.id.tab2);
        tela2.setIndicator("Pessoa encontradas", getResources().getDrawable(R.color.colorAccent));
        tab.addTab(tela2);

        tela3 = tab.newTabSpec("T 3");
        tela3.setContent(R.id.map);
        tela3.setIndicator("Mapa");
        tab.addTab(tela3);
        //
        //AdaptadorDeUsuarios adapter = new AdaptadorDeUsuarios(this,lstUsuario);
        /*AdaptadorDeUsuarios adapter = new AdaptadorDeUsuarios(this,list);

        mRecyclerView.setAdapter(adapter);*/
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if(true) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Preciso de Sua Localização!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    // Mapa
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setMapToolbarEnabled(false);
        new Localizar().execute();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_ACCESS_COARSE_LOCATION);
        }
        mMap.setMyLocationEnabled(true);

    }

    ////////////////////////////////////////
    protected class Localizar implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        private GoogleApiClient m_api;

        public Localizar() {
            super();
        }

        protected void execute() {
            m_api = new GoogleApiClient.Builder(getBaseContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            m_api.connect();
        }

        @Override
        public void onConnected(Bundle bundle) {

            final LatLng latLng = location();

            databaseReference = FirebaseDatabase.getInstance().getReference();

            databaseReference.addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {

                     if (dataSnapshot.getChildrenCount()==0){
                         //se tá vazio o banco sai do método de busca
                     }else{
                         //para cada usuario no banco, cria um objeto e add numa lista...
                         for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                             Usuario use = postSnapshot.getValue(Usuario.class);
                             lstUsuario.add(use);
                         }
                         list=lstUsuario;
                         AdaptadorDeUsuarios adapter = new AdaptadorDeUsuarios(getBaseContext(),list);

                         mRecyclerView.setAdapter(adapter);
                         // somente após os dados chegarem de fato...
                         for (int i=0; i< lstUsuario.size(); i++){

                             Double lt = Double.parseDouble(lstUsuario.get(i).getLat());
                             Double lg = Double.parseDouble(lstUsuario.get(i).getLng());

                             if(!lstUsuario.get(i).getId().equals(ID_usuarioCorrente)) {
                                 if (lstUsuario.get(i).getGenero().equals("Indefinido")) {
                                     Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lt, lg)));
                                     marker.setTitle(lstUsuario.get(i).getNome());
                                     marker.setSnippet(lstUsuario.get(i).getEmail());
                                     marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.indefinido));
                                     //mMap.addMarker(new MarkerOptions().position(new LatLng(lt,lg)).title(Integer.toString(lstUsuario.size())).snippet(Integer.toString(cont)));
                                     mMarcadores.add(marker);
                                 }
                                 if (lstUsuario.get(i).getGenero().equals("Feminino")) {
                                     Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lt, lg)));
                                     marker.setTitle(lstUsuario.get(i).getNome());
                                     marker.setSnippet(lstUsuario.get(i).getEmail());
                                     marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.feminino));
                                     //mMap.addMarker(new MarkerOptions().position(new LatLng(lt,lg)).title(Integer.toString(lstUsuario.size())).snippet(Integer.toString(cont)));
                                     mMarcadores.add(marker);
                                 }
                                 if (lstUsuario.get(i).getGenero().equals("Masculino")) {
                                     Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lt, lg)));
                                     marker.setTitle(lstUsuario.get(i).getNome());
                                     marker.setSnippet(lstUsuario.get(i).getEmail());
                                     marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.masculino));
                                     //mMap.addMarker(new MarkerOptions().position(new LatLng(lt,lg)).title(Integer.toString(lstUsuario.size())).snippet(Integer.toString(cont)));
                                     mMarcadores.add(marker);
                                 }
                             }
                         }

                     }
                 }

                 @Override
                 public void onCancelled(DatabaseError databaseError) {

                 }
            });
/*
            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)));
            marker.setTitle("Titulo test");
            marker.setSnippet("test");
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.indefinido));
            //mMap.addMarker(new MarkerOptions().position(new LatLng(lt,lg)).title(Integer.toString(lstUsuario.size())).snippet(Integer.toString(cont)));
            mMarcadores.add(marker);

*/
            mMap.animateCamera(cameraPosition(latLng, 15, 0, 0));
            m_api.disconnect();
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }

        public CameraUpdate cameraPosition(LatLng latLng, float zoom, float tilt, float bearing) {
            CameraPosition.Builder builder = new CameraPosition.Builder();

            CameraPosition position = builder.target(latLng)
                    .zoom(zoom)
                    .tilt(tilt)
                    .bearing(bearing)
                    .build();
            return CameraUpdateFactory.newCameraPosition(position);
        }

        public LatLng location() {
            if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            Location loc = LocationServices.FusedLocationApi.getLastLocation(m_api);

            return new LatLng(loc.getLatitude(),loc.getLongitude());
        }
        //////////////////////////////////////////////////////////////////////////////

    } // Fim da Classe Localizar
    /////////////////////////////////////////////////////////////////////////////////
} // Fim da Classe Apontamento