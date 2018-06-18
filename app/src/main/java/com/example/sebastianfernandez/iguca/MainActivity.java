package com.example.sebastianfernandez.iguca;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private ListView myListView;
    private String[] listItems;
    private String[] listItemsDescriptions;
    private String[] listItemsIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        // startActivityForResult(loginIntent, 2);

        Resources res = getResources();
        myListView = (ListView) findViewById(R.id.myListView);
        listItems = res.getStringArray(R.array.main_menu_items);
        listItemsIcons = res.getStringArray( R.array.main_menu_icons );
        listItemsDescriptions = res.getStringArray(R.array.main_menu_description);

        // myListView.setAdapter(new ArrayAdapter<String>(this, R.layout.main_list, listItems));
        ListItemAdaptor itemAdapter = new ListItemAdaptor(this, listItems, listItemsDescriptions, listItemsIcons);
        myListView.setAdapter(itemAdapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent showInstructionsActivity = new Intent(getApplicationContext(), InstructionsActivity.class);
                    startActivity(showInstructionsActivity);
                } else if (position == 1) {
                    Intent showCoursesActivity = new Intent(getApplicationContext(), CoursesActivity.class);
                    startActivity(showCoursesActivity);
                }
            }
        });
    }

   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2

        switch(requestCode) {
            case (2) : {
                if (resultCode == Activity.RESULT_OK) {
                    String message = data.getStringExtra("personName");
                    textView = findViewById(R.id.textView);
                    textView.setText(message);
                }
                break;
            }
        }
    }

}
