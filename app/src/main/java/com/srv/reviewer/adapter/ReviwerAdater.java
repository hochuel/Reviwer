package com.srv.reviewer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.srv.reviewer.R;
import com.srv.reviewer.vo.ReViewVO;

import java.util.ArrayList;

public class ReviwerAdater extends BaseAdapter {

    Context context = null;
    LayoutInflater layoutInflater = null;

    ArrayList<ReViewVO> list;

    public ReviwerAdater(Context context, ArrayList<ReViewVO> list){
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_custom, null);
        }

        TextView title = (TextView)convertView.findViewById(R.id.title);
        title.setText(list.get(position).getTitle());

        return convertView;
    }
}
