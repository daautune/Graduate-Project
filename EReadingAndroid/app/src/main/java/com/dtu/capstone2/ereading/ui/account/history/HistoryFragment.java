package com.dtu.capstone2.ereading.ui.account.history;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dtu.capstone2.ereading.R;
import com.dtu.capstone2.ereading.network.response.ListHistoryResponse;
import com.dtu.capstone2.ereading.ui.utils.BaseFragment;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HistoryFragment extends BaseFragment {
    private HistoryViewModel viewModel;
    private HistoryAdapter adapter;

    private RecyclerView mRecycleListView;
    private ImageView mImageListHistoryBack;
    private SwipeRefreshLayout refreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new HistoryViewModel();
        adapter = new HistoryAdapter(viewModel.getListHistory());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        mRecycleListView = view.findViewById(R.id.recycler_list_history);
        mImageListHistoryBack = view.findViewById(R.id.image_back_list_history);
        refreshLayout = view.findViewById(R.id.layout_swipe_refresh_list_history);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        initScrollListener();
        initEventView();
        initData();
    }

    private void initView() {
        mRecycleListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycleListView.setAdapter(adapter);
        refreshLayout.setRefreshing(true);
        refreshLayout.setColorSchemeResources(R.color.colorPink, R.color.colorIndigo, R.color.colorLime);
    }

    private void initEventView() {
        mImageListHistoryBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromServer();
            }
        });
    }

    private void getDataFromServer() {
        getManagerSubscribe().add(viewModel.getListHistoryFromServerFirstLoad()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ListHistoryResponse>() {
                    @Override
                    public void accept(ListHistoryResponse listHistoryResponse) throws Exception {
                        refreshLayout.setRefreshing(false);
                        adapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        handleGetDataFromServerError();
                    }
                }));
    }

    private void initData() {
        getDataFromServer();
    }

    private void initScrollListener() {
        mRecycleListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!viewModel.getLoadingMore() && viewModel.getIsCanLoadMore()) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == viewModel.getSizeListFavorite() - 1) {
                        //bottom of list!
                        loadMore();
                        viewModel.setLoadingMore(true);
                    }
                }
            }
        });
    }

    private void loadMore() {
        viewModel.getListHistory().add(null);
        adapter.notifyItemInserted(viewModel.getSizeListFavorite() - 1);


        getManagerSubscribe().add(viewModel.loadMore(viewModel.getPageData())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ListHistoryResponse>() {
                    @Override
                    public void accept(ListHistoryResponse dataFavoriteReponse) throws Exception {
                        handleLoadMoreDataSuccess(dataFavoriteReponse);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        handleGetDataFromServerError();
                    }
                }));
    }

    private void handleLoadMoreDataSuccess(ListHistoryResponse listHistoryResponse) {
        refreshLayout.setRefreshing(false);
        viewModel.getListHistory().remove(viewModel.getPositionItemLater());
        adapter.notifyItemRemoved(viewModel.getSizeListFavorite());
        viewModel.getListHistory().addAll(listHistoryResponse.getListData());
        adapter.notifyDataSetChanged();
        viewModel.setLoadingMore(false);
    }

    private void handleGetDataFromServerError() {
        showApiErrorDialog();
        refreshLayout.setRefreshing(false);
        if (viewModel.getLoadingMore()) {
            viewModel.setLoadingMore(false);
            viewModel.getListHistory().remove(viewModel.getPositionItemLater());
            adapter.notifyItemRemoved(viewModel.getSizeListFavorite());
        }
    }
}
