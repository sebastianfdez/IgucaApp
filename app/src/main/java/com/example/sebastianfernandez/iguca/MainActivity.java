package com.example.sebastianfernandez.iguca;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private FirebaseAuth mAuth;

    private TextView textView;
    private ListView myListView;
    private ImageView mySecretLogin;
    private String[] listItems;
    private String[] listItemsDescriptions;
    private String[] listItemsIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        Intent loginIntent = new Intent( getApplicationContext(), LoginActivity.class );
        // startActivityForResult(loginIntent, 2);

        this.makeMainList();
        this.createSecretLogin();

    }

    private void createSecretLogin() {
        mySecretLogin = (ImageView) findViewById( R.id.secretLoginIV );
        mySecretLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showSecretLoginActivity = new Intent( getApplicationContext(), SecretLogin.class );
                startActivityForResult ( showSecretLoginActivity, 3);
            }
        } );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        Log.d ( "h", "l" );

        if (requestCode == 3) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void makeMainList() {


        Resources res = getResources();
        listItems = res.getStringArray( R.array.main_menu_items );
        listItemsIcons = res.getStringArray( R.array.main_menu_icons );
        listItemsDescriptions = res.getStringArray( R.array.main_menu_description );

        // myListView.setAdapter(new ArrayAdapter<String>(this, R.layout.main_list, listItems));
        ListItemAdaptor itemAdapter = new ListItemAdaptor( this, listItems, listItemsDescriptions, listItemsIcons );

        myListView = (ListView) findViewById( R.id.myListView );
        myListView.setAdapter( itemAdapter );

        myListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent showInstructionsActivity = new Intent( getApplicationContext(), InstructionsActivity.class );
                    startActivity( showInstructionsActivity );
                } else if (position == 1) {
                    Intent showCoursesActivity = new Intent( getApplicationContext(), CoursesActivity.class );
                    startActivity( showCoursesActivity );
                }
            }
        } );
    }

}
