package com.zx.buidemo.other

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zx.buidemo.R
import com.zx.buidemo.bean.KeyValueBean
import com.zx.zxutils.other.ZXItemClickSupport
import com.zx.zxutils.util.ZXTimeUtil
import java.util.*

/**
 * Created by Xiangb on 2018/1/22.
 * 功能：
 */

class BtnBarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    private val datas = ArrayList<String>()
    private val printInfos = ArrayList<KeyValueBean>()
    private val rvBtnbar: RecyclerView
    private val rvPrint: RecyclerView
    private val llPrintInfo: LinearLayout
    private val mAdapter: BtnBarAdapter
    private val mPrintAdapter: PrintInfoAdapter
    private var onItemClickListener: OnItemClickListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_normal_btnbarlist, this, true)
        rvBtnbar = findViewById(R.id.rv_btnbar)
        rvPrint = findViewById(R.id.rv_printInfo)
        llPrintInfo = findViewById(R.id.ll_print_info)
        rvPrint.layoutManager = LinearLayoutManager(context)
        rvBtnbar.layoutManager = LinearLayoutManager(context)
        mAdapter = BtnBarAdapter(datas)
        rvBtnbar.adapter = mAdapter

        ZXItemClickSupport.addTo(rvBtnbar)
            .setOnItemClickListener(object : ZXItemClickSupport.OnItemClickListener{
                override fun onItemClicked(recyclerView: RecyclerView, position: Int, view: View) {
                    if (onItemClickListener != null) {
                        onItemClickListener!!.onItemClick(position)
                    }
                }
            })

        mPrintAdapter = PrintInfoAdapter(printInfos)
        rvPrint.adapter = mPrintAdapter
    }

    fun addBtn(name: String): BtnBarView {
        datas.add(name)
        return this
    }

    /**
     * 打印信息
     *
     * @param printInfo
     */
    fun printInfo(printInfo: String) {
        llPrintInfo.visibility = View.VISIBLE
        printInfos.add(KeyValueBean(printInfo, ZXTimeUtil.getCurrentTime()))
        mPrintAdapter.notifyItemInserted(printInfos.size - 1)
        rvPrint.scrollToPosition(printInfos.size - 1)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener): BtnBarView {
        this.onItemClickListener = onItemClickListener
        return this
    }

    fun build() {
        mAdapter.notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    internal inner class BtnBarAdapter(private val dataList: List<String>) :
        RecyclerView.Adapter<BtnBarAdapter.BtnBarHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BtnBarHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_btn_bar, null, false)
            return BtnBarHolder(view)
        }

        override fun onBindViewHolder(holder: BtnBarHolder, position: Int) {
            holder.tvBtnBar.text = dataList[position]
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        internal inner class BtnBarHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val tvBtnBar: TextView

            init {
                tvBtnBar = itemView.findViewById(R.id.tv_btn_bar)
            }
        }
    }

    internal inner class PrintInfoAdapter(private val printInfos: List<KeyValueBean>) :
        RecyclerView.Adapter<PrintInfoAdapter.PrintInfoHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrintInfoHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_print_info, null, false)
            return PrintInfoHolder(view)
        }

        override fun onBindViewHolder(holder: PrintInfoHolder, position: Int) {
            holder.tvInfo.setText(printInfos[position].key)
            holder.tvDate.setText(printInfos[position].value)
        }

        override fun getItemCount(): Int {
            return printInfos.size
        }

        internal inner class PrintInfoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

           val tvInfo: TextView
           val tvDate: TextView

            init {
                tvInfo = itemView.findViewById(R.id.tv_print_info)
                tvDate = itemView.findViewById(R.id.tv_print_date)
            }
        }
    }
}
