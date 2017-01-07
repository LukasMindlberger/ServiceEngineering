package jku.serviceengineering.androidblogservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        System.out.println("Currently logged in: " + ((BlogApplication)getApplicationContext()).getUser());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_create_post:
                startActivity(new Intent(MainActivity.this, PostActivity.class));






                return true;

            case R.id.action_create_group:
                Intent CreateGroupIntent = new Intent(MainActivity.this, CreateGroupActivity.class);
                MainActivity.this.startActivity(CreateGroupIntent);
                return true;

            case R.id.action_sign_out:
                ((BlogApplication)getApplicationContext()).userSignOut();
                Intent LogInIntent = new Intent(MainActivity.this, LoginActivity.class);
                LogInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.this.startActivity(LogInIntent);
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
