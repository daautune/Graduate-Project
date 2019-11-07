package com.dtu.capstone2.ereading.ui.account.register;

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
import android.widget.Toast;

import com.dtu.capstone2.ereading.R;
import com.dtu.capstone2.ereading.datasource.repository.EReadingRepository;
import com.dtu.capstone2.ereading.network.request.AccountRegisterRequest;
import com.dtu.capstone2.ereading.network.utils.ApiExceptionResponse;
import com.dtu.capstone2.ereading.ui.model.AccountErrorResponse;
import com.dtu.capstone2.ereading.ui.utils.BaseFragment;
import com.dtu.capstone2.ereading.ui.utils.RxBusTransport;
import com.dtu.capstone2.ereading.ui.utils.Transport;
import com.dtu.capstone2.ereading.ui.utils.TypeTransportBus;
import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RegisterFragment extends BaseFragment implements View.OnClickListener, View.OnFocusChangeListener {
    private final String TAG = getClass().getSimpleName();

    private RegisterViewModel viewModel;

    private Button btnContinue;
    private EditText edtUserName;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtPasswordConfirm;
    private TextInputLayout layoutUserName;
    private TextInputLayout layoutEmail;
    private TextInputLayout layoutPassword;
    private TextInputLayout layoutPasswordConfirm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new RegisterViewModel(new EReadingRepository());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        btnContinue = view.findViewById(R.id.btnRegisterContinue);
        edtUserName = view.findViewById(R.id.edtRegisterUserName);
        edtEmail = view.findViewById(R.id.edtRegisterEmail);
        edtPassword = view.findViewById(R.id.edtRegisterPassword);
        edtPasswordConfirm = view.findViewById(R.id.edtRegisterPasswordConfirm);
        layoutUserName = view.findViewById(R.id.layoutRegisterUserName);
        layoutEmail = view.findViewById(R.id.layoutRegisterEmail);
        layoutPassword = view.findViewById(R.id.layoutRegisterPassword);
        layoutPasswordConfirm = view.findViewById(R.id.layoutRegisterPasswordConfirm);

        return view;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEventView();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnRegisterContinue) {
            showLoadingDialog();
            clearErrorMessageOnLayout();
            AccountRegisterRequest account = new AccountRegisterRequest(edtUserName.getText().toString().trim(),
                    edtPassword.getText().toString().trim(),
                    edtPasswordConfirm.getText().toString().trim(),
                    edtEmail.getText().toString().trim());

            viewModel.createNewAccount(account).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<AccountRegisterRequest>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onSuccess(AccountRegisterRequest accountRegisterRequest) {
                            dismissLoadingDialog();
                            RxBusTransport.INSTANCE.publish(new Transport(TypeTransportBus.REGISTER_SUCCESS, TAG, accountRegisterRequest));
                            Toast.makeText(getContext(), "Đăng kí tài khoản thành công! Bạn có thể đăng nhập.", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }

                        @Override
                        public void onError(Throwable e) {
                            dismissLoadingDialog();
                            ApiExceptionResponse response = ((ApiExceptionResponse) e);
                            if (response.getStatusCode() != null && response.getStatusCode() == HttpsURLConnection.HTTP_BAD_REQUEST) {
                                try {
                                    Gson gson = new Gson();
                                    AccountErrorResponse accountErrorResponse = gson.fromJson(response.getMessageError(), AccountErrorResponse.class);

                                    layoutUserName.setError(accountErrorResponse.getUserNameError());
                                    layoutEmail.setError(accountErrorResponse.getEmailError());
                                    layoutPassword.setError(accountErrorResponse.getPasswordError());
                                    layoutPasswordConfirm.setError(accountErrorResponse.getPasswordConfirmError());
                                } catch (Exception ex) {
                                    Log.e(TAG, ex.getMessage());
                                }
                            } else {
                                showApiErrorDialog();
                            }
                        }
                    });
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.edtRegisterUserName: {
                if (hasFocus && layoutUserName != null) {
                    layoutUserName.setError(null);
                }
                break;
            }
            case R.id.edtRegisterEmail: {
                if (hasFocus && layoutEmail != null) {
                    layoutEmail.setError(null);
                }
                break;
            }
            case R.id.edtRegisterPassword: {
                if (hasFocus && layoutPassword != null) {
                    layoutPassword.setError(null);
                }
                break;
            }
            case R.id.edtRegisterPasswordConfirm: {
                if (hasFocus && layoutPasswordConfirm != null) {
                    layoutPasswordConfirm.setError(null);
                }
                break;
            }
        }
    }

    private void initEventView() {
        btnContinue.setOnClickListener(this);
        edtUserName.setOnFocusChangeListener(this);
        edtEmail.setOnFocusChangeListener(this);
        edtPassword.setOnFocusChangeListener(this);
        edtPasswordConfirm.setOnFocusChangeListener(this);
    }

    private void clearErrorMessageOnLayout() {
        if (layoutUserName.getError() != null) {
            layoutUserName.setError(null);
        }
        if (layoutEmail.getError() != null) {
            layoutEmail.setError(null);
        }
        if (layoutPassword.getError() != null) {
            layoutPassword.setError(null);
        }
        if (layoutPasswordConfirm.getError() != null) {
            layoutPasswordConfirm.setError(null);
        }
    }
}
