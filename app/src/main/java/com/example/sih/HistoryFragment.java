package com.example.sih;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private RecyclerView recycler;
    private FloatingActionButton syncBtn;
    private DatabaseHelper dbHelper;
    private HistoryAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        recycler = v.findViewById(R.id.historyRecycler);
        syncBtn = v.findViewById(R.id.syncBtn);
        dbHelper = new DatabaseHelper(requireContext());

        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new HistoryAdapter(new ArrayList<>());
        recycler.setAdapter(adapter);

        dbHelper.getLocalEntries(entries -> adapter.setItems(entries));

        syncBtn.setOnClickListener(view -> dbHelper.syncAllUnsynced(() -> dbHelper.getLocalEntries(entries -> adapter.setItems(entries))));
        return v;
    }

    // Inner adapter
    static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.VH> {
        private List<DatabaseHelper.Entry> items;
        HistoryAdapter(List<DatabaseHelper.Entry> list) { items = list; }
        void setItems(List<DatabaseHelper.Entry> list) { items = list; notifyDataSetChanged(); }

        static class VH extends RecyclerView.ViewHolder {
            android.widget.TextView title, subtitle;
            android.widget.ImageView status, thumb;
            VH(View v) {
                super(v);
                title = v.findViewById(R.id.itemTitle);
                subtitle = v.findViewById(R.id.itemSubtitle);
                status = v.findViewById(R.id.itemStatus);
                thumb = v.findViewById(R.id.itemThumb);
            }
        }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
            return new VH(v);
        }
        @Override public void onBindViewHolder(@NonNull VH holder, int position) {
            DatabaseHelper.Entry e = items.get(position);
            holder.title.setText(e.breed);
            holder.subtitle.setText(e.timestamp);
            holder.status.setImageResource(e.synced ? R.drawable.ic_synced : R.drawable.ic_unsynced);
        }
        @Override public int getItemCount() { return items.size(); }
    }
}
