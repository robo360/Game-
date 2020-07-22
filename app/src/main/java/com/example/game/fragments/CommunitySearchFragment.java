package com.example.game.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.game.R;
import com.example.game.adapters.CommunitySearchAdapter;
import com.example.game.databinding.FragmentCommunitySearchBinding;
import com.example.game.models.Community;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class CommunitySearchFragment extends Fragment {
    public static final String QUERY = "QUERY";
    private static final String TAG = "CommunitySearchFragment";

    private String query;
    private List<Community> communities;
    private CommunitySearchAdapter adapter;
    private TextView tvMessage;
    private RecyclerView rvCommunitiesSearch;

    public static CommunitySearchFragment newInstance(String query) {
        CommunitySearchFragment fragment = new CommunitySearchFragment();
        Bundle args = new Bundle();
        args.putString(QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            query = args.getString(QUERY);
        }
        FragmentCommunitySearchBinding binding = FragmentCommunitySearchBinding.bind(view);
        tvMessage = binding.tvMessage;
        communities = new ArrayList<>();
        //set the adapter
        adapter = new CommunitySearchAdapter(communities, view.getContext());
        rvCommunitiesSearch = binding.rvCommunitiesSearch;
        rvCommunitiesSearch.setAdapter(adapter);
        rvCommunitiesSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        getCommunities(query);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community_search, container, false);
    }

    public void getCommunities(String query) {
        ParseQuery<Community> q = ParseQuery.getQuery(Community.class);
        if( query.length() != 0 ){
            q.whereContains(Community.KEY_NAME, query);
        }
        q.include(Community.KEY_CREATOR);
        q.findInBackground(new FindCallback<Community>() {
            @Override
            public void done(List<Community> objects, ParseException e) {
                if (objects.size() > 0) {
                    communities.addAll(objects);
                    adapter.notifyDataSetChanged();
                    Log.e(TAG, "Results: " + objects);
                } else {
                    tvMessage.setVisibility(View.VISIBLE);
                    tvMessage.setText(String.format("No communities matches '%s'", query));
                    rvCommunitiesSearch.setVisibility(View.GONE);
                    Log.e(TAG, "Error: " + e);
                }
            }
        });
    }
}
