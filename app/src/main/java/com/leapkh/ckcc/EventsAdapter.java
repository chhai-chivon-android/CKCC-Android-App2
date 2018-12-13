package com.leapkh.ckcc;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    private Event[] eventsToShow;

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public void setEvents(Event[] events) {
        eventsToShow = events;
        notifyDataSetChanged();
    }

    public Event[] getEventsToShow() {
        return eventsToShow;
    }

    public void search(String keyword) {
        List<Event> foundEvents = new ArrayList<>();
        for (Event event : Singleton.getInstance().getEvents()) {
            if (event.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                foundEvents.add(event);
            }
        }
        eventsToShow = new Event[foundEvents.size()];
        foundEvents.toArray(eventsToShow);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.viewholder_event, viewGroup, false);
        EventViewHolder eventViewHolder = new EventViewHolder(view);

        return eventViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder eventViewHolder, int i) {
        Event event = eventsToShow[i];
        eventViewHolder.textView.setText(event.getTitle());
        eventViewHolder.simpleDraweeView.setImageURI(event.getImageUrl());
    }

    @Override
    public int getItemCount() {
        if (eventsToShow == null) {
            return 0;
        } else {
            return eventsToShow.length;
        }
    }

    class EventViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView simpleDraweeView;
        TextView textView;
        ImageView imgOption;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            simpleDraweeView = itemView.findViewById(R.id.imgEvent);
            textView = itemView.findViewById(R.id.tvtitle);
            imgOption = itemView.findViewById(R.id.img_option);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecyclerViewItemClickListener.onRecyclerViewItemClick(getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    onRecyclerViewItemClickListener.onRecyclerViewOptionItemClick(getAdapterPosition(), imgOption);
                    return true;
                }
            });
            imgOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecyclerViewItemClickListener.onRecyclerViewOptionItemClick(getAdapterPosition(), imgOption);
                }
            });
        }
    }

}
