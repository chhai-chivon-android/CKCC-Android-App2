package com.leapkh.ckcc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

public class EventsFragment extends Fragment {

    private EventAdapter eventAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View home = inflater.inflate(R.layout.fragment_events, container, false);
        return home;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton btnAddEvent = view.findViewById(R.id.btn_add_event);
        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddEventActivity.class);
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        eventAdapter = new EventAdapter();
        recyclerView.setAdapter(eventAdapter);

        //loadEventsFromWebService();
        loadEventsFromFirestore();
    }

    private void loadEventsFromWebService() {
        String url = "http://test.js-cambodia.com/ckcc/events.php";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                Event[] events = gson.fromJson(response, Event[].class);
                eventAdapter.setEvents(events);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(request);
    }

    private void loadEventsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Non-realtime query
        /*db.collection("events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Event[] events = new Event[task.getResult().size()];
                    int index = 0;
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        Event event = documentSnapshot.toObject(Event.class);
                        event.setId(documentSnapshot.getId());
                        events[index] = event;
                        index++;
                    }
                    eventAdapter.setEvents(events);
                } else {
                    Toast.makeText(getActivity(), "Load events fail.", Toast.LENGTH_LONG).show();
                    Log.d("ckcc", "Load events fail: " + task.getException());
                }
            }
        });*/

        // Realtime query
        db.collection("events").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                Event[] events = new Event[queryDocumentSnapshots.size()];
                int index = 0;
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Event event = documentSnapshot.toObject(Event.class);
                    event.setId(documentSnapshot.getId());
                    events[index] = event;
                    index++;
                }
                eventAdapter.setEvents(events);
            }
        });

    }

    class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {
        private Event[] events;

        public Event[] getEvents() {
            return events;
        }

        public void setEvents(Event[] events) {
            this.events = events;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.viewholder_event, viewGroup, false);
            EventViewHolder eventViewHolder = new EventViewHolder(view);

            return eventViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull EventViewHolder eventViewHolder, int i) {
            Event event = events[i];
            eventViewHolder.textView.setText(event.getTitle());
            eventViewHolder.simpleDraweeView.setImageURI(event.getImageUrl());
        }

        @Override
        public int getItemCount() {
            if (events == null) {
                return 0;
            } else {
                return events.length;
            }
        }
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView simpleDraweeView;
        TextView textView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            simpleDraweeView = itemView.findViewById(R.id.imgEvent);
            textView = itemView.findViewById(R.id.tvtitle);


        }
    }
}
