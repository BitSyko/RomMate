package com.lovejoy777.rommate.bootanimation;

import android.app.ActivityOptions;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lovejoy777.rommate.R;
import com.lovejoy777.rommate.Themes;
import com.lovejoy777.rommate.adapters.CardViewAdapter;
import com.lovejoy777.rommate.adapters.RecyclerItemClickListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by lovejoy777 on 14/07/15.
 */
public class DonateBootAnim extends AppCompatActivity {

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

        themesList = new ArrayList<Themes>();
        new JSONAsyncTask().execute("https://dl.dropboxusercontent.com/u/75065013/rommate/bootanims.json");

        mRecyclerView = (RecyclerView)findViewById(R.id.cardList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new CardViewAdapter(themesList, R.layout.adapter_card_layout, this);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(DonateBootAnim.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {


                        String title = themesList.get(position).gettitle();
                        String link = themesList.get(position).getlink();
                        String promo = themesList.get(position).getpromo();
                        String developer = themesList.get(position).getauthor();
                        String description = themesList.get(position).getdescription();
                        String video = themesList.get(position).getvideo();


                        Intent Infoactivity = new Intent(DonateBootAnim.this, DetailBootAnim.class);

                        Infoactivity.putExtra("keytitle", title);
                        Infoactivity.putExtra("keylink", link);
                        Infoactivity.putExtra("keypromo", promo);
                        Infoactivity.putExtra("keydescription", description);
                        Infoactivity.putExtra("keydeveloper", developer);
                        Infoactivity.putExtra("keyvideo", video);

                        Bundle bndlanimation =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                        startActivity(Infoactivity, bndlanimation);
                    }
                })
        );
        //initialize swipetorefresh

        mSwipeRefresh.setColorSchemeResources(R.color.accent,R.color.primary);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                themesList.clear();
                new JSONAsyncTask().execute("https://dl.dropboxusercontent.com/u/75065013/rommate/bootanims.json");
                onItemsLoadComplete();
            }

            void onItemsLoadComplete(){
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

                //------------------>>
                HttpGet httppost = new HttpGet(urls[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);


                    JSONObject jsono = new JSONObject(data);
                    JSONArray jarray = jsono.getJSONArray("DonateBoots");

                    Random rnd = new Random();
                    for (int i = jarray.length() - 1; i >= 0; i--)
                    {
                        HashMap<String, String> map = new HashMap<String, String>();
                        int j = rnd.nextInt(i + 1);

                        // Simple swap
                        JSONObject object = jarray.getJSONObject(j);
                        jarray.put(j, jarray.get(i));
                        jarray.put(i, object);
                        Themes theme = new Themes();

                        theme.settitle(object.getString("title"));
                        theme.setauthor(object.getString("author"));
                        theme.setlink(object.getString("link"));
                        theme.seticon(object.getString("icon"));
                        theme.setpromo(object.getString("promo"));
                        theme.setdescription(object.getString("description"));
                        theme.setvideo(object.getString("video"));

                        themesList.add(theme);
                    }
                    return true;
                }

                //------------------>>

            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
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
            if(result == false)
                Toast.makeText(getApplicationContext(), "Unable to fetch data from server", Toast.LENGTH_LONG).show();
            System.out.println(themesList.size());

        }
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
        if  (id == R.id.action_search) {
            Toast.makeText(getApplicationContext(), "Search Clicked",
                    Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}