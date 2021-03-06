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

/**
 * @author Öykü, Lara, Yaren, Sarper, Berk, Onur, Enis
 * @version 1.0
 * @date 24/04/2020
 * This class provides the sign in feature
 */
public class SignInUser extends AppCompatActivity {

    //properties
    private Button signIn;

    private EditText password;
    private EditText email;

    private TextView signUpHere;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState ); // calling parent constructor
        setContentView( R.layout.activity_log_in_screen );

        //so sign in will be a pop-up window
        makeItPopUp();

        //initializing properties
        signIn = ( Button )findViewById( R.id.sign_in_button );
        password = findViewById( R.id.password );
        email = findViewById( R.id.email_address );
        signUpHere = findViewById( R.id.sign_up_here );
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged( @NonNull FirebaseAuth firebaseAuth ) {
                signedInOrNot();
            }
        };

        //if sign in button is clicked
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //if sign up text is clicked
        signUpHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                toSignUp();
            }
        });

    }

    /**
     * a method which allows user to sign in.
     */
    public void signIn(){
        String strEmail = email.getText().toString();
        String strPassword = password.getText().toString();

        //if e-mail field is empty
        if ( strEmail.isEmpty() ) {
            email.setError( "Please enter a valid e-mail address." );
            email.requestFocus();
        }

        //if password field is empty
        else if ( strPassword.isEmpty() ){
            password.setError( "Please enter a valid password." );
            password.requestFocus();
        }

        //if both e-mail and password fields are empty
        else if ( strEmail.isEmpty() && strPassword.isEmpty() ){
            Toast.makeText( SignInUser.this, "Fields are empty!", Toast.LENGTH_SHORT ).show();
        }

        //if the fields are filled
        else if ( !(strEmail.isEmpty() && strPassword.isEmpty() ) ){
            mFirebaseAuth.signInWithEmailAndPassword( strEmail, strPassword ).addOnCompleteListener(SignInUser.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task ) {

                    //if sign in is failed
                    if ( !task.isSuccessful() ){
                        Toast.makeText( SignInUser.this, "Could not sign in, please try again.", Toast.LENGTH_SHORT ).show();
                    }

                    //if sign in is successful
                    else{
                        startActivity( new Intent( SignInUser.this, MainActivity.class ) );
                    }
                }
            });
        }

        //if some other non-predicted error has occurred
        else{
            Toast.makeText( SignInUser.this, "Nani??! Some error??!", Toast.LENGTH_SHORT ).show();
        }
    }

    /**
     * a method which checks the user's status to see if signed in or not.
     */
    public void signedInOrNot(){
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        //if user is signed in
        if( user != null ) {
            Toast.makeText( SignInUser.this, "Signed in successfully.", Toast.LENGTH_SHORT ).show();
            backToMain();
        }

        //if user is not signed in
        else{
            Toast.makeText( SignInUser.this, "Please sign in.", Toast.LENGTH_SHORT ).show();
        }
    }

    /**
     * a method which makes the related window a pop-up.
     */
    public void makeItPopUp(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( dm );

        int width = ( int ) ( dm.widthPixels * .8 );
        int height = ( int ) ( dm.heightPixels * .8 );

        getWindow().setLayout( width, height );
    }

    /**
     * a method which changes the window to sign up window.
     */
    public void toSignUp() {
        Intent i = new Intent(SignInUser.this, SignedUpUser.class );
        startActivity( i );
    }

    /**
     * a method which changes the window to main screen.
     */
    public void backToMain(){
        Intent backToMain = new Intent(SignInUser.this, MainActivity.class );
        startActivity( backToMain );
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener( mAuthStateListener );
    }
}
