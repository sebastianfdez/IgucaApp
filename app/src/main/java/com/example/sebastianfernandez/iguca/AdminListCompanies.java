package com.example.sebastianfernandez.iguca;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class AdminListCompanies extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DataSnapshot dataSnapshot;
    private StorageReference storageRef;
    private FirebaseStorage storage;

    private TextView downloadedCourses;
    private ListView coursesListView;
    private Button saveButton;

    private ArrayList <HashMap> companies;
    private String[] companyNames;
    private HashMap companySelected;
    private HashMap companySelectedOriginal;

    private ArrayList <String> selectedCoursesIds;
    private boolean[] selectedCoursesCheck;
    private ArrayList selectedCompanyCourses;
    private String[] selectedCompanyCoursesNames;

    private HashMap courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_admin_list_companies );

        mAuth = FirebaseAuth.getInstance ();
        mDatabase = FirebaseDatabase.getInstance ().getReference ();

        storage = FirebaseStorage.getInstance ();

        downloadedCourses = ( TextView ) findViewById ( R.id.courses_list_item_name );

        this.enableSaveButton ();
        this.getCourses ();
    }

    public static boolean cleanDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = cleanDir (new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }

    public void confirmResults() {
        Intent secretLoginIntent = new Intent();
        secretLoginIntent.putExtra("result", selectedCoursesIds.get ( 0 ));
        if (getParent() == null) {
            setResult( Activity.RESULT_OK, secretLoginIntent);
        }
        else {
            getParent().setResult(Activity.RESULT_OK, secretLoginIntent);
        }

        SharedPreferences sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt ( "CoursesLength", selectedCoursesIds.size () );
        for (Integer i = 0; i < selectedCoursesIds.size (); i++) {
            String key = selectedCoursesIds.get ( i );
            editor.putString ( "CourseKey" + String.valueOf ( i ), key );
            editor.putString ( "CourseName" + String.valueOf ( i ), ((HashMap<String, String>)dataSnapshot.child ( "Cursos" ).child ( selectedCoursesIds.get ( i ) ).getValue ()).get ( "name" ));
            ArrayList<HashMap<String, Object>> questions = ((HashMap<String, ArrayList<HashMap<String, Object >>>)dataSnapshot.child ( "Cursos" ).child ( key ).getValue ()).get ( "finalExam" );
            ArrayList<HashMap<String, Object>> newQuestions = new ArrayList<HashMap<String, Object>> ();
            for (Integer j = 0; j < questions.size (); j++) {
                HashMap<String, Object> newQuestion = new HashMap<String, Object>();
                for (String key_ : questions.get ( j ).keySet()) {
                    if (questions.get ( j ).get ( String.valueOf(key_) ).equals ( "" )) {
                        newQuestion.put ( String.valueOf(key_), "-" );
                    } else {
                        newQuestion.put ( String.valueOf(key_), questions.get ( j ).get ( String.valueOf(key_) ) );
                    }
                }
                newQuestions.add ( j, newQuestion );
            }
            editor.putString ( "FinalExam" + key, newQuestions.toString ());
        }
        editor.commit();
        finish();
    }

    public void displayCompanies() {

        selectedCoursesIds = new ArrayList <String> ();

        getCompanyNames ();

        uploadCourseNumber ();

        final ListItemAdaptor itemAdapter = new ListItemAdaptor( this, companyNames );

        coursesListView = (ListView) findViewById( R.id.coursesListView );
        coursesListView.setAdapter( itemAdapter );

        coursesListView.setOnItemClickListener ( new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView <?> parent, View view, final int position, long id) {
                companySelected = companies.get(position);
                if (companySelectedOriginal == null) {
                    companySelectedOriginal = companySelected;
                }
                getCompanyCourses();
                AlertDialog.Builder mBuilder = new AlertDialog.Builder ( AdminListCompanies.this );
                mBuilder.setTitle ( "Seleccionar Cursos de " + companyNames[position] );
                mBuilder.setMultiChoiceItems ( selectedCompanyCoursesNames, selectedCoursesCheck, new DialogInterface.OnMultiChoiceClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int positionCourse, boolean isChecked) {

                    }
                } );
                mBuilder.setPositiveButton ( "SELECCIONAR", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Boolean courseSelected = false;
                        if (companySelected != companySelectedOriginal) {
                            selectedCoursesIds.clear ();
                        }
                        for ( int i = 0; i < selectedCoursesCheck.length; i++ ) {
                            String courseKey = ( String ) selectedCompanyCourses.get ( i );
                            if (selectedCoursesCheck[i]) {
                                if (!selectedCoursesIds.contains ( courseKey )) {
                                    selectedCoursesIds.add ( courseKey );
                                    saveButton.setEnabled ( true );
                                    courseSelected = true;
                                }
                            } else {
                                if (selectedCoursesIds.contains ( courseKey )) {
                                    selectedCoursesIds.remove ( courseKey );
                                    saveButton.setEnabled ( true );
                                }
                            }
                        }
                        companySelectedOriginal = companySelected;
                        getCompanyNames ();
                        if (selectedCoursesIds.size () > 0) {
                            companyNames[position] = companyNames[position] + " ( " + selectedCoursesIds.size () + " )";
                        }
                        for (Integer j = 0; j < companies.size (); j++) {
                            TextView nameTV = ((View) coursesListView.getChildAt ( j )).findViewById ( R.id.main_list_item_name );
                            nameTV.setText ( companyNames[j] );

                        }
                        uploadCourseNumber ();
                    }
                } );
                mBuilder.setNegativeButton ( "CANCELAR", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss ();
                    }
                } );
                AlertDialog mDialog = mBuilder.create ();
                mDialog.show ();
            }
        } );
    }

    public void getCompanyCourses() {
        selectedCompanyCourses = (new ArrayList<String> ((Collection)companySelected.get ( "courses" )));
        final Integer coursesListSize = (selectedCompanyCourses).size ();
        selectedCompanyCoursesNames = new String[coursesListSize];
        selectedCoursesCheck = new boolean[coursesListSize];
        Integer i = 0;
        // Iguca id key
        for (  Integer num = 0; num < selectedCompanyCourses.size (); num++ ) {
            DataSnapshot course = dataSnapshot.child ( "Cursos" ).child ( ( String ) selectedCompanyCourses.get ( num ) );
            selectedCompanyCoursesNames[num] = ( String ) ((HashMap)course.getValue ()).get ( "name" );
            if (selectedCoursesIds.contains (course.getKey ()) && (companySelected.get ( "_id" ) == companySelectedOriginal.get ( "_id" ))) {
                selectedCoursesCheck[num] = true;
            } else {
                selectedCoursesCheck[num] = false;
            }
        }

    }

    public void getCompanyNames() {
        companies = new ArrayList <HashMap> ( ( Collection ) (( HashMap ) dataSnapshot.child ( "Companies" ).getValue ()).values () );
        courses = ((HashMap) dataSnapshot.child ( "Cursos" ).getValue ());

        companyNames = new String[companies.size ()];
        Integer i = 0;
        for ( HashMap company : companies ) {
            companyNames[i] = ( String ) company.get ( "name" );
            i++;
        }
    }

    public void enableSaveButton() {

        saveButton = findViewById ( R.id.saveButton );
        saveButton.setEnabled ( false );
        saveButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                cleanDir ( (getBaseContext ()).getCacheDir () );

                File[] filesDirectories = new File[4];
                filesDirectories[0] = new File(getBaseContext ().getCacheDir (), "Manual");
                filesDirectories[0].mkdir ();
                filesDirectories[1] = new File(getBaseContext ().getCacheDir (), "Ejercicios");
                filesDirectories[1].mkdir ();
                filesDirectories[2] = new File(getBaseContext ().getCacheDir (), "Examen");
                filesDirectories[2].mkdir ();
                filesDirectories[3] = new File(getBaseContext ().getCacheDir (), "Respuestas");
                filesDirectories[3].mkdir ();

                final Integer[] succesExpect = {selectedCoursesIds.size () * 4};
                final Integer[] succesCount = {0};

                new File(getBaseContext ().getCacheDir (), "Manual");
                for ( Integer i = 0; i < selectedCoursesIds.size (); i++ ) {
                    String newCourseKey = ( String  ) dataSnapshot.child ( "Cursos" ).child ( selectedCoursesIds.get ( i ) ).getKey ();

                    StorageReference[] StorageRefs = new StorageReference[4];

                    StorageRefs[0] = (StorageReference) (storage.getReference ().child ( newCourseKey + "/Manual" ));
                    StorageRefs[1] = (StorageReference) (storage.getReference ().child ( newCourseKey + "/Ejercicios" ));
                    StorageRefs[2] = (StorageReference) (storage.getReference ().child ( newCourseKey + "/Examen" ));
                    StorageRefs[3] = (StorageReference) (storage.getReference ().child ( newCourseKey + "/Respuestas" ));

                    for (Integer fileNum = 0; fileNum < 4; fileNum++) {

                        //File localFile = File.createTempFile(newCourseKey, ".pdf", filesDirectories[fileNum]);
                        File localFile = new File(filesDirectories[fileNum], newCourseKey + ".pdf");
                        StorageRefs[fileNum].getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot> () {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // Local temp file has been created
                                succesCount[0]++;
                                if (succesCount[0] == succesExpect[0]) {
                                    confirmResults ();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener () {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Log.d ( "!" , "H");
                            }
                        });
                    }
                }
            }
        } );

    }

    private void getCourses() {
        mDatabase.addListenerForSingleValueEvent (
                new ValueEventListener () {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot_) {
                        dataSnapshot = dataSnapshot_;
                        displayCompanies ();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w ( "AdminListCourses", "Failed to read value.", databaseError.toException () );
                    }
                }
        );
    }

    private void uploadCourseNumber() {
        downloadedCourses.setText ( "Cursos descargados ( " + selectedCoursesIds.size () + " )" );
    }
}
