package com.manan.dev.ec2018app;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.manan.dev.ec2018app.Adapters.DashboardCategoryScrollerAdapter;
import com.manan.dev.ec2018app.Adapters.DashboardSlideAdapter;
import com.manan.dev.ec2018app.Models.CategoryItemModel;
import com.manan.dev.ec2018app.Models.Coordinators;
import com.manan.dev.ec2018app.Models.EventDetails;
import com.manan.dev.ec2018app.Xunbao.XunbaoActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager;
    private TextView[] dots;
    private DashboardSlideAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private ArrayList<CategoryItemModel> allSampleData = new ArrayList<CategoryItemModel>();
    private ArrayList<EventDetails> allEvents;
    TextView categoriesHeadingTextView;
    private DrawerLayout drawer;
    private NavigationView nav_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navbar_content);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        allEvents = new ArrayList<>();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                ContentActivity.this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        nav_view = (NavigationView) findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
        nav_view.setCheckedItem(R.id.nav_home);


        categoriesHeadingTextView = findViewById(R.id.text_viewcategories);
        viewPager = (ViewPager) findViewById(R.id.slliderview_pager);
        myViewPagerAdapter = new DashboardSlideAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        ImageView img = findViewById(R.id.drawerTogglebtn);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        categoriesHeadingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContentActivity.this , CategoryEventDisplayActivity.class));
            }
        });


        addBottomDots(0);

        addData();
        retreiveEvents();
        RecyclerView categoryRecycleview = (RecyclerView) findViewById(R.id.category_recycler_view);

        categoryRecycleview.setHasFixedSize(true);

        DashboardCategoryScrollerAdapter adapter = new DashboardCategoryScrollerAdapter(ContentActivity.this, allSampleData);

        categoryRecycleview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryRecycleview.setAdapter(adapter);

    }

    private void retreiveEvents() {
        if (true)
        {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = getResources().getString(R.string.get_all_events_api);
            StringRequest stringRequest =new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject object=new JSONObject(response);
                        JSONArray eventArray =object.getJSONArray("data");
                        EventDetails event = new EventDetails();
                        for(int i=0;i<eventArray.length();i++)
                        {
                            JSONObject currEvent = eventArray.getJSONObject(i);
                            event.setmName(currEvent.getString("title"));
                            event.setmFees(currEvent.getLong("fee"));
                            JSONObject timing =currEvent.getJSONObject("timing");
                            event.setmStartTime(timing.getLong("from"));
                            event.setmEndTime(timing.getLong("to"));
                            event.setmClubname(currEvent.getString("clubname"));
                            event.setmCategory(currEvent.getString("category"));
                            event.setmDesc(currEvent.getString("desc"));
                            event.setmVenue(currEvent.getString("venue"));
                            event.setmRules(currEvent.getString("rules"));
                            event.setmPhotoUrl(currEvent.getString("photolink"));
                            JSONArray coordinators =currEvent.getJSONArray("coordinators");
                            event.setmCoordinators(new ArrayList<Coordinators>());
                            for(int j=0;j<coordinators.length();j++)
                            {
                                JSONObject coordinatorsDetail = coordinators.getJSONObject(j);
                                Coordinators coord = new Coordinators();
                                coord.setmCoordId(coordinatorsDetail.getString("_id"));
                                coord.setmCoordPhone(coordinatorsDetail.getLong("phone"));
                                coord.setmCoordName(coordinatorsDetail.getString("name"));
                                event.getmCoordinators().add(coord);
                            }

                            event.setmEventId(currEvent.getString("_id"));
                            Toast.makeText(ContentActivity.this, event.toString(), Toast.LENGTH_SHORT).show();
                            allEvents.add(event);
                        }
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(ContentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            queue.add(stringRequest);
        }

    }

    private void addData() {
        CategoryItemModel item1 = new CategoryItemModel();
        item1.setName("Dance");
        item1.setImage(BitmapFactory.decodeResource(ContentActivity.this.getResources(),
                R.drawable.ek));

        allSampleData.add(item1);

        CategoryItemModel item3 = new CategoryItemModel();
        item3.setName("Photography");
        item3.setImage(BitmapFactory.decodeResource(ContentActivity.this.getResources(),
                R.drawable.jh));
        allSampleData.add(item3);
        CategoryItemModel item4 = new CategoryItemModel();
        item4.setName("Debate");
        item4.setImage(BitmapFactory.decodeResource(ContentActivity.this.getResources(),
                R.drawable.dk));
        allSampleData.add(item4);

        CategoryItemModel item5 = new CategoryItemModel();
        item5.setName("Music");
        item5.setImage(BitmapFactory.decodeResource(ContentActivity.this.getResources(),
                R.drawable.ek));
        allSampleData.add(item5);
        CategoryItemModel item6 = new CategoryItemModel();
        item6.setName("Love");

        item6.setImage(BitmapFactory.decodeResource(ContentActivity.this.getResources(),
                R.drawable.an));
        allSampleData.add(item6);
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[3];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);


        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }


    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START, true);
        }
        if(!drawer.isDrawerOpen(GravityCompat.START)){
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        item.setChecked(false);

        switch (id){
            case R.id.nav_home:
                //handle home case
                break;
            case R.id.nav_profile:
                //TODO
                //check whether guest or logged in user
                //if guest pass intent to reg activity
                //if logged in pass intent to user profile activity
                startActivity(new Intent(ContentActivity.this, ProfileActivity.class));
                break;
            case R.id.nav_tickets:
                //TODO
                //if logged in show users tickets
                //if guest user pass intent to reg activity
                break;
            case R.id.nav_trending:
                //TODO
                //pass intent to activity with tab layout with 2 tabs
                //first for trending among all users using firebase analytics
                //second for trending among facebook friends

                //currently xunbao.. remove it later
                startActivity(new Intent(ContentActivity.this, XunbaoActivity.class));
                break;
            case R.id.nav_about:
                //TODO
                //display about fest and college
                break;
            case R.id.nav_logout:
                SharedPreferences preferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                startActivity(new Intent(getApplicationContext(), UserLoginActivity.class));
                finish();
                break;
            case R.id.nav_sponsors:
                //TODO
                //add sponsors
                break;
            case R.id.nav_share:
                //TODO
                //app share kardo
                break;
            case R.id.nav_bug:
                //TODO
                //report bugs
                break;
            case R.id.nav_dev:
                //TODO
                //show developers
                break;
            case R.id.nav_location:
                startActivity(new Intent(ContentActivity.this, MapsActivity.class));
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
