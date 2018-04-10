package vn.com.mattana.dms.checkin;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.com.mattana.adapter.CWorkAdapter;
import vn.com.mattana.dms.BaseActivity;
import vn.com.mattana.dms.R;
import vn.com.mattana.model.api.ResultInfo;
import vn.com.mattana.model.api.checkin.CWorkInfo;
import vn.com.mattana.model.api.checkin.CWorkResult;
import vn.com.mattana.model.api.checkin.CheckInResult;
import vn.com.mattana.util.MRes;


public class CheckInActivity extends BaseActivity {

    int SHOW_TASK = 1;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    CWorkAdapter adapter;

    List<CWorkInfo> workInfos;

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        ButterKnife.bind(this);

        createToolbar();

        workInfos = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        workInfos = new ArrayList<>();
        workInfos.addAll(workInfos);
        adapter = new CWorkAdapter(workInfos, this);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        makeRequest();

    }

    private void makeRequest() {
        showpDialog();
        workInfos.clear();
        String user = prefsHelper.get(MRes.getInstance().PREF_KEY_USER, null);
        Call<CWorkResult> call = apiInterface().showWorks(user);

        call.enqueue(new Callback<CWorkResult>() {
            @Override
            public void onResponse(Call<CWorkResult> call, Response<CWorkResult> response) {

                if (response.body() != null) {

                    if ("1".equals(response.body().getId())) {
                        workInfos.addAll(response.body().getWorks());
                        adapter.notifyDataSetChanged();
                    } else {
                        commons.makeToast(CheckInActivity.this, response.body().getMsg()).show();
                    }

                }

                hidepDialog();
            }

            @Override
            public void onFailure(Call<CWorkResult> call, Throwable t) {
                hidepDialog();
                commons.showToastDisconnect(CheckInActivity.this);
            }
        });
    }

    public void makeCheckIn(String agency) {
        showpDialog();
        Call<CheckInResult> call = apiInterface().checkIn(user, token, agency);

        call.enqueue(new Callback<CheckInResult>() {
            @Override
            public void onResponse(Call<CheckInResult> call, Response<CheckInResult> response) {

                if (response.body() != null) {
                    if ("1".equals(response.body().getId())) {
                        Intent intent = commons.createIntent(CheckInActivity.this, CheckInTaskActivity.class);
                        intent.putExtra("des", response.body().getDes());
                        intent.putExtra("workId", response.body().getWorkId());
                        intent.putExtra("perform", response.body().getPerform());

                        startActivity(intent);
                    } else {
                        commons.makeToast(CheckInActivity.this, response.body().getMsg()).show();
                    }
                }

                hidepDialog();
            }

            @Override
            public void onFailure(Call<CheckInResult> call, Throwable t) {
                hidepDialog();
                commons.showToastDisconnect(CheckInActivity.this);
            }
        });
    }

    public void makeUpdateLocation(final String agencyCode, final int position) {
        commons.showAlertCancel(CheckInActivity.this, "Cảnh báo", "Bạn phải cập nhật tọa độ tại đại lý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showpDialog();
                final double lat = getLat();
                final double lng = getLng();

                if (lat == 0 || lng == 0) {
                    Toast.makeText(CheckInActivity.this, "Không tìm thấy tọa độ, kiểm tra GPS", Toast.LENGTH_LONG).show();
                    hidepDialog();
                } else {
                    Call<ResultInfo> call = apiInterface().updateLocation(lat, lng, agencyCode);

                    call.enqueue(new Callback<ResultInfo>() {
                        @Override
                        public void onResponse(Call<ResultInfo> call, Response<ResultInfo> response) {

                            if (response.body() != null) {
                                if (response.body().getId().equals("1")) {
                                    workInfos.get(position).setLat(lat);
                                    workInfos.get(position).setLng(lng);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    commons.makeToast(CheckInActivity.this, response.body().getMsg()).show();
                                }
                            }
                            hidepDialog();
                        }

                        @Override
                        public void onFailure(Call<ResultInfo> call, Throwable t) {
                            hidepDialog();
                            commons.showToastDisconnect(CheckInActivity.this);
                        }
                    });
                }


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SHOW_TASK) {
            if (resultCode == RESULT_OK) {
             //   makeRequest();
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    public float distance(double lat, double lng) {
        if (getLat() == 0 || getLng() == 0)
            return -1;

        Location locationA = new Location("point A");

        locationA.setLatitude(lat);
        locationA.setLongitude(lng);

        Location locationB = new Location("point B");

        locationB.setLatitude(getLat());
        locationB.setLongitude(getLng());

        float distance = locationA.distanceTo(locationB);

        return distance;
    }
}
