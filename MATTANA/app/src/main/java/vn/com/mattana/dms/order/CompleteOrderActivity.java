package vn.com.mattana.dms.order;

import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import vn.com.mattana.dms.BaseActivity;
import vn.com.mattana.dms.R;

public class CompleteOrderActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_order);
        ButterKnife.bind(this);
        createToolbar();
    }


    public void orderClick(View view) {

    }
}
