package com.dtu.capstone2.ereading.ui.account.login;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dtu.capstone2.ereading.R;
import com.dtu.capstone2.ereading.datasource.repository.EReadingRepository;
import com.dtu.capstone2.ereading.datasource.repository.LocalRepository;
import com.dtu.capstone2.ereading.network.request.AccountLoginRequest;
import com.dtu.capstone2.ereading.network.request.AccountRegisterRequest;
import com.dtu.capstone2.ereading.network.request.DataLoginRequest;
import com.dtu.capstone2.ereading.network.utils.ApiExceptionResponse;
import com.dtu.capstone2.ereading.ui.account.register.RegisterFragment;
import com.dtu.capstone2.ereading.ui.model.AccountErrorResponse;
import com.dtu.capstone2.ereading.ui.utils.BaseFragment;
import com.dtu.capstone2.ereading.ui.utils.RxBusTransport;
import com.dtu.capstone2.ereading.ui.utils.Transport;
import com.dtu.capstone2.ereading.ui.utils.TypeTransportBus;
import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LoginFragment extends BaseFragment implements View.OnClickListener, View.OnFocusChangeListener {
    public final String TAG = getClass().getSimpleName();

    LoginViewModel loginviewmodel;
    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnLogin;
    private TextView btnLoginRegister;
    private TextView textSlogan;
    private TextInputLayout layoutPassword;
    private TextInputLayout layoutUsername;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginviewmodel = new LoginViewModel(new EReadingRepository(), new LocalRepository(getContext()));
        RxBusTransport.INSTANCE.listen()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Transport>() {
                    @Override
                    public void accept(Transport transport) throws Exception {
                        if (transport.getSender().equals(RegisterFragment.class.getSimpleName()) && transport.getTypeTransport() == TypeTransportBus.REGISTER_SUCCESS) {
                            AccountRegisterRequest accountRegisterRequest = (AccountRegisterRequest) transport.getMessage();
                            edtUsername.setText(accountRegisterRequest.getUserName());
                            edtPassword.setText(accountRegisterRequest.getPassword());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        edtPassword = view.findViewById(R.id.tvLoginPassword);
        edtUsername = view.findViewById(R.id.tvLoginUsername);
        btnLogin = view.findViewById(R.id.btnSignInAccount);
        btnLoginRegister = view.findViewById(R.id.btnLoginRegister);
        textSlogan = view.findViewById(R.id.textSlogan);
        layoutPassword = view.findViewById(R.id.layoutLoginPassword);
        layoutUsername = view.findViewById(R.id.layout_login_username);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEvent();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignInAccount: {
                eventOnLoginClicked();
                break;
            }
            case R.id.btnLoginRegister: {
                replaceFragment(R.id.layoutManagerAccountContainerActivity, new RegisterFragment(), true, true);
                break;
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.tvLoginUsername: {
                if (hasFocus && layoutUsername.getError() != null) {
                    layoutUsername.setError(null);
                }
                break;
            }
            case R.id.tvLoginPassword: {
                if (hasFocus && layoutPassword.getError() != null) {
                    layoutPassword.setError(null);
                }
                break;
            }
        }
    }

    private void initEvent() {
        btnLogin.setOnClickListener(this);
        btnLoginRegister.setOnClickListener(this);
        edtUsername.setOnFocusChangeListener(this);
        edtPassword.setOnFocusChangeListener(this);
    }

    private void eventOnLoginClicked() {
        String strUserName = edtUsername.getText().toString().trim();
        String strPassword = edtPassword.getText().toString().trim();
        clearErrorMessageOnLayout();

        showLoadingDialog();
        getManagerSubscribe().add(loginviewmodel.GetDataLoginRequest(new AccountLoginRequest(strUserName, strPassword))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DataLoginRequest>() {
                    @Override
                    public void accept(DataLoginRequest dataLoginRequest) throws Exception {
                        showSuccessDialog(TAG, false);
                        onBackPressed();
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        dismissLoadingDialog();
                        ApiExceptionResponse response = ((ApiExceptionResponse) throwable);
                        if (response.getStatusCode() != null && response.getStatusCode() == HttpsURLConnection.HTTP_BAD_REQUEST) {
                            try {
                                Gson gson = new Gson();
                                AccountErrorResponse accountErrorResponse = gson.fromJson(response.getMessageError(), AccountErrorResponse.class);
                                layoutUsername.setError(accountErrorResponse.getUserNameError());
                                layoutPassword.setError(accountErrorResponse.getPasswordError());
                            } catch (Exception ex) {
                                Log.e(TAG, ex.getMessage());
                            }
                        } else {
                            showApiErrorDialog();
                        }
                    }
                }));
    }

    private void clearErrorMessageOnLayout() {
        if (layoutUsername.getError() != null) {
            layoutUsername.setError(null);
        }
        if (layoutPassword.getError() != null) {
            layoutPassword.setError(null);
        }
    }
}
