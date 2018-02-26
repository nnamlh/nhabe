package vn.com.mattana.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import vn.com.mattana.dms.R;
import vn.com.mattana.model.api.checkin.CWorkInfo;

/**
 * Created by HAI on 2/26/2018.
 */

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder>{

    List<CWorkInfo> workInfos;

    public CalendarAdapter(List<CWorkInfo> workInfos) {
        this.workInfos = workInfos;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cworks_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CWorkInfo info = workInfos.get(position);
        holder.name.setText(info.getStore());
        holder.code.setText(info.getCode());
        holder.phone.setText(info.getPhone());
        holder.address.setText(info.getAddress());
        holder.status.setText(info.getStatus());

        if (info.getPerform() == 1) {
            holder.imgLocation.setVisibility(View.VISIBLE);
            holder.imgLocation.setImageResource(R.mipmap.ic_finish);
        } else {
            holder.imgLocation.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return workInfos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, code, phone, address, status;
        public ImageView imgLocation;
        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.txtname);
            code = (TextView) view.findViewById(R.id.txtcode);
            phone = (TextView) view.findViewById(R.id.txtphone);
            address = (TextView) view.findViewById(R.id.txtaddress);
            imgLocation =(ImageView) view.findViewById(R.id.imglocation);
            status = (TextView) view.findViewById(R.id.txtstatus);
        }
    }
}
