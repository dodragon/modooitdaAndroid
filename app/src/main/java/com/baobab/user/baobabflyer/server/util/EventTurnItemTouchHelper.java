package com.baobab.user.baobabflyer.server.util;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.baobab.user.baobabflyer.server.util.listener.OnItemMovementListener;

public class EventTurnItemTouchHelper extends ItemTouchHelper.Callback {

    private final OnItemMovementListener itemMovementListener;

    public EventTurnItemTouchHelper(OnItemMovementListener itemMovementListener) {
        this.itemMovementListener = itemMovementListener;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        itemMovementListener.onItemMove(viewHolder.getAdapterPosition(), viewHolder1.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }
}
