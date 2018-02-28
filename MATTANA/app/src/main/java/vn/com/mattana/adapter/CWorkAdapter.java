package vn.com.mattana.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import vn.com.mattana.dms.R;
import vn.com.mattana.dms.checkin.CheckInActivity;
import vn.com.mattana.model.api.checkin.CWorkInfo;
import vn.com.mattana.model.data.DAgencyInfo;
import vn.com.mattana.util.MRes;

/**
 * Created by HAI on 2/26/2018.
 */

public class CWorkAdapter extends RecyclerView.Adapter<CWorkAdapter.MyViewHolder> {

    List<CWorkInfo> workInfos;

    CheckInActivity activity;

    public CWorkAdapter(List<CWorkInfo> workInfos, CheckInActivity activity) {
        this.workInfos = workInfos;
        this.activity = activity;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cworks_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final CWorkInfo info = workInfos.get(position);
        holder.name.setText(info.getStore());
        holder.code.setText("(" + info.getCode() + ")");
        holder.phone.setText("Điện thoại: " + info.getPhone());
        holder.address.setText("Địa chỉ: " + info.getAddress());
        holder.status.setText(info.getStatus());

        holder.imgLocation.setVisibility(View.GONE);
        holder.btnUpdate.setVisibility(View.GONE);
        holder.btnCheckIn.setVisibility(View.GONE);
        holder.notice.setVisibility(View.VISIBLE);

        if (info.getPerform() == 1) {
            holder.imgLocation.setImageResource(R.mipmap.ic_finish);
            holder.imgLocation.setVisibility(View.VISIBLE);
            holder.notice.setText("Hoàn thành ghé thăm");
        } else {
            if (info.getLat() == 0 || info.getLng() == 0) {
                holder.btnUpdate.setVisibility(View.VISIBLE);
                holder.notice.setText("Khách hàng chưa có tọa độ");
            } else {

                float distance = activity.distance(info.getLat(), info.getLng());

                if (distance == -1)
                    holder.notice.setText("Thử lại để cập nhật checkin");
                else if (distance > 1000)
                    holder.notice.setText("Bạn đang ở xa khách hàng");
                else{
                    holder.btnCheckIn.setVisibility(View.VISIBLE);
                    holder.notice.setText("Bạn có thể ghé thăm");
                }

            }
        }

        holder.btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DAgencyInfo agencyInfo = new DAgencyInfo();
                agencyInfo.setCode(info.getCode());
                agencyInfo.setAddress(info.getAddress());
                agencyInfo.setPhone(info.getPhone());
                agencyInfo.setStore(info.getStore());
                agencyInfo.setLat(info.getLat());
                agencyInfo.setLng(info.getLng());
                MRes.getInstance().agency = agencyInfo;
                activity.makeCheckIn(info.getWorkId());
            }
        });

        holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.makeUpdateLocation(info.getCode(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return workInfos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, code, phone, address, status, notice;
        public ImageView imgLocation;
        public Button btnCheckIn, btnUpdate;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.txtname);
            code = (TextView) view.findViewById(R.id.txtcode);
            phone = (TextView) view.findViewById(R.id.txtphone);
            address = (TextView) view.findViewById(R.id.txtaddress);
            imgLocation = (ImageView) view.findViewById(R.id.imglocation);
            status = (TextView) view.findViewById(R.id.txtstatus);
            btnCheckIn = (Button) view.findViewById(R.id.btncheckin);
            btnUpdate = (Button) view.findViewById(R.id.btnupdatelocation);
            notice = (TextView) view.findViewById(R.id.enotice);
        }
    }
}
