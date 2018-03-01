package vn.com.mattana.dms;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.com.mattana.adapter.NoticeAdapter;
import vn.com.mattana.model.api.NoticeResult;
import vn.com.mattana.model.api.ResultInfo;
import vn.com.mattana.view.LoadMoreListView;

public class NoticeActivity extends BaseActivity {

    @BindView(R.id.list)
    LoadMoreListView listView;

    NoticeAdapter adapter;
    List<NoticeResult> noticeResultList;
    int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        ButterKnife.bind(this);
        createToolbar();


        noticeResultList = new ArrayList<>();
        adapter = new NoticeAdapter(noticeResultList, this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NoticeResult info = noticeResultList.get(i);
                if(info.getRead() == 0) {
                    makeUpdate(info.getId(), info.getTitle(), info.getMessage(), i);
                } else {
                    commons.showAlertInfo(NoticeActivity.this, info.getTitle(), info.getMessage(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                }
            }
        });

        listView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                makeGet();
            }
        });

        listView.setAdapter(adapter);

        makeGet();
    }

    private void makeUpdate(String id, final String title, final String msg, final int position) {
        showpDialog();
        Call<ResultInfo> call = apiInterface().updateNoticeRead(id);

        call.enqueue(new Callback<ResultInfo>() {
            @Override
            public void onResponse(Call<ResultInfo> call, Response<ResultInfo> response) {

                if (response.body() != null){
                    noticeResultList.get(position).setRead(1);
                    adapter.notifyDataSetChanged();
                    commons.showAlertInfo(NoticeActivity.this, title, msg, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                } else
                    commons.makeToast(NoticeActivity.this, "Lỗi wifi").show();

                hidepDialog();
            }

            @Override
            public void onFailure(Call<ResultInfo> call, Throwable t) {
                hidepDialog();
                commons.showToastDisconnect(NoticeActivity.this);
            }
        });
    }


    private void makeGet() {
        showpDialog();
        Call<List<NoticeResult>> call = apiInterface().notices(user, page);

        call.enqueue(new Callback<List<NoticeResult>>() {
            @Override
            public void onResponse(Call<List<NoticeResult>> call, Response<List<NoticeResult>> response) {

                if(response.body() != null && response.body().size() > 0) {
                    page++;
                    noticeResultList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }

                hidepDialog();
            }

            @Override
            public void onFailure(Call<List<NoticeResult>> call, Throwable t) {
                hidepDialog();
                commons.showToastDisconnect(NoticeActivity.this);
            }
        });
    }
}
