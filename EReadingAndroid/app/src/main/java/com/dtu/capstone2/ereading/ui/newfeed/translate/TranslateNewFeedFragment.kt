package com.dtu.capstone2.ereading.ui.newfeed.translate

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dtu.capstone2.ereading.R
import com.dtu.capstone2.ereading.datasource.repository.EReadingRepository
import com.dtu.capstone2.ereading.datasource.repository.LocalRepository
import com.dtu.capstone2.ereading.network.utils.ApiExceptionResponse
import com.dtu.capstone2.ereading.ui.model.ErrorUnauthorizedRespone
import com.dtu.capstone2.ereading.ui.model.VocabularyLocation
import com.dtu.capstone2.ereading.ui.utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_translate_result.*
import javax.net.ssl.HttpsURLConnection

class TranslateNewFeedFragment : BaseFragment(), View.OnClickListener, DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnClickListener {
    companion object {
        const val TITLE_DIALOG_FAVORITE = "favorite"
        const val TITLE_DIALOG_REFRESH = "refresh"
    }

    private lateinit var mAlertDialogBuilder: AlertDialog.Builder
    private lateinit var viewModel: TranslateNewFeedViewModel
    private lateinit var adapter: TranslateNewFeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = TranslateNewFeedViewModel(EReadingRepository(), LocalRepository(context))
        viewModel.urlNewFeed = arguments?.getString(Constants.KEY_URL_NEW_FEED)
        mAlertDialogBuilder = AlertDialog.Builder(context!!)

        managerSubscribe.add(RxBusTransport.listen()
                .observeOnUiThread()
                .subscribe({
                    if (it.typeTransport == TypeTransportBus.SPAN_ON_CLICK && it.sender == DefaultWordClickableSpan::class.java.simpleName) {
                        viewModel.addOrRemoveVocabularyToListRefresh(it.message as VocabularyLocation).also { positionItemChange ->
                            if (positionItemChange != TranslateNewFeedViewModel.NO_ITEM_CHANGE) {
                                adapter.notifyItemChanged(positionItemChange)
                            }
                        }
                        reloadIconRefresh()
                    }
                    if (it.typeTransport == TypeTransportBus.SPAN_ON_CLICK && it.sender == FavoriteWordClickableSpan::class.java.simpleName) {
                        viewModel.addOrRemoveVocabularyToListAddFavoriteByLocationVocabulary(it.message as VocabularyLocation).also { positionItemChange ->
                            if (positionItemChange != TranslateNewFeedViewModel.NO_ITEM_CHANGE) {
                                adapter.notifyItemChanged(positionItemChange)
                            }
                        }
                        reloadIconFavorite()
                    }
                }, {}, {}))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_translate_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initEventsView()
        managerSubscribe.add(viewModel.getDataFromHTMLAndOnNextDetectWord()
                .observeOnUiThread()
                .subscribe({
                    adapter.notifyItemInserted(viewModel.getPositionItemInsertedOfRV())
                }, {
                    Log.w("Translate", it.toString())
                    Toast.makeText(context, "Quá trình dịch gián đoạn! Kiểm tra kết nối Internet.", Toast.LENGTH_LONG).show()
                }, {
                    Toast.makeText(context, "Dịch hoàn tất.", Toast.LENGTH_SHORT).show()
                }))
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imgTranslateNewFeedBack -> {
                activity?.onBackPressed()
            }
            R.id.imgTranslateNewFeedRefresh -> {
                with(viewModel) {
                    if (!isLogin()) {
                        showToastRequirementLogin()
                        return
                    }
                    this.nameListDialogShowing = TITLE_DIALOG_REFRESH
                    showDialog("Danh sách các từ đã chọn.", this.getArrayWordRefresh(), this.getArraySelectedRefresh())
                }
            }
            R.id.imgTranslateNewFeedFavoriteReview -> {
                with(viewModel) {
                    if (!isLogin()) {
                        showToastRequirementLogin()
                        return
                    }
                    this.nameListDialogShowing = TITLE_DIALOG_FAVORITE
                    showDialog("Danh sách các từ yêu thích đã chọn.", this.getArrayWordAddFavorite(), this.getArraySelectedAddFavorite())
                }
            }
        }
    }

    override fun onClick(dialog: DialogInterface, which: Int, isChecked: Boolean) {

    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                when (viewModel.nameListDialogShowing) {
                    TITLE_DIALOG_REFRESH -> {
                        viewModel.sendVocabularySelectedToServerToTranslateAgain()
                                .observeOnUiThread()
                                .subscribe({
                                    adapter.notifyItemChanged(it)
                                    reloadIconRefresh()
                                }, {
                                    Toast.makeText(context, "Lỗi trong quá trình dich. Vui lòng thử lại.", Toast.LENGTH_SHORT).show()
                                }, {})
                    }
                    TITLE_DIALOG_FAVORITE -> {
                        managerSubscribe.add(viewModel.addFavoriteToServer()
                                .publishDialogLoading()
                                .observeOnUiThread()
                                .subscribe({
                                    deleteListFavorite()
                                    adapter.notifyDataSetChanged()
                                }, {
                                    (it as? ApiExceptionResponse)?.let { exception ->
                                        if (exception.statusCode == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                                            val dataError = Gson().fromJson(exception.messageError, ErrorUnauthorizedRespone::class.java)
                                            showMessageErrorDialog(dataError.detail)
                                        }
                                    }
                                }))
                    }
                }
            }
            DialogInterface.BUTTON_NEGATIVE -> {
            }
            DialogInterface.BUTTON_NEUTRAL -> {
                when (viewModel.nameListDialogShowing) {
                    TITLE_DIALOG_REFRESH -> {
                        deleteListRefresh()
                        adapter.notifyDataSetChanged()
                    }
                    TITLE_DIALOG_FAVORITE -> {
                        deleteListFavorite()
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun initView() {
        adapter = TranslateNewFeedAdapter(viewModel.dataRecyclerView)
        recyclerViewTranslateNewFeed?.layoutManager = LinearLayoutManager(context)
        recyclerViewTranslateNewFeed?.adapter = adapter
        tv_translate_new_feed_level?.text = if (viewModel.getNameLevelOfUser().isEmpty()) {
            "Người dùng vãng lai."
        } else {
            viewModel.getNameLevelOfUser()
        }
    }

    private fun initEventsView() {
        imgTranslateNewFeedBack?.setOnClickListener(this)
        imgTranslateNewFeedRefresh?.setOnClickListener(this)
        imgTranslateNewFeedFavoriteReview?.setOnClickListener(this)
    }

    private fun showDialog(title: String, arrayVocabulary: Array<String>, arraySelect: BooleanArray) {
        mAlertDialogBuilder.setTitle(title)
        mAlertDialogBuilder.setMultiChoiceItems(arrayVocabulary, arraySelect, this)
        mAlertDialogBuilder.setPositiveButton("Xác nhận", this)
        mAlertDialogBuilder.setNegativeButton("Quay lại", this)
        mAlertDialogBuilder.setNeutralButton("Xoá tất cả", this)
        mAlertDialogBuilder.setCancelable(false)
        mAlertDialogBuilder.show()
    }

    private fun reloadIconFavorite() {
        if (viewModel.getSizeListAddFavorite() > 0) {
            imgTranslateNewFeedFavoriteReview?.visibility = View.VISIBLE
            tv_new_feed_translate_guide_favorite?.visibility = View.VISIBLE
            tvTranslateNewFeedFavoriteReviewCount?.visibility = View.VISIBLE
            var count = viewModel.getSizeListAddFavorite().toString()
            if (viewModel.getSizeListAddFavorite() > 99) {
                count = "99+"
            }
            tvTranslateNewFeedFavoriteReviewCount?.text = count
        } else {
            imgTranslateNewFeedFavoriteReview?.visibility = View.GONE
            tv_new_feed_translate_guide_favorite?.visibility = View.GONE
            tvTranslateNewFeedFavoriteReviewCount?.visibility = View.GONE
        }
    }

    private fun reloadIconRefresh() {
        if (viewModel.getSizeListRefresh() > 0) {
            imgTranslateNewFeedRefresh?.visibility = View.VISIBLE
            tv_new_feed_translate_guide_refresh?.visibility = View.VISIBLE
            tvTranslateNewFeedRefreshCount?.visibility = View.VISIBLE
            var count = viewModel.getSizeListRefresh().toString()
            if (viewModel.getSizeListRefresh() > 99) {
                count = "99+"
            }
            tvTranslateNewFeedRefreshCount?.text = count
        } else {
            imgTranslateNewFeedRefresh?.visibility = View.GONE
            tv_new_feed_translate_guide_refresh?.visibility = View.GONE
            tvTranslateNewFeedRefreshCount?.visibility = View.GONE
        }
    }

    private fun deleteListFavorite() {
        viewModel.resetListVocabularyAddFavorite()
        reloadIconFavorite()
    }

    private fun deleteListRefresh() {
        viewModel.resetListVocabularyRefresh()
        reloadIconRefresh()
    }
}
