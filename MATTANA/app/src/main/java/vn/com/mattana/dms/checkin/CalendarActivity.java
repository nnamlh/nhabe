package vn.com.mattana.dms.checkin;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.com.mattana.adapter.CWorkAdapter;
import vn.com.mattana.adapter.CalendarAdapter;
import vn.com.mattana.dms.BaseActivity;
import vn.com.mattana.dms.R;
import vn.com.mattana.mdatepicker.DatePickerTimeline;
import vn.com.mattana.mdatepicker.MonthView;
import vn.com.mattana.model.api.checkin.CWorkInfo;
import vn.com.mattana.model.api.checkin.CWorkResult;

public class CalendarActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.timeline)
    DatePickerTimeline timeline;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    CalendarAdapter mAdapter;

    List<CWorkInfo> workInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ButterKnife.bind(this);

        createToolbar();



        workInfos = new ArrayList<>();


        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new CalendarAdapter(workInfos);
        recyclerView.setAdapter(mAdapter);
        createTimeLine();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    private void createTimeLine() {
        timeline.setVisibility(View.VISIBLE);

        Date date = new Date();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int showMonth = calendar.get(Calendar.MONTH) + 1;
        int showYear = calendar.get(Calendar.YEAR);
        int showDay = calendar.get(Calendar.DATE);



        timeline.setDateLabelAdapter(new MonthView.DateLabelAdapter() {
            @Override
            public CharSequence getLabel(Calendar calendar, int index) {
                return Integer.toString(calendar.get(Calendar.MONTH) + 1) + "/" + (calendar.get(Calendar.YEAR) % 2000);
            }
        });


        timeline.setFirstVisibleDate(showYear - 1, getCalendarMonth(1), 01);
        timeline.setLastVisibleDate(showYear + 1,  getCalendarMonth(12), 31);


        timeline.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int index) {
                makeCalendar(day, month+1, year);
            }
        });

        timeline.setSelectedDate(showYear, getCalendarMonth(showMonth), showDay);



        timeline.setFollowScroll(true);

    }

    private void makeCalendar(final int day, final int month, final int year) {
        showpDialog();
        workInfos.clear();


        Call<CWorkResult> call = apiInterface().calendarWork(user, day, month, year);

        call.enqueue(new Callback<CWorkResult>() {
            @Override
            public void onResponse(Call<CWorkResult> call, Response<CWorkResult> response) {

                if (response.body() != null) {

                    if ("1".equals(response.body().getId())) {

                        if (response.body().getWorks().size()== 0)
                            commons.makeToast(CalendarActivity.this, "Không có lịch trong ngày " + day + "/" + month + "/" + year).show();
                        else {
                            workInfos.addAll(response.body().getWorks());
                        }
                    } else {
                        commons.makeToast(CalendarActivity.this, response.body().getMsg()).show();
                    }


                }

                mAdapter.notifyDataSetChanged();
                hidepDialog();
            }

            @Override
            public void onFailure(Call<CWorkResult> call, Throwable t) {
                hidepDialog();
                commons.showToastDisconnect(CalendarActivity.this);
            }
        });


    }

    private int getCalendarMonth(int month) {
        switch (month) {
            case 1:
                return Calendar.JANUARY;
            case 2:
                return Calendar.FEBRUARY;
            case 3:
                return Calendar.MARCH;
            case 4:
                return Calendar.APRIL;
            case 5:
                return Calendar.MAY;
            case 6:
                return Calendar.JUNE;
            case 7:
                return Calendar.JULY;
            case 8:
                return Calendar.AUGUST;
            case 9:
                return Calendar.SEPTEMBER;
            case 10:
                return Calendar.OCTOBER;
            case 11:
                return Calendar.NOVEMBER;
            case 12:
                return Calendar.DECEMBER;
            default:
                return 1;
        }
    }
}
