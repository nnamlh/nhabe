package vn.com.mattana.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import vn.com.mattana.dms.R;
import vn.com.mattana.model.api.NoticeResult;

/**
 * Created by HAI on 3/1/2018.
 */

public class NoticeAdapter   extends BaseAdapter {


    List<NoticeResult> noticeResults;

    Activity activity;

    LayoutInflater inflater;

    public NoticeAdapter(List<NoticeResult> noticeResults, Activity activity) {
        this.noticeResults = noticeResults;
        this.activity = activity;
        inflater = activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return noticeResults.size();
    }

    @Override
    public Object getItem(int i) {
        return noticeResults.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if(view == null) {
            view = inflater.inflate(R.layout.notice_item, null);
        }
        TextView txtTile, txtMessenge, txtTime;

        txtTile = (TextView) view.findViewById(R.id.title);
        txtMessenge = (TextView) view.findViewById(R.id.messenge);
        txtTime = (TextView) view.findViewById(R.id.time);

        NoticeResult info = noticeResults.get(position);
        txtTile.setText(info.getTitle());
        txtMessenge.setText(info.getMessage());
        txtTime.setText(info.getTime());

        if(info.getRead() == 1) {
            txtTime.setTextColor(Color.parseColor("#E0E0E0"));
            txtMessenge.setTextColor(Color.parseColor("#E0E0E0"));
            txtTime.setTextColor(Color.parseColor("#E0E0E0"));
        }
        return view;
    }

}
