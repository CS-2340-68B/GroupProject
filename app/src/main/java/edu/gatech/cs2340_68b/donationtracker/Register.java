package edu.gatech.cs2340_68b.donationtracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Register extends AppCompatActivity {

    private TextView username;
    private TextView password;
    private TextView confirmPassword;
    private Button register;
    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        username = (TextView)findViewById(R.id.registerUsername);
        password = (TextView)findViewById(R.id.registerPassword);
        confirmPassword = (TextView)findViewById(R.id.registerConfirmPassword);
        register = (Button)findViewById(R.id.register);
        cancel = (Button)findViewById(R.id.cancel);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
