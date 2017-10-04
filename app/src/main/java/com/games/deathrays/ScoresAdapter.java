package com.games.deathrays;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by PastoriXx on 21.09.14.
 */
public class ScoresAdapter extends ArrayAdapter<String> {

    private static ArrayList<String> mContacts;

    Context mContext;

    // Конструктор
    public ScoresAdapter(Context context, int textViewResourceId, ArrayList<String> scores) {
        super(context, textViewResourceId, scores);
        this.mContext = context;
        mContacts = scores;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView label = (TextView) convertView;

        if (convertView == null) {
            convertView = new TextView(mContext);
            label = (TextView) convertView;
        }
        label.setText(mContacts.get(position));
        return (convertView);
    }

//    // возвращает содержимое выделенного элемента списка
//    public String GetItem(int position) {
//        return mContacts[position];
//    }
}