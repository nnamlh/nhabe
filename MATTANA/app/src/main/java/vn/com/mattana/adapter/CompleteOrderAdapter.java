package vn.com.mattana.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import vn.com.mattana.dms.R;
import vn.com.mattana.dms.order.CompleteOrderActivity;
import vn.com.mattana.model.api.order.ProductInfo;
import vn.com.mattana.util.MRes;

/**
 * Created by HAI on 2/26/2018.
 */

public class CompleteOrderAdapter extends RecyclerView.Adapter<CompleteOrderAdapter.MyViewHolder> {

    private List<ProductInfo> productOrders;
    private CompleteOrderActivity activity;


    public CompleteOrderAdapter(CompleteOrderActivity activity) {
        productOrders = MRes.getInstance().getProductOrder();
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.complete_order_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final ProductInfo order = productOrders.get(position);


        holder.name.setText(order.getName());
        holder.code.setText("Mã code: " + order.getCode());
        holder.detail.setText("SL đặt: " + order.getQuantityBuy());
        holder.size.setText("Cở vóc: " + order.getSize());
        double price = order.getPrice() * order.getQuantityBuy();

        holder.price.setText(MRes.getInstance().formatMoneyToText(price));

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MRes.getInstance().removeProductOrderAt(position);
                activity.notifyAdapter();
                activity.resetMoneyAll();
            }
        });

        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.changeQuantity( position);
            }
        });

        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.changeQuantity( position);
            }
        });


    }



    @Override
    public int getItemCount() {
        return productOrders.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, code, detail, price, mainCode, size;
        public ImageView imgDelete, imgEdit;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            code = (TextView) view.findViewById(R.id.code);
            detail = (TextView) view.findViewById(R.id.detail);
            imgDelete = (ImageView) view.findViewById(R.id.imgdelete);
            price = (TextView) view.findViewById(R.id.price);
            imgEdit  = (ImageView) view.findViewById(R.id.imgedit);
            size = (TextView) view.findViewById(R.id.size);
            mainCode = (TextView) view.findViewById(R.id.maincode);
        }
    }

}
