package vn.com.mattana.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import vn.com.mattana.adapter.CWorkAdapter;
import vn.com.mattana.dms.R;
import vn.com.mattana.dms.checkin.CheckInActivity;
import vn.com.mattana.model.api.checkin.CWorkInfo;
import vn.com.mattana.model.data.DAgencyInfo;
import vn.com.mattana.util.MRes;


public class CheckInPlanFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    CWorkAdapter adapter;

    List<CWorkInfo> workInfos;

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private CheckInActivity activity;

    public void refestData(List<CWorkInfo> cWorkInfos) {
        workInfos.clear();
        workInfos.addAll(cWorkInfos);
        adapter.notifyDataSetChanged();
    }

    public void setData(CheckInActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_check_in_plan, container, false);

        ButterKnife.bind(this, view);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        workInfos = new ArrayList<>();
        workInfos.addAll(activity.workInfos);
        adapter = new CWorkAdapter(workInfos);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                CWorkInfo info = workInfos.get(position);
                DAgencyInfo agencyInfo = new DAgencyInfo();
                agencyInfo.setCode(info.getCode());
                agencyInfo.setAddress(info.getAddress());
                agencyInfo.setPhone(info.getPhone());
                agencyInfo.setStore(info.getStore());
                agencyInfo.setLat(info.getLat());
                agencyInfo.setLng(info.getLng());
                MRes.getInstance().agency = agencyInfo;
                activity.makeCheckIn(info.getWorkId());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        refeshList();

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refeshList();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        return view;
    }

    private void refeshList() {

        adapter.notifyDataSetChanged();
    }
}
