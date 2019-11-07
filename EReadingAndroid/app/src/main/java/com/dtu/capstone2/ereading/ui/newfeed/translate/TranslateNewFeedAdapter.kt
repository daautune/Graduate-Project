package com.dtu.capstone2.ereading.ui.newfeed.translate

import android.support.v7.widget.RecyclerView
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dtu.capstone2.ereading.R
import com.dtu.capstone2.ereading.ui.model.LineContentNewFeed
import com.dtu.capstone2.ereading.ui.utils.setSpannerEvent
import kotlinx.android.synthetic.main.item_translate_result_content.view.*

class TranslateNewFeedAdapter(private val data: List<LineContentNewFeed>) : RecyclerView.Adapter<TranslateNewFeedAdapter.TranslateNewFeedViewHolder>() {

    override fun onCreateViewHolder(container: ViewGroup, itemType: Int): TranslateNewFeedViewHolder {
        val view = LayoutInflater.from(container.context).inflate(R.layout.item_translate_result_content, container, false)
        return TranslateNewFeedViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(viewHolder: TranslateNewFeedViewHolder, position: Int) {
        viewHolder.onBindData(data[position])
    }

    inner class TranslateNewFeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.tv_item_translate_new_feed_content?.movementMethod = LinkMovementMethod.getInstance()
        }

        fun onBindData(newFeed: LineContentNewFeed) {
            itemView.tv_item_translate_new_feed_content?.text = newFeed.setSpannerEvent()
            itemView.tv_item_translate_new_feed_content?.tag = adapterPosition
        }
    }
}
