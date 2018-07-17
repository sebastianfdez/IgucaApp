package com.example.sebastianfernandez.iguca;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;

public class CourseExamMenu extends AppCompatActivity {

    private ListView examListView;
    private String[] examContent;
    private String[] examIcons;
    private String[] examDescriptions;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_course_exam_menu );

        // Set menu title
        TextView titleTV = ( TextView ) findViewById ( R.id.examMenuTV );
        sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        String i = sharedPreferences.getString ("SelectedCourse","");
        String name = sharedPreferences.getString ("CourseName" + i,"");
        final String courseKey = sharedPreferences.getString ( "CourseKey" + i, "" );

        titleTV.setText ( "Examen final " + name );

        // Set options
        examContent = new String[2];
        examContent[0] = "Caso examen final";
        examContent[1] = "Cuestionario final Examen (" + sharedPreferences.getInt (courseKey + "takes",0) + ")";
        examDescriptions = new String[2];
        examDescriptions[0] = "Lectura del caso para rendir el examen";
        examDescriptions[1] = "Preguntas para rendir el examen del curso";
        examIcons = new String[2];
        examIcons[0] = "IconoCaso.png";
        examIcons[1] = "IconoEjercicios.png";
        examListView = findViewById ( R.id.examMenuLV );
        ListItemAdaptor itemAdapter = new ListItemAdaptor(this, examContent, examDescriptions, examIcons);
        examListView.setAdapter(itemAdapter);
        examListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent showCourseExercises = new Intent(getApplicationContext(), CourseExamCase.class);
                    startActivity(showCourseExercises);
                } else if (position == 1) {
                    if (isOnline ()) {
                        if (sharedPreferences.getInt ( courseKey + "takes", 0 ) < 3) {
                            Intent showExerciseSolutions = new Intent ( getApplicationContext (), CourseExamActivity.class );
                            startActivity ( showExerciseSolutions );
                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(findViewById ( R.id.examMenuLayout ), "Puedes rendir el examen maximo 3 veces", Snackbar.LENGTH_LONG);
                            snackbar.getView ().setBackgroundColor ( Color.argb ( 230, 241, 22, 97 ) );
                            TextView tv = snackbar.getView ().findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTextColor(Color.WHITE);

                            snackbar.show();
                        }
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(findViewById ( R.id.examMenuLayout ), "Debes estar conectado a internet para rendir el examen", Snackbar.LENGTH_LONG);
                        snackbar.getView ().setBackgroundColor ( Color.argb ( 230, 241, 22, 97 ) );
                        TextView tv = snackbar.getView ().findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.WHITE);

                        snackbar.show();
                    }
                }
            }
        });
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }
}
