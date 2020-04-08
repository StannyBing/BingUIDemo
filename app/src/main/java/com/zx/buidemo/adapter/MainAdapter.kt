package com.zx.buidemo.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zx.buidemo.R
import com.zx.buidemo.bean.MainBean

/**
 * Created by Xiangb on 2019/11/8.
 * 功能：
 */
class MainAdapter(var dataList: List<MainBean>) : RecyclerView.Adapter<MainAdapter.MainHolder>() {

    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MainHolder {
        this.context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_main, null, false)
        return MainHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.tvToolName.setText(dataList[position].uiName)
        holder.tvClassName.setText(dataList[position].className)
        if (dataList[position].resId !== 0) {
            holder.ivImg.setBackground(ContextCompat.getDrawable(context!!, dataList[position].resId))
        }
    }

    inner class MainHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvClassName: TextView
        var tvToolName: TextView
        var ivImg: ImageView

        init {
            ivImg = itemView.findViewById(R.id.main_item_img)
            tvClassName = itemView.findViewById(R.id.main_item_className)
            tvToolName = itemView.findViewById(R.id.main_item_toolName)
            itemView.setOnClickListener {
                context?.startActivity(
                    Intent(
                        context,
                        dataList[adapterPosition].classFile
                    )
                )
            }
        }
    }

}