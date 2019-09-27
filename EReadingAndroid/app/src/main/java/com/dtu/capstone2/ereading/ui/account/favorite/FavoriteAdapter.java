package com.dtu.capstone2.ereading.ui.account.favorite;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtu.capstone2.ereading.R;
import com.dtu.capstone2.ereading.network.request.Favorite;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Create by Vo The Doan on 04/30/2019
 */
public class FavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private List<Favorite> mArrContact;
    private OnItemListener listener;

    FavoriteAdapter(List<Favorite> data) {
        mArrContact = data;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_favorite, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    void setItemDeleteOnClickListener(OnItemListener onItemListener) {
        listener = onItemListener;
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return mArrContact == null ? 0 : mArrContact.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mArrContact.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvWord, tvMeanShort, tvType, tvDateTime;
        private ImageView imgDelete;

        ItemViewHolder(View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tvWord);
            tvMeanShort = itemView.findViewById(R.id.tvNghia);
            imgDelete = itemView.findViewById(R.id.imgdeleteitem);
            tvType = itemView.findViewById(R.id.tvtype);
            tvDateTime = itemView.findViewById(R.id.txt_favorite_datetime);

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }

        void onBindData(Favorite favorite) {
            tvWord.setText((getAdapterPosition() + 1) + ". " + favorite.getStrWord());
            tvMeanShort.setText(favorite.getStrMeanShort() == null ? "..." : favorite.getStrMeanShort());
            tvType.setText(favorite.getStrType());
            tvDateTime.setText(favorite.getDateCreate());
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
    }

    private void populateItemRows(ItemViewHolder viewHolder, int position) {
        viewHolder.onBindData(mArrContact.get(position));
    }

    interface OnItemListener {
        void onItemClick(int position);
    }
}
