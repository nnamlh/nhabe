package vn.com.mattana.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.com.mattana.adapter.ShowOrderProductAdapter;
import vn.com.mattana.dms.R;
import vn.com.mattana.dms.order.ShowOrderDetailActivity;
import vn.com.mattana.model.api.order.ShowOrderProductInfo;

public class OrderProductFragment extends Fragment {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    ShowOrderProductAdapter adapter;

    List<ShowOrderProductInfo> orderProductInfos;

    ShowOrderDetailActivity activity;

    public void setActivity(ShowOrderDetailActivity activity) {
        this.activity = activity;
    }

    public void setData (List<ShowOrderProductInfo> data) {
        orderProductInfos.clear();
        orderProductInfos.addAll(data);

        adapter.notifyDataSetChanged();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_product, container, false);

        ButterKnife.bind(this, view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        orderProductInfos = new ArrayList<>();

        adapter = new ShowOrderProductAdapter(orderProductInfos, activity);
        recyclerView.setAdapter(adapter);

        return view;
    }


}