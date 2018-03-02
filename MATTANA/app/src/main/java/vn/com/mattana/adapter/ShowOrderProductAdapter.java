package vn.com.mattana.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import vn.com.mattana.dms.BaseActivity;
import vn.com.mattana.dms.R;
import vn.com.mattana.dms.order.ShowOrderDetailActivity;
import vn.com.mattana.model.api.order.ShowOrderProductInfo;
import vn.com.mattana.util.MRes;

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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ShowOrderProductInfo productInfo = orderProductInfos.get(position);
        holder.name.setText(productInfo.getName());
        holder.code.setText("Mã SP: " + productInfo.getCode());

        holder.quantity.setText("SL: " + productInfo.getQuantityBuy());
        holder.equantityreal.setText("SL thực: " + productInfo.getQuantityReal());

        holder.total.setText("Tổng tiền: " + productInfo.getPriceTotal());

        holder.lupdate.setVisibility(View.GONE);
        if (activity.isAdmin) {
            holder.lupdate.setVisibility(View.VISIBLE);

            if ("create".equals(MRes.getInstance().orderInfo.getStatusCode()) || "accept".equals(MRes.getInstance().orderInfo.getStatusCode())){
                holder.lupdate.setVisibility(View.VISIBLE);
            } else
                holder.lupdate.setVisibility(View.GONE);


            holder.eQuantity.setText("");

            holder.btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(holder.eQuantity.getText().toString())) {

                        try
                        {
                            int quantity = Integer.parseInt(holder.eQuantity.getText().toString());

                            activity.updateDelivery(position, productInfo.getId(),quantity);

                        }catch (Exception e){

                        }

                    } else
                        Toast.makeText(activity, "Nhập số lượng", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return orderProductInfos.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, code, quantity, total, equantityreal;

        public Button btnSend;
        private EditText eQuantity;

        LinearLayout lupdate;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.ename);
            code = (TextView) view.findViewById(R.id.ecode);
            quantity = (TextView) view.findViewById(R.id.equantity);
            total = (TextView) view.findViewById(R.id.emoney);
            equantityreal = (TextView) view.findViewById(R.id.equantityreal);
            btnSend = (Button) view.findViewById(R.id.btnsend);
            eQuantity = (EditText) view.findViewById(R.id.txtquantity);

            lupdate =(LinearLayout) view.findViewById(R.id.lupdate);
        }
    }


}
