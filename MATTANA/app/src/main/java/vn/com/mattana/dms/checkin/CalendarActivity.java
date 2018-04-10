package vn.com.mattana.dms.checkin;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.com.mattana.adapter.CalendarWorkAdapter;
import vn.com.mattana.dms.BaseActivity;
import vn.com.mattana.dms.R;
import vn.com.mattana.model.api.checkin.CWorkResult;
import vn.com.mattana.model.api.checkin.CalendarWorkDay;
import vn.com.mattana.model.api.checkin.CalendarWorkResult;

public class CalendarActivity extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    CalendarWorkAdapter mAdapter;

    @BindView(R.id.eyear)
    EditText eYear;

    @BindView(R.id.eweek)
    EditText eWeek;

    @BindView(R.id.efromdate)
    EditText eFdate;

    @BindView(R.id.etodate)
    EditText eToDate;

    List<CalendarWorkDay> calendarWorkDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calendar);

        ButterKnife.bind(this);

        createToolbar();

        calendarWorkDays = new ArrayList<>();

        mAdapter = new CalendarWorkAdapter(calendarWorkDays);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);

        eYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(eYear.getText().toString()))
                    eYear.setText("0");

                if(TextUtils.isEmpty(eWeek.getText().toString()))
                    eWeek.setText("0");

                try{
                    int week = Integer.parseInt(eWeek.getText().toString());

                    int year = Integer.parseInt(eYear.getText().toString());

                    if (week < 0 || week > 52)
                        eWeek.setText("0");

                    if (year < 0)
                        eYear.setText("0");
                }catch (Exception e) {

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        makeCalendar(0,0);

    }

    private void makeCalendar( final int week, final int year) {
        showpDialog();
        calendarWorkDays.clear();
        Call<CalendarWorkResult> call = apiInterface().calendarWork(user, week, year);

        call.enqueue(new Callback<CalendarWorkResult>() {
            @Override
            public void onResponse(Call<CalendarWorkResult> call, Response<CalendarWorkResult> response) {

                if (response.body() != null) {

                    eYear.setText(response.body().getYear() + "");
                    eWeek.setText(response.body().getWeek() + "");
                    eFdate.setText(response.body().getfDate());
                    eToDate.setText(response.body().gettDate());

                    if ("1".equals(response.body().getId())) {
                        calendarWorkDays.addAll(response.body().getWorks());

                    } else {
                        commons.makeToast(CalendarActivity.this, response.body().getMsg()).show();
                    }

                    mAdapter.notifyDataSetChanged();
                }


                hidepDialog();
            }

            @Override
            public void onFailure(Call<CalendarWorkResult> call, Throwable t) {
                hidepDialog();
                commons.showToastDisconnect(CalendarActivity.this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.calendar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.find_action:
                try{
                    int week = Integer.parseInt(eWeek.getText().toString());

                    int year = Integer.parseInt(eYear.getText().toString());

                    makeCalendar(week, year);
                }catch (Exception e) {

                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
