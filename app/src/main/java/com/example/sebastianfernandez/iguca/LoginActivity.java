package com.example.sebastianfernandez.iguca;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private Button enterButton;
    private TextView nameInput;
    private TextView rutInput;
    private TextView mailInput;

    private Boolean nameInputEmpty = true;
    private Boolean rutInputEmpty = true;
    private Boolean mailInputEmpty = true;

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_login );
        sharedPreferences = getSharedPreferences ( "myPref", Context.MODE_PRIVATE );
        editor = sharedPreferences.edit ();

        enterButton = findViewById ( R.id.enterButton );
        enterButton.setEnabled ( false );
        enterButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                editor.putString ( "UserName", "" + nameInput.getText () );
                editor.putString ( "UserRut", "" + rutInput.getText () );
                editor.putString ( "UserMail", "" + mailInput.getText () );
                editor.commit ();
                finish ();//finishing activity
            }
        } );

        nameInput = findViewById ( R.id.nameInput );
        nameInput.addTextChangedListener ( new TextWatcher () {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameInputEmpty = (nameInput.getText ().length () == 0);
                checkButton ();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        } );

        rutInput = findViewById ( R.id.rutInput );
        rutInput.addTextChangedListener ( new TextWatcher () {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                rutInputEmpty = (rutInput.getText ().length () == 0);
                checkButton ();
            }
        } );

        mailInput = findViewById ( R.id.mailInput );
        mailInput.addTextChangedListener ( new TextWatcher () {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mailInputEmpty = (mailInput.getText ().length () == 0);
                checkButton ();
            }
        } );

        nameInput.setText ( sharedPreferences.getString ( "UserName", "" ) );
        rutInput.setText ( sharedPreferences.getString ( "UserRut", "" ) );
        mailInput.setText ( sharedPreferences.getString ( "UserMail", "" ) );

    }

    private void checkButton() {
        if (!nameInputEmpty && !rutInputEmpty && !mailInputEmpty) {
            enterButton.setEnabled ( true );
        } else {
            enterButton.setEnabled ( false );
        }
    }

}
