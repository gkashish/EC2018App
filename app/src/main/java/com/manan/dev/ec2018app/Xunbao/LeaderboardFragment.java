package com.manan.dev.ec2018app.Xunbao;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.manan.dev.ec2018app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LeaderboardFragment extends Fragment {

    static LeaderboardAdapter leaderboardAdapter;
    static RecyclerView recyclerView;
    static Context c;
    public LeaderboardFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        c=getActivity();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(c);
        recyclerView.setLayoutManager(mLayoutManager);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("heyt","resume");
    }

    public void setData(){
        RequestQueue queue = Volley.newRequestQueue(c);
        Log.d("HEYT","resumed");
        String url = c.getResources().getString(R.string.xunbao_leaderboard_api);
        Toast.makeText(c, url, Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    List<LeaderboardList> leaderboardList=new ArrayList<>();
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray k = new JSONArray(response);
                            for (int i=0;i<k.length();i++) {
                                JSONObject k1 = k.getJSONObject(i);
                                String k2 = k1.getString("user");
                                String k3 = k1.getString("solved");
                                LeaderboardList k4 = new LeaderboardList(k2, Integer.toString(i + 1), k3);
                                leaderboardList.add(k4);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                            leaderboardAdapter = new LeaderboardAdapter(c, leaderboardList);
                            recyclerView.setAdapter(leaderboardAdapter);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                List<LeaderboardList> leaderboardList=new ArrayList<>();
                Log.d("hey", "Error: " + error);

                    leaderboardAdapter = new LeaderboardAdapter(c, leaderboardList);
                    recyclerView.setAdapter(leaderboardAdapter);
            }
        });
        queue.add(stringRequest);
    }
}
