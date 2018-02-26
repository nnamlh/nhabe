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
import vn.com.mattana.model.api.order.ShowOrderProductInfo;

/**
 * Created by HAI on 2/27/2018.
 */

public class ShowOrderProductAdapter extends BaseAdapter {

    List<ShowOrderProductInfo> orderProductInfos;
    LayoutInflater inflater;

    public ShowOrderProductAdapter(List<ShowOrderProductInfo> orderProductInfos, Activity activity) {
        this.orderProductInfos = orderProductInfos;
        inflater = activity.getLayoutInflater();

    }

    @Override
    public int getCount() {
        return orderProductInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return orderProductInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null)
            view = inflater.inflate(R.layout.showorder_product_item, null);
        TextView name, code, quantity, price, total;
        name = (TextView) view.findViewById(R.id.ename);
        code = (TextView) view.findViewById(R.id.ecode);
        price = (TextView) view.findViewById(R.id.eprice);
        quantity = (TextView) view.findViewById(R.id.equantity);
        total = (TextView) view.findViewById(R.id.emoney);

        ShowOrderProductInfo productInfo = orderProductInfos.get(position);
        name.setText(productInfo.getName());
        code.setText("Mã SP: " + productInfo.getCode());

        quantity.setText("SL: " + productInfo.getQuantityBuy());

        price.setText("Giá bán lẻ: " + productInfo.getPrice());

        total.setText("Tổng tiền: " + productInfo.getPriceTotal());
        return view;
    }


}
