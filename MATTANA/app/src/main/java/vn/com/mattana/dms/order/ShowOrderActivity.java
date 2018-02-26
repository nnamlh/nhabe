package vn.com.mattana.dms.order;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.com.mattana.adapter.ShowOrderAdapter;
import vn.com.mattana.dms.BaseActivity;
import vn.com.mattana.dms.R;
import vn.com.mattana.model.api.checkin.CWorkInfo;
import vn.com.mattana.model.api.order.ShowOrderInfo;
import vn.com.mattana.model.api.order.ShowOrderResult;
import vn.com.mattana.model.data.DAgencyInfo;
import vn.com.mattana.util.MRes;
import vn.com.mattana.view.DividerItemDecoration;
import vn.com.mattana.view.RecyclerTouchListener;

public class ShowOrderActivity extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    List<ShowOrderInfo> showOrderInfos;

    ShowOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_order);
        ButterKnife.bind(this);

        createToolbar();

        showOrderInfos = new ArrayList<>();
        adapter = new ShowOrderAdapter(showOrderInfos);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                MRes.getInstance().orderInfo = showOrderInfos.get(position);

                commons.startActivity(ShowOrderActivity.this, ShowOrderDetailActivity.class);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        makeDate();
    }

    private void makeDate() {
        showpDialog();
        showOrderInfos.clear();
        Call<ShowOrderResult> call = apiInterface().showOrder(user);

        call.enqueue(new Callback<ShowOrderResult>() {
            @Override
            public void onResponse(Call<ShowOrderResult> call, Response<ShowOrderResult> response) {

                if (response.body() != null) {
                    showOrderInfos.addAll(response.body().getOrders());
                    adapter.notifyDataSetChanged();
                }

                hidepDialog();
            }

            @Override
            public void onFailure(Call<ShowOrderResult> call, Throwable t) {
                hidepDialog();
                commons.showToastDisconnect(ShowOrderActivity.this);
            }
        });
    }





}
