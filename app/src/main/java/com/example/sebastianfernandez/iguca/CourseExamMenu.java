package com.example.sebastianfernandez.iguca;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class CourseExamMenu extends AppCompatActivity {

    private ListView examListView;
    private String[] examContent;
    private String[] examIcons;
    private String[] examDescriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_course_exam_menu );

        // Set menu title
        TextView titleTV = ( TextView ) findViewById ( R.id.examMenuTV );
        String i = getSharedPreferences("myPref", Context.MODE_PRIVATE).getString ("SelectedCourse","");
        String name = getSharedPreferences ( "myPref", Context.MODE_PRIVATE).getString ("CourseName" + i,"");
        titleTV.setText ( "Examen final " + name );

        // Set options
        examContent = new String[2];
        examContent[0] = "Caso examen final";
        examContent[1] = "Cuestionario final Examen";
        examDescriptions = new String[2];
        examDescriptions[0] = "Lectura del caso para rendir el examen";
        examDescriptions[1] = "Preguntas para rendir el examen del curso";
        examIcons = new String[2];
        examIcons[0] = "examencase.png";
        examIcons[1] = "examenquiz.png";
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
                    Intent showExerciseSolutions = new Intent(getApplicationContext(), CourseExamActivity.class);
                    startActivity(showExerciseSolutions);
                }
            }
        });
    }
}
