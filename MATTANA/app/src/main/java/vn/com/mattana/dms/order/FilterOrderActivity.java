package vn.com.mattana.dms.order;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.com.mattana.dms.BaseActivity;
import vn.com.mattana.dms.R;

public class FilterOrderActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {


    @BindView(R.id.fromdate)
    TextView fDate;

    @BindView(R.id.todate)
    TextView tDate;

    @BindView(R.id.status)
    Spinner eStatus;

    DatePickerDialog datePickerDialog;

    int chooseDate = 0;

    String[] arrStt = {"Tất cả","Đã gửi đơn hàng", "Xác nhận đơn hàng", "Chờ thanh toán", "Thanh toán", "Xuất hàng", "Hoàn tất đơn hàng"};

    String[] arrSttCode = {"all","create", "accept", "waitpay", "haspay", "export", "finish"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_order);

        ButterKnife.bind(this);
        createToolbar();

        createDateDialog();

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int showMonth = calendar.get(Calendar.MONTH) + 1;
        int showYear = calendar.get(Calendar.YEAR);

        int days = countDayInMonth(showYear, showMonth);

        fDate.setText("01/" + showMonth + "/" + showYear );

        tDate.setText(days + "/" + showMonth + "/" + showYear);

        fDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDate = 1;
                datePickerDialog.show();
            }
        });

        tDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDate = 2;
                datePickerDialog.show();
            }
        });


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrStt);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        eStatus.setAdapter(dataAdapter);


    }



    private void createDateDialog() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        datePickerDialog = new DatePickerDialog(
                this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

    }

    @Override
    public void onDateSet(DatePicker datePicker,  int year, int month, int day) {
        if (chooseDate == 1) {
            fDate.setText(day + "/" + (month + 1) + "/" + year);
        } else if(chooseDate == 2) {
            tDate.setText(day + "/" + (month + 1) + "/" + year);
        }
    }

    public void filterClick(View view) {
        if (TextUtils.isEmpty(fDate.getText().toString()) || TextUtils.isEmpty(tDate.getText().toString())){
            commons.makeToast(FilterOrderActivity.this, "Chọn đủ thông tin").show();
            return;
        }

        int idx = eStatus.getSelectedItemPosition();

        if(idx != -1 && idx < arrSttCode.length) {
            Intent intent = getIntent();

            intent.putExtra("fDate", fDate.getText().toString());
            intent.putExtra("tDate", tDate.getText().toString());
            intent.putExtra("status", arrSttCode[idx]);
            setResult(Activity.RESULT_OK,intent);
            finish();


        } else {
            commons.makeToast(FilterOrderActivity.this, "Sai").show();
        }
    }
}
