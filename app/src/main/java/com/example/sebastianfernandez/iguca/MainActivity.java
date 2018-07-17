package com.example.sebastianfernandez.iguca;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private TextView textView;
    private ListView myListView;
    private ImageView mySecretLogin;
    private String[] listItems;
    private String[] listItemsDescriptions;
    private String[] listItemsIcons;

    private String[] coursesSelectedNames = new String[0];

    private SharedPreferences sharedPref;

    private Integer secretClicks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        sharedPref  = getSharedPreferences("myPref", Context.MODE_PRIVATE);

        final Intent loginIntent = new Intent( getApplicationContext(), LoginActivity.class );
        if (sharedPref.getString ( "UserName", "" ) == "") {
            Log.d("long", sharedPref.getString ( "UserName", "" ));
            startActivity(loginIntent);
        }

        findViewById ( R.id.buttonLogin ).setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                startActivity ( loginIntent );
            }
        } );

        File imgFile = new  File(getBaseContext ().getCacheDir (), "companyImg/companyIcon.png");

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView myImage = (ImageView) findViewById(R.id.companyIV);

            myImage.setImageBitmap(myBitmap);

        }

        this.makeMainList();
        this.createSecretLogin();

    }

    private void createSecretLogin() {
        secretClicks = 0;
        mySecretLogin = (ImageView) findViewById( R.id.secretLoginIV );
        mySecretLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secretClicks++;
                if (secretClicks > 10) {
                    Intent showSecretLoginActivity = new Intent ( getApplicationContext (), SecretLogin.class );
                    startActivityForResult ( showSecretLoginActivity, 3 );
                    secretClicks = 0;
                }
            }
        } );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == 3) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

        if (requestCode == 3) {
            makeMainList ();
        }
    }

    private void makeMainList() {

        Integer coursesLength = sharedPref.getInt ("CoursesLength", 0);
        coursesSelectedNames = new String[coursesLength];
        for (Integer i = 0; i < coursesLength; i++) {
            coursesSelectedNames[i] = getSharedPreferences("myPref", Context.MODE_PRIVATE).getString ("CourseName" + i,"");
        }
        Resources res = getResources();
        listItems = new String[1 + coursesSelectedNames.length];
        listItems[0] = "Instrucciones";
        listItemsIcons = new String[1 + coursesSelectedNames.length];
        listItemsIcons[0] = "iconoinformación1.png";
        listItemsDescriptions = new String[1 + coursesSelectedNames.length];
        listItemsDescriptions[0] = "Reglas generales de los cursos";

        for (Integer i = 0; i < coursesSelectedNames.length; i++) {
            listItems[1 + i] = coursesSelectedNames[i];
            listItemsIcons[1 + i] = "Icon1evaluacion.png";
            listItemsDescriptions[1 + i] = "Presiona para realizar este curso";
        }

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
                } else {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString ( "SelectedCourse", String.valueOf (position - 1) );
                    editor.commit();
                    Intent showCoursesActivity = new Intent( getApplicationContext(), CoursesActivity.class );
                    startActivity( showCoursesActivity );
                }
            }
        } );
    }

}
