package com.example.sebastianfernandez.iguca;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class CourseExerciseMenu extends AppCompatActivity {

    private ListView exercisesListView;
    private String[] exerciseContent;
    private String[] exerciseIcons;
    private String[] exerciseDescriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_exercise_menu );

        // Set menu title
        TextView titleTV = ( TextView ) findViewById ( R.id.exerciseMenuTV );
        String i = getSharedPreferences("myPref", Context.MODE_PRIVATE).getString ("SelectedCourse","");
        String name = getSharedPreferences ( "myPref", Context.MODE_PRIVATE).getString ("CourseName" + i,"");
        titleTV.setText ( "Ejercicios de " + name );

        // Set options
        exerciseContent = new String[2];
        exerciseContent[0] = "Ejercicios";
        exerciseContent[1] = "Respuestas";
        exerciseDescriptions = new String[2];
        exerciseDescriptions[0] = "Ejercicios de prueba para practicar";
        exerciseDescriptions[1] = "Pauta con soluciones";
        exerciseIcons = new String[2];
        exerciseIcons[0] = "exercise.png";
        exerciseIcons[1] = "solutions.png";
        exercisesListView = findViewById ( R.id.exerciseMenuLV );
        ListItemAdaptor itemAdapter = new ListItemAdaptor(this, exerciseContent, exerciseDescriptions, exerciseIcons);
        exercisesListView.setAdapter(itemAdapter);
        exercisesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent showCourseExercises = new Intent(getApplicationContext(), CourseExercises.class);
                    startActivity(showCourseExercises);
                } else if (position == 1) {
                    Intent showExerciseSolutions = new Intent(getApplicationContext(), CourseExercicesSolutions.class);
                    startActivity(showExerciseSolutions);
                }
            }
        });
    }
}
