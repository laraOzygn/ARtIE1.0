package com.example.artie10;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {

    //properties
    private Button signIn;
    private EditText password;
    private EditText email;
    private TextView signUpHere;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_log_in_screen );

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( dm );

        int width = (int) ( dm.widthPixels * .6 );
        int height = (int) ( dm.heightPixels * .6 );

        getWindow().setLayout( width, height );

        signIn = ( Button )findViewById( R.id.sign_in_button );
        password = findViewById( R.id.password );
        email = findViewById( R.id.email_address );
        signUpHere = findViewById( R.id.sign_up_here );
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged( @NonNull FirebaseAuth firebaseAuth ) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if( user != null ) {
                    Toast.makeText( SignIn.this, "Logged in successfully.", Toast.LENGTH_SHORT ).show();
                    Intent i = new Intent( SignIn.this, MainActivity.class );
                    startActivity( i );
                }
                else{
                    Toast.makeText( SignIn.this, "Please log in.", Toast.LENGTH_SHORT ).show();
                }
            }
        };

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = email.getText().toString();
                String strPassword = password.getText().toString();
                if ( strEmail.isEmpty() ) {
                    email.setError( "Please enter a valid e-mail address." );
                    email.requestFocus();
                }
                else if ( strPassword.isEmpty() ){
                    password.setError( "Please enter a valid password." );
                    password.requestFocus();
                }
                else if ( strEmail.isEmpty() && strPassword.isEmpty() ){
                    Toast.makeText( SignIn.this, "Fields are empty!", Toast.LENGTH_SHORT ).show();
                }
                else if ( !(strEmail.isEmpty() && strPassword.isEmpty() ) ){
                    mFirebaseAuth.signInWithEmailAndPassword( strEmail, strPassword ).addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task ) {
                            if ( !task.isSuccessful() ){
                                Toast.makeText( SignIn.this, "Could not sign in, please try again.", Toast.LENGTH_SHORT ).show();
                            }
                            else{
                                startActivity( new Intent( SignIn.this, MainActivity.class ) );
                            }
                        }
                    });
                }
                else{
                    Toast.makeText( SignIn.this, "Nani??! Some error??!", Toast.LENGTH_SHORT ).show();
                }
            }
        });

        signUpHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                toSignUp();
            }
        });

    }

    public void toSignUp() {
        Intent i = new Intent(SignIn.this, SignUp.class );
        startActivity( i );
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener( mAuthStateListener );
    }
}