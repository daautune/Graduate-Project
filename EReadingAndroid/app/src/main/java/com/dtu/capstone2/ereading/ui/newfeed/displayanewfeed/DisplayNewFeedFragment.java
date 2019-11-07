package com.dtu.capstone2.ereading.ui.newfeed.displayanewfeed;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtu.capstone2.ereading.R;
import com.dtu.capstone2.ereading.datasource.repository.LocalRepository;
import com.dtu.capstone2.ereading.ui.newfeed.translate.TranslateNewFeedFragment;
import com.dtu.capstone2.ereading.ui.utils.BaseFragment;
import com.dtu.capstone2.ereading.ui.utils.Constants;

import org.jetbrains.annotations.Nullable;

public class DisplayNewFeedFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private WebView mWebViewNewFeed;
    private DisplayNewFeedViewModel mViewModel;
    private ImageView imgBack;
    private ImageView imgTranslate;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvTypeDisplayNewFeed;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new DisplayNewFeedViewModel(new LocalRepository(getContext()));

        if (getArguments() != null) {
            String parseUrl = getArguments().getString(Constants.KEY_URL_NEW_FEED);
            mViewModel.setUrlNewFeed(parseUrl);
            mViewModel.setTypeNewFeed(getArguments().getString(Constants.KEY_TYPE_NEW_FEED));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_display_new_feed, container, false);
        mWebViewNewFeed = view.findViewById(R.id.webViewDisplayNewFeed);
        mWebViewNewFeed.getSettings().setJavaScriptEnabled(true);
        imgBack = view.findViewById(R.id.imgDisplayNewFeedBack);
        imgTranslate = view.findViewById(R.id.imgDisplayNewFeedTranslate);
        swipeRefreshLayout = view.findViewById(R.id.layout_swipe_refresh_display_new_feed);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPink, R.color.colorIndigo, R.color.colorLime);
        tvTypeDisplayNewFeed = view.findViewById(R.id.tv_display_new_feed_type);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEventsView();
        mWebViewNewFeed.loadUrl(mViewModel.getUrlNewFeed());
    }

    @Override
    public void onRefresh() {
        mWebViewNewFeed.reload();
    }

    private void initEventsView() {
        tvTypeDisplayNewFeed.setText(mViewModel.getTypeNewFeed());

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mWebViewNewFeed.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        imgTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewModel.isNotSetLevelWhenLogin()) {
                    showMessageErrorDialog("Vui lòng cho chúng tôi biết trình độ tiếng anh của bạn.");
                    return;
                }

                Fragment fragment = new TranslateNewFeedFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.KEY_URL_NEW_FEED, mViewModel.getUrlNewFeed());
                bundle.putString(Constants.KEY_TYPE_NEW_FEED, mViewModel.getTypeNewFeed());
                fragment.setArguments(bundle);
                addFragment(R.id.layoutPageNewFeedContainer, fragment, true, true);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
    }
}
