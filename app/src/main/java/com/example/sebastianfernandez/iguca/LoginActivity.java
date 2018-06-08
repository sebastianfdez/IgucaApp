package com.example.sebastianfernandez.iguca;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        enterButton = findViewById(R.id.enterButton);
        enterButton.setEnabled(false);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("personName", nameInput.getText());
                setResult(2, intent);
                finish();//finishing activity
            }
        });

        nameInput = findViewById(R.id.nameInput);
        nameInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft ) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameInputEmpty = (nameInput.getText().length() == 0);
                checkButton();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        rutInput = findViewById(R.id.rutInput);
        rutInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft ) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                rutInputEmpty = (rutInput.getText().length() == 0);
                checkButton();
            }
        });

        mailInput = findViewById(R.id.mailInput);
        mailInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft ) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mailInputEmpty = (mailInput.getText().length() == 0);
                checkButton();
            }
        });
    }

    private void checkButton() {
        Log.d("LOGIN3:", nameInputEmpty + "," + rutInputEmpty + "," + mailInputEmpty);
        if (!nameInputEmpty && !rutInputEmpty && !mailInputEmpty) {
            enterButton.setEnabled(true);
        } else {
            enterButton.setEnabled(false);
        }
    }

}
