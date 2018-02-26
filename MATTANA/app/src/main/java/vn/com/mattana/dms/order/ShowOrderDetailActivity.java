package vn.com.mattana.dms.order;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.com.mattana.adapter.ShowOrderProductAdapter;
import vn.com.mattana.dms.BaseActivity;
import vn.com.mattana.dms.R;
import vn.com.mattana.model.api.order.ShowOrderInfo;
import vn.com.mattana.model.api.order.ShowOrderProductInfo;
import vn.com.mattana.util.MRes;
import vn.com.mattana.view.DividerItemDecoration;
import vn.com.mattana.view.NonScrollListView;

public class ShowOrderDetailActivity extends BaseActivity {

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

    @BindView(R.id.list)
    NonScrollListView list;

    List<ShowOrderProductInfo> orderProductInfos;

    ShowOrderProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_order_detail);
        ButterKnife.bind(this);
        createToolbar();
        ShowOrderInfo info = MRes.getInstance().orderInfo;
        getSupportActionBar().setTitle("Đơn hàng: " + info.getCode());

        store.setText(info.getStore());
        phone.setText(info.getPhone());
        address.setText(info.getAddress());
        money.setText(info.getOrderPrice());
        time.setText(info.getCreateTime());
        status.setText(info.getStatus());

        orderProductInfos = new ArrayList<>();

        adapter = new ShowOrderProductAdapter(orderProductInfos, this);


        list.setAdapter(adapter);

        makeData();
    }

    private void makeData() {
        showpDialog();
        orderProductInfos.clear();
        Call<List<ShowOrderProductInfo>> call = apiInterface().showOrderProducts(MRes.getInstance().orderInfo.getOrderId());

        call.enqueue(new Callback<List<ShowOrderProductInfo>>() {
            @Override
            public void onResponse(Call<List<ShowOrderProductInfo>> call, Response<List<ShowOrderProductInfo>> response) {

                if (response.body() != null) {
                    orderProductInfos.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
                hidepDialog();
            }

            @Override
            public void onFailure(Call<List<ShowOrderProductInfo>> call, Throwable t) {
                hidepDialog();
                commons.showToastDisconnect(ShowOrderDetailActivity.this);
            }
        });
    }
}
