package com.zx.bui.ui.buidialog

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.zx.bui.R

/**
 * Created by Xiangb on 2019/11/11.
 * 功能：
 */
class DialogAnimView @JvmOverloads constructor(context: Context, attributes: AttributeSet? = null, defStyle: Int = 0) :
        LinearLayout(context, attributes, defStyle) {

    private val pointPaint = Paint()
    private val ringPaint = Paint()
    private val textPaint = Paint()
    private val ringWidth = 10f
    private val ringRadius = 50f
    private val ringArc = 150f
    private val pointNum = 18

    private var isPlay = false
    private var isAddPoint = true

    private var loadingNum = -1

    private var animProgress = 0f
        set(value) {
            field = value
            invalidate()
        }

    init {
        setWillNotDraw(false)
        pointPaint.color = ContextCompat.getColor(context, R.color.bui_gray)
        pointPaint.isAntiAlias = true
        pointPaint.style = Paint.Style.FILL
        pointPaint.strokeWidth = ringWidth / 2

        ringPaint.color = ContextCompat.getColor(context, R.color.bui_gray)
        ringPaint.strokeCap = Paint.Cap.ROUND
        ringPaint.style = Paint.Style.STROKE
        ringPaint.isAntiAlias = true
        ringPaint.strokeWidth = ringWidth

        textPaint.color = ContextCompat.getColor(context, R.color.bui_gray)
        textPaint.textSize = resources.getDimension(R.dimen.text_smaller_size)
        textPaint.isAntiAlias = true
        textPaint.strokeWidth = 1f
        textPaint.style = Paint.Style.FILL

        viewTreeObserver.addOnDrawListener {
            if (!isPlay) {
                playAnim()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(120, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(120, MeasureSpec.EXACTLY)
        )
    }

    @SuppressLint("NewApi")
    override fun onDraw(canvas: Canvas?) {
        //计算角度
        val startAngle = if (animProgress < ringArc / 360f) {
            -90f
        } else {
            (animProgress - ringArc / 360) / (1 - ringArc / 360) * 360 - 90f
        }
        val sweepAngle = if (animProgress < ringArc / 360f) {
            animProgress / (ringArc / 360) * ringArc
        } else if (animProgress > 1 - ringArc / 360f) {
            ringArc - (animProgress - (1 - ringArc / 360f)) / (ringArc / 360f) * ringArc
        } else {
            ringArc
        }
        //绘制圆点
        for (i in 0..pointNum) {
            var pointX: Float? = null
            var pointY: Float? = null
            if (isAddPoint) {
                if ((i / pointNum.toFloat()) * 360f - 90 <= startAngle) {
                    pointX = width / 2 + Math.cos(Math.PI * ((i / pointNum.toFloat()) * 360 - 90) / 180.toDouble()).toFloat() * ringRadius
                    pointY = height / 2 + Math.sin(Math.PI * ((i / pointNum.toFloat()) * 360 - 90) / 180.toDouble()).toFloat() * ringRadius
                }
            } else {
                if ((i / pointNum.toFloat()) * 360f - 90 > startAngle + sweepAngle) {
                    pointX = width / 2 + Math.cos(Math.PI * ((i / pointNum.toFloat()) * 360 - 90) / 180.toDouble()).toFloat() * ringRadius
                    pointY = height / 2 + Math.sin(Math.PI * ((i / pointNum.toFloat()) * 360 - 90) / 180.toDouble()).toFloat() * ringRadius
                }
            }
            if (pointX != null && pointY != null) {
                canvas?.drawCircle(pointX, pointY, ringWidth / 2, pointPaint)
            }
        }
        //绘制圆环
        canvas?.drawArc(width / 2 - ringRadius, height / 2 - ringRadius, width / 2 + ringRadius, height / 2 + ringRadius, startAngle, sweepAngle, false, ringPaint
        )
        //绘制加载数字
        if (loadingNum > -1) {
            val numRect = Rect()
            textPaint.getTextBounds("$loadingNum%", 0, "$loadingNum%".length, numRect)
            canvas?.drawText("$loadingNum%", width / 2 - numRect.width() / 2f, height / 2 + numRect.height() / 2f - 2f, textPaint
            )
        }
    }

    /**
     * 设置加载数字
     */
    fun showLoadingNum(loadingNum: Int) {
        this.loadingNum = loadingNum
    }

    private fun playAnim() {
        isPlay = true
        val anim = ObjectAnimator.ofFloat(this@DialogAnimView, "animProgress", 0f, 1f)
        anim.interpolator = FastOutSlowInInterpolator() as TimeInterpolator?
        anim.duration = 1500
        anim.repeatCount = ObjectAnimator.INFINITE
        anim.repeatMode = ObjectAnimator.RESTART
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
                isAddPoint = !isAddPoint
            }

            override fun onAnimationEnd(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        anim.start()
    }

}
