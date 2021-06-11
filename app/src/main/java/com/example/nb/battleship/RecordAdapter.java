package com.example.nb.battleship;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class RecordAdapter extends BaseAdapter{

    private List<Record> records;
    private Context context;

    public RecordAdapter(List<Record> records, Context context) {
        this.records = records;
        this.context = context;
    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public Object getItem(int position) {
        return records.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.table_record, viewGroup, false);
        }


        Record record = records.get(position);
        TextView winnerPositionTv = view.findViewById(R.id.winner_position);
        TextView winnerNameTv = view.findViewById(R.id.winner_name);
        TextView winnerScore = view.findViewById(R.id.max_score);
        TextView winnerTimeTv = view.findViewById(R.id.player_game_time_tv);

        winnerPositionTv.setText(record.getPosition() + " ");
        winnerNameTv.setText(record.getName());
        winnerScore.setText(record.getScore() + "");
        winnerTimeTv.setText(record.getTime());

        return view;
    }
}
