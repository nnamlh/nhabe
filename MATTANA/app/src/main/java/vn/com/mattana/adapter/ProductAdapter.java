package vn.com.mattana.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import vn.com.mattana.dms.R;
import vn.com.mattana.dms.order.ProductActivity;
import vn.com.mattana.model.api.order.ProductInfo;
import vn.com.mattana.util.MRes;

/**
 * Created by HAI on 2/26/2018.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    ProductActivity activity;
    List<ProductInfo> productInfos;
    int inOder = 0;

    public ProductAdapter(List<ProductInfo> productInfos, ProductActivity activity) {
        this.productInfos = productInfos;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ProductInfo productInfo = productInfos.get(position);
        holder.name.setText(productInfo.getName());
        holder.price.setText("Giá bán lẽ: " + MRes.getInstance().formatMoneyToText(productInfo.getPrice()));
        holder.code.setText("Mã code: " + productInfo.getCode());
        holder.size.setText("Cở vóc: " + productInfo.getSize());
        holder.type.setText("Nhóm: " + productInfo.getType());
        holder.mainCode.setText("Mã hàng: " + productInfo.getMainCode());
        holder.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productInfo.setQuantityBuy(1);
                MRes.getInstance().addProductOrder(productInfo);
                activity.notifyAdapterProduct();
                activity.resetCountOder();
                Toast.makeText(activity, "Đã chọn mua : " + productInfo.getName(), Toast.LENGTH_LONG).show();
            }
        });

        //
        final ProductInfo order = MRes.getInstance().getProductOrder(productInfo.getId());

        if (order != null) {
            final int orderPosition = MRes.getInstance().getProductOrderIndex(productInfo.getId());
            holder.imgDelete.setVisibility(View.VISIBLE);
            holder.btnOrder.setVisibility(View.GONE);
            holder.detail.setVisibility(View.VISIBLE);


            holder.detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.changeQuantity(orderPosition);
                }
            });
            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MRes.getInstance().removeProductOrderAt(orderPosition);

                    activity.notifyAdapterProduct();
                    activity.resetCountOder();
                }
            });
            holder.detail.setText("Số lượng đặt: " + order.getQuantityBuy());
        } else {
            holder.imgDelete.setVisibility(View.GONE);
            holder.btnOrder.setVisibility(View.VISIBLE);
            holder.detail.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return productInfos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, code, price, size,type, mainCode;

        public ImageView imgDelete;
        public Button btnOrder, detail;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            code = (TextView) view.findViewById(R.id.code);
            price = (TextView) view.findViewById(R.id.price);
            btnOrder = (Button) view.findViewById(R.id.btnorder);
            detail = (Button) view.findViewById(R.id.detail);
            imgDelete = (ImageView) view.findViewById(R.id.imgdelete);
            size = (TextView) view.findViewById(R.id.size);
            type = (TextView)view.findViewById(R.id.type);
            mainCode = (TextView) view.findViewById(R.id.maincode);
        }
    }



}
