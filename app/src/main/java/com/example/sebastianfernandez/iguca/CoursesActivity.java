package com.example.sebastianfernandez.iguca;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class CoursesActivity extends AppCompatActivity {

    private ListView coursesListView;
    private String[] courseContent;
    private String[] courseIcons;
    private String[] courseDescriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_courses );

        Resources res = getResources();
        coursesListView = (ListView) findViewById(R.id.coursesListView);
        courseContent = res.getStringArray(R.array.courses_content);
        courseIcons = res.getStringArray( R.array.courses_content_icons);
        courseDescriptions = res.getStringArray(R.array.courses_content_descriptions);

        TextView titleTV = ( TextView ) findViewById ( R.id.courseTitleTV );
        String i = getSharedPreferences("myPref", Context.MODE_PRIVATE).getString ("SelectedCourse","");
        String name = getSharedPreferences ( "myPref", Context.MODE_PRIVATE).getString ("CourseName" + i,"");
        titleTV.setText ( name );

        // myListView.setAdapter(new ArrayAdapter<String>(this, R.layout.main_list, listItems));
        ListItemAdaptor itemAdapter = new ListItemAdaptor(this, courseContent, courseDescriptions, courseIcons);
        coursesListView.setAdapter(itemAdapter);

        coursesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent showCourseManualActivity = new Intent(getApplicationContext(), CourseManualActivity.class);
                    startActivity(showCourseManualActivity);
                } else if (position == 1) {
                    Intent showExerciseMenu = new Intent(getApplicationContext(), CourseExerciseMenu.class);
                    startActivity(showExerciseMenu);
                } else if (position == 2) {
                    Intent showCourseExamActivity = new Intent(getApplicationContext(), CourseExamMenu.class);
                    startActivity(showCourseExamActivity);
                }
            }
        });
    }
}
