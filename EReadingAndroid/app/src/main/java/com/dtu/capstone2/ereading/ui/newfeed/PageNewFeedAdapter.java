package com.dtu.capstone2.ereading.ui.newfeed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dtu.capstone2.ereading.R;
import com.dtu.capstone2.ereading.ui.model.ItemPageNewFeed;

import java.util.List;

public class PageNewFeedAdapter extends RecyclerView.Adapter<PageNewFeedAdapter.PageNewFeedViewHolder> {
    private List<ItemPageNewFeed> mItemPageNewFeeds;
    private OnItemListener mOnItemListener;
    private Context mContext;

    PageNewFeedAdapter(List<ItemPageNewFeed> itemPageNewFeeds, Context context) {
        mItemPageNewFeeds = itemPageNewFeeds;
        mContext = context;
    }

    void setmItemPageNewFeeds(OnItemListener onItemListener) {
        mOnItemListener = onItemListener;
    }

    @NonNull
    @Override
    public PageNewFeedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PageNewFeedViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_page_new_feed, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PageNewFeedViewHolder pageNewFeedViewHolder, int i) {
        pageNewFeedViewHolder.onBindData(mItemPageNewFeeds.get(i));
    }

    @Override
    public int getItemCount() {
        return mItemPageNewFeeds.size();
    }

    /**
     * Class is view holder of this adapter
     */
    class PageNewFeedViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgLogo;
        private TextView titleSourceFeed;
        private RequestOptions options = new RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.ic_image_thumbnail_default)
                .error(R.drawable.ic_thumbnail_error);

        PageNewFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            imgLogo = itemView.findViewById(R.id.imgPageNewFeedLogo);
            titleSourceFeed = itemView.findViewById(R.id.tvPageNewFeedTitle);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemListener.onItemClick(getAdapterPosition());
                }
            });
        }

        void onBindData(ItemPageNewFeed itemPageNewFeed) {
            Glide.with(mContext).load(itemPageNewFeed.getUrlImageLogo()).apply(options).into(imgLogo);
            titleSourceFeed.setText(itemPageNewFeed.getTitleSourceFeed());
        }
    }

    /**
     * Interface is used listener typeTransport of item
     */
    interface OnItemListener {
        void onItemClick(int position);
    }
}
