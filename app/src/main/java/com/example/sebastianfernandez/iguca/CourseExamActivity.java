package com.example.sebastianfernandez.iguca;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CourseExamActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DataSnapshot dataSnapshot;
    private SharedPreferences sharedPreferences;

    private ArrayList <HashMap> examInfo;
    private FinalExamQuestion[] examQuestions;
    private char[] userAnswers;
    private String[] userAnswersOpen;
    private String courseKey;
    private Integer score;
    private Boolean scoreGiven = false;
    private Boolean alternativesExam = true;

    TextView questionTV;
    TextView alternativesTV;
    Button nextButton;
    Button lastButton;
    LinearLayout alternativeButtons;
    RelativeLayout questionFile;
    Button questionFileButton;
    EditText answerText;

    Integer currentQuestion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_course_exam );

        mAuth = FirebaseAuth.getInstance ();
        mDatabase = FirebaseDatabase.getInstance ().getReference ();

        sharedPreferences = getSharedPreferences ( "myPref", Context.MODE_PRIVATE );

        questionTV = (findViewById ( R.id.questionTV ));
        alternativesTV = (findViewById ( R.id.alternativesTV ));
        nextButton = (findViewById ( R.id.buttonNext ));
        lastButton = (findViewById ( R.id.buttonBack ));
        answerText = (findViewById ( R.id.answer ));
        questionFile = (findViewById ( R.id.questionPDFView ));
        questionFileButton = ( findViewById ( R.id.buttonQuestionPDFView ));
        nextButton.setTextColor ( Color.rgb ( 0, 0, 0 ) );
        nextButton.setBackgroundColor ( Color.rgb ( 196, 196, 196 ) );
        nextButton.setVisibility ( View.INVISIBLE );
        lastButton.setTextColor ( Color.rgb ( 0, 0, 0 ) );
        lastButton.setBackgroundColor ( Color.rgb ( 196, 196, 196 ) );
        lastButton.setVisibility ( View.INVISIBLE );

        this.getFirebaseInfo ();

    }

    private boolean allQuestionReady() {
        Boolean ready = true;
        if (alternativesExam) {
            for ( char answer : userAnswers ) {
                if (answer == 0) {
                    ready = false;
                }
            }
        } else {
            for ( CharSequence answer : userAnswersOpen ) {
                if (answer.length () == 0) {
                    ready = false;
                }
            }
        }
        return ready;
    }

    private void calculateScore() {
        if (alternativesExam) {
            Integer numberCorrect = 0;
            for ( FinalExamQuestion question : examQuestions ) {
                if (("" + userAnswers[question.number - 1]).equalsIgnoreCase ( question.correct )) {
                    numberCorrect++;
                }
            }
            score = ( Integer ) ((numberCorrect * 100) / userAnswers.length);
            SharedPreferences.Editor editor = sharedPreferences.edit ();
            editor.putInt ( courseKey + "score" + sharedPreferences.getInt ( courseKey + "takes", 0 ), score );
            editor.putInt ( courseKey + "takes", sharedPreferences.getInt ( courseKey + "takes", 0 ) + 1 );
            editor.commit ();
        } else {
            score = 0;
        }
    }

    private void createNewReport() {

        String newReportkey = mDatabase.child ( "Reports" ).child ( courseKey ).push ().getKey ();

        String courseName = sharedPreferences.getString ( "CompanyName", "" );

        String i = getSharedPreferences ( "myPref", Context.MODE_PRIVATE ).getString ( "SelectedCourse", "" );
        String keySence = getSharedPreferences ( "myPref", Context.MODE_PRIVATE ).getString ( "CourseKeySence" + i, "" );
        String userName = getSharedPreferences ( "myPref", Context.MODE_PRIVATE ).getString ( "UserName", "" );
        String userRut = getSharedPreferences ( "myPref", Context.MODE_PRIVATE ).getString ( "UserRut", "" );
        String userMail = getSharedPreferences ( "myPref", Context.MODE_PRIVATE ).getString ( "UserMail", "" );

        calculateScore ();

        Integer index = 0;
        Map <String, String> userAnswersReport = new HashMap <> ();
        if (alternativesExam) {
            for ( char answer : userAnswers ) {
                userAnswersReport.put ( index.toString (), String.valueOf ( answer ) );
                index++;
            }
        } else {
            for ( CharSequence answer : userAnswersOpen ) {
                userAnswersReport.put ( index.toString (), String.valueOf ( answer ) );
                index++;
            }
        }

        Report report = new Report ( courseName, keySence, userAnswersReport, userRut, userName, userMail, score, alternativesExam );
        Map <String, Object> postValues = report.toMap ();

        Map <String, Object> childUpdates = new HashMap <> ();
        childUpdates.put ( "/Reports/" + courseKey + "/" + newReportkey, postValues );
        mDatabase.updateChildren ( childUpdates );

        showScore ();
    }

    private void getExamInfo() {

        String i = sharedPreferences.getString ( "SelectedCourse", "" );
        courseKey = sharedPreferences.getString ( "CourseKey" + i, "" );
        alternativesExam = ( Boolean ) dataSnapshot.child ( "Cursos" ).child ( courseKey ).child("alternatives").getValue ();
        if (alternativesExam) {
            examInfo = ( ArrayList <HashMap> ) dataSnapshot.child ( "Cursos" ).child ( courseKey ).child ( "finalExam" ).getValue ();
            examQuestions = new FinalExamQuestion[examInfo.size ()];
            userAnswers = new char[examInfo.size ()];
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
            findViewById ( R.id.answerText ).setVisibility ( View.GONE );
            for ( Integer j = 0; j < 8; j++ ) {
                alternativeButtons = findViewById ( R.id.buttonsListView );
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT );
                params.setMargins ( 0, 15, 0, 15 );
                Button btn = new Button ( this );
                btn.setId ( j );
                btn.setText ( "ALTERNATIVA " + alternatives.charAt ( j ) );
                btn.setBackgroundColor ( Color.rgb ( 196, 196, 196 ) );
                alternativeButtons.addView ( btn, params );
                btn.setVisibility ( View.GONE );
                btn.setOnClickListener ( new View.OnClickListener () {
                    public void onClick(View view) {
                        userAnswers[currentQuestion] = alternatives.charAt ( view.getId () );
                        setColorButtons ();
                    }
                } );

            }

        } else {
            examInfo = ( ArrayList <HashMap> ) dataSnapshot.child ( "Cursos" ).child ( courseKey ).child ( "finalExamOpen" ).getValue ();
            examQuestions = new FinalExamQuestion[examInfo.size ()];
            userAnswers = new char[examInfo.size ()];
            findViewById ( R.id.answerText ).setVisibility ( View.VISIBLE );
            questionFile.setVisibility ( View.VISIBLE );
            userAnswersOpen = new String[examInfo.size ()];
            questionFileButton.setOnClickListener ( new View.OnClickListener () {
                public void onClick(View view) {
                    Intent showExerciseSolutions = new Intent ( getApplicationContext (), QuestionFile.class );
                    showExerciseSolutions.putExtra ( "currentQuestion", currentQuestion.toString () );
                    startActivity( showExerciseSolutions);
                }
            } );
            answerText.addTextChangedListener ( new TextWatcher () {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {

                     userAnswersOpen[currentQuestion] = String.valueOf ( s );
                }
            } );
            Integer index = 0;
            for ( HashMap examQuestion : examInfo ) {
                FinalExamQuestion newQuestion = new FinalExamQuestion ();
                newQuestion.question = ( String ) examQuestion.get ( "question" );
                newQuestion.number = ( int ) ( long ) examQuestion.get ( "number" );
                examQuestions[index] = newQuestion;
                index++;
            }
        }
        lastButton.setVisibility ( View.VISIBLE );
        nextButton.setVisibility ( View.VISIBLE );
        lastButton.setEnabled ( false );
        nextButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                View view = questionTV;
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                if (currentQuestion + 1 < examQuestions.length) {
                    lastButton.setEnabled ( true );
                    currentQuestion += 1;
                    showQuestion ( currentQuestion );
                    if (currentQuestion + 1 == examQuestions.length) {
                        nextButton.setText ( "Enviar" );
                        nextButton.setTextColor ( Color.rgb ( 255, 255, 255 ) );
                        nextButton.setBackgroundColor ( Color.rgb ( 241, 22, 97 ) );
                    }
                } else if (currentQuestion + 1 == examQuestions.length) {
                    if (isOnline ()) {
                        if (scoreGiven) {
                            finish ();
                            finishActivity ( 0 );
                        } else {
                            if (allQuestionReady ()) {
                                createNewReport ();
                                scoreGiven = true;
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make ( findViewById ( R.id.examScrollView ), "Debes responder todas las preguntas", Snackbar.LENGTH_LONG );
                                snackbar.getView ().setBackgroundColor ( Color.argb ( 230, 241, 22, 97 ) );
                                TextView tv = snackbar.getView ().findViewById ( android.support.design.R.id.snackbar_text );
                                tv.setTextColor ( Color.WHITE );
                                snackbar.show ();
                            }
                        }
                    } else {

                        Snackbar snackbar = Snackbar
                                .make ( findViewById ( R.id.examScrollView ), "Debes estar conectado a internet para rendir el examen", Snackbar.LENGTH_LONG );
                        snackbar.getView ().setBackgroundColor ( Color.argb ( 230, 241, 22, 97 ) );
                        TextView tv = snackbar.getView ().findViewById ( android.support.design.R.id.snackbar_text );
                        tv.setTextColor ( Color.WHITE );

                        snackbar.show ();
                    }
                }
            }
        } );
        lastButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                currentQuestion -= 1;
                showQuestion ( currentQuestion );
                nextButton.setText ( "Siguiente" );
                nextButton.setTextColor ( Color.rgb ( 0, 0, 0 ) );
                nextButton.setBackgroundColor ( Color.rgb ( 196, 196, 196 ) );
                if (currentQuestion < 1) {
                    lastButton.setEnabled ( false );
                }
                (( ScrollView ) findViewById ( R.id.examScrollView )).fullScroll ( ScrollView.FOCUS_UP );
            }
        } );

        showQuestion ( currentQuestion );
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime ();
        try {
            Process ipProcess = runtime.exec ( "/system/bin/ping -c 1 8.8.8.8" );
            int exitValue = ipProcess.waitFor ();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace ();
        } catch (InterruptedException e) {
            e.printStackTrace ();
        }

        return false;
    }

    private void getFirebaseInfo() {
        mDatabase.addListenerForSingleValueEvent (
                new ValueEventListener () {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot_) {
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

    private void setColorButtons() {
        if (alternativesExam) {
            alternativesTV.setVisibility ( View.VISIBLE );
            alternativeButtons.setVisibility ( View.VISIBLE );

            for ( Integer k = 0; k < 8; k++ ) {
                Button button = findViewById ( k );
                button.setBackgroundColor ( Color.rgb ( 196, 196, 196 ) );
                button.setTextColor ( Color.rgb ( 0, 0, 0 ) );
            }
            if (userAnswers[currentQuestion] != 0) {
                Integer buttonID = "ABCDEFGH".indexOf ( userAnswers[currentQuestion] );
                Button button = findViewById ( buttonID );
                button.setBackgroundColor ( Color.rgb ( 241, 22, 97 ) );
                button.setTextColor ( Color.rgb ( 255, 255, 255 ) );

            }

        }
    }

    private void showQuestion(Integer number) {
        setColorButtons ();
        FinalExamQuestion question = examQuestions[number];
        if (alternativesExam) {
            for ( Integer i = 3; i < 8; i++ ) {
                (( Button ) findViewById ( i )).setVisibility ( View.GONE );
            }
            String questionText = "PREGUNTA " + (currentQuestion + 1) + "/" + examQuestions.length + "\n" + question.question;
            questionTV.setText ( questionText );
            String alternativesText = "Seleccione la alternativa correcta:\n\n";
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
            alternativesTV.setText ( alternativesText );
        } else {
            String questionText = "PREGUNTA " + (currentQuestion + 1) + "/" + examQuestions.length + "\n\n" + question.question;
            answerText.setText ( userAnswersOpen[currentQuestion] );
            questionTV.setText ( questionText );
        }
    }

    private void showScore() {
        lastButton.setVisibility ( View.INVISIBLE );
        answerText.setVisibility ( View.INVISIBLE );
        if (alternativesExam) {
            for ( Integer i = 0; i < 8; i++ ) {
                (( Button ) findViewById ( i )).setVisibility ( View.GONE );
            }
        }
        nextButton.setText ( "Volver" );
        questionTV.setText ( "Examen terminado" );
        String scoreText = "Puntaje obtenido: " + score + "%\n\n";
        if (sharedPreferences.getInt ( courseKey + "takes", 0 ) > 1) {
            scoreText += "Puntajes anteriores:\n\n";
            for ( Integer i = 0; i < sharedPreferences.getInt ( courseKey + "takes", 0 ); i++ ) {
                scoreText += "Puntaje obtenido " + String.valueOf ( i + 1 ) + ": " + String.valueOf ( sharedPreferences.getInt ( courseKey + "score" + String.valueOf ( i ), 0 ) ) + "%\n";
            }
        }
        scoreText += "\nPuedes rendir el examen máximo 3 veces.\n\n\n";
        alternativesTV.setText ( scoreText );
    }
}

@IgnoreExtraProperties
class Report {

    public String company;
    public String idSence;
    public Map <String, String> questions;
    public String rut;
    public String userName;
    public String userMail;
    public Integer score;
    public Long date;
    public Boolean alternatives;

    public Report() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Report(String company, String idSence, Map <String, String> questions, String rut, String userName,
                  String userMail, Integer score, Boolean alternatives) {
        this.company = company;
        this.idSence = idSence;
        this.questions = questions;
        this.rut = rut;
        this.userName = userName;
        this.userMail = userMail;
        this.score = score;
        this.date = (new Date ()).getTime ();
        this.alternatives = alternatives;
    }

    public Map <String, Object> toMap() {
        HashMap <String, Object> result = new HashMap <> ();
        result.put ( "company", company );
        result.put ( "idSence", idSence );
        result.put ( "questions", questions );
        result.put ( "rut", rut );
        result.put ( "mail", userMail );
        result.put ( "userName", userName );
        result.put ( "score", score );
        result.put ( "date", date );
        result.put ( "alternatives", alternatives );

        return result;
    }


}