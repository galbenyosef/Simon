package gal.simon;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StartActivity extends Activity {

    EditText name;
    Button startButton;
    Spinner levelChoose;
    ListView highlights;
    MyListAdapter highlights_adapter;
    ArrayAdapter<String> levels_adapter;
    List<String> levelFeed;
    int levelChosen;
    List<String> topFive;
    public static SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        startButton = (Button)findViewById(R.id.buttonStart);
        highlights = (ListView)findViewById(R.id.highlights_list);
        name= (EditText)findViewById(R.id.nameText);
        shared = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed = shared.edit();
        ed.putString("test","Under Construction");
        ed.commit();
        topFive = new ArrayList<String>();
        levelFeed = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.levels)));
        levels_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, levelFeed);
        levels_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelChoose = (Spinner)findViewById(R.id.spinner1) ;
        levelChoose.setAdapter(levels_adapter);
        levelChoose.setSelection(0);
        levelChosen=1;

        topFive.add(shared.getString("test",""));

        highlights_adapter = new MyListAdapter(topFive);
        highlights.setAdapter(highlights_adapter);

        levelChoose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        levelChosen=1;
                        break;
                    case 1:
                        levelChosen=5;
                        break;
                    case 2:
                        levelChosen=10;
                        break;
                    case 3:
                        levelChosen=15;
                        break;
                    case 4:
                        levelChosen=20;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this,SimonActivity.class);
                intent.putExtra("level",levelChosen);
                intent.putExtra("name",name.getText());
                startActivity(intent);
            }
        });
    }


}
