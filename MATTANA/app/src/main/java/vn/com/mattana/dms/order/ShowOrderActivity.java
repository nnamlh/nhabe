package vn.com.mattana.dms.order;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.com.mattana.adapter.ShowOrderAdapter;
import vn.com.mattana.adapter.ShowOrderProductAdapter;
import vn.com.mattana.dms.BaseActivity;
import vn.com.mattana.dms.R;
import vn.com.mattana.dms.checkin.CheckInTaskActivity;
import vn.com.mattana.model.api.checkin.CWorkInfo;
import vn.com.mattana.model.api.order.ProductInfo;
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

    List<ShowOrderInfo> showOrderInfosTemp;

    ShowOrderAdapter adapter;

    private static final int FILTER_ORDER = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_order);
        ButterKnife.bind(this);

        createToolbar();

        showOrderInfos = new ArrayList<>();
        showOrderInfosTemp = new ArrayList<>();
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

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int showMonth = calendar.get(Calendar.MONTH) + 1;
        int showYear = calendar.get(Calendar.YEAR);

        int days = countDayInMonth(showYear, showMonth);

        makeDate("01/" + showMonth + "/" + showYear,days + "/" + showMonth + "/" + showYear, "all", "");
    }

    private void makeDate(String fDate, String tDate, String status, String agency) {
        showpDialog();
        showOrderInfos.clear();
        showOrderInfosTemp.clear();
        Call<ShowOrderResult> call = apiInterface().showOrder(user, fDate, tDate, status, agency);

        call.enqueue(new Callback<ShowOrderResult>() {
            @Override
            public void onResponse(Call<ShowOrderResult> call, Response<ShowOrderResult> response) {

                if (response.body() != null) {
                    showOrderInfos.addAll(response.body().getOrders());
                    showOrderInfosTemp.addAll(response.body().getOrders());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case FILTER_ORDER:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        String fDate = data.getStringExtra("fDate");
                        String tDate = data.getStringExtra("tDate");
                        String status = data.getStringExtra("status");
                        String agency = data.getStringExtra("agency");
                        makeDate(fDate, tDate, status, agency);
                    case Activity.RESULT_CANCELED:

                        break;
                }
                break;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_order_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.find_action).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handleSearch(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.filter:
               Intent intent =  commons.createIntent(ShowOrderActivity.this, FilterOrderActivity.class);
                startActivityForResult(intent, FILTER_ORDER);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void handleSearch(String query) {

        showOrderInfos.clear();

        for (ShowOrderInfo info : showOrderInfosTemp) {
            if (info.getCode().toLowerCase().contains(query.toLowerCase())) {
                showOrderInfos.add(info);
            }
        }
        adapter.notifyDataSetChanged();
    }



}
