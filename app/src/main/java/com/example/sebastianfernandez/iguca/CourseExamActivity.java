package com.example.sebastianfernandez.iguca;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CourseExamActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DataSnapshot dataSnapshot;
    private SharedPreferences editor;

    private ArrayList <HashMap> examInfo;
    private FinalExamQuestion[] examQuestions;

    TextView questionTV;
    TextView alternativesTV;
    Button nextButton;
    Button lastButton;

    Integer currentQuestion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_course_exam );

        mAuth = FirebaseAuth.getInstance ();
        mDatabase = FirebaseDatabase.getInstance ().getReference ();

        editor = getSharedPreferences ( "myPref", Context.MODE_PRIVATE );

        questionTV = findViewById ( R.id.questionTV );
        alternativesTV = findViewById ( R.id.alternativesTV );


        this.getFirebaseInfo ();

    }

    private void getExamInfo() {

        String i = editor.getString ( "SelectedCourse", "" );
        String key = editor.getString ( "CourseKey" + i, "" );
        examInfo = ( ArrayList <HashMap> ) dataSnapshot.child ( "Cursos" ).child ( key ).child ( "finalExam" ).getValue ();
        examQuestions = new FinalExamQuestion[examInfo.size ()];
        Integer index = 0;
        for ( HashMap examQuestion : examInfo ) {
            FinalExamQuestion newQuestion = new FinalExamQuestion ();
            newQuestion.question = ( String ) examQuestion.get ( "question" );
            newQuestion.a = ( String ) examQuestion.get ( "a" );
            newQuestion.b = ( String ) examQuestion.get ( "b" );
            newQuestion.c = ( String ) examQuestion.get ( "c" );
            newQuestion.d = ( String ) examQuestion.get ( "d" );
            newQuestion.e = ( String ) examQuestion.get ( "e" );
            newQuestion.f = ( String ) examQuestion.get ( "f" );
            newQuestion.g = ( String ) examQuestion.get ( "g" );
            newQuestion.h = ( String ) examQuestion.get ( "h" );
            newQuestion.correct = ( String ) examQuestion.get ( "correct" );
            newQuestion.number = ( int ) ( long ) examQuestion.get ( "number" );
            examQuestions[index] = newQuestion;
            index++;
        }
        final String alternatives = "ABCDEFGH";
        for ( Integer j = 0; j < 8; j++ ) {
            LinearLayout linear = findViewById ( R.id.buttonsListView );
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT );
            params.setMargins ( 0, 15, 0, 15 );
            Button btn = new Button ( this );
            btn.setId ( j );
            btn.setText ( "ALTERNATIVA " + alternatives.charAt ( j ) );
            btn.setBackgroundColor ( Color.rgb ( 255, 255, 255 ) );
            linear.addView ( btn, params );
            btn.setVisibility ( View.GONE );
            btn.setOnClickListener ( new View.OnClickListener () {
                public void onClick(View view) {
                    for ( Integer k = 0; k < 8; k++ ) {
                        Button button = findViewById ( k );
                        button.setBackgroundColor ( Color.rgb ( 255, 255, 255 ) );
                        button.setTextColor ( Color.rgb ( 0, 0, 0 ) );
                    }
                    view.setBackgroundColor ( Color.rgb ( 241, 22, 97 ) );
                    (( Button ) view).setTextColor ( Color.rgb ( 255, 255, 255 ) );
                }
            } );

        }
        nextButton = (findViewById ( R.id.buttonNext ));
        lastButton = (findViewById ( R.id.buttonBack ));
        lastButton.setVisibility ( View.GONE );
        nextButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                lastButton.setVisibility ( View.VISIBLE );
                if (currentQuestion + 1 < examQuestions.length) {
                    currentQuestion += 1;
                    showQuestion ( currentQuestion );
                    if (currentQuestion + 1 == examQuestions.length) {
                        ((Button) v).setText ( "Enivar" );
                    }
                }
            }
        } );
        lastButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                currentQuestion -= 1;
                showQuestion ( currentQuestion );
                if (currentQuestion == 0) {
                    lastButton.setVisibility ( View.GONE );
                }
            }
        } );

        showQuestion ( currentQuestion );
    }

    private void getFirebaseInfo() {
        mDatabase.addListenerForSingleValueEvent (
                new ValueEventListener () {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot_) {
                        dataSnapshot = dataSnapshot_;
                        getExamInfo ();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w ( "Exam", "Failed to read value.", databaseError.toException () );
                    }
                }
        );
    }

    private void showQuestion(Integer number) {
        FinalExamQuestion question = examQuestions[number];
        questionTV.setText ( question.question );
        String alternativesText = "Alternativas:\n\n";
        alternativesText += "A)  " + question.a + "\n\n";
        alternativesText += "B)  " + question.b + "\n\n";
        (( Button ) findViewById ( 0 )).setVisibility ( View.VISIBLE );
        (( Button ) findViewById ( 1 )).setVisibility ( View.VISIBLE );
        if (!question.c.equals ( "" )) {
            alternativesText += "C)  " + question.c + "\n\n";
            (( Button ) findViewById ( 2 )).setVisibility ( View.VISIBLE );
            if (!question.d.equals ( "" )) {
                alternativesText += "D)  " + question.d + "\n\n";
                (( Button ) findViewById ( 3 )).setVisibility ( View.VISIBLE );
                if (!question.e.equals ( "" )) {
                    alternativesText += "E)  " + question.e + "\n\n";
                    (( Button ) findViewById ( 4 )).setVisibility ( View.VISIBLE );
                    if (!question.f.equals ( "" )) {
                        alternativesText += "F)  " + question.f + "\n\n";
                        (( Button ) findViewById ( 5 )).setVisibility ( View.VISIBLE );
                        if (!question.g.equals ( "" )) {
                            alternativesText += "G)  " + question.g + "\n\n";
                            (( Button ) findViewById ( 6 )).setVisibility ( View.VISIBLE );
                            if (!question.h.equals ( "" )) {
                                alternativesText += "H)  " + question.h + "\n\n";
                                (( Button ) findViewById ( 7 )).setVisibility ( View.VISIBLE );
                            }
                        }
                    }
                }
            }
        }
        alternativesText += "\n\nSeleccione la alternativa correcta:";
        alternativesTV.setText ( alternativesText );
    }
}