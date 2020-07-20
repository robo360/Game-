package com.example.game.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.game.R;
import com.example.game.utils.AddressUtil;
import com.example.game.models.Attendance;
import com.example.game.models.Community;
import com.example.game.models.Event;
import com.example.game.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.Objects;

public class EventDetailFragment extends Fragment {
    public static final String KEY_EVENT = "event";
    public static final String KEY_COMMUNITY = "community";

    public static EventDetailFragment newInstance(Event event, Community community) {
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_EVENT, Parcels.wrap(event));
        args.putParcelable(KEY_COMMUNITY, Parcels.wrap(community));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        Event event = Parcels.unwrap(Objects.requireNonNull(args).getParcelable(KEY_EVENT));
        Community community = Parcels.unwrap(args.getParcelable(KEY_COMMUNITY));
        ImageButton btnGo = view.findViewById(R.id.btnGo);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvDate = view.findViewById(R.id.tvDate);
        TextView tvOrganizer = view.findViewById(R.id.tvOrganizer);
        ImageView ivImage = view.findViewById(R.id.ivImage);
        TextView tvCommunity = view.findViewById(R.id.tvCommunity);
        TextView tvDetail = view.findViewById(R.id.tvDetail);
        TextView tvAddress = view.findViewById(R.id.tvAddress);

        tvCommunity.setText(String.format("@%s", Objects.requireNonNull(community).getName()));
        try {
            tvOrganizer.setText(Objects.requireNonNull(Objects.requireNonNull(event).getUser().fetchIfNeeded().get(User.KEY_NAME)).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvDate.setText(event.getDate().toString());
        tvTitle.setText(event.getTitle());
        ParseFile image = event.getImage();
        double latitude = event.getAddress().getLatitude();
        double longitude = event.getAddress().getLongitude();
        Log.i("EventAdapter", "Longitude" + longitude + "latitude" + latitude);
        tvAddress.setText(AddressUtil.getCompleteAddressString(getContext(), latitude, longitude));
        if (image != null) {
            Glide.with(Objects.requireNonNull(getContext())).load(event.getImage().getUrl()).into(ivImage);
        }
        tvDetail.setText(community.getDescription());

        //add a listener on btnGo
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: change the button into a Going TextView
                Attendance attendance = new Attendance();
                attendance.setUser(ParseUser.getCurrentUser());
                attendance.setEvent(event);
                attendance.setAttendStatus(true);
                attendance.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(getContext(), "Going", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_detail, container, false);
    }
}