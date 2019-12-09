package com.zx.bui.util

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.OnChildAttachStateChangeListener
import android.view.View

class ItemClickHelper private constructor(private val recyclerView: RecyclerView) {
    private var onItemClick: (Int) -> Unit = {}
    private var onItemLongClick: (Int) -> Unit = {}

    private val attachListener = object : OnChildAttachStateChangeListener {
        override fun onChildViewAttachedToWindow(view: View) {
            view.setOnClickListener {
                val holder = this@ItemClickHelper.recyclerView.getChildViewHolder(view)
                onItemClick(holder.adapterPosition)
            }
            view.setOnLongClickListener {
                val holder = this@ItemClickHelper.recyclerView.getChildViewHolder(view)
                onItemLongClick(holder.adapterPosition)
                true
            }
        }

        override fun onChildViewDetachedFromWindow(view: View) {}
    }

    init {
        this.recyclerView.tag = recyclerView.id
        this.recyclerView.addOnChildAttachStateChangeListener(this.attachListener)
    }

    fun setOnItemClickListener(click: (Int) -> Unit): ItemClickHelper {
        this.onItemClick = click
        return this
    }

    fun setOnItemLongClickListener(click: (Int) -> Unit): ItemClickHelper {
        this.onItemLongClick = click
        return this
    }

    private fun detach(view: RecyclerView) {
        view.removeOnChildAttachStateChangeListener(this.attachListener)
        view.tag = view.id
    }

    companion object {

        fun addTo(view: RecyclerView): ItemClickHelper {
            if (view.tag != null && view.tag is ItemClickHelper) {
                return view.getTag(view.id) as ItemClickHelper
            } else {
                return ItemClickHelper(view)
            }
        }

        fun removeFrom(view: RecyclerView): ItemClickHelper? {
            if (view.tag != null && view.tag is ItemClickHelper) {
                return (view.getTag(view.id) as ItemClickHelper).apply {
                    detach(view)
                }
            } else {
                return ItemClickHelper(view).apply {
                    detach(view)
                }
            }
        }
    }
}
