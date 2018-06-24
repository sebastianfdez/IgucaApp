package com.example.sebastianfernandez.iguca;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SecretLogin extends AppCompatActivity {

    private String TAG = "Secret Login";

    private FirebaseAuth mAuth;

    private Button enterButton;
    private EditText mailText;
    private EditText passText;
    private TextView statusText;
    private ProgressBar progressBar;

    private String mail;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_secret_login );

        this.getViewModels();

        mAuth = FirebaseAuth.getInstance();
        //db = FirebaseDatabase.getInstance().getReference( "igucapp/Cursos" );
    }

    private void getViewModels() {
        enterButton = (Button) findViewById( R.id.enterButtonSecretLogin );
        mailText = (EditText) findViewById( R.id.mailEditText );
        passText = (EditText) findViewById( R.id.passEditText );
        statusText = (TextView) findViewById( R.id.statusTextView );
        progressBar = (ProgressBar) findViewById( R.id.progressBar );
        progressBar.setVisibility( View.INVISIBLE );

        enterButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mail = mailText.getText().toString().trim();
                pass = passText.getText().toString().trim();

                if (TextUtils.isEmpty( mail )) {
                    statusText.setText( "Agregar Mail" );
                    return;
                }
                if (TextUtils.isEmpty( pass )) {
                    statusText.setText( "Agregar Contraseña" );
                    return;
                }
                progressBar.setVisibility( View.VISIBLE );
                signIn( mail, pass );

            }
        } );
    }

    private void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword( email, password )
            .addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d( TAG, "signInWithEmail:success" );
                        statusText.setText( "Ingreso exitoso" );
                        FirebaseUser user = mAuth.getCurrentUser();
                        progressBar.setVisibility( View.INVISIBLE );
                        Intent showAdminCoursesActivity = new Intent( getApplicationContext(), AdminListCourses.class );
                        startActivity( showAdminCoursesActivity );
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w( TAG, "signInWithEmail:failure", task.getException() );
                        statusText.setText( "Ingreso fallido" );
                        progressBar.setVisibility( View.INVISIBLE );
                    }
                }
            } );
    }
}
