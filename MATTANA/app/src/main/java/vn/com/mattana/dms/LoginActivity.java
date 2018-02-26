package vn.com.mattana.dms;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import vn.com.mattana.model.api.LoginResult;
import vn.com.mattana.util.LoginService;
import vn.com.mattana.util.MRes;
import vn.com.mattana.util.RealmController;
import vn.com.mattana.util.ServiceGenerator;

public class LoginActivity extends BaseActivity {


    @BindView(R.id.epass)
    EditText ePass;

    @BindView(R.id.ename)
    EditText eName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }


    public void loginClick(View view) {
        String pass = ePass.getText().toString();
        String user = eName.getText().toString();
        if (TextUtils.isEmpty(pass) || TextUtils.isEmpty(user)) {
            commons.makeToast(getApplicationContext(), "Nhập đủ thông tin").show();
        } else {
            loginCheck(user, pass);
        }
    }

    private void loginCheck(final String name, String pass) {

        showpDialog();

        LoginService apiService = ServiceGenerator.createService(LoginService.class, name, pass);

        Call<LoginResult> call = apiService.basicLogin();

        call.enqueue(new Callback<LoginResult>() {

            @Override
            public void onResponse(Call<LoginResult> call, retrofit2.Response<LoginResult> response) {
                hidepDialog();

                if (response.body() != null) {
                    if ("1".equals(response.body().getId())) {
                        loginSuccess(response.body().getUser(), response.body().getToken(), response.body().getName(), response.body().getCode());
                    } else {
                        commons.makeToast(getApplicationContext(), response.body().getMsg()).show();
                    }
                }
                hidepDialog();
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                hidepDialog();
            }
        });
    }

    protected void loginSuccess(String user, String token, String name, String code) {

        String oldUser = prefsHelper.get(MRes.getInstance().PREF_KEY_USER, "");

        if (oldUser != null && !oldUser.equals(user)) {
            // delete all old data
            RealmController.getInstance().clearAll();
        }

        prefsHelper.put(MRes.getInstance().PREF_KEY_USER, user);
        prefsHelper.put(MRes.getInstance().PREF_KEY_TOKEN, token);
        prefsHelper.put(MRes.getInstance().PREF_KEY_NAME, name);
        prefsHelper.put(MRes.getInstance().PREF_KEY_CODE, code);
        commons.startActivity(LoginActivity.this, MainActivity.class);

        finish();
    }
}
