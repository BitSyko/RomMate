package com.lovejoy777.rommate.bootanimation;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;
import com.lovejoy777.rommate.R;
import com.lovejoy777.rommate.Themes;
import com.lovejoy777.rommate.adapters.CardViewAdapter;
import com.lovejoy777.rommate.adapters.RecyclerItemClickListener;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class FreeBootAnim extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    ArrayList<Themes> themesList;
    private CardViewAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefresh = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen2bootanim);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        themesList = new ArrayList<>();

        new JSONAsyncTask().execute("https://raw.githubusercontent.com/BitSyko/rommate_boots_json/master/bootanims.json");

        mRecyclerView = (RecyclerView) findViewById(R.id.cardList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new CardViewAdapter(themesList, R.layout.adapter_card_layout, this);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(FreeBootAnim.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {


                        String title = themesList.get(position).gettitle();
                        String link = themesList.get(position).getlink();
                        String md5 = themesList.get(position).getmd5();
                        String promo = themesList.get(position).getpromo();
                        String developer = themesList.get(position).getauthor();
                        String video = themesList.get(position).getvideo();
                        String description = themesList.get(position).getdescription();


                        Intent Infoactivity = new Intent(FreeBootAnim.this, DetailBootAnim.class);

                        Infoactivity.putExtra("keytitle", title);
                        Infoactivity.putExtra("keylink", link);
                        Infoactivity.putExtra("keymd5", md5);
                        Infoactivity.putExtra("keypromo", promo);
                        Infoactivity.putExtra("keydescription", description);
                        Infoactivity.putExtra("keyvideo", video);
                        Infoactivity.putExtra("keydeveloper", developer);

                        Bundle bndlanimation =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                        startActivity(Infoactivity, bndlanimation);
                    }
                })
        );
        //initialize swipetorefresh

        mSwipeRefresh.setColorSchemeResources(R.color.accent, R.color.primary);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                themesList.clear();
                new JSONAsyncTask().execute("https://raw.githubusercontent.com/BitSyko/rommate_boots_json/master/bootanims.json");
            }
        });
    }


    class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefresh.setRefreshing(true);
                }
            });
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {


                String data = IOUtils.toString(new URL(urls[0]));

                JSONObject jsono = new JSONObject(data);
                JSONArray jarray = jsono.getJSONArray("Boots");

                Random rnd = new Random();
                for (int i = jarray.length() - 1; i >= 0; i--) {

                    int j = rnd.nextInt(i + 1);

                    // Simple swap
                    JSONObject object = jarray.getJSONObject(j);
                    jarray.put(j, jarray.get(i));
                    jarray.put(i, object);
                    Themes theme = new Themes();

                    theme.settitle(object.getString("title"));
                    theme.setauthor(object.getString("author"));
                    theme.setlink(object.getString("link"));
                    theme.setmd5(object.getString("md5"));
                    theme.seticon(object.getString("icon"));
                    theme.setpromo(object.getString("promo"));
                    theme.setdescription(object.getString("description"));
                    theme.setvideo(object.getString("video"));

                    themesList.add(theme);
                }
                return true;


            } catch (JSONException | IOException e1) {
                e1.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            mSwipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefresh.setRefreshing(false);
                }
            });
            mAdapter.notifyDataSetChanged();

            if (!result) {
                Toast.makeText(FreeBootAnim.this, "Unable to fetch data from server", Toast.LENGTH_LONG).show();
            }

            System.out.println(themesList.size());

        }
    }


}