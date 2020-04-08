package com.zx.bui.ui.buidialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zx.bui.R
import com.zx.bui.ui.buicheckbox.BUICheckBox

/**
 * Created by Xiangb on 2019/11/8.
 * 功能：
 */
class DialogListAdapter(var dataList: List<BUIDialog.ListBean>, var multiCheck: Boolean) : RecyclerView.Adapter<DialogListAdapter.ListHolder>() {

    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ListHolder {
        this.context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout._item_dialog_list, null, false)
        return ListHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        holder.tvListInfo.text = dataList[position].key
        holder.viewDivider.visibility = if (holder.adapterPosition > 0) {
            View.VISIBLE
        } else {
            View.GONE
        }
        if (multiCheck) {
            holder.checkBox.visibility = View.VISIBLE
        } else {
            holder.checkBox.visibility = View.GONE
        }
        holder.checkBox.setChecked(dataList[position].checked, false)
    }

    inner class ListHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvListInfo: TextView
        var viewDivider: View
        var checkBox: BUICheckBox

        init {
            tvListInfo = itemView.findViewById(R.id.tv_list_info)
            viewDivider = itemView.findViewById(R.id.view_list_divider)
            checkBox = itemView.findViewById(R.id.cb_list_check)
            checkBox.setCheckedChangeListener {
                dataList[adapterPosition].checked = it
            }
            if (multiCheck) {
                itemView.setOnClickListener {
                    checkBox.setChecked(!checkBox.isChecked())
                }
            }
        }
    }

}