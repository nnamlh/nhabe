package vn.com.mattana.dms.order;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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
import vn.com.mattana.adapter.ViewPagerAdapter;
import vn.com.mattana.dms.BaseActivity;
import vn.com.mattana.dms.R;
import vn.com.mattana.dms.checkin.CheckInActivity;
import vn.com.mattana.model.api.ResultInfo;
import vn.com.mattana.model.api.order.ShowOrderInfo;
import vn.com.mattana.model.api.order.ShowOrderProductInfo;
import vn.com.mattana.model.api.order.UpdateStatusResult;
import vn.com.mattana.util.MRes;
import vn.com.mattana.view.CheckInPlanFragment;
import vn.com.mattana.view.CheckOutPlanFragment;
import vn.com.mattana.view.DividerItemDecoration;
import vn.com.mattana.view.NonScrollListView;
import vn.com.mattana.view.OrderDetailFragment;
import vn.com.mattana.view.OrderProductFragment;

public class ShowOrderDetailActivity extends BaseActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    OrderDetailFragment orderDetailView;

    OrderProductFragment orderProductView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_order_detail);

        createToolbar();

        ShowOrderInfo info = MRes.getInstance().orderInfo;
        getSupportActionBar().setTitle("ĐH: " + info.getCode());


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        createTab();

        makeData();
    }
    private void createTab() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        orderProductView = new OrderProductFragment();
        orderProductView.setActivity(this);
        adapter.addFragment(orderProductView, "SẢN PHẨM ĐẶT");


        orderDetailView = new OrderDetailFragment();
        adapter.addFragment(orderDetailView, "CHI TIẾT");

        viewPager.setAdapter(adapter);

        isAdmin = checkAdmin();

    }

    public boolean isAdmin = false;

    private void makeData() {
        showpDialog();

        Call<List<ShowOrderProductInfo>> call = apiInterface().showOrderProducts(MRes.getInstance().orderInfo.getOrderId());

        call.enqueue(new Callback<List<ShowOrderProductInfo>>() {
            @Override
            public void onResponse(Call<List<ShowOrderProductInfo>> call, Response<List<ShowOrderProductInfo>> response) {

                if (response.body() != null) {
                    orderProductView.setData(response.body());
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

    public void updateStatus() {

        showpDialog();
        ShowOrderInfo info = MRes.getInstance().orderInfo;
        Call<UpdateStatusResult> call = apiInterface().updateOrderStatus(user, token, info.getOrderId(), info.getNextStatusCode());

        call.enqueue(new Callback<UpdateStatusResult>() {
            @Override
            public void onResponse(Call<UpdateStatusResult> call, Response<UpdateStatusResult> response) {

                if (response.body() != null) {

                    if (response.body().getId().equals("1")) {
                        MRes.getInstance().orderInfo.setStatus(response.body().getStatus());
                        MRes.getInstance().orderInfo.setStatusCode(response.body().getStatusCode());
                        MRes.getInstance().orderInfo.setNextStatus(response.body().getNextStatus());
                        MRes.getInstance().orderInfo.setNextStatusCode(response.body().getNextStatusCode());

                        orderDetailView.refesh();
                        orderProductView.setButtonStatus();

                        commons.makeToast(ShowOrderDetailActivity.this, "Hoàn thành");

                    } else {
                        commons.makeToast(ShowOrderDetailActivity.this, response.body().getMsg()).show();
                    }
                }
                hidepDialog();
            }

            @Override
            public void onFailure(Call<UpdateStatusResult> call, Throwable t) {
                showpDialog();
                commons.showToastDisconnect(ShowOrderDetailActivity.this);
            }
        });
    }

    public void updateDelivery(final int postion, String productId, final int quantity) {
        showpDialog();

        Call<ResultInfo> call = apiInterface().updateDelivery(MRes.getInstance().orderInfo.getOrderId(), productId, quantity, user, token );

        call.enqueue(new Callback<ResultInfo>() {
            @Override
            public void onResponse(Call<ResultInfo> call, Response<ResultInfo> response) {

                if(response.body() != null) {

                    if(response.body().getId().equals("1")) {
                        orderProductView.updateDelivery(postion, quantity);

                    } else {
                        commons.makeToast(ShowOrderDetailActivity.this, response.body().getMsg()).show();
                    }

                }

                hidepDialog();
            }

            @Override
            public void onFailure(Call<ResultInfo> call, Throwable t) {
                showpDialog();
                commons.showToastDisconnect(ShowOrderDetailActivity.this);
            }
        });
    }
}
