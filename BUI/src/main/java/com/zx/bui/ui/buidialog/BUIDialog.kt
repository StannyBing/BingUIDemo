package com.zx.bui.ui.buidialog

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.method.ScrollingMovementMethod
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zx.bui.R
import com.zx.bui.util.ItemClickHelper

/**
 * Created by Xiangb on 2019/11/8.
 * 功能：
 */
@SuppressLint("StaticFieldLeak")
object BUIDialog {

    private val handler = Handler()

    private var loadingDialog: AlertDialog? = null
    private var loadingContext: Context? = null

    /**
     * 打开加载dialog
     * @param context
     * @param message 信息
     * @param progress 进度
     * 加载弹窗相互之间共享，一次只能存在一个加载dialog
     */
    fun showLoading(context: Context, message: String, progress: Int = -1): AlertDialog {
        try {
            if (loadingContext != null && loadingContext?.javaClass?.toString().equals(context.javaClass.toString())) {
                if (loadingDialog != null && loadingDialog?.isShowing == true) {
                    sendMessage(message, progress)
                } else {
                    showNewLoading(context, message, progress)
                }
            } else {
                showNewLoading(context, message, progress)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showNewLoading(context, message, progress)
        }
        return loadingDialog!!
    }

    /**
     * 打开普通dialog
     * @param context
     * @param title 标题
     * @param message 内容
     * @param btnBuidler 按钮构造器
     */
    fun showInfo(context: Context, title: String = "", message: String, btnBuidler: BtnBuilder? = null): AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout._layout_dialog_normal, null)
        val titleView = view.findViewById<TextView>(R.id.tv_normal_title)
        if (title.isEmpty()) {
            titleView.visibility = View.GONE
        } else {
            titleView.text = title
        }
        view.findViewById<TextView>(R.id.tv_normal_msg).text = message
        view.findViewById<TextView>(R.id.tv_normal_msg).movementMethod = ScrollingMovementMethod.getInstance()
        return initDialog(context, view, btnBuidler = if (btnBuidler == null) BtnBuilder().withSubmitBtn() else btnBuidler)
    }

    /**
     * 打开列表dialog
     * @param context
     * @param title 标题
     * @param itemList 列表内容
     * @param listClick 列表点击事件
     * @param btnBuidler 按钮构造器
     */
    fun showSimpleList(context: Context, title: String = "", itemList: List<ListBean>, listClick: (ListBean) -> Unit, btnBuidler: BtnBuilder? = null): AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout._layout_dialog_list, null)
        val titleView = view.findViewById<TextView>(R.id.tv_list_title)
        if (title.isEmpty()) {
            titleView.visibility = View.GONE
        } else {
            titleView.text = title
        }
        view.findViewById<RecyclerView>(R.id.rv_list_content).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = DialogListAdapter(itemList, false)
        }
        val dialog = initDialog(context, view, btnBuidler = if (btnBuidler == null) BtnBuilder().withCancelBtn() else btnBuidler)
        ItemClickHelper.addTo(view.findViewById(R.id.rv_list_content)).setOnItemClickListener {
            listClick(itemList[it])
            dialog.dismiss()
        }
        return dialog
    }

    /**
     * 打开列表dialog
     * @param context
     * @param title 标题
     * @param itemList 列表内容
     * @param btnBuidler 按钮构造器
     */
    fun showCheckList(context: Context, title: String = "", itemList: List<ListBean>, btnBuidler: BtnBuilder? = null): AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout._layout_dialog_list, null)
        val titleView = view.findViewById<TextView>(R.id.tv_list_title)
        if (title.isEmpty()) {
            titleView.visibility = View.GONE
        } else {
            titleView.text = title
        }
        view.findViewById<RecyclerView>(R.id.rv_list_content).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = DialogListAdapter(itemList, true)
        }
        return initDialog(context, view, btnBuidler = if (btnBuidler == null) BtnBuilder().withSubmitBtn() else btnBuidler)
    }

    /**
     * 打开自定义界面dialog
     * @param context
     * @param title 标题
     * @param view 自定义界面
     * @param btnBuidler 按钮构造器
     */
    fun showCustom(context: Context, title: String = "", view: View, btnBuidler: BtnBuilder? = null): AlertDialog {
        val contentView = LayoutInflater.from(context).inflate(R.layout._layout_dialog_custom, null)
        val titleView = contentView.findViewById<TextView>(R.id.tv_custom_title)
        if (title.isEmpty()) {
            titleView.visibility = View.GONE
        } else {
            titleView.text = title
        }
        contentView.findViewById<LinearLayout>(R.id.ll_custom_content).addView(view)
        return initDialog(context, contentView, btnBuidler = if (btnBuidler == null) BtnBuilder().withCancelBtn() else btnBuidler)
    }

    /**
     * 关闭加载dialog
     */
    fun dismissLoading() {
        handler.post {
            if (null != loadingDialog && loadingDialog!!.isShowing()) {
                try {
                    loadingDialog!!.dismiss()
                    loadingDialog = null
                    loadingContext = null
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    /**
     * 判断加载dialog是否为打开状态
     *
     * @return
     */
    fun isLoadingShow(): Boolean {
        return if (loadingDialog != null) {
            loadingDialog!!.isShowing()
        } else {
            false
        }
    }

    /**
     * 初始化dialog，填入容器
     */
    private fun initDialog(context: Context, contentView: View, btnBuidler: BtnBuilder? = null): AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout._layout_dialog_frame, null)
        val llFrame = view.findViewById<LinearLayout>(R.id.ll_dialog_frame)
        llFrame.addView(contentView, 0)
        val llBtnBar = view.findViewById<LinearLayout>(R.id.ll_dialog_btnBar)
        val dialog = AlertDialog.Builder(context, R.style.DialogStyle).setView(view).create()
        if (btnBuidler != null) {
            llBtnBar.visibility = View.VISIBLE
            btnBuidler.bindDialog(dialog, context, llBtnBar)
        }
        dialog!!.show()
//        dialog.window.setWindowAnimations(R.style.DialogAnim)
        return dialog
    }

    /**
     * 按钮构造器
     */
    class BtnBuilder {
        /**
         * 内部使用方法
         */
        internal fun bindDialog(dialog: AlertDialog, context: Context, llBtnBar: LinearLayout) {
            btnInfoList.forEachIndexed { index, bean ->
                llBtnBar.addView(TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                        weight = 1f
                    }
                    setPadding(10, 0, 10, 0)
                    gravity = Gravity.CENTER
                    text = bean.text
                    setSingleLine(true)
                    maxEms = 8
                    setTextColor(ContextCompat.getColor(context, if (bean.color == 0) R.color.dialog_button_submit else bean.color!!))
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.text_normal_size))
                    setOnClickListener {
                        bean.listener(dialog)
                        if (bean.autoDismiss) {
                            dialog.dismiss()
                        }
                    }
                })
                if (btnInfoList.size > 1 && index < btnInfoList.size - 1) {
                    llBtnBar.addView(View(context).apply {
                        layoutParams = LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                            topMargin = 5
                            bottomMargin = 5
                        }
                        setBackgroundColor(ContextCompat.getColor(context, R.color.bui_gray))
                    })
                }
            }
        }

        internal var btnInfoList = arrayListOf<BtnInfoBean>()

        data class BtnInfoBean(var text: String, var color: Int? = 0, var listener: (AlertDialog) -> Unit = {}, var autoDismiss: Boolean = true)

        /**
         * 添加一个普通的自定义按钮
         * @param text 按钮文本
         * @param color 按钮颜色
         * @param autoDismiss 是否点击后自动关闭dialog，默认开启
         * @param click 按钮点击事件
         */
        fun withBtn(text: String, @ColorRes color: Int? = 0, autoDismiss: Boolean = true, click: (AlertDialog) -> Unit = {}): BtnBuilder {
            btnInfoList.add(BtnInfoBean(text, color, click, autoDismiss))
            return this
        }

        /**
         * 添加一个普通的确认按钮
         * @param autoDismiss 是否点击后自动关闭dialog，默认开启
         * @param click 按钮点击事件
         */
        fun withSubmitBtn(autoDismiss: Boolean = true, click: (AlertDialog) -> Unit = {}): BtnBuilder {
            btnInfoList.add(BtnInfoBean("确定", R.color.dialog_button_submit, click, autoDismiss))
            return this
        }

        /**
         * 添加一个普通的关闭按钮
         * @param autoDismiss 是否点击后自动关闭dialog，默认开启
         * @param click 按钮点击事件
         */
        fun withCancelBtn(autoDismiss: Boolean = true, click: (AlertDialog) -> Unit = {}): BtnBuilder {
            btnInfoList.add(BtnInfoBean("取消", R.color.dialog_button_cancel, click, autoDismiss))
            return this
        }
    }

    data class ListBean(var key: String, var value: String = "", var checked: Boolean = false)

    /**
     * 判断是否处于主线程
     */
    private fun isMainThread(): Boolean {
        return Looper.getMainLooper() == Looper.myLooper()
    }

    /**
     * 打开新的加载dialog
     */
    private fun showNewLoading(context: Context, message: String, progress: Int) {
        if (!isMainThread()) {
            Looper.loop()
        }
        loadingContext = context
        if (loadingDialog != null) {
            if (loadingDialog!!.isShowing()) {
                loadingDialog!!.dismiss()
            }
            loadingDialog = null
        }
        val dialogView = LayoutInflater.from(context).inflate(R.layout._layout_dialog_loading, null)
        val davLoading = dialogView.findViewById<DialogAnimView>(R.id.dav_loading)
        val tvText = dialogView.findViewById<TextView>(R.id.tv_loading_text)
        if (progress > -1) {
            davLoading.showLoadingNum(progress)
        }
        tvText.text = message
        loadingDialog = initDialog(context, dialogView)
        loadingDialog!!.setCanceledOnTouchOutside(false)
        loadingDialog!!.setCancelable(true)
        if (!isMainThread()) {
            Looper.prepare()
        }
    }

    private fun sendMessage(message: String, progress: Int) {
        val msg = Message()
        if (progress == -1) {
            msg.what = 0
        } else {
            msg.what = 1
        }
        msg.arg1 = progress
        msg.obj = message
        loadingHandle.sendMessage(msg)
    }

    private val loadingHandle = @SuppressLint("HandlerLeak") object : Handler() {
        override fun handleMessage(msg: Message) {
            val davLoading = loadingDialog?.findViewById<DialogAnimView>(R.id.dav_loading)
            val tvText = loadingDialog?.findViewById<TextView>(R.id.tv_loading_text)
            davLoading?.showLoadingNum(msg.arg1)
            tvText?.text = msg.obj.toString()
            super.handleMessage(msg)
        }
    }

}