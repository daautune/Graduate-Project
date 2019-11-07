package com.dtu.capstone2.ereading.ui.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dtu.capstone2.ereading.R;
import com.dtu.capstone2.ereading.datasource.repository.EReadingRepository;
import com.dtu.capstone2.ereading.datasource.repository.LocalRepository;
import com.dtu.capstone2.ereading.network.request.DataStringReponse;
import com.dtu.capstone2.ereading.network.request.Vocabulary;
import com.dtu.capstone2.ereading.ui.utils.BaseFragment;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PageHomeFragment extends BaseFragment {
    HomeFragmentViewModal mViewModel;
    private List<Vocabulary> listWord;
    private String strInputText;
    private String strReponseText;
    private EditText edtInputText;
    private TextView edtReponserText;
    private Button btnTranslate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new HomeFragmentViewModal(new EReadingRepository(), new LocalRepository(getContext()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_page_translate, container, false);
        edtInputText = view.findViewById(R.id.edtInputText);
        edtReponserText = view.findViewById(R.id.edtResponseText);
        btnTranslate = view.findViewById(R.id.btnTranslate);
        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strInputText = edtInputText.getText().toString().trim();
                if (strInputText.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập văn bản cần đọc.", Toast.LENGTH_SHORT).show();
                    return;
                }
                showLoadingDialog();
                mViewModel.getDataStringReponse(strInputText)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<DataStringReponse>() {
                            @Override
                            public void accept(DataStringReponse dataStringReponse) throws Exception {
                                dismissLoadingDialog();
                                strReponseText = dataStringReponse.getStringData();
                                edtReponserText.setText(strReponseText);
                                listWord = dataStringReponse.getListVocabulary();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                dismissLoadingDialog();
                                showApiErrorDialog();

                            }
                        });
            }
        });
        return view;
    }
}
