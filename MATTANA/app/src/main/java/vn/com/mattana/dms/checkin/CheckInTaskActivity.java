package vn.com.mattana.dms.checkin;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.com.mattana.dms.BaseActivity;
import vn.com.mattana.dms.R;
import vn.com.mattana.dms.order.ProductActivity;
import vn.com.mattana.model.api.ResultInfo;
import vn.com.mattana.util.MRes;

public class CheckInTaskActivity extends BaseActivity {

    @BindView(R.id.txtagency)
    TextView agency;

    @BindView(R.id.txtphone)
    TextView phone;

    @BindView(R.id.txtaddress)
    TextView address;

    @BindView(R.id.txttimein)
    TextView timeIn;

    @BindView(R.id.enotes)
    EditText eNotes;

    String workId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_task);
        ButterKnife.bind(this);

        createToolbar();

        Intent intent = getIntent();

        workId = intent.getStringExtra("workId");

        timeIn.setText("Ghé thăm lúc: "  + intent.getStringExtra("TimeIn"));


        address.setText("Địa chỉ: " + MRes.getInstance().agency.getAddress());

        agency.setText(MRes.getInstance().agency.getStore() + " - " + MRes.getInstance().agency.getCode());

        phone.setText("Điện thoại: " + MRes.getInstance().agency.getPhone());


    }

    public void finishClick(View view) {

        if (TextUtils.isEmpty(eNotes.getText().toString()))
        {
            eNotes.setError("Nhập ghi chú");
        } else {

            //

            commons.showAlertCancel(CheckInTaskActivity.this, "Cảnh báo", "Sau khi hoàn tất thì bạn sẽ không thể vào để thao tác tiếp với đại lý được nữa", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    makeCheckOut();
                }
            });

        }

    }

    private void makeCheckOut() {
        showpDialog();

        Call<ResultInfo> call = apiInterface().checkOut(user, token, workId, eNotes.getText().toString());
        call.enqueue(new Callback<ResultInfo>() {
            @Override
            public void onResponse(Call<ResultInfo> call, Response<ResultInfo> response) {

                if(response.body() != null) {
                    if (response.body().getId().equals("1"))
                    {
                        commons.showAlertInfo(CheckInTaskActivity.this, "Thông báo", "Bạn đã hoàn thành ghé thăm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent returnIntent = getIntent();
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            }
                        });

                    }else {
                        commons.makeToast(CheckInTaskActivity.this, response.body().getMsg()).show();
                    }
                }

                hidepDialog();
            }

            @Override
            public void onFailure(Call<ResultInfo> call, Throwable t) {
                hidepDialog();
                commons.showToastDisconnect(CheckInTaskActivity.this);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.checkin_task_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.create_order:
                commons.startActivity(CheckInTaskActivity.this, ProductActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
