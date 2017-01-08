package jku.serviceengineering.androidblogservice;

import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BlogSingleActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private TextView mTitle;
    private TextView mDesc;
    private TextView mAuthor;
    private TextView mDate;
    private TextView mComments;

    private Button mAddCommentBtn;

    private String mUserComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String blog_id = getIntent().getExtras().getString("blog_id");
        String blog_title = getIntent().getExtras().getString("blog_title");
        String blog_desc = getIntent().getExtras().getString("blog_desc");
        String blog_author = getIntent().getExtras().getString("blog_author");
        String blog_group = getIntent().getExtras().getString("blog_group");
        String blog_date = getIntent().getExtras().getString("blog_date");

        getSupportActionBar().setTitle(blog_title);

        mTitle = (TextView) findViewById(R.id.single_post_title);
        mDesc = (TextView) findViewById(R.id.single_post_description);
        mDesc.setMovementMethod(new ScrollingMovementMethod());
        mAuthor = (TextView) findViewById(R.id.single_post_author);
        mDate = (TextView) findViewById(R.id.single_post_date);
        mComments = (TextView) findViewById(R.id.single_post_comments);
        mComments.setMovementMethod(new ScrollingMovementMethod());

        mAddCommentBtn = (Button) findViewById(R.id.add_comment_button);

        mTitle.setText(blog_title);
        mDesc.setText(blog_desc);
        mAuthor.setText(blog_author);
        mDate.setText(blog_date);

        loadComments(blog_id);

        mAddCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment(blog_id);
            }
        });
    }

    private void addComment(final String blog_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please enter your comment");

        // Set up the input
        final EditText input = new EditText(this);

        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUserComment = input.getText().toString();

                Map<String, String> commentData = new HashMap<>();

                commentData.put("BlogID", blog_id);
                commentData.put("Text", mUserComment);
                commentData.put("AuthorMail", ((BlogApplication) getApplicationContext()).getUser());
                commentData.put("AuthorName", ((BlogApplication) getApplicationContext()).getUser());

                JSONObject commentDataJSON = new JSONObject(commentData);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://" + ((BlogApplication) getApplicationContext()).getIp() + ":" + ((BlogApplication) getApplicationContext()).getPort() + "/addComment")
                        .post(RequestBody.create(JSON, commentDataJSON.toString()))
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        Snackbar.make(findViewById(android.R.id.content), "Failed to post comment", Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Snackbar.make(findViewById(android.R.id.content), "Comment posted", Snackbar.LENGTH_LONG).show();
                        loadComments(blog_id);
                    }
                });



            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    //Retrieve comments from server
    private void loadComments(String blog_id) {
        Map<String, String> postID = new HashMap<>();
        postID.put("ID", blog_id);

        JSONObject postIDJSON = new JSONObject(postID);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://" + ((BlogApplication) getApplicationContext()).getIp() + ":" + ((BlogApplication) getApplicationContext()).getPort() + "/listComments")
                .post(RequestBody.create(JSON, postIDJSON.toString()))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Snackbar.make(findViewById(android.R.id.content), "Failed to retrieve comments", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respBody = response.body().string();
                System.out.println(response.code() + ": " + respBody);

                final StringBuilder commentStringBuilder = new StringBuilder();

                try {
                    JSONArray comments = new JSONArray(respBody);

                    for (int i = 0; i < comments.length(); i++) {
                        JSONObject comment = comments.getJSONObject(i);
                        commentStringBuilder.append("\n" + comment.getString("Text") + " - " + comment.getString("AuthorMail"));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (TextUtils.isEmpty(commentStringBuilder.toString().trim())) {
                    commentStringBuilder.append("\n[no comments for this post]");
                }

                //We have to run stuff that is updating the UI on the main thread...
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mComments.setText(commentStringBuilder.toString());
                    }
                });
            }
        });
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
