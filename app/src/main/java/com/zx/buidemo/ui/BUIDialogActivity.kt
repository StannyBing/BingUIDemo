package com.zx.buidemo.ui

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import com.zx.bui.ui.buidialog.BUIDialog
import com.zx.bui.util.BUITool
import com.zx.buidemo.R
import com.zx.buidemo.other.BtnBarView
import com.zx.zxutils.util.ZXToastUtil
import kotlinx.android.synthetic.main.activity_bing_dialog.*
import java.util.*

class BUIDialogActivity : AppCompatActivity() {

    private var num = 0
    private var mHandler = mHandle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bing_dialog)
        btnbar_view.addBtn("普通dialog")
                .addBtn("进度dialog")
                .addBtn("提示dialog-普通")
                .addBtn("提示dialog-多文本+自定义")
                .addBtn("列表dialog-单选（无checkbox）")
                .addBtn("列表dialog-多选（有checkbox）")
                .addBtn("自定义dialog")
                .setItemClickListener(object : BtnBarView.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        when (position) {
                            0 -> {
                                BUIDialog.showLoading(this@BUIDialogActivity, "测试")
                            }
                            1 -> {
                                num = 0
                                Timer().schedule(object : TimerTask() {
                                    override fun run() {
                                        if (num == 100) {
                                            cancel()
                                        }
                                        mHandler.sendEmptyMessage(0)
                                    }
                                }, 200, 50)
                            }
                            2 -> {
                                BUIDialog.showInfo(this@BUIDialogActivity, "提示", "这里是测试提示，按钮将自定义添加，文本过长将允许滑动查看。")
                            }
                            3 -> {
                                val message = "这里是测试提示，按钮将自定义添加，文本过长将允许滑动查看。这里是测试提示，按钮将自定义添加，文本过长将允许滑动查看。这里是测试提示，按钮将自定义添加，文本过长将允许滑动查看。这里是测试提示，按钮将自定义添加，文本过长将允许滑动查看。这里是测试提示，按钮将自定义添加，文本过长将允许滑动查看。"
                                BUIDialog.showInfo(this@BUIDialogActivity, "提示", message, BUIDialog.BtnBuilder().withCancelBtn().withSubmitBtn {
                                    ZXToastUtil.showToast("这里是测试提示，按钮将自定义添加")
                                }.withBtn("测试", R.color.colorPrimary, false) {
                                    ZXToastUtil.showToast("关闭了自动关闭dialog的功能后，需要手动关闭")
                                })
                            }
                            4 -> {
                                val checkList = arrayListOf<BUIDialog.ListBean>().apply {
                                    for (i in 0..10) {
                                        add(BUIDialog.ListBean("第${i}条选择栏"))
                                    }
                                }
                                BUIDialog.showSimpleList(this@BUIDialogActivity, "提示", checkList, {
                                    ZXToastUtil.showToast(it.toString())
                                })
                            }
                            5 -> {
                                val checkList = arrayListOf<BUIDialog.ListBean>().apply {
                                    for (i in 0..10) {
                                        add(BUIDialog.ListBean("第${i}条选择栏", checked = i % 3 == 0))
                                    }
                                }
                                BUIDialog.showCheckList(this@BUIDialogActivity, "提示", checkList,
                                        BUIDialog.BtnBuilder()
                                                .withCancelBtn()
                                                .withSubmitBtn {
                                                    var info = "当前选中"
                                                    checkList.forEach {
                                                        if (it.checked) {
                                                            info += "${it.key}\n"
                                                        }
                                                    }
                                                    ZXToastUtil.showToast(info)
                                                })
                            }
                            6 -> {
                                val customView = View(this@BUIDialogActivity).apply {
                                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, BUITool.dp2px(context, 200f))
                                    setBackgroundColor(ContextCompat.getColor(this@BUIDialogActivity, R.color.colorAccent))
                                }
                                BUIDialog.showCustom(this@BUIDialogActivity, "自定义view弹窗", view = customView)
                            }
                        }
                    }
                }).build()
    }

    private inner class mHandle : Handler() {
        override fun handleMessage(msg: Message?) {
            if (num < 100) {
                num++
                BUIDialog.showLoading(this@BUIDialogActivity, "下载中...", num)
            } else {
                BUIDialog.dismissLoading()
            }
            super.handleMessage(msg)
        }
    }

}
