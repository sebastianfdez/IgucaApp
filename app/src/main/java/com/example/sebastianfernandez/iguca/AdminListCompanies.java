package com.example.sebastianfernandez.iguca;

import android.content.DialogInterface;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
    private ArrayList <String> selectedCoursesIds;
    private boolean[] selectedCoursesCheck;
    private HashMap companySelected;

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

    public void displayCompanies() {
        companies = new ArrayList <HashMap> ( ( Collection ) (( HashMap ) dataSnapshot.child ( "Companies" ).getValue ()).values () );
        courses = ((HashMap) dataSnapshot.child ( "Cursos" ).getValue ());
        selectedCoursesIds = new ArrayList <String> ();

        final String[] companyNames = new String[companies.size ()];
        Integer i = 0;
        for ( HashMap company : companies ) {
            companyNames[i] = ( String ) company.get ( "name" );
            i++;
        }
        uploadCourseNumber ();

        ListItemAdaptor itemAdapter = new ListItemAdaptor( this, companyNames );

        coursesListView = (ListView) findViewById( R.id.coursesListView );
        coursesListView.setAdapter( itemAdapter );

        coursesListView.setOnItemClickListener ( new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView <?> parent, View view, int position, long id) {
                companySelected = companies.get ( position );
                final Integer coursesListSize = (( HashMap ) companySelected.get ( "courses" )).size ();
                final String[] coursesNames = new String[coursesListSize];
                selectedCoursesCheck = new boolean[coursesListSize];
                Integer i = 0;
                for ( Map.Entry <String, String> courseId : (( HashMap <String, String> ) companySelected.get ( "courses" )).entrySet () ) {
                    coursesNames[i] = courseId.getValue ();
                    if (selectedCoursesIds.contains ( courseId.getKey () )) {
                        selectedCoursesCheck[i] = true;
                    } else {
                        selectedCoursesCheck[i] = false;
                    }
                    i++;
                }
                AlertDialog.Builder mBuilder = new AlertDialog.Builder ( AdminListCompanies.this );
                mBuilder.setTitle ( "Seleccionar Cursos de " + companyNames[position] );
                mBuilder.setMultiChoiceItems ( coursesNames, selectedCoursesCheck, new DialogInterface.OnMultiChoiceClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int positionCourse, boolean isChecked) {

                    }
                } );
                mBuilder.setPositiveButton ( "SELECCIONAR", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for ( int i = 0; i < selectedCoursesCheck.length; i++ ) {
                            String courseId = ( String ) (new ArrayList <> ( ( Collection ) (( HashMap ) companySelected.get ( "courses" )).keySet () )).get ( i );
                            if (selectedCoursesCheck[i]) {
                                if (!selectedCoursesIds.contains ( courseId )) {
                                    selectedCoursesIds.add ( courseId );
                                    saveButton.setEnabled ( true );
                                }
                            } else {
                                if (selectedCoursesIds.contains ( courseId )) {
                                    selectedCoursesIds.remove ( courseId );
                                    saveButton.setEnabled ( true );
                                }
                            }
                            uploadCourseNumber ();
                        }
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

    public void enableSaveButton() {

        saveButton = findViewById ( R.id.saveButton );
        saveButton.setEnabled ( false );
        saveButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                for ( Integer i = 0; i < selectedCoursesIds.size (); i++ ) {
                    HashMap newCourse = ( HashMap ) ((HashMap) dataSnapshot.child ( "Cursos" ).getValue ()).get ( selectedCoursesIds.get ( i ) );
                    StorageReference manualRef = (StorageReference) (storage.getReference ().child ( "" + newCourse.get ( "name" ) + "/Manual" ));
                    try {
                        File localFile = File.createTempFile("" + newCourse.get ( "_id" ) + "Manual", ".pdf");
                        manualRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot> () {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // Local temp file has been created
                                Log.d ( "herma", "agua" );
                            }
                        }).addOnFailureListener(new OnFailureListener () {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Log.d ( "herma", "agua" );
                            }
                        });
                    } catch (IOException e) {
                        Log.d ( "herma", "agua" );
                        e.printStackTrace ();
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
