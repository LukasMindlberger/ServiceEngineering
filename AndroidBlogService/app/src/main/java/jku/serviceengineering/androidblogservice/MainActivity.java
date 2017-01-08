package jku.serviceengineering.androidblogservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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

public class MainActivity extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private List<Blog> blogList = new ArrayList<>();
    private RecyclerView recyclerView;
    private BlogAdapter bAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Home");

        recyclerView = (RecyclerView) findViewById(R.id.blog_list);

        bAdapter = new BlogAdapter(blogList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(bAdapter);

        prepareBlogData();
    }

    private void prepareBlogData() {

        //Retrieve groups from server
        Map<String, String> userEmail = new HashMap<>();
        userEmail.put("EMail", ((BlogApplication) getApplicationContext()).getUser());

        JSONObject userEmailJSON = new JSONObject(userEmail);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://" + ((BlogApplication) getApplicationContext()).getIp() + ":" + ((BlogApplication) getApplicationContext()).getPort() + "/getBlogEntities")
                .post(RequestBody.create(JSON, userEmailJSON.toString()))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Snackbar.make(findViewById(android.R.id.content), "Failed to retrieve blog posts from server...", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respBody = response.body().string();
                System.out.println(response.code() + ": " + respBody);

                try {
                    JSONArray users = new JSONArray(respBody);
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject post = users.getJSONObject(i);

                        String title = post.getString("Title");
                        String desc = post.getString("Text");
                        String author = post.getString("AuthorMail");
                        String id = String.valueOf(post.getInt("ID"));
                        String date = post.getString("Date").split("T")[0];
                        String group = post.getString("GroupID");

                        Blog blog = new Blog(title,desc,id,author,date,group);
                        blogList.add(blog);
                    }

                    //We have to run stuff that is updating the UI on the main thread...
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

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
