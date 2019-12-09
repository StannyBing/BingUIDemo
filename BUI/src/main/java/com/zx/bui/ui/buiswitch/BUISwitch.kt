package com.zx.bui.ui.buiswitch

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.zx.bui.BUIConfig
import com.zx.bui.R
import com.zx.bui.util.BUITool

/**
 * Created by Xiangb on 2019/12/5.
 * 功能：
 */
@SuppressLint("NewApi")
class BUISwitch @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : View(context, attributeSet, defStyle) {

    private val bgPaint = Paint()//背景画笔
    private val ballPaint = Paint()//小球画笔

    private var bgPadding = BUITool.dp2px(context, 5f).toFloat()//背景间隔
    private var ballRadius: Float//小球半径
    private var bgWidth: Float//背景宽度
    private var ballPadding: Float//小球间隔
    private var bgHeight: Float//背景高度

    @ColorInt
    var checkedColor: Int
    @ColorInt
    var unCheckedColor: Int

    @ColorInt
    private var animCheckedColor: Int = 0
        set(value) {
            ballPaint.color = field
            field = value
            invalidate()
        }
    @ColorInt
    private var animUnCheckedColor: Int = 0
        set(value) {
            field = value
            invalidate()
            bgPaint.color = field
        }

    private var checkedChange: (Boolean) -> Unit = {}//切换监听

    private var isChecked = false//是否选中
    private var moveProgress = 1.0f
        set(value) {
            field = value
            invalidate()
        }

    init {
        val typeArray = context.obtainStyledAttributes(attributeSet, R.styleable.BUISwitch)
        isChecked = typeArray.getBoolean(R.styleable.BUISwitch_isChecked, false)
        checkedColor = typeArray.getColor(R.styleable.BUISwitch_color_checked, BUIConfig.uiColor)
        bgHeight = typeArray.getDimension(R.styleable.BUISwitch_switch_height, BUITool.dp2px(context, 24f).toFloat())
        bgWidth = typeArray.getDimension(R.styleable.BUISwitch_switch_width, BUITool.dp2px(context, 43f).toFloat())
        ballPadding = typeArray.getDimension(R.styleable.BUISwitch_ball_padding, BUITool.dp2px(context, 2f).toFloat())

        ballRadius = bgHeight / 2 - ballPadding
        setWillNotDraw(false)

        unCheckedColor = ContextCompat.getColor(context, R.color.bui_light_gray)

        animCheckedColor = if (isChecked) checkedColor else Color.WHITE
        animUnCheckedColor = if (isChecked) BUITool.getLighterColor(checkedColor, 0.4f) else unCheckedColor

        bgPaint.apply {
            color = animUnCheckedColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        ballPaint.apply {
            color = animCheckedColor
            style = Paint.Style.FILL
            isAntiAlias = true
            setShadowLayer(5f, 0f, 3f, BUITool.getDarkerColor(unCheckedColor))
        }
        typeArray.recycle()
        setOnClickListener {
            setChecked(!isChecked)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        //绘制背景
        canvas?.save()
        canvas?.clipRect(bgPadding, height / 2 - bgHeight / 2, bgPadding + bgWidth, height / 2 + bgHeight / 2)
        canvas?.drawCircle(bgPadding + bgHeight / 2, height / 2f, bgHeight / 2f, bgPaint)
        canvas?.drawRect(bgPadding + bgHeight / 2, height / 2 - bgHeight / 2, bgPadding + bgWidth - bgHeight / 2, height / 2 + bgHeight / 2, bgPaint)
        canvas?.drawCircle(bgPadding + bgWidth - bgHeight / 2, height / 2f, bgHeight / 2f, bgPaint)
        canvas?.restore()
        //绘制小球
        canvas?.drawCircle(bgPadding + bgHeight / 2 + (bgWidth - bgHeight) * if (isChecked) {
            moveProgress
        } else {
            1 - moveProgress
        }, height / 2f, ballRadius, ballPaint)
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = (bgWidth + bgPadding * 2).toInt()
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = ((bgHeight + bgPadding * 2).toInt())
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(widthSize, widthMode), MeasureSpec.makeMeasureSpec(heightSize, heightMode))
    }

    /**
     * 返回当前选中状态
     */
    fun isChecked(): Boolean {
        return isChecked
    }

    /**
     * 设置选中状态
     */
    fun setChecked(isChecked: Boolean, anim: Boolean = true) {
        this.isChecked = isChecked
        checkedChange(isChecked)
        if (anim) {
            startAnim()
        } else {
            invalidate()
        }
    }

    /**
     * 切换监听
     */
    fun setCheckedChangeListener(checkedChange: (Boolean) -> Unit) {
        this.checkedChange = checkedChange
    }

    private fun startAnim() {
        val animorSet = AnimatorSet()
        val moveAnimator = ObjectAnimator.ofFloat(this, "moveProgress", 0.0f, 1.0f)
        moveAnimator.interpolator = AnticipateOvershootInterpolator()

        val ballColorAnimator = if (isChecked) {
            ObjectAnimator.ofInt(this, "animCheckedColor", Color.WHITE, checkedColor)
        } else {
            ObjectAnimator.ofInt(this, "animCheckedColor", checkedColor, Color.WHITE)
        }
        ballColorAnimator.interpolator = LinearInterpolator()
        ballColorAnimator.setEvaluator(ArgbEvaluator())
        val bgColorAnimator = if (isChecked) {
            ObjectAnimator.ofInt(this, "animUnCheckedColor", unCheckedColor, BUITool.getLighterColor(checkedColor, 0.4f))
        } else {
            ObjectAnimator.ofInt(this, "animUnCheckedColor", BUITool.getLighterColor(checkedColor, 0.4f), unCheckedColor)
        }
        bgColorAnimator.interpolator = LinearInterpolator()
        bgColorAnimator.setEvaluator(ArgbEvaluator())
        animorSet.playTogether(moveAnimator, ballColorAnimator, bgColorAnimator)
        animorSet.duration = 300
        animorSet.start()
    }

}