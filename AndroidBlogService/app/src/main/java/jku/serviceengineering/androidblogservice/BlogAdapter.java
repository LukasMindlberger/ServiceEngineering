package jku.serviceengineering.androidblogservice;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Lukas Mindlberger on 08.01.2017.
 */

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.MyViewHolder> {
    private List<Blog> blogList;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, desc, date, author, group, id;

        public MyViewHolder(View view) {
            super(view);

            view.setOnClickListener(this);

            title = (TextView) view.findViewById(R.id.post_title);
            desc = (TextView) view.findViewById(R.id.post_desc);

            date = (TextView) view.findViewById(R.id.post_date);
            author = (TextView) view.findViewById(R.id.post_author);
            group = (TextView) view.findViewById(R.id.post_group);

            id = (TextView) view.findViewById(R.id.post_id);
        }

        @Override
        public void onClick(View v) {

            Context c = v.getContext();

            Intent i = new Intent();
            i.setClass(c, BlogSingleActivity.class);
            i.putExtra("blog_id",id.getText().toString());
            i.putExtra("blog_title",title.getText().toString());
            i.putExtra("blog_desc",desc.getText().toString());
            i.putExtra("blog_author",author.getText().toString());
            i.putExtra("blog_group",group.getText().toString());
            i.putExtra("blog_date",date.getText().toString());
            c.startActivity(i);
        }
    }


    public BlogAdapter(List<Blog> blogList) {
        this.blogList = blogList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blog_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Blog blog = blogList.get(position);
        holder.title.setText(blog.getTitle());
        holder.desc.setText(blog.getDesc());
        holder.group.setText(blog.getGroup());
        holder.author.setText(blog.getAuthorMail());
        holder.date.setText(blog.getDate());
        holder.id.setText(blog.getId());
    }



    @Override
    public int getItemCount() {
        return blogList.size();
    }
}
