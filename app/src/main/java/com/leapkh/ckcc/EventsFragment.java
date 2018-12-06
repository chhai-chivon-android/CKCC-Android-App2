package com.leapkh.ckcc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
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

public class EventsFragment extends Fragment implements OnRecyclerViewItemClickListener, TextWatcher {

    private EventsAdapter eventAdapter;
    private EditText etxtSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View home = inflater.inflate(R.layout.fragment_events, container, false);
        return home;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etxtSearch = view.findViewById(R.id.etxt_search);
        etxtSearch.addTextChangedListener(this);

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
        eventAdapter = new EventsAdapter();
        eventAdapter.setOnRecyclerViewItemClickListener(this);
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
                };
                eventAdapter.setEvents(events);
            }
        });

    }

    @Override
    public void onRecyclerViewItemClick(int position) {
        Toast.makeText(getActivity(), "onRecyclerViewItemClick: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecyclerViewOptionItemClick(int position, View optionView) {
        Toast.makeText(getActivity(), "onRecyclerViewItemLongClick: " + position, Toast.LENGTH_SHORT).show();
        // Show option popup menu
        PopupMenu popupMenu = new PopupMenu(getActivity(), optionView);
        popupMenu.inflate(R.menu.menu_event_option);
        popupMenu.show();
    }

    // TextWatcher
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.d("ckcc", "afterTextChanged: " + s.toString());
        String keyword = s.toString();
        eventAdapter.search(keyword);
    }
}















