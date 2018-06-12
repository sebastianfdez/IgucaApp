package com.example.sebastianfernandez.iguca;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private ListView myListView;
    private String[] listItems;
    private String[] listItemsDescriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_main);

        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(loginIntent, 2);

        Resources res = getResources();
        myListView = (ListView) findViewById(R.id.myListView);
        listItems = res.getStringArray(R.array.main_menu_items);
        listItemsDescriptions = res.getStringArray(R.array.main_menu_description);

        // myListView.setAdapter(new ArrayAdapter<String>(this, R.layout.main_list, listItems));
        ListItemAdaptor itemAdapter = new ListItemAdaptor(this, listItems, listItemsDescriptions);
        myListView.setAdapter(itemAdapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent showInstructionsActivity = new Intent(getApplicationContext(), InstructionsActivity.class);
                    startActivity(showInstructionsActivity);
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
