package vn.com.mattana.dms.order;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.com.mattana.adapter.ProductAdapter;
import vn.com.mattana.dms.BaseActivity;
import vn.com.mattana.dms.R;
import vn.com.mattana.model.api.order.ProductInfo;
import vn.com.mattana.util.MRes;
import vn.com.mattana.view.DividerItemDecoration;

public class ProductActivity extends BaseActivity {

    private List<ProductInfo> productInfos;

    private List<ProductInfo> productInfosTemp;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.txtcountoder)
    TextView eCountOrder;

    private ProductAdapter mAdapter;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        ButterKnife.bind(this);

        createToolbar();

        resetCountOder();

        MRes.getInstance().clearProductOrder();

        productInfosTemp = new ArrayList<>();
        productInfos = new ArrayList<>();
        mAdapter = new ProductAdapter(productInfos, this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commons.startActivity(ProductActivity.this, CompleteOrderActivity.class);
            }
        });

        makeRequest();
    }

    private void makeRequest() {
        showpDialog();
        Call<List<ProductInfo>> call = apiInterface().products();

        call.enqueue(new Callback<List<ProductInfo>>() {
            @Override
            public void onResponse(Call<List<ProductInfo>> call, Response<List<ProductInfo>> response) {

                if (response.body() != null) {
                    productInfos.addAll(response.body());
                    productInfosTemp.addAll(response.body());

                    mAdapter.notifyDataSetChanged();

                }

                hidepDialog();
            }

            @Override
            public void onFailure(Call<List<ProductInfo>> call, Throwable t) {
                hidepDialog();
                commons.showToastDisconnect(ProductActivity.this);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.product_menu, menu);

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
    private void handleSearch(String query) {

        productInfos.clear();

        for (ProductInfo info : productInfosTemp) {
            if (info.getName().toLowerCase().contains(query.toLowerCase()) || info.getCode().toLowerCase().contains(query.toLowerCase())) {
                productInfos.add(info);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public void notifyAdapterProduct() {
        mAdapter.notifyDataSetChanged();
    }

    public void resetCountOder() {
        eCountOrder.setText("" + MRes.getInstance().getProductOrder().size());
    }


    public void changeQuantity(final int position) {

        final ProductInfo info = MRes.getInstance().getProductOrder().get(position);

        View viewDialog = ProductActivity.this.getLayoutInflater().inflate(R.layout.dialog_change_quantity_order, null);
        final EditText eQantity = (EditText) viewDialog.findViewById(R.id.euantity);
        eQantity.setText("" + info.getQuantityBuy());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thao tác");
        builder.setMessage("Thay đổi số lượng mua");
        builder.setIcon(R.mipmap.ic_logo);
        builder.setView(viewDialog);
        builder.setNegativeButton("Thôi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setPositiveButton("Nhập", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!TextUtils.isEmpty(eQantity.getText().toString())) {
                    try{
                        int quantity  = Integer.parseInt(eQantity.getText().toString());
                        MRes.getInstance().getProductOrder().get(position).setQuantityBuy(quantity);
                        mAdapter.notifyDataSetChanged();
                    }catch (Exception e) {

                    }
                }else {
                    commons.makeToast(ProductActivity.this, "Nhập số lượng").show();
                }

            }
        });

        builder.show();
    }

    @Override
    public void onBackPressed() {
       commons.showAlertCancel(ProductActivity.this, "Cảnh báo", "Không đặt hàng", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               MRes.getInstance().clearProductOrder();
               finish();
           }
       });
    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyAdapterProduct();
        resetCountOder();
    }
}
