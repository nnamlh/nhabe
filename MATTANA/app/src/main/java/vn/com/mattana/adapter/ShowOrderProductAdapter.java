package vn.com.mattana.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import vn.com.mattana.dms.BaseActivity;
import vn.com.mattana.dms.R;
import vn.com.mattana.dms.order.ShowOrderDetailActivity;
import vn.com.mattana.model.api.order.ShowOrderProductInfo;

/**
 * Created by HAI on 2/27/2018.
 */

public class ShowOrderProductAdapter extends RecyclerView.Adapter<ShowOrderProductAdapter.MyViewHolder>{

    List<ShowOrderProductInfo> orderProductInfos;
    ShowOrderDetailActivity activity;

    public ShowOrderProductAdapter(List<ShowOrderProductInfo> orderProductInfos, ShowOrderDetailActivity activity) {
        this.orderProductInfos = orderProductInfos;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.showorder_product_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ShowOrderProductInfo productInfo = orderProductInfos.get(position);
        holder.name.setText(productInfo.getName());
        holder.code.setText("Mã SP: " + productInfo.getCode());

        holder.quantity.setText("SL: " + productInfo.getQuantityBuy());

        holder.total.setText("Tổng tiền: " + productInfo.getPriceTotal());
    }

    @Override
    public int getItemCount() {
        return orderProductInfos.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, code, quantity, total;


        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.ename);
            code = (TextView) view.findViewById(R.id.ecode);
            quantity = (TextView) view.findViewById(R.id.equantity);
            total = (TextView) view.findViewById(R.id.emoney);
        }
    }


}
