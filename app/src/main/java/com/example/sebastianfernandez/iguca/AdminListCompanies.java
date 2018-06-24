package com.example.sebastianfernandez.iguca;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class AdminListCompanies extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private DataSnapshot dataSnapshot;

    private ArrayList<HashMap> companies;
    private ListView coursesListView;

    public HashMap databaseKeyValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_admin_list_companies );

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        this.getCourses();
    }

    private void getCourses() {
        mDatabase.addListenerForSingleValueEvent(
            new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot_) {
                    dataSnapshot = dataSnapshot_;
                    databaseKeyValues = (HashMap<String,Object>) dataSnapshot.getValue();
                    displayCourses();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("AdminListCourses", "Failed to read value.", databaseError.toException());
                }
            }
        );
    }

    public void displayCourses() {
        companies = new ArrayList<HashMap>((Collection) ((HashMap)dataSnapshot.child("Companies").getValue()).values());

        String[] companyNames = new String[companies.size()];
        Integer i = 0;
        for (HashMap company : companies) {
            companyNames[i] = (String) company.get( "name" );
            i++;
        }

        ListItemAdaptor itemAdapter = new ListItemAdaptor( this, companyNames );

        coursesListView = (ListView) findViewById( R.id.coursesListView );
        coursesListView.setAdapter( itemAdapter );

        coursesListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        } );

    }
}
