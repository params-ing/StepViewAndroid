package jugnoo.com.learningcustomvviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View


/**
 * Created by Parminder Saini on 12/06/18.
 */
class StatusView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /*
        todo
                add Gravity params i.e if width is wrap content and height is 20dp if gravity is center draw from center, left right etc
               1.Account for stroke ratio
               1. Account for StokTextSize and drawable Ratio 2. Add labels 3.dp to px things
               3. Pass font to textView
               5.Labels
               6.Line gaps
               6. Add drawable instead of text
               2. Orientation draw vertical too
               PUSH TO ORIGIN

     */
    companion object {
        const val CIRCLE_COLOR_TYPE_FILL = 1
        const val CIRCLE_COLOR_TYPE_STROKE = 2
        const val INVALID_STATUS_COUNT = -1
    }

    private var circleStrokePaint: Paint? = null
    private var circleFillPaint: Paint? = null
    private lateinit var textPaint: Paint
    private lateinit var linePaint: Paint


    private var circleStrokePaintIncomplete: Paint? = null
    private var circleFillPaintIncomplete: Paint? = null
    private lateinit var textPaintIncomplete: Paint
    private lateinit var linePaintIncomplete: Paint


    //To store the data of each circle
    private data class Item(val textData: StatusItemText?, val circleItem: CircleItem, val lineItem: LineItem?)

    private data class StatusItemText(val text: String? = null, val paint: Paint? = null, val x: Float = 0.0f, val y: Float = 0.0f, val drawableItem: DrawableItem? = null)
    private data class CircleItem(val center: PointF, val radius: Float, val strokePaint: Paint?, val fillPaint: Paint?)
    private data class LineItem(val start: PointF, val end: PointF, val paint: Paint)
    private data class DrawableItem(val rect: Rect, val drawable: Drawable)


    private var statusCount: Int = 4
    private var completeCount: Int = INVALID_STATUS_COUNT
    private var circleRadius: Float = 50.0f
    private var lineLength: Float = 50.0f
    private var mStrokeWidth: Float = 2.0f
    private var mLineWidth: Float = 2.0f
    private var lineColor: Int = ContextCompat.getColor(context, R.color.colorAccent)
    private var circleFillColor: Int = ContextCompat.getColor(context, android.R.color.transparent)
    private var circleStrokeColor: Int = ContextCompat.getColor(context, R.color.colorAccent)
    private var textColor: Int = ContextCompat.getColor(context, R.color.colorAccent)
    private var lineColorIncomplete: Int
    private var circleFillColorIncomplete: Int
    private var circleStrokeColorIncomplete: Int
    private var textColorIncomplete: Int
    private var textSize: Float = 20.0f
    private var mDrawCountText: Boolean = true
    private var circleColorType = CIRCLE_COLOR_TYPE_FILL
    private var completeDrawable: Drawable? = null
    private var inCompleteDrawable: Drawable? = null
    private var lineGap = 0.0f


    private val lastPoint = PointF()
    private var lineRatio = 0.0f
    private var statusData = mutableListOf<Item>()


    init {
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.StatusView,
                0, 0)

        try {

            statusCount = a.getInt(R.styleable.StatusView_statusCount, statusCount)
            completeCount = a.getInt(R.styleable.StatusView_completeCount, INVALID_STATUS_COUNT)

            circleRadius = a.getDimension(R.styleable.StatusView_circleRadius, circleRadius)
            lineLength = a.getDimension(R.styleable.StatusView_lineLength, lineLength)
            lineColor = a.getColor(R.styleable.StatusView_lineColor, lineColor)
            circleFillColor = a.getColor(R.styleable.StatusView_circleColor, circleFillColor)
            circleStrokeColor = a.getColor(R.styleable.StatusView_circleStrokeColor, circleStrokeColor)

            textColor = a.getColor(R.styleable.StatusView_textColor, textColor)
            textSize = a.getDimension(R.styleable.StatusView_textSize, textSize)
            mStrokeWidth = a.getDimension(R.styleable.StatusView_circleStrokeWidth, mStrokeWidth)
            mLineWidth = a.getDimension(R.styleable.StatusView_lineWidth, mLineWidth)
            circleColorType = a.getInteger(R.styleable.StatusView_circleColorType, circleColorType)


            textColorIncomplete = a.getColor(R.styleable.StatusView_textColorIncomplete, textColor)
            lineColorIncomplete = a.getColor(R.styleable.StatusView_lineColorIncomplete, lineColor)
            circleFillColorIncomplete = a.getColor(R.styleable.StatusView_circleColorInComplete, circleFillColor)
            circleStrokeColorIncomplete = a.getColor(R.styleable.StatusView_circleStrokeColorIncomplete, circleStrokeColor)

            completeDrawable = a.getDrawable(R.styleable.StatusView_complete_drawable)
            inCompleteDrawable = a.getDrawable(R.styleable.StatusView_inccomplete_drawable)
            mDrawCountText = a.getBoolean(R.styleable.StatusView_drawCount, mDrawCountText)
            lineGap = a.getDimension(R.styleable.StatusView_lineGap, lineGap)

            if (statusCount < 0) statusCount = 4
            if (completeCount < INVALID_STATUS_COUNT) completeCount = INVALID_STATUS_COUNT


        } finally {
            a.recycle()
        }

        init()
    }


    private fun init() {

        if (containsFlag(circleColorType, CIRCLE_COLOR_TYPE_STROKE)) {
            circleStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            circleStrokePaint?.style = Paint.Style.STROKE
            circleStrokePaint?.strokeWidth = mStrokeWidth
            circleStrokePaint?.color = circleStrokeColor

            if (isShwoingIncompleteStatus()) {
                circleStrokePaintIncomplete = Paint(circleStrokePaint)
                circleStrokePaintIncomplete?.color = circleStrokeColorIncomplete
            }

        } else {
            mStrokeWidth = 0.0f
        }

        if (containsFlag(circleColorType, CIRCLE_COLOR_TYPE_FILL)) {
            circleFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            circleFillPaint?.style = Paint.Style.FILL
            circleFillPaint?.color = circleFillColor

            if (isShwoingIncompleteStatus()) {
                circleFillPaintIncomplete = Paint(circleFillPaint)
                circleFillPaintIncomplete?.color = circleFillColorIncomplete
            }
        }


        linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = mLineWidth
        linePaint.color = lineColor


        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.style = Paint.Style.FILL
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.strokeWidth = mStrokeWidth
        textPaint.color = textColor
        textPaint.textSize = textSize
        lineRatio =( lineLength + (lineGap * 2)) / circleRadius


        if (isShwoingIncompleteStatus()) {
            linePaintIncomplete = Paint(linePaint)
            linePaintIncomplete.color = lineColorIncomplete

            textPaintIncomplete = Paint(textPaint)
            textPaintIncomplete.color = textColorIncomplete
        }

    }

    private fun isShwoingIncompleteStatus() =
            completeCount != INVALID_STATUS_COUNT && completeCount < statusCount


    override fun getSuggestedMinimumWidth(): Int {
        return ((statusCount * (2 * (circleRadius))) + ((statusCount - 1) * ( lineLength + (lineGap * 2)))).toInt()
    }

    override fun getSuggestedMinimumHeight(): Int {
        return ((circleRadius) * 2).toInt()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = paddingLeft + paddingRight + suggestedMinimumWidth
        val desiredHeight = paddingTop + paddingBottom + suggestedMinimumHeight

        var measuredWidth = resolveSize(desiredWidth, widthMeasureSpec)
        var measuredHeight = resolveSize(desiredHeight, heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)

        val maxHorizontalRadius = ((measuredWidth-(paddingLeft+paddingRight)) / (((statusCount - 1) * lineRatio) + (2 * statusCount)))
        val maxVerticalRadius = (measuredHeight -(paddingTop+paddingBottom)) / 2.0f
        if (heightMode == MeasureSpec.AT_MOST) {
            measuredHeight = (maxHorizontalRadius * 2.0f).toInt() + paddingTop + paddingBottom

        }
        if (widthMode == MeasureSpec.AT_MOST && maxVerticalRadius < maxHorizontalRadius) {
            measuredWidth = ((((statusCount - 1) * lineRatio) + (2 * statusCount)) * maxVerticalRadius).toInt() + paddingLeft + paddingRight
        }

        setMeasuredDimension(measuredWidth, measuredHeight)


    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (item in statusData) {

            if (item.circleItem.fillPaint != null) {
                canvas?.drawCircle(item.circleItem.center.x, item.circleItem.center.y, item.circleItem.radius, item.circleItem.fillPaint)

            }
            if (item.circleItem.strokePaint != null) {
                canvas?.drawCircle(item.circleItem.center.x, item.circleItem.center.y, item.circleItem.radius, item.circleItem.strokePaint)

            }
            if (item.textData != null) {

                if (item.textData.drawableItem != null) {
                    val drawableItem: DrawableItem = item.textData.drawableItem
                    drawableItem.drawable.bounds = drawableItem.rect
                    item.textData.drawableItem.drawable.draw(canvas)

                } else if (item.textData.text != null && item.textData.paint != null) {

                    canvas?.drawText(item.textData.text, item.textData.x, item.textData.y, item.textData.paint)

                }

            }
            if (item.lineItem != null) {
                canvas?.drawLine(item.lineItem.start.x, item.lineItem.start.y,
                        item.lineItem.end.x, item.lineItem.end.y, item.lineItem.paint)
            }


        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setDrawingDimensions(w, h)
    }


    private fun setDrawingDimensions(w: Int, h: Int) {
        val actualWidth = w - (paddingLeft + paddingRight)
        val actualHeight = h - (paddingBottom + paddingTop)


        val maxHorizontalRadius = ((actualWidth) / (((statusCount - 1) * lineRatio) + (2 * statusCount)))
        val maxVerticalRadius = actualHeight.toFloat() / 2
        var circleRadius = Math.min(maxHorizontalRadius, maxVerticalRadius)
        val lineLength = (circleRadius * lineRatio)-(2 * lineGap)
        circleRadius -= (mStrokeWidth / 2)

        lastPoint.x = paddingLeft.toFloat() + (mStrokeWidth / 2)
        lastPoint.y = paddingTop.toFloat() + (circleRadius + (mStrokeWidth / 2))
        for (i in 0 until statusCount) {

            var circleStrokePaint = this.circleStrokePaint
            var circleFillPaint = this.circleFillPaint
            var textPaint = this.textPaint
            var linePaint = this.linePaint
            var itemDrawable: Drawable? = completeDrawable
            if (completeCount > -1 && i >= completeCount) {
                circleStrokePaint = circleStrokePaintIncomplete
                circleFillPaint = circleFillPaintIncomplete
                textPaint = textPaintIncomplete
                linePaint = linePaintIncomplete
                itemDrawable = inCompleteDrawable

            }


            var lineItem: StatusView.LineItem? = null
            var statusItemText: StatusView.StatusItemText? = null

            if (i != 0) {
                lastPoint.x += lineGap;
                lineItem = LineItem(PointF(lastPoint.x, lastPoint.y), PointF(lastPoint.x + lineLength, lastPoint.y), linePaint)
                lastPoint.x = lineItem.end.x + lineGap +  (mStrokeWidth / 2)
            }


            val circleItem = CircleItem(PointF((lastPoint.x + circleRadius), lastPoint.y), circleRadius, circleStrokePaint, circleFillPaint)
            lastPoint.x += ((circleRadius) * 2.0f) + (mStrokeWidth / 2)



            if (itemDrawable != null) {
                val width = itemDrawable.intrinsicWidth
                val height = itemDrawable.intrinsicHeight
                val xPos = circleItem.center.x.toInt()
                val yPos = circleItem.center.y.toInt()
                val drawableRect = Rect(xPos - width / 2, yPos - height / 2, xPos + width / 2, yPos + height / 2)
                statusItemText = StatusItemText(drawableItem = DrawableItem(drawableRect, itemDrawable))

            } else if (mDrawCountText) {
                val text: String = (i + 1).toString()
                val measuringRect = Rect()
                textPaint.getTextBounds(text, 0, text.length, measuringRect)
                statusItemText = StatusItemText(text, textPaint, circleItem.center.x, circleItem.center.y - measuringRect.exactCenterY())

            }



            statusData.add(Item(statusItemText, circleItem, lineItem))


        }


    }

    private fun containsFlag(flagSet: Int, flag: Int): Boolean {
        return flagSet or flag == flagSet
    }

}