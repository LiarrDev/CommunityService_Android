package com.liarr.communityservice.View.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.liarr.communityservice.Database.Event;
import com.liarr.communityservice.R;
import com.liarr.communityservice.Util.LogUtil;
import com.liarr.communityservice.View.Activity.EventDetailActivity;

import java.util.List;

public class EventItemAdapter extends RecyclerView.Adapter<EventItemAdapter.ViewHolder> {

    private Context mContext;

    private List<Event> mEventList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        AppCompatImageView eventCategoryImage;
        AppCompatTextView eventName;
        AppCompatTextView city;
        AppCompatTextView county;
        AppCompatTextView eventCoin;
        AppCompatTextView eventTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            eventCategoryImage = itemView.findViewById(R.id.event_category);
            eventName = itemView.findViewById(R.id.event_name);
            city = itemView.findViewById(R.id.city);
            county = itemView.findViewById(R.id.county);
            eventCoin = itemView.findViewById(R.id.event_coin);
            eventTime = itemView.findViewById(R.id.event_time);
        }
    }

    public EventItemAdapter(List<Event> eventList) {
        mEventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LogUtil.e("==Adapter==", "==onCreateViewHolder==");
        if (mContext == null) {
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_event_list_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Event event = mEventList.get(position);
            Intent intent = new Intent(mContext, EventDetailActivity.class);
            intent.putExtra("eid", event.getEid());
            mContext.startActivity(intent);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        LogUtil.e("==Adapter==", "==onBindViewHolder==");
        Event event = mEventList.get(i);
        switch (event.getCategory()) {
            case "医护":
                Glide.with(mContext).load(R.drawable.ic_nursing).into(viewHolder.eventCategoryImage);
                break;
            case "跑腿":
                Glide.with(mContext).load(R.drawable.ic_legwork).into(viewHolder.eventCategoryImage);
                break;
            case "清洁":
                Glide.with(mContext).load(R.drawable.ic_cleaning).into(viewHolder.eventCategoryImage);
                break;
            case "教育":
                Glide.with(mContext).load(R.drawable.ic_education).into(viewHolder.eventCategoryImage);
                break;
            case "餐饮":
                Glide.with(mContext).load(R.drawable.ic_repast).into(viewHolder.eventCategoryImage);
                break;
            case "维修":
                Glide.with(mContext).load(R.drawable.ic_repair).into(viewHolder.eventCategoryImage);
                break;
            default:
        }
        viewHolder.eventName.setText(event.getEventName());
        viewHolder.city.setText(event.getCity());
        viewHolder.county.setText(event.getCounty());
        viewHolder.eventCoin.setText(String.valueOf(event.getCoin()));
        viewHolder.eventTime.setText(event.getEventTime());
    }

    @Override
    public int getItemCount() {
        return mEventList.size();
    }
}