package jugnoo.com.learningcustomvviews

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.annotation.FloatRange
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewCompat
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.properties.Delegates
import kotlin.reflect.KProperty


/**
 * Created by Parminder Saini on 12/06/18.
 */
class StatusView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


        /*
         TODO
       Display status above|bottom
       Text Appearance?
       LTR Support
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
    private var mCircleStrokePaint: Paint? = null
    private var mCircleStrokePaintIncomplete: Paint? = null
    private var mCircleStrokePaintCurrent: Paint? = null

    private var mCircleFillPaint: Paint? = null
    private var mCircleFillPaintIncomplete: Paint? = null
    private var mCircleFillPaintCurrent: Paint? = null

    private lateinit var mLinePaint: Paint
    private lateinit var mLinePaintIncomplete: Paint
    private lateinit var mLinePaintCurrent: Paint
    private lateinit var mTextPaintStatus: TextPaint
    private lateinit var mTextPaintLabelsIncomplete: TextPaint
    private lateinit var mTextPaintLabelCurrent: TextPaint
    private lateinit var mTextPaintLabels: TextPaint


    /**
     * Magnification value for current Circle
     */
    @setparam:FloatRange(from = 0.0, to = 1.0)
    var  currentStatusZoom:Float = 0.0f
    set(value) {
        require(value in 0..1) { "Zoom should be in between 0 to 1, but was $value." }

        currentStatusRadius = circleRadius * (1 + value)
    }

    /**
     *  A min margin that there should be between every adjacent status Text
     *  Note# This only applies if obeyLineLength is set to false
     */
    var minMarginStatusText: Float by OnLayoutProp(5.0f.pxValue())

    /**
     *  Set true to obey Status Text i.e alphabets or words belonging to one line would not cross
     *  to the other line so the line length between circles would be increased as per if needed.
     *
     *  Set false to strictly obey line length.
     *
     *
     */
    var obeyLineLength: Boolean by OnLayoutProp(false)


    /**
    * The total number of statuses to be displayed
    */
    var statusCount: Int by OnLayoutProp(4)

    /*
    The count up to  which status has been completed
    */
    var currentCount:  Int by OnLayoutProp(INVALID_STATUS_COUNT)


    /*
     Radius of each circle to be drawn
     #Note: This does not include stroke width
     */

    var circleRadius: Float  by OnLayoutProp(20.0f.pxValue()) //dp


    /**
     *  Length of line to be drawn between circles
     *  #Note: This does not include line gap
     */
     var lineLength: Float  by OnLayoutProp(30.0f.pxValue()) //dp

    /**
     * Stroke width of each circle to be drawn
     */
     var circleStrokeWidth: Float by OnLayoutProp(2.0f.pxValue()) //dp


    /**
     * Margin or gap on each side of circle.
     * Note: This does not apply for extreme sides.
     */
     var lineGap by OnLayoutProp( 0.0f)


    /**
     * Top Margin of Labels from circle
     */
     var labelTopMargin by OnLayoutProp( 4.0f.pxValue())

    /**
     *  set true to align all status to align same as current Status y-pos
     *
     */
     var alignStatusWithCurrent by OnValidateProp(false){
        setDrawingDimensions()
    }

    /**
     * Stroke width of the line between circles (dp)
     */
    var lineStrokeWidth: Float by OnValidateProp(2.0f.pxValue()) {
        mLinePaint.strokeWidth = lineStrokeWidth
        mLinePaintIncomplete.strokeWidth = lineStrokeWidth
    }

    /**
     * Color of line between circles
     */
    var lineColor: Int by OnValidateProp(Color.BLACK){
        mLinePaint.color = lineColor
    }

    /**
     * Color of line between circles for incomplete statuses
     */
    var lineColorIncomplete: Int by OnValidateProp(Color.BLACK){
        mLinePaintIncomplete.color = lineColorIncomplete
    }

    /**
     * Color of line adjacent to  circles for current status
     */
    var lineColorCurrent: Int by OnValidateProp(Color.BLACK){
        mLinePaintCurrent.color = lineColorCurrent
    }

    /**
     * Fill Color of circles for complete statuses
     */
    var circleFillColor: Int by OnValidateProp(Color.CYAN){
        mCircleFillPaint?.color = circleFillColor

    }
    /**
     * Fill Color of circles for incomplete statuses
     */
    var circleFillColorIncomplete: Int by OnValidateProp(Color.CYAN){
        mCircleFillPaintIncomplete?.color = circleFillColorIncomplete

    }
    /**
     * Fill Color of circles for current status
     */
    var circleFillColorCurrent: Int by OnValidateProp(Color.CYAN){
        mCircleFillPaintCurrent?.color = circleFillColorCurrent

    }
    /**
     * Stroke Color of circles for complete statuses
     */
    var circleStrokeColor: Int by OnValidateProp(Color.BLACK){
        mCircleStrokePaint?.color = circleStrokeColor

    }
    /**
     * Stroke Color of circles for incomplete statuses
     */
    var circleStrokeColorIncomplete: Int by OnValidateProp(Color.BLACK){
        mCircleStrokePaintIncomplete?.color = circleStrokeColorIncomplete
    }

    /**
     * Stroke Color of circles for current Status
     */
    var circleStrokeColorCurrent: Int by OnValidateProp(Color.BLACK){
        mCircleStrokePaintCurrent?.color = circleStrokeColorCurrent
    }
    /**
     * Text Color of labels
     *
     */
    var textColorLabels: Int by OnValidateProp(Color.BLACK){
        mTextPaintLabels.color = textColorLabels
    }

    /**
     * Text Color of labels  Incomplete
     */
    var textColorLabelsIncomplete: Int by OnValidateProp(Color.BLACK){
        mTextPaintLabelsIncomplete.color = textColorLabelsIncomplete
    }

    /**
     * Text Color of labels  Current
     */
    var textColorLabelCurrent : Int by OnValidateProp(Color.BLACK){
        mTextPaintLabelCurrent.color = textColorLabelCurrent
    }

    /**
     *  Text Size of Labels
     */
    var textSizeLabels: Float by OnValidateProp(15.0f.pxValue(TypedValue.COMPLEX_UNIT_SP) ){//sp
        mTextPaintLabels.textSize = textSizeLabels
        mTextPaintLabelsIncomplete.textSize = textSizeLabels

    }

    /**
     * Text Color of Statuses
     */
    var textColorStatus: Int by OnValidateProp(Color.BLACK){
        mTextPaintStatus.color = textColorStatus
        for(item in statusData){
            item.staticLayout?.paint?.color = textColorStatus
        }
    }

    /**
     * Text Size of statuses
     */

    var textSizeStatus: Float by OnLayoutProp(14.0f.pxValue(TypedValue.COMPLEX_UNIT_SP)){
        mTextPaintStatus.textSize = textSizeStatus
    }

    /**
     * Text Font of statuses
     */

    var statusTypeface:Typeface? by OnLayoutProp(null){
        statusTypeface?.run {
            mTextPaintStatus.typeface = this
        }

    }

    /**
     * Text Font of Labels
     */

    var labelsTypeface:Typeface? by OnLayoutProp(null){
        labelsTypeface?.run {
            mTextPaintLabels.typeface = this
            mTextPaintLabelsIncomplete.typeface = this
            mTextPaintLabelCurrent.typeface = this
        }

    }

    /**
     *  A boolean which decides if to draw labels or not
     */

    var drawLabels: Boolean by OnValidateProp(false){
        setDrawingDimensions()
    }

    /**
     * A drawable for complete status
     * #Note: If this is set then it would be given preference over the labels.
     */
    var completeDrawable: Drawable? by OnValidateProp(null){
        setDrawingDimensions()
    }

    /**
     * A drawable for current Status
     * #Note: If this is set then it would be given preference over the labels.
     */
    var currentDrawable: Drawable? by OnValidateProp(null){
        setDrawingDimensions()
    }

    /**
     * A drawable for incomplete statuses
     * #Note: If this is set then it would be given preference over the labels.
     */
    var incompleteDrawable: Drawable? by OnValidateProp(null){
        setDrawingDimensions()
    }

    /**
     * circle fill type. Has been set to fill by default.
     *
     * To change pass a single flag
     * To set both the flags i.e (fill & stroke) pass (CIRCLE_COLOR_TYPE_FILL | CIRCLE_COLOR_TYPE_STROKE)
     *
     */
    var circleColorType:Int by Delegates.observable(CIRCLE_COLOR_TYPE_FILL)
    { prop, old, new ->
        if(ViewCompat.isLaidOut(this)){


            initCirclePaints()
            val oldHadStrokeFlagSet = containsFlag(old, CIRCLE_COLOR_TYPE_STROKE)
            val newHasStrokeFlagSet = containsFlag(new, CIRCLE_COLOR_TYPE_STROKE)

            if((oldHadStrokeFlagSet && !newHasStrokeFlagSet) || (!oldHadStrokeFlagSet && newHasStrokeFlagSet))
            {
                requestLayout()

            }else{
                setDrawingDimensions()
                invalidate()
            }
        }
    }


    /**
     * This contains list of LabelInfo which stores static layout and text.
     * To the caller it is a list of string and custom getter setters have been made accordingly
     *
     */
    private var statusData:MutableList<StatusInfo> by OnLayoutProp(mutableListOf())

    /**
     * Extracts List<String> from List<LabelInfo> and returns back to the caller
     */
    fun getStatusList():List<String> =  statusData.map { it.text  }

    /**
     * Creates List<LabelInfo> from List<String> by passing default values.
     * #Note: It trims any extra statuses (if size is greater than statusCount)
     */
    fun setStatusList(list: List<String>) {
        //to make sure original list is not modified convert to mutableList
       val input =  list.toMutableList().dropLast(statusCount)

        statusData  = (input.map{StatusInfo(it)}).toMutableList()


    }


    /**
     * Stores all the drawing data that is used while drawing on canvas
     */
    private var drawingData = mutableListOf<Item>()
    private var currentStatusRadius:Float by OnLayoutProp(circleRadius)
    private var lineLengthComputed = 0.0f




    //To store the data of each circle
    private class Item(val textData: LabelItemText?, val circleItem: CircleItem, val lineItem: LineItem?, val labelData: StatusItemText?=null)

    private class LabelItemText(val text: String? = null, val paint: Paint? = null, val x: Float = 0.0f, val y: Float = 0.0f, val drawableItem: DrawableItem? = null)
    private class StatusItemText(val x: Float = 0.0f, val y: Float = 0.0f, val staticLayout: StaticLayout? = null)
    private class CircleItem(val center: PointF, val radius: Float, val strokePaint: Paint?, val fillPaint: Paint?)
    private class LineItem(val start: PointF, val end: PointF, val paint: Paint)
    private class DrawableItem(val rect: Rect, val drawable: Drawable)
    private class StatusInfo(val text:String, var width:Float=0.0f, var height:Float=0.0f, var staticLayout: StaticLayout? = null)



    init {


        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.StatusView, 0, 0)

        try {

            statusCount = a.getInt(R.styleable.StatusView_statusCount, statusCount)
            currentCount = a.getInt(R.styleable.StatusView_currentCount, INVALID_STATUS_COUNT)

            circleRadius = a.getDimension(R.styleable.StatusView_circleRadius, circleRadius)
            lineLength = a.getDimension(R.styleable.StatusView_lineLength, lineLength)


            circleStrokeWidth = a.getDimension(R.styleable.StatusView_circleStrokeWidth, circleStrokeWidth)
            lineStrokeWidth = a.getDimension(R.styleable.StatusView_lineWidth, lineStrokeWidth)


            completeDrawable = a.getDrawable(R.styleable.StatusView_complete_drawable)
            incompleteDrawable = a.getDrawable(R.styleable.StatusView_incomplete_drawable)
            currentDrawable = a.getDrawable(R.styleable.StatusView_current_drawable)
            drawLabels = a.getBoolean(R.styleable.StatusView_drawCount, drawLabels)
            obeyLineLength = a.getBoolean(R.styleable.StatusView_obeyLineLength, obeyLineLength)
            lineGap = a.getDimension(R.styleable.StatusView_lineGap, lineGap)
            minMarginStatusText = a.getDimension(R.styleable.StatusView_minStatusMargin, minMarginStatusText)
            labelTopMargin = a.getDimension(R.styleable.StatusView_labelTopMargin, labelTopMargin)


            lineColor = a.getColor(R.styleable.StatusView_lineColor, lineColor)
            circleFillColor = a.getColor(R.styleable.StatusView_circleColor, circleFillColor)
            circleStrokeColor = a.getColor(R.styleable.StatusView_circleStrokeColor, circleStrokeColor)
            textColorStatus = a.getColor(R.styleable.StatusView_textColor, textColorStatus)
            textColorLabels = a.getColor(R.styleable.StatusView_textColorLabels, textColorLabels)
            textSizeStatus = a.getDimension(R.styleable.StatusView_textSize, textSizeStatus)
            textSizeLabels = a.getDimension(R.styleable.StatusView_textSizeLabels, textSizeLabels)

            circleColorType = a.getInteger(R.styleable.StatusView_circleColorType, circleColorType)
            textColorLabelsIncomplete = a.getColor(R.styleable.StatusView_textColorLabelsIncomplete, textColorStatus)
            textColorLabelCurrent = a.getColor(R.styleable.StatusView_textColorLabelsCurrent, textColorLabelCurrent)
            lineColorIncomplete = a.getColor(R.styleable.StatusView_lineColorIncomplete, lineColorIncomplete)
            lineColorCurrent= a.getColor(R.styleable.StatusView_lineColorCurrent, lineColorCurrent)
            circleFillColorIncomplete = a.getColor(R.styleable.StatusView_circleColorIncomplete, circleFillColor)
            circleStrokeColorIncomplete = a.getColor(R.styleable.StatusView_circleStrokeColorIncomplete, circleStrokeColor)

            circleFillColorCurrent = a.getColor(R.styleable.StatusView_circleColorCurrent, circleFillColorIncomplete)
            currentStatusZoom = a.getFloat(R.styleable.StatusView_currentStatusZoom, currentStatusZoom)
            alignStatusWithCurrent = a.getBoolean(R.styleable.StatusView_alignStatusWithCurrent, alignStatusWithCurrent)

            val entries = a.getTextArray(R.styleable.StatusView_android_entries)
            if (entries != null) {
                for(entry in entries){
                    statusData.add(StatusInfo(entry.toString()))
                }
            }

            try {
                val resource: Int = a.getResourceId(R.styleable.StatusView_statusFont, -1)
                if (resource != -1) {
                    statusTypeface = ResourcesCompat.getFont(getContext(), resource)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val resource: Int = a.getResourceId(R.styleable.StatusView_labelFont, -1)
                if (resource != -1) {
                    labelsTypeface = ResourcesCompat.getFont(getContext(), resource)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        } finally {
            a.recycle()
        }




        initCirclePaints()


        mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mLinePaint.style = Paint.Style.STROKE
        mLinePaint.strokeWidth = lineStrokeWidth
        mLinePaint.color = lineColor


        mTextPaintStatus = TextPaint(Paint.ANTI_ALIAS_FLAG)
        mTextPaintStatus.style = Paint.Style.FILL
        mTextPaintStatus.textAlign = Paint.Align.CENTER
        mTextPaintStatus.color = textColorStatus
        mTextPaintStatus.textSize = textSizeStatus
        statusTypeface?.run {
            mTextPaintStatus.typeface = this
        }

        mLinePaintIncomplete = Paint(mLinePaint)
        mLinePaintIncomplete.color = lineColorIncomplete

        mLinePaintCurrent = Paint(mLinePaint)
        mLinePaintCurrent.color = lineColorCurrent

        mTextPaintLabels = TextPaint(Paint.ANTI_ALIAS_FLAG)
        mTextPaintLabels.style = Paint.Style.FILL
        mTextPaintLabels.textAlign = Paint.Align.CENTER
        mTextPaintLabels.textSize = textSizeLabels
        mTextPaintLabels.color = textColorLabels


        mTextPaintLabelsIncomplete = TextPaint(mTextPaintLabels)
        mTextPaintLabelsIncomplete.color = textColorLabelsIncomplete

        mTextPaintLabelCurrent = TextPaint(mTextPaintLabels)
        mTextPaintLabelCurrent.color = textColorLabelCurrent

        labelsTypeface?.run {
            mTextPaintLabels.typeface = this
            mTextPaintLabelsIncomplete.typeface = this
            mTextPaintLabelCurrent.typeface = this
        }



    }

    private fun initCirclePaints() {
        if (containsFlag(circleColorType, CIRCLE_COLOR_TYPE_STROKE)) {
            if (mCircleStrokePaint == null) {

                mCircleStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
                mCircleStrokePaint?.style = Paint.Style.STROKE
                mCircleStrokePaint?.strokeWidth = circleStrokeWidth
                mCircleStrokePaint?.color = circleStrokeColor
            }

            if (isShowingIncompleteStatus()) {
                if (mCircleStrokePaintIncomplete == null) {
                    mCircleStrokePaintIncomplete = Paint(mCircleStrokePaint)
                    mCircleStrokePaintIncomplete?.color = circleStrokeColorIncomplete
                }
            }

            if(isShowingCurrentStatus()){
                if (mCircleStrokePaintCurrent == null) {
                    mCircleStrokePaintCurrent = Paint(mCircleStrokePaint)
                    mCircleStrokePaintCurrent?.color = circleStrokeColorCurrent
                }
            }

        } else {
            circleStrokeWidth = 0.0f
            mCircleStrokePaint = null
            mCircleStrokePaintIncomplete = null
            mCircleStrokePaintCurrent = null
        }

        if (containsFlag(circleColorType, CIRCLE_COLOR_TYPE_FILL)) {
            if (mCircleFillPaint == null) {
                mCircleFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                mCircleFillPaint?.style = Paint.Style.FILL
                mCircleFillPaint?.color = circleFillColor
            }
            if (isShowingIncompleteStatus()) {
                if (mCircleFillPaintIncomplete == null) {
                    mCircleFillPaintIncomplete = Paint(mCircleFillPaint)
                    mCircleFillPaintIncomplete?.color = circleFillColorIncomplete
                }
            }

            if(isShowingCurrentStatus()){
                if (mCircleFillPaintCurrent == null) {
                    mCircleFillPaintCurrent = Paint(mCircleFillPaint)
                    mCircleFillPaintCurrent?.color = circleFillColorCurrent
                }
            }
        } else {
            mCircleFillPaint = null
            mCircleFillPaintIncomplete =null
            mCircleFillPaintCurrent=null
        }
    }


    private fun isShowingIncompleteStatus() =
            currentCount > INVALID_STATUS_COUNT && currentCount < statusCount

    private fun isShowingCurrentStatus()=
            currentCount in 1..statusCount


    override fun getSuggestedMinimumWidth(): Int {


        lineLengthComputed = lineLength
        var extraWidth =  if(obeyLineLength){
            setWidthDataForObeyingLineLength()
        }else{
            setWidthDataForObeyingStatusText()
        }

        if(isShowingCurrentStatus()){
            extraWidth += (currentStatusRadius-circleRadius)*2
        }


        if(statusCount==1) {
            extraWidth *= 2
        }

        return ((statusCount * (2 * (circleRadius + (circleStrokeWidth/2)))) + ((statusCount - 1) * ( lineLengthComputed + (lineGap * 2))) + extraWidth).toInt()
    }




    override fun getSuggestedMinimumHeight(): Int {

        var labelHeight = 0.0f
        for(item in statusData){
            labelHeight = Math.max(labelHeight,setLabelsHeight(mTextPaintStatus,item))
        }
        if(statusData.size>0){
            labelHeight+=labelTopMargin
        }

        val circleRadius =  if(isShowingCurrentStatus()) currentStatusRadius  else circleRadius
        return  (((circleRadius * 2)+circleStrokeWidth) + labelHeight).toInt()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = paddingLeft + paddingRight + suggestedMinimumWidth
        val desiredHeight = paddingTop + paddingBottom + suggestedMinimumHeight

        val measureSpecWidth = MeasureSpec.getMode(widthMeasureSpec)
        val measureSpecHeight = MeasureSpec.getMode(heightMeasureSpec)
        /*if(measureSpecHeight!=MeasureSpec.AT_MOST || measureSpecWidth!=MeasureSpec.AT_MOST){
            throw IllegalStateException("Width and height should be wrap_content")
        }*/



        val measuredWidth = resolveSize(desiredWidth, widthMeasureSpec)
        val measuredHeight = resolveSize(desiredHeight, heightMeasureSpec)



        setMeasuredDimension(measuredWidth, measuredHeight)



    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
      setDrawingDimensions()
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


    /**
     *  Sets data in DrawingData class that is to be drawn by Canvas
     */
    private fun setDrawingDimensions() {

        /**
         * For keeping reference where last point was drawn
         */
        val lastPoint = PointF()
        lastPoint.x = paddingLeft.toFloat() + (circleStrokeWidth / 2)
        lastPoint.y = paddingTop.toFloat() + (circleRadius + (circleStrokeWidth / 2))
        if(isShowingCurrentStatus()) lastPoint.y += currentStatusRadius-circleRadius
        for (i in 0 until statusCount) {

            var circleStrokePaint: Paint?
            var circleFillPaint : Paint?
            var textPaintLabel : TextPaint
            var linePaint : Paint
            var itemDrawable: Drawable?

            var circleRadius = this.circleRadius
            if(isShowingCurrentStatus() && i==(currentCount-1)){
                circleRadius = currentStatusRadius
                circleStrokePaint = mCircleStrokePaintCurrent
                circleFillPaint = mCircleFillPaintCurrent
                textPaintLabel = mTextPaintLabelCurrent
                linePaint = mLinePaintCurrent
                itemDrawable = currentDrawable
            }else if (isShowingIncompleteStatus() && i in (currentCount)..statusCount) {
                circleStrokePaint = mCircleStrokePaintIncomplete
                circleFillPaint = mCircleFillPaintIncomplete
                textPaintLabel = mTextPaintLabelsIncomplete
                linePaint = mLinePaintIncomplete
                itemDrawable = incompleteDrawable

            }else{

                 circleStrokePaint = this.mCircleStrokePaint
                 circleFillPaint = this.mCircleFillPaint
                 textPaintLabel = this.mTextPaintLabels
                 linePaint = this.mLinePaint
                 itemDrawable = completeDrawable
            }


            var lineItem: StatusView.LineItem? = null
            var statusItemText: StatusView.LabelItemText? = null
            var labelItemText: StatusView.StatusItemText? = null


            if(i==0){
                if(statusData.size>0){
                    val minWidthForExtreme = 2 * circleRadius + circleStrokeWidth
                    lastPoint.x+= Math.max(0.0f, (statusData[0].width - minWidthForExtreme)/2)
                }
            }else{
                lastPoint.x += lineGap
                lineItem = LineItem(PointF(lastPoint.x, lastPoint.y), PointF(lastPoint.x + lineLengthComputed, lastPoint.y), linePaint)
                lastPoint.x = lineItem.end.x + lineGap +  (circleStrokeWidth / 2)
            }


            val circleItem = CircleItem(PointF((lastPoint.x + circleRadius), lastPoint.y), circleRadius, circleStrokePaint, circleFillPaint)
            lastPoint.x += ((circleRadius) * 2.0f) + (circleStrokeWidth / 2)

            if(i<statusData.size){
                val radii = if(isShowingCurrentStatus() && alignStatusWithCurrent) currentStatusRadius else circleRadius
                labelItemText = StatusItemText(circleItem.center.x, circleItem.center.y + radii + circleStrokeWidth/2 + labelTopMargin, statusData[i].staticLayout)
            }



            if (itemDrawable != null) {
                val width = itemDrawable.intrinsicWidth
                val height = itemDrawable.intrinsicHeight
                val xPos = circleItem.center.x.toInt()
                val yPos = circleItem.center.y.toInt()
                val drawableRect = Rect(xPos - width / 2, yPos - height / 2, xPos + width / 2, yPos + height / 2)
                statusItemText = LabelItemText(drawableItem = DrawableItem(drawableRect, itemDrawable))

            } else if (drawLabels) {
                val text: String = (i + 1).toString()
                val measuringRect = Rect()
                textPaintLabel.getTextBounds(text, 0, text.length, measuringRect)
                statusItemText = LabelItemText(text, textPaintLabel, circleItem.center.x, circleItem.center.y - measuringRect.exactCenterY())

            }

            drawingData.add(Item(statusItemText, circleItem, lineItem,labelItemText))

        }


    }


    /**
     * @param lineLength lineLength of StatusView
     * @param circleRadius circleRadius Of StatusView
     *
     * For non-extreme statuses:
     * Function sets the width value to circleRadius + linelength/2 (left side) + linelength/2 (rightSide)
     *
     * For extreme statuses:
     * It calculates value using findAdjustWidthForExtremes() as assigning the same width as
     * non-extreme may end up giving extraPadding to the view
     */
    private fun setWidthDataForObeyingLineLength():Float {
        var adjacentExtraWidthForView = 0.0f

        for (pos in 0 until statusData.size){

            val item = statusData[pos]
            when (pos) {
                0, statusCount - 1 -> {

                    val minStatusWidthExtremes = minStatusWidthExtremes(pos)
                    val minStatusWidth = minStatusWidth(pos)
                    val statusWidth = getTextWidth(mTextPaintStatus, item.text)
                    var extraExtremeWidth = 0.0f

                    if (statusWidth > minStatusWidthExtremes) {
                        extraExtremeWidth = Math.min(minStatusWidth - minStatusWidthExtremes, statusWidth - minStatusWidthExtremes) / 2
                    }

                    item.width = minStatusWidthExtremes + 2 * extraExtremeWidth
                    adjacentExtraWidthForView += extraExtremeWidth


                }
                else -> item.width = minStatusWidth(pos)

            }
        }
        return adjacentExtraWidthForView
    }


    private fun setWidthDataForObeyingStatusText(): Float {
        var extraWidth = 0.0f

        val widestLineData: StatusTextWidthInfo = getStatusTextWidthInfo(statusData.map { it.text }, mTextPaintStatus)

        val minStatusWidthWidestPos = minStatusWidth(widestLineData.widestStatus.pos)
        if(widestLineData.widestStatus.width>minStatusWidthWidestPos){
            lineLengthComputed += widestLineData.widestStatus.width - minStatusWidthWidestPos

        }

        widestLineData.subordinateWidestStatus?.run {
            val minStatusWidth = minStatusWidth(pos)

            if (width > minStatusWidth) {
                lineLengthComputed += width - minStatusWidth
            }
        }


        var addPadding = false
        for (pos in 0 until statusData.size) {
            val item = statusData[pos]
            when (pos) {
                0, (statusCount - 1) -> {

                    item.width = if (pos == 0) {
                        widestLineData.extremeLeftStatusWidth
                    } else {
                        widestLineData.extremeRightStatusWidth
                    }

                    val extraExtremesWidth = (item.width - minStatusWidthExtremes(pos)) / 2

                    if (extraExtremesWidth > 0) {
                        extraWidth += extraExtremesWidth
                    }

                }
                else -> item.width = widestLineData.widestStatus.width
            }
            if(minMarginStatusText> 0 && !addPadding &&  pos in 1 until  statusData.size){
                if(minStatusWidth(pos)+minStatusWidth(pos-1) - (item.width+statusData[pos-1].width)<minMarginStatusText){
                    addPadding = true
                }

            }
        }
        if(addPadding){
            lineLengthComputed+= minMarginStatusText
        }
        return extraWidth
    }


    private fun minStatusWidth(pos:Int): Float {

        var circleRadius = this.circleRadius
        val lineWidth = (lineLengthComputed + lineGap*2)

        if(isShowingCurrentStatus() && pos==currentCountIndex()){
            circleRadius = currentStatusRadius
        }

        return  (2 * circleRadius + circleStrokeWidth) + lineWidth
    }

    private fun minStatusWidthExtremes(pos:Int): Float {
        var circleRadius = this.circleRadius

        if(isShowingCurrentStatus() && pos==currentCountIndex()){
            circleRadius = currentStatusRadius
        }
        return (2 * circleRadius + circleStrokeWidth)
    }

    /**
     * Maximum width that a text would need
     */
    private fun getTextWidth(paint:Paint, text:String):Float{
        return paint.measureText(text)
    }


    /**
     * @param textPaint textPaint to be set on Labels
     * @param labelInfo LabelInfo which contains width and text
     * @return Height that the label would require
     */
     private fun setLabelsHeight(textPaint:TextPaint, labelInfo: StatusInfo):Float{
        val staticLayoutHeight = getStaticLayout(labelInfo.text, textPaint, labelInfo.width)
        labelInfo.staticLayout = staticLayoutHeight
         labelInfo.height = staticLayoutHeight.height.toFloat()
        return labelInfo.height
     }


    /**
     *  @param text text to be shown
     *  @param textPaint textPaint to be set on text
     *  @param width available width
     *  @return Static Layout which decides the height and auto-adjusts multiline text
     */
    private fun getStaticLayout(text: String, textPaint: TextPaint, width: Float): StaticLayout {
        val alignment = Layout.Alignment.ALIGN_NORMAL
        val spacingMultiplier = 1f
        val spacingAddition = 0f
        val includePadding = false
        return StaticLayout(text, textPaint, width.toInt(), alignment, spacingMultiplier, spacingAddition, includePadding)
    }


    class StatusTextWidthInfo(var widestStatus:StatusWidth, var subordinateWidestStatus:StatusWidth?=null,
                              var extremeLeftStatusWidth:Float=0.0f, var extremeRightStatusWidth:Float=0.0f)

    class StatusWidth(var width: Float = 0.0f, var pos:Int=-1)

    private fun getStatusTextWidthInfo(list:List<String>, paint:TextPaint):StatusTextWidthInfo{

        val widestStatus = StatusWidth()
        var subordinateWidestStatus: StatusWidth? = null
        val statusWidthInfo = StatusTextWidthInfo(widestStatus)


        for (pos in 0 until list.size) {

            val result = getStatusWidth(list[pos], paint)

            if (isShowingCurrentStatus() && pos == (currentCountIndex()) && result > widestStatus.width) {

                subordinateWidestStatus = StatusWidth(widestStatus.width, widestStatus.pos)
                widestStatus.width = result
                widestStatus.pos = pos

            } else if (result > widestStatus.width) {

                widestStatus.width = result
                widestStatus.pos = pos
                subordinateWidestStatus = null

            } else if (subordinateWidestStatus != null && result > subordinateWidestStatus.width) {

                subordinateWidestStatus.width = result
                subordinateWidestStatus.pos = pos
            }

            when (pos) {
                0 -> statusWidthInfo.extremeLeftStatusWidth= result
                statusCountIndex() -> statusWidthInfo.extremeRightStatusWidth = result
            }

        }

        statusWidthInfo.widestStatus = widestStatus
        statusWidthInfo.subordinateWidestStatus = subordinateWidestStatus

        return statusWidthInfo

    }

    private fun getStatusWidth(text:String, paint: TextPaint):Float{

        val arr:List<String> = text.split('\n')
        var result = 0.0f
        for(sub in arr){
            val subWidth = paint.measureText(sub)
            result = if(subWidth>result) subWidth else result
        }

        return result


    }


    /**
     * checks if a flagSet contains a flag
     *
     */

    private fun containsFlag(flagSet: Int, flag: Int): Boolean {
        return flagSet or flag == flagSet
    }


    private fun currentCountIndex() = currentCount-1

    private fun statusCountIndex() = statusCount-1


    /**
     * Delegate property used to requestLayout if any value changed
     */
    inner class OnLayoutProp<T> (private var field:T, private inline var func:()->Unit={}){
        operator fun setValue(thisRef: Any?,p: KProperty<*>,v: T) {
            field = v
            if(ViewCompat.isLaidOut(this@StatusView)){
                drawingData.clear()
                func()
                requestLayout()

            }

        }
        operator fun getValue(thisRef: Any?,p: KProperty<*>):T{
            return field
        }

    }

    /**
     * Delegate Property used to invalidate a layout after executing a custom function
     */
    inner class  OnValidateProp<T> (private var field:T, private inline var func:()->Unit={}){
        operator fun setValue(thisRef: Any?,p: KProperty<*>,v: T) {
            field = v
            if(ViewCompat.isLaidOut(this@StatusView)){
                func()
                invalidate()

            }

        }
        operator fun getValue(thisRef: Any?,p: KProperty<*>):T{
            return field
        }

    }

    private fun Float.pxValue(unit:Int = TypedValue.COMPLEX_UNIT_DIP):Float{
        return TypedValue.applyDimension(unit,this,resources.displayMetrics)
    }


}