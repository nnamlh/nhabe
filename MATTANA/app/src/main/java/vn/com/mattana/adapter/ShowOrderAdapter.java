package vn.com.mattana.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import vn.com.mattana.dms.R;
import vn.com.mattana.model.api.order.ShowOrderInfo;

/**
 * Created by HAI on 2/27/2018.
 */

public class ShowOrderAdapter extends RecyclerView.Adapter<ShowOrderAdapter.MyViewHolder>{

    List<ShowOrderInfo> showOrderInfoList;

    public ShowOrderAdapter(List<ShowOrderInfo> showOrderInfoList) {
        this.showOrderInfoList = showOrderInfoList;

}

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.showorder_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
       ShowOrderInfo showOrderInfo = showOrderInfoList.get(position);
       holder.code.setText(showOrderInfo.getCode());
       holder.store.setText("Đại lý: " + showOrderInfo.getStore());
       holder.time.setText("Ngày đặt: " + showOrderInfo.getCreateTime());
       holder.number.setText("Số SP đặt: " + showOrderInfo.getProductNumber());
       holder.price.setText("Tổng tiền: " + showOrderInfo.getOrderPrice());
       holder.status.setText(showOrderInfo.getStatus());
    }

    @Override
    public int getItemCount() {
        return showOrderInfoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView code, price, number, time, store, status;


        public MyViewHolder(View view) {
            super(view);
            store = (TextView) view.findViewById(R.id.txtstore);
            code = (TextView) view.findViewById(R.id.txtcode);
            price = (TextView) view.findViewById(R.id.txtmoney);
            number = (TextView) view.findViewById(R.id.txtnumber);
            time = (TextView) view.findViewById(R.id.txttime);
            status = (TextView)view.findViewById(R.id.txtstatus);
        }
    }


}