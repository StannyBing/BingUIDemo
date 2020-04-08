package com.zx.bui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.zx.bui.R

/**
 * Created by Xiangb on 2019/11/14.
 * 功能：
 */
class MaxHeightLinearLayout @JvmOverloads constructor(context: Context, attributeSet: AttributeSet, defStyle: Int = 0) :
    LinearLayout(context, attributeSet, defStyle) {

    private var maxHeight: Int

    init {
        val typeArray = context.obtainStyledAttributes(attributeSet, R.styleable.MaxHeightLinearLayout)
        maxHeight = typeArray.getLayoutDimension(R.styleable.MaxHeightLinearLayout_maxHeight, 0)
        typeArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (maxHeight > 0) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST))
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

}