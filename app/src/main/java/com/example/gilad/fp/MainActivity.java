package com.example.gilad.fp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public enum Types{
        LIST, DOUBLE_STORY, TRIPLE_STORY
    }
    private Types type;

    // In lieu of enum for SharedPrefs, for now?
    static final int list = 0;
    static final int story = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(getString(R.string.filename), MODE_PRIVATE);

        int passType = prefs.getInt(getString(R.string.pass_type), -1);

        switch (passType)
        {
            case list:
                listOnClick(null);
                break;
            case story:
                storyOnClick(null);
                break;
            case -1:
                prefs.edit().putString("char0", "").commit();
        }
    }

    public void listOnClick(View v)
    {
        getSharedPreferences(getString(R.string.filename), MODE_PRIVATE).edit().putInt(getString(R.string.pass_type), list).commit();
        Intent next = new Intent(this, ListActivity.class);
        startActivity(next);
        finish();
    }

    public void storyOnClick(View v)
    {
        getSharedPreferences(getString(R.string.filename), MODE_PRIVATE).edit().putInt(getString(R.string.pass_type), story).commit();
        Intent next = new Intent(this, StoryActivity.class);
        startActivity(next);
        finish();
    }
}
