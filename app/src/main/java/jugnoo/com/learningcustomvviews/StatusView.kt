package jugnoo.com.learningcustomvviews

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import java.lang.IllegalStateException


/**
 * Created by Parminder Saini on 12/06/18.
 */
class StatusView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

        /*
         TODO
       Status Labels margin from top or sides?
       Text Appearance?
       Renaming variables & Set radius programatically etc.g

       --- LOW PRIORITY ---
       Pass font to textView
       LTR Support
       Optimise Calculations
       Orientation draw vertical too
      */

    companion object {
        const val CIRCLE_COLOR_TYPE_FILL = 1
        const val CIRCLE_COLOR_TYPE_STROKE = 2
        const val INVALID_STATUS_COUNT = -1
    }

    /**
     * Circle stroke paints for incomplete & complete status
     */
    private var circleStrokePaint: Paint? = null
    private var circleStrokePaintIncomplete: Paint? = null

    private var circleFillPaint: Paint? = null
    private var circleFillPaintIncomplete: Paint? = null

    private var textPaint: TextPaint
    private lateinit var textPaintIncomplete: TextPaint

    private var linePaint: Paint
    private lateinit var linePaintIncomplete: Paint

    private lateinit var textPaintLabels: TextPaint


    private var statusCount: Int = 4
    private var completeCount: Int = INVALID_STATUS_COUNT
    private var circleRadius: Float = 25.0f //dp
    private var lineLength: Float = 50.0f //dp
    private var circleStrokeWidth: Float = 2.0f //dp
    private var mLineWidth: Float = 2.0f //dp
    private var lineGap = 0.0f
    private var labelTopMargin = 0.0f

    private var mDrawCountText: Boolean = true
    private var completeDrawable: Drawable? = null
    private var inCompleteDrawable: Drawable? = null


    private val lastPoint = PointF()
    private var drawingData = mutableListOf<Item>()
    private var statusData = mutableListOf<LabelInfo>()




    //To store the data of each circle
    private class Item(val textData: StatusItemText?, val circleItem: CircleItem, val lineItem: LineItem?,val labelData: LabelItemText?=null)

    private class StatusItemText(val text: String? = null, val paint: Paint? = null, val x: Float = 0.0f, val y: Float = 0.0f, val drawableItem: DrawableItem? = null)
    private class LabelItemText(val x: Float = 0.0f, val y: Float = 0.0f,val staticLayout: StaticLayout? = null)
    private class CircleItem(val center: PointF, val radius: Float, val strokePaint: Paint?, val fillPaint: Paint?)
    private class LineItem(val start: PointF, val end: PointF, val paint: Paint)
    private class DrawableItem(val rect: Rect, val drawable: Drawable)
    private data class LabelInfo(val text:String,var width:Float=0.0f,var height:Float=0.0f,var staticLayout: StaticLayout? = null)



    init {

        var lineColor: Int = Color.BLACK
        var circleFillColor: Int = Color.CYAN
        var circleStrokeColor: Int = Color.BLACK
        var textColor: Int = Color.BLACK

        val lineColorIncomplete: Int
        val circleFillColorIncomplete: Int
        val circleStrokeColorIncomplete: Int
        val textColorIncomplete: Int
        var textColorLabels:Int = Color.BLACK

        var textSize = 14.0f //sp
        var textSizeLabels = 15.0f //sp

        var circleColorType = CIRCLE_COLOR_TYPE_FILL

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.StatusView, 0, 0)

        try {

            statusCount = a.getInt(R.styleable.StatusView_statusCount, statusCount)
            completeCount = a.getInt(R.styleable.StatusView_completeCount, INVALID_STATUS_COUNT)

            circleRadius = a.getDimension(R.styleable.StatusView_circleRadius, circleRadius)
            lineLength = a.getDimension(R.styleable.StatusView_lineLength, lineLength)


            circleStrokeWidth = a.getDimension(R.styleable.StatusView_circleStrokeWidth, circleStrokeWidth)
            mLineWidth = a.getDimension(R.styleable.StatusView_lineWidth, mLineWidth)


            completeDrawable = a.getDrawable(R.styleable.StatusView_complete_drawable)
            inCompleteDrawable = a.getDrawable(R.styleable.StatusView_inccomplete_drawable)
            mDrawCountText = a.getBoolean(R.styleable.StatusView_drawCount, mDrawCountText)
            lineGap = a.getDimension(R.styleable.StatusView_lineGap, lineGap)
            labelTopMargin = a.getDimension(R.styleable.StatusView_labelTopMargin, labelTopMargin)


            lineColor = a.getColor(R.styleable.StatusView_lineColor, lineColor)
            circleFillColor = a.getColor(R.styleable.StatusView_circleColor, circleFillColor)
            circleStrokeColor = a.getColor(R.styleable.StatusView_circleStrokeColor, circleStrokeColor)
            textColor = a.getColor(R.styleable.StatusView_textColor, textColor)
            textColorLabels = a.getColor(R.styleable.StatusView_textColorLabels, textColorLabels)
            textSize = a.getDimension(R.styleable.StatusView_textSize, textSize)
            textSizeLabels = a.getDimension(R.styleable.StatusView_textSizeLabels, textSizeLabels)

            circleColorType = a.getInteger(R.styleable.StatusView_circleColorType, circleColorType)
            textColorIncomplete = a.getColor(R.styleable.StatusView_textColorIncomplete, textColor)
            lineColorIncomplete = a.getColor(R.styleable.StatusView_lineColorIncomplete, lineColor)
            circleFillColorIncomplete = a.getColor(R.styleable.StatusView_circleColorInComplete, circleFillColor)
            circleStrokeColorIncomplete = a.getColor(R.styleable.StatusView_circleStrokeColorIncomplete, circleStrokeColor)

            val entries = a.getTextArray(R.styleable.StatusView_android_entries)
            if (entries != null) {
                for(entry in entries){
                    statusData.add(LabelInfo(entry.toString()))
                }
            }


        } finally {
            a.recycle()
        }


        if (statusCount < 0) statusCount = 4
        if (completeCount < INVALID_STATUS_COUNT) completeCount = INVALID_STATUS_COUNT

        if(statusData.size>statusCount){

            while(statusData.size!=statusCount){
                statusData.removeAt(statusData.size-1)
            }
        }



        if (containsFlag(circleColorType, CIRCLE_COLOR_TYPE_STROKE)) {
            circleStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            circleStrokePaint?.style = Paint.Style.STROKE
            circleStrokePaint?.strokeWidth = circleStrokeWidth
            circleStrokePaint?.color = circleStrokeColor

            if (isShwoingIncompleteStatus()) {
                circleStrokePaintIncomplete = Paint(circleStrokePaint)
                circleStrokePaintIncomplete?.color = circleStrokeColorIncomplete
            }

        } else {
            circleStrokeWidth = 0.0f
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


        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        textPaint.style = Paint.Style.FILL
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.color = textColor
        textPaint.textSize = textSize


        if (isShwoingIncompleteStatus()) {
            linePaintIncomplete = Paint(linePaint)
            linePaintIncomplete.color = lineColorIncomplete

            textPaintIncomplete = TextPaint(textPaint)
            textPaintIncomplete.color = textColorIncomplete
        }

        if(statusData.size>0){
            textPaintLabels = TextPaint(textPaint)
            textPaintLabels.textSize = textSizeLabels
            textPaintLabels.color = textColorLabels
        }
    }


    private fun isShwoingIncompleteStatus() =
            completeCount != INVALID_STATUS_COUNT && completeCount < statusCount


    override fun getSuggestedMinimumWidth(): Int {
        val extraWidthIncase = setWidthData(lineLength,circleRadius)
        return ((statusCount * (2 * (circleRadius + (circleStrokeWidth/2)))) + ((statusCount - 1) * ( lineLength + (lineGap * 2))) + extraWidthIncase).toInt()
    }



    override fun getSuggestedMinimumHeight(): Int {

        var labelheight = 0.0f
        for(item in statusData){
            labelheight = Math.max(labelheight,setLabelsHeight(textPaintLabels,item))
        }

        return  (((circleRadius  * 2)+circleStrokeWidth) + labelheight + labelTopMargin).toInt()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = paddingLeft + paddingRight + suggestedMinimumWidth
        val desiredHeight = paddingTop + paddingBottom + suggestedMinimumHeight

        val measureSpecWidth = MeasureSpec.getMode(widthMeasureSpec);
        val measureSpecHeight = MeasureSpec.getMode(heightMeasureSpec);
        if(measureSpecHeight!=MeasureSpec.AT_MOST || measureSpecWidth!=MeasureSpec.AT_MOST){
            throw IllegalStateException("Width and height should be wrap_content")
        }



        val measuredWidth = resolveSize(desiredWidth, widthMeasureSpec)
        val measuredHeight = resolveSize(desiredHeight, heightMeasureSpec)



        setMeasuredDimension(measuredWidth, measuredHeight)


    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (item in drawingData) {

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
            if (item.labelData != null) {

                if (item.labelData.staticLayout!=null) {

                    canvas?.save()
                    canvas?.translate(item.labelData.x, item.labelData.y)
                    item.labelData.staticLayout.draw(canvas)
                    canvas?.restore()

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
        setDrawingDimensions()
    }


    private fun setDrawingDimensions() {

        lastPoint.x = paddingLeft.toFloat() + (circleStrokeWidth / 2)
        lastPoint.y = paddingTop.toFloat() + (circleRadius + (circleStrokeWidth / 2))
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
            var labelItemText: StatusView.LabelItemText? = null

            if(i==0){
                if(statusData.size>0){
                    val minWidthForExtreme = 2 * circleRadius + circleStrokeWidth
                    lastPoint.x+= Math.max(0.0f, (statusData[0].width - minWidthForExtreme)/2)
                }
            }else{
                lastPoint.x += lineGap
                lineItem = LineItem(PointF(lastPoint.x, lastPoint.y), PointF(lastPoint.x + lineLength, lastPoint.y), linePaint)
                lastPoint.x = lineItem.end.x + lineGap +  (circleStrokeWidth / 2)
            }


            val circleItem = CircleItem(PointF((lastPoint.x + circleRadius), lastPoint.y), circleRadius, circleStrokePaint, circleFillPaint)
            lastPoint.x += ((circleRadius) * 2.0f) + (circleStrokeWidth / 2)

            if(i<statusData.size){
                labelItemText = LabelItemText(circleItem.center.x, circleItem.center.y + circleRadius + circleStrokeWidth/2 + labelTopMargin, statusData[i].staticLayout)
            }



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



            drawingData.add(Item(statusItemText, circleItem, lineItem,labelItemText))


        }


    }


    /**
     * Helper Functions
     */


    private fun containsFlag(flagSet: Int, flag: Int): Boolean {
        return flagSet or flag == flagSet
    }

    private fun setWidthData(lineLength: Float, circleRadius: Float):Float {
        var adjacentExtraWidthForView = 0.0f
        for (i in 0 until statusData.size){

            if(i==0 || i==statusCount-1){
                val extraWidth = findAdjustWidthForExtremes(statusData[i].text,lineLength,circleRadius)
                adjacentExtraWidthForView+=extraWidth
                val minWidthForExtreme = (2 * circleRadius + circleStrokeWidth)
                if(extraWidth>0){
                    statusData[i].width = minWidthForExtreme + 2 * extraWidth

                }else{
                    statusData[i].width = minWidthForExtreme
                }
            }else{
                statusData[i].width = (lineLength + lineGap*2) + (2 * circleRadius + circleStrokeWidth)
            }
        }
        return adjacentExtraWidthForView
    }

    private fun findAdjustWidthForExtremes(text: String, lineLength: Float, circleRadius: Float):Float {
        val totalWidth = getTextWidth(textPaintLabels, text).toFloat()
        val actualWidth = (2*(circleRadius + circleStrokeWidth/2))
        val extraWidth = Math.max(totalWidth, actualWidth)
        return if(extraWidth==totalWidth){
            Math.min((lineLength + lineGap * 2)/2,(totalWidth-actualWidth)/2)
        }else{
            0.0f
        }

    }


    private fun getTextWidth(paint:Paint, text:String):Int{
        val measuringRect = Rect()
        paint.getTextBounds(text, 0, text.length, measuringRect)
        return measuringRect.width()
    }


     private fun  setLabelsHeight(textPaint:TextPaint, labelInfo: LabelInfo):Float{
        val staticLayoutHeight = getStaticLayout(labelInfo.text, textPaint, labelInfo.width)
        labelInfo.staticLayout = staticLayoutHeight
         labelInfo.height = staticLayoutHeight.height.toFloat()
        return labelInfo.height
     }


    private fun getStaticLayout(text: String, textPaint: TextPaint, width: Float): StaticLayout {
        val alignment = Layout.Alignment.ALIGN_NORMAL
        val spacingMultiplier = 1f
        val spacingAddition = 0f
        val includePadding = false
        return StaticLayout(text, textPaint, width.toInt(), alignment, spacingMultiplier, spacingAddition, includePadding)
    }




}