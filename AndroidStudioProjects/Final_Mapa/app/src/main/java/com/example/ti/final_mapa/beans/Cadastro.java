package com.example.ti.final_mapa.beans;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ti.final_mapa.R;
import com.example.ti.final_mapa.modelo.Usuario;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Cadastro extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient m_api;
    private EditText email, senha, nomeApelido;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference, banco, banco1, refBanco, banco2;
    //private DatabaseReference banco;
    private Usuario usuario;
    private String[] paises, generos, estados;
    private Spinner selectPaisSpinner, selectEstadosSpinner, selectGeneroSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        ////////////////////////////////////////////////////////////////////////////////////////////
        m_api = new GoogleApiClient.Builder(getBaseContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        m_api.connect();
        ////////////////////////////////////////////////////////////////////////////////////////////
        paises = getResources().getStringArray(R.array.listaDePaises);

        selectPaisSpinner = (Spinner) findViewById(R.id.IDspinnerPais);
        selectEstadosSpinner = (Spinner) findViewById(R.id.IDspinnerEstados);
        selectGeneroSpinner = (Spinner) findViewById(R.id.IDspinnerGenero);

        selectPaisSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String nome = selectPaisSpinner.getSelectedItem().toString();
                int idSpinner = getResources().getIdentifier(nome, "array", Cadastro.this.getPackageName());

                ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(Cadastro.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(idSpinner));

                selectEstadosSpinner.setAdapter(stringArrayAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();
//        banco = databaseReference.child(selectPaisSpinner.getSelectedItem().toString());
        //Obter instância Firebase auth
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        nomeApelido = (EditText) findViewById(R.id.ID_nome);
        email = (EditText) findViewById(R.id.ID_email);
        senha = (EditText) findViewById(R.id.ID_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        ////////////////////////////////////////////////////////////////////////////////////////////

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        final LatLng latLng = location();
        if(latLng != null){
        m_api.disconnect();

            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String emailLocal = email.getText().toString().trim();
                    final String senhaLocal = senha.getText().toString().trim();
                    final String nomeLocal = nomeApelido.getText().toString().trim();

                    if (TextUtils.isEmpty(nomeLocal)) {
                        Toast.makeText(getApplicationContext(), "Insira um nome ou apelido!!!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (nomeLocal.length() < 2) {
                        Toast.makeText(getApplicationContext(), "Nome/Apelido muito curto, insira no minimo 2 caracteres!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(emailLocal)) {
                        Toast.makeText(getApplicationContext(), "Insira o endereço de Email", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(senhaLocal)) {
                        Toast.makeText(getApplicationContext(), "Insira a Senha!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (senhaLocal.length() < 6) {
                        Toast.makeText(getApplicationContext(), "Senha muito curta, insira no minimo 6 caracteres!", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    progressBar.setVisibility(View.VISIBLE);
                    //cria usuário
                    auth.createUserWithEmailAndPassword(emailLocal, senhaLocal)
                            .addOnCompleteListener(Cadastro.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Toast.makeText(Cadastro.this, "Cria Usuário com Email: " + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    // Se a assinatura falhar, exibir uma mensagem ao usuário.
                                    // Se a assinatura for bem sucedida será mostrado uma
                                    // notificação com o estado deste metodo auth e a lógica
                                    // para lidar com o usuario conectado pode ser tratado neste Listener.
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(Cadastro.this, "Autentificação falhou." + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        usuario = new Usuario();
                                        usuario.setPais(selectPaisSpinner.getSelectedItem().toString());
                                        usuario.setEstado(selectEstadosSpinner.getSelectedItem().toString());
                                        usuario.setGenero(selectGeneroSpinner.getSelectedItem().toString());
                                        usuario.setEmail(emailLocal);
                                        usuario.setSenha(senhaLocal);
                                        usuario.setNome(nomeLocal);
                                        usuario.setId(auth.getCurrentUser().getUid().toString());
                                        usuario.setOnline("true");
                                        usuario.setLat(Double.toString(latLng.latitude));
                                        usuario.setLng(Double.toString(latLng.longitude));
                                        //banco = databaseReference.child(usuario.getPais());
                                        //banco1 = banco.child(usuario.getEstado());
                                        //refBanco = banco1.child(usuario.getId());
                                        //refBanco.setValue(usuario);
                                        databaseReference.child(usuario.getId()).setValue(usuario);
                                        usuario = new Usuario();
                                        Toast.makeText(getBaseContext(), "Usuario Criado com Sucesso !!!", Toast.LENGTH_SHORT).show();

                                        finish();
                                    }
                                }
                            });

                }
            });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public LatLng location() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        Location loc = LocationServices.FusedLocationApi.getLastLocation(m_api);
        return new LatLng(loc.getLatitude(),loc.getLongitude());
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
}
