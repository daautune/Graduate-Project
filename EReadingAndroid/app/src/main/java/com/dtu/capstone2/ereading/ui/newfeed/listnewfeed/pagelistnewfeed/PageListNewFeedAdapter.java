package com.dtu.capstone2.ereading.ui.newfeed.listnewfeed.pagelistnewfeed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dtu.capstone2.ereading.R;
import com.dtu.capstone2.ereading.network.response.BBCRssItemResponse;

import java.util.List;

public class PageListNewFeedAdapter extends RecyclerView.Adapter<PageListNewFeedAdapter.ListNewFeedViewHolder> {
    private List<BBCRssItemResponse> mRssItemResponses;
    private Context mContext;
    private OnItemListener mOnItemListener;

    PageListNewFeedAdapter(List<BBCRssItemResponse> rssItemResponses, Context context) {
        mRssItemResponses = rssItemResponses;
        mContext = context;
    }

    void setmOnItemListener(OnItemListener mOnItemListener) {
        this.mOnItemListener = mOnItemListener;
    }

    @NonNull
    @Override
    public ListNewFeedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_list_news_feed, viewGroup, false);

        return new ListNewFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListNewFeedViewHolder listNewFeedViewHolder, int i) {
        listNewFeedViewHolder.onBindData(mRssItemResponses.get(i));
    }

    @Override
    public int getItemCount() {
        return mRssItemResponses.size();
    }

    /**
     * Class is view holder view for adapter
     */
    class ListNewFeedViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgNewsThumbnail;
        private TextView tvNewsTitle;
        private TextView tvNewsDescription;
        private TextView tvNewsPushDate;

        //Setting các tuỳ chọn cho thư viện load ảnh Glide
        private RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_image_thumbnail_default)
                .error(R.drawable.ic_thumbnail_error);

        ListNewFeedViewHolder(@NonNull View itemView) {
            super(itemView);
            imgNewsThumbnail = itemView.findViewById(R.id.img_news_thumbnail);
            tvNewsTitle = itemView.findViewById(R.id.tv_news_title);
            tvNewsDescription = itemView.findViewById(R.id.tv_news_description);
            tvNewsPushDate = itemView.findViewById(R.id.tv_news_push_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemListener.onItemClick(getAdapterPosition());
                }
            });
        }

        void onBindData(BBCRssItemResponse rssItemResponse) {
            try {
                tvNewsTitle.setText(rssItemResponse.getTitle());
                tvNewsDescription.setText(rssItemResponse.getDescription());
                tvNewsPushDate.setText(rssItemResponse.getPushDate());
            } catch(Exception e) {}

        }
    }

    /**
     * Interface is used listener typeTransport of item
     */
    interface OnItemListener {
        void onItemClick(int position);
    }
}
