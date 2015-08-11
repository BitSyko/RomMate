package com.lovejoy777.rommate.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovejoy777.rommate.R;
import com.lovejoy777.rommate.Themes;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Niklas on 26.06.2015.
 */
public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder>{

    private ArrayList<Themes> themes;
    private int rowLayout;
    private Context mContext;

    public CardViewAdapter(ArrayList<Themes> themes, int rowLayout, Context context) {
        this.themes = themes;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Themes theme = themes.get(i);
        viewHolder.themeName.setText(theme.gettitle());
        viewHolder.themeDeveloper.setText(theme.getauthor());
        Picasso.with(mContext).load(theme.geticon()).placeholder(R.mipmap.ic_launcher).into(viewHolder.themeImage);
    }

    @Override
    public int getItemCount() {
        return themes == null ? 0 : themes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView themeName;
        public TextView themeDeveloper;
        public ImageView themeImage;

        public ViewHolder(View itemView) {
            super(itemView);
            themeName = (TextView) itemView.findViewById(R.id.txtName);
            themeImage = (ImageView)itemView.findViewById(R.id.iv_themeImage);
            themeDeveloper = (TextView) itemView.findViewById(R.id.txtSurname);
        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmicon;

        public DownloadImageTask(ImageView bmimage) {
            this.bmicon = bmimage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap micon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                micon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return micon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmicon.setImageBitmap(result);
        }

    }
}
