package jku.serviceengineering.androidblogservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private EditText mPostTitle;
    private EditText mPostDesc;

    private Button mSubmitBtn;

    private Spinner mGroupSpinner;

    private List<String> groupList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPostTitle = (EditText) findViewById(R.id.titleField);
        mPostDesc = (EditText) findViewById(R.id.descField);

        mSubmitBtn = (Button) findViewById(R.id.submitBtn);

        mGroupSpinner = (Spinner) findViewById(R.id.postGroup);

        //Retrieve groups from server
        Map<String, String> userEmail = new HashMap<>();
        userEmail.put("EMail", ((BlogApplication) getApplicationContext()).getUser());

        JSONObject userEmailJSON = new JSONObject(userEmail);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://" + ((BlogApplication) getApplicationContext()).getIp() + ":" + ((BlogApplication) getApplicationContext()).getPort() + "/listGroups")
                .post(RequestBody.create(JSON, userEmailJSON.toString()))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("FAILURE [retrieve groups]: " + e.toString());
                e.printStackTrace();
                Snackbar.make(findViewById(android.R.id.content), "Failed to retrieve group list", Snackbar.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respBody = response.body().string();
                System.out.println(response.code() + ": " + respBody);

                //public = default group
                groupList.add("public");
                try {
                    JSONArray users = new JSONArray(respBody);
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        groupList.add(user.getString("Name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //We have to run stuff that is updating the UI on the main thread...
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Setup spinner to show groups
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(PostActivity.this, android.R.layout.simple_spinner_item, groupList);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mGroupSpinner.setAdapter(dataAdapter);
                    }
                });
            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });


    }

    private void startPosting() {
        ProgressDialog progress = ProgressDialog.show(this, "Uploading", "Uploading Post...", true);

        String title_val = mPostTitle.getText().toString().trim();
        String desc_val = mPostDesc.getText().toString().trim();
        String group_val = mGroupSpinner.getSelectedItem().toString();

        if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val)) {

            Map<String, String> postData = new HashMap<>();
            postData.put("Title", title_val);
            postData.put("Text", desc_val);
            postData.put("AuthorMail", ((BlogApplication) getApplicationContext()).getUser());
            postData.put("AuthorName", ((BlogApplication) getApplicationContext()).getUser());
            postData.put("GroupID", group_val);

            JSONObject postDataJSON = new JSONObject(postData);

            //Upload post to server
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://" + ((BlogApplication) getApplicationContext()).getIp() + ":" + ((BlogApplication) getApplicationContext()).getPort() + "/createBlogEntity")
                    .post(RequestBody.create(JSON, postDataJSON.toString()))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Snackbar.make(findViewById(android.R.id.content), "Failed to upload post", Snackbar.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Snackbar.make(findViewById(android.R.id.content), "Post uploaded", Snackbar.LENGTH_LONG).show();
                }
            });

        }

        progress.dismiss();
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
