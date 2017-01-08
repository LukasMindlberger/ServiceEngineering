package jku.serviceengineering.androidblogservice;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.id.list;

public class CreateGroupActivity extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected List<CharSequence> list = new ArrayList<>();

    protected String mGroupName;
    protected JSONArray mGroupParticipants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create a group");

        //Retrieve users from server
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://" + ((BlogApplication) getApplicationContext()).getIp() + ":" + ((BlogApplication) getApplicationContext()).getPort() + "/listUsers")
                .post(RequestBody.create(null, new byte[0]))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("FAILURE [retrieve users]: " + e.toString());
                e.printStackTrace();
                Snackbar.make(findViewById(android.R.id.content), "Failed to retrieve user list", Snackbar.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respBody = response.body().string();
                System.out.println(response.code() + ": " + respBody);

                try {
                    JSONArray users = new JSONArray(respBody);
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        list.add(user.getString("EMail"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Button mAddUsersButton = (Button) findViewById(R.id.add_users_button);
        mAddUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUsers(list);
            }
        });

        final Button mAddGroupButton = (Button) findViewById(R.id.create_group_button);
        mAddGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cancel = false;

                //Check if user selected participants
                if (mGroupParticipants == null) {
                    ((EditText) findViewById(R.id.group_name)).setError("Please select group members");
                    cancel = true;
                }
                //Check if user entered a group name
                mGroupName = ((EditText) findViewById(R.id.group_name)).getText().toString();
                if (mGroupName.trim().equals("")) {
                    ((EditText) findViewById(R.id.group_name)).setError("Please enter a group name");
                    cancel = true;
                }

                if (!cancel) {
                    ((EditText) findViewById(R.id.group_name)).setError(null);
                    try {
                        createGroup();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void createGroup() throws JSONException {

        JSONObject groupJSON = new JSONObject();

        groupJSON.put("Name", mGroupName);
        groupJSON.put("Participants", mGroupParticipants);

        //Create new group
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://" + ((BlogApplication) getApplicationContext()).getIp() + ":" + ((BlogApplication) getApplicationContext()).getPort() + "/addGroup")
                .post(RequestBody.create(JSON, groupJSON.toString()))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("FAILURE [add group]: " + e.toString());
                e.printStackTrace();
                Snackbar.make(findViewById(android.R.id.content), "Some error occurred", Snackbar.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.code() + ": " + "GROUP CREATED");

                Snackbar.make(findViewById(android.R.id.content), "GROUP SUCCESSFULLY CREATED", Snackbar.LENGTH_LONG)
                        .show();
            }
        });

    }

    private void addUsers(List<CharSequence> list) {

        final CharSequence[] dialogList = list.toArray(new CharSequence[list.size()]);
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(CreateGroupActivity.this);
        builderDialog.setTitle("Select Item");
        int count = dialogList.length;
        boolean[] is_checked = new boolean[count]; // set is_checked boolean false;
        // Creating multiple selection by using setMutliChoiceItem method
        builderDialog.setMultiChoiceItems(dialogList, is_checked,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton, boolean isChecked) {
                    }
                });

        builderDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mGroupParticipants = new JSONArray();

                        ListView list = ((AlertDialog) dialog).getListView();
                        // make selected item in the comma seprated string
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < list.getCount(); i++) {
                            boolean checked = list.isItemChecked(i);
                            if (checked) {
                                if (stringBuilder.length() > 0) {
                                    stringBuilder.append(";");
                                }
                                stringBuilder.append(list.getItemAtPosition(i));

                                //Add user to JSON array


                                JSONObject user = new JSONObject();
                                try {
                                    user.put("Name", list.getItemAtPosition(i));
                                    user.put("EMail", list.getItemAtPosition(i));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                mGroupParticipants.put(user);
                            }
                        }
                        /*Check string builder is empty or not. If string builder is not empty.
                          It will display on the screen.
                         */
                        if (stringBuilder.toString().trim().equals("")) {
                            //((TextView) findViewById(R.id.add_users_button)).setText("Click here to open Dialog");
                            stringBuilder.setLength(0);
                        } else {
                            ((TextView) findViewById(R.id.add_users_button)).setText(stringBuilder);
                        }
                    }
                });

        builderDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //((TextView) findViewById(R.id.add_users_button)).setText("Add members");
                    }
                });
        AlertDialog alert = builderDialog.create();
        alert.show();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
