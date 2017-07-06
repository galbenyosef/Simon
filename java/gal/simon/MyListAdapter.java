package gal.simon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyListAdapter extends BaseAdapter {
    private final ArrayList<String> mData;

    public MyListAdapter(List<String> map) {
        mData = new ArrayList<String>(map);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return  mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO implement you own logic with ID
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.highlight_cell, parent, false);
        } else {
            result = convertView;
        }

        String item = getItem(position);
        String personName = item.split(" ")[0];
        String personScore = item.split(" ")[1];
        // TODO replace findViewById by ViewHolder
        ((TextView) result.findViewById(R.id.nameList)).setText(personName);
        ((TextView) result.findViewById(R.id.scoreList)).setText(personScore);

        return result;
    }
}