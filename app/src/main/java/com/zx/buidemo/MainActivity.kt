package com.zx.buidemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.zx.buidemo.adapter.MainAdapter
import com.zx.buidemo.bean.MainBean
import com.zx.buidemo.ui.BUICheckBoxActivity
import com.zx.buidemo.ui.BUIDialogActivity
import com.zx.buidemo.ui.BUISwitchActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val dataList = ArrayList<MainBean>()
    private var mainAdapter = MainAdapter(dataList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv_main.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 3)
            adapter = mainAdapter
        }

        initData()
    }

    private fun initData() {
        dataList.add(MainBean(BUIDialogActivity::class.java, "BUIDialog", "弹窗", R.mipmap.ic_launcher))
//        dataList.add(MainBean(BUIButtonActivity::class.java, "BUIButton", "按钮", R.mipmap.ic_launcher))
        dataList.add(MainBean(BUICheckBoxActivity::class.java, "BUICheckBox", "勾选", R.mipmap.ic_launcher))
        dataList.add(MainBean(BUISwitchActivity::class.java, "BUISwitch", "开关", R.mipmap.ic_launcher))
        mainAdapter.notifyDataSetChanged()
    }
}
