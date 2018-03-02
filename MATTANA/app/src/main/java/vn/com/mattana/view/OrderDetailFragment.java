package vn.com.mattana.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.com.mattana.dms.R;
import vn.com.mattana.model.api.order.ShowOrderInfo;
import vn.com.mattana.util.MRes;

public class OrderDetailFragment extends Fragment {

    @BindView(R.id.estore)
    EditText store;

    @BindView(R.id.ephone)
    EditText phone;

    @BindView(R.id.eaddress)
    EditText address;


    @BindView(R.id.emoney)
    EditText money;

    @BindView(R.id.edatecreate)
    EditText time;

    @BindView(R.id.estatus)
    EditText status;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_order_detail, container, false);

        ButterKnife.bind(this, view);

        refesh();
        return view;
    }


    public void refesh() {

        ShowOrderInfo info = MRes.getInstance().orderInfo;
        store.setText(info.getStore());
        phone.setText(info.getPhone());
        address.setText(info.getAddress());
        money.setText(info.getOrderPrice());
        time.setText(info.getCreateTime());
        status.setText(info.getStatus());
    }

}
