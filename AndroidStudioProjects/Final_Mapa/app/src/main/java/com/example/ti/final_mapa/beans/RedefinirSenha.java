package com.example.ti.final_mapa.beans;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ti.final_mapa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RedefinirSenha extends AppCompatActivity {

    private EditText emailDeEntrada;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnReset,btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redefinir_senha);
        auth=FirebaseAuth.getInstance();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        emailDeEntrada = (EditText)  findViewById(R.id.ID_email_redefinirSenha);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        btnVoltar= (Button) findViewById(R.id.btn_back);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);

                auth.sendPasswordResetEmail(emailDeEntrada.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "email enviado com sucesso !", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    Toast.makeText(getApplicationContext(), "email não cadastrado !", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
