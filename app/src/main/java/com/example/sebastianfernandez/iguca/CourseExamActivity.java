package com.example.sebastianfernandez.iguca;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

public class CourseExamActivity extends AppCompatActivity {

    DataSnapshot finalExamData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_course_exam );

        SharedPreferences editor = getSharedPreferences("myPref", Context.MODE_PRIVATE);

        String i = editor.getString ("SelectedCourse","");
        String key = editor.getString ("CourseKey" + i,"");
        String finalExamString = editor.getString ("FinalExam" + key,"");
        try {
            JSONObject finalExam = new JSONObject ( finalExamString );
        } catch (JSONException e) {
            e.printStackTrace ();
        }
    }
}

/*
*
[{a=Usando el switch on/off, number=1, b=Poniendole baterias, c=Conectándola y sintonizando la estacion FM de emergiencias , question=Como usar la radio de comunicaciones, g=-, e=-, d=Tirando la radio al agua , f=-, correct=c, h=-}, {a=Gritar "hombre al agua" y no hacer nada , number=2, b=Tirarse al agua a recatar a esa persona, c=Gritar "hombre al agua" y seguir los estándares de seguridad, question=Que hacer si un tripulante de la embarcacion se cae al agua, g=-, e=-, d=usar la bengala de emergencias, f=-, correct=c, h=-}, {a=Usar el extintor, number=3, b=Saltar al agua , c=Usar la radio de emergencias, question=Que hacer en caso de fuego dentro de la embarcación, g=-, e=-, d=Apagar el motor de la embarcacion, f=-, correct=a, h=-}, {a=Dirigirse a babor, number=4, b=Dirigirse a estribor, c=apagar el mtor, question=Como evitar una colición entre 2 embarcaciones, g=-, e=-, d=poner reversa, f=-, correct=b, h=-}]
*
* */
