package jugnoo.com.learningcustomvviews

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View


/**
 * Created by Parminder Saini on 12/06/18.
 */
public class StatusView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
        ) : View(context, attrs, defStyleAttr) {

/*
    todo
            Case left height 20dp and width wrap_content
           1. Account for TextSize Ratio 2. Add labels 3.dp to px things
           3. Pass font to textView
           4.Complete color /Incompletecolor
           5.Labels
           6.Line gaps
           6. Add drawable instead of text
           2. Orientation draw vertical too

 */



    private  var circleStrokePaint:Paint?=null
    private  var circleFillPaint:Paint?=null
    private lateinit var textPaint:Paint
    private lateinit var linePaint:Paint


    //To store the data of each circle
    private data class Item(val textData:StatusItemText?, val circleItem: CircleItem, val lineItem: LineItem?)
    private data class StatusItemText(val text: String, val paint: Paint, val x:Float, val y:Float)
    private data class CircleItem(val center: PointF,val radius:Float,val strokePaint: Paint?,val fillPaint: Paint?)
    private data class LineItem(val start: PointF,val end: PointF,val paint: Paint)
    private val CIRCLE_COLOR_TYPE_FILL = 1;
    private val CIRCLE_COLOR_TYPE_STROKE = 2;


    private var statusCount:Int = 4;
    private var circleRadius:Float = 50.0f
    private var lineLength:Float = 50.0f
    private var mStrokeWidth:Float = 2.0f
    private var mLineWidth:Float = 2.0f
    private var lineColor:Int = ContextCompat.getColor(context,R.color.colorAccent)
    private var circleFillColor:Int = ContextCompat.getColor(context,android.R.color.transparent)
    private var circleStrokeColor:Int = ContextCompat.getColor(context,R.color.colorAccent)
    private var textColor:Int = ContextCompat.getColor(context,R.color.colorAccent)
    private var textSize:Float = 20.0f
    private var mDrawCountText:Boolean=true
    private var circleColorType = CIRCLE_COLOR_TYPE_FILL


    private val lastPoint = PointF();
    private var lineRatio = 0.0f;
    private var statusData = mutableListOf<Item>()






    init {
        val a = context.theme.obtainStyledAttributes(
                    attrs,
                    R.styleable.StatusView,
                    0, 0)

            try {
                statusCount = a.getInt(R.styleable.StatusView_statusCount,statusCount);
                circleRadius = a.getDimension(R.styleable.StatusView_circleRadius,circleRadius)
                lineLength = a.getDimension(R.styleable.StatusView_lineLength,lineLength)
                lineColor = a.getColor(R.styleable.StatusView_lineColor,lineColor)
                circleFillColor = a.getColor(R.styleable.StatusView_circleColor,circleFillColor)
                circleStrokeColor = a.getColor(R.styleable.StatusView_circleStrokeColor,circleStrokeColor)
                textColor = a.getColor(R.styleable.StatusView_textColor,textColor)
                textSize = a.getDimension(R.styleable.StatusView_textSize,textSize)
                mStrokeWidth = a.getDimension(R.styleable.StatusView_circleStrokeWidth,mStrokeWidth)
                mLineWidth = a.getDimension(R.styleable.StatusView_lineWidth,mLineWidth)
                circleColorType = a.getInteger(R.styleable.StatusView_circleColorType,circleColorType)
            } finally {
                a.recycle();
            }

        init()
    }


    private fun init() {

         if(containsFlag(circleColorType,CIRCLE_COLOR_TYPE_STROKE)){
            circleStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            circleStrokePaint?.style = Paint.Style.STROKE
            circleStrokePaint?.strokeWidth = mStrokeWidth
            circleStrokePaint?.color = circleStrokeColor
        }else{
             mStrokeWidth=0.0f
         }

        if(containsFlag(circleColorType,CIRCLE_COLOR_TYPE_FILL)){
            circleFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            circleFillPaint?.style = Paint.Style.FILL
            circleFillPaint?.color = circleFillColor
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
        lineRatio = lineLength/circleRadius

    }


    override fun getSuggestedMinimumWidth(): Int {
        return ((statusCount * (2 * (circleRadius))) + ((statusCount - 1) * lineLength)).toInt()
    }

    override fun getSuggestedMinimumHeight(): Int {
        return  ((circleRadius)* 2).toInt();
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
       val desiredWidth =paddingLeft + paddingRight + suggestedMinimumWidth
       val desiredHeight = paddingTop + paddingBottom + suggestedMinimumHeight

        val measuredWidth = resolveSize(desiredWidth,widthMeasureSpec)
        var measuredHeight = resolveSize(desiredHeight,heightMeasureSpec)
        val heightMode =   MeasureSpec.getMode(heightMeasureSpec);
        if(heightMode == MeasureSpec.AT_MOST){
            val maxHorizontalRadius = ((measuredWidth)/(((statusCount-1)*lineRatio)+(2*statusCount))) ;
            measuredHeight = (maxHorizontalRadius * 2.0f).toInt() + paddingTop + paddingBottom;

        }

        setMeasuredDimension(measuredWidth,measuredHeight)


    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for(item in statusData){

            if(item.circleItem.fillPaint!=null){
                canvas?.drawCircle(item.circleItem.center.x,item.circleItem.center.y,item.circleItem.radius,item.circleItem.fillPaint)

            }
            if(item.circleItem.strokePaint!=null){
                canvas?.drawCircle(item.circleItem.center.x,item.circleItem.center.y,item.circleItem.radius,item.circleItem.strokePaint)

            }
            if(item.textData!=null){
                canvas?.drawText(item.textData.text,item.textData.x,item.textData.y,item.textData.paint)

            }
            if(item.lineItem!=null){
                canvas?.drawLine(item.lineItem.start.x,item.lineItem.start.y,
                        item.lineItem.end.x,item.lineItem.end.y,item.lineItem.paint);
            }


        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh);
        setDrawingDimensions(w,h);
    }


    private fun setDrawingDimensions(w:Int,h:Int){
        val actualWidth = w-(paddingLeft+paddingRight)
        val actualHeight = h-(paddingBottom+paddingTop)


        val maxHorizontalRadius = ((actualWidth)/(((statusCount-1)*lineRatio)+(2*statusCount))) ;
        val maxVerticalRadius = actualHeight.toFloat()/2
        var circleRadius = Math.min(maxHorizontalRadius,maxVerticalRadius)
        val lineLength = circleRadius*lineRatio
         circleRadius = circleRadius-(mStrokeWidth/2);

        lastPoint.x = paddingLeft.toFloat()+ (mStrokeWidth/2)
        lastPoint.y = paddingTop.toFloat() + (circleRadius+ (mStrokeWidth/2))
        for (i in 0 until statusCount){
            var lineItem:StatusView.LineItem? = null
            var statusItemText:StatusView.StatusItemText? = null

            val circleItem  = CircleItem(PointF((lastPoint.x+circleRadius),lastPoint.y),circleRadius,circleStrokePaint,circleFillPaint)
            lastPoint.x += ((circleRadius) * 2.0f)+ (mStrokeWidth/2)

            if(mDrawCountText){
                val text:String = (i+1).toString();
                val measuringRect = Rect();
                textPaint.getTextBounds(text,0,text.length,measuringRect)
                statusItemText = StatusItemText(text,textPaint,circleItem.center.x,circleItem.center.y-measuringRect.exactCenterY())
            }

            if (i!=statusCount-1) {
                lineItem = LineItem(PointF(lastPoint.x,lastPoint.y), PointF(lastPoint.x+lineLength,lastPoint.y), linePaint)
                lastPoint.x = lineItem.end.x + (mStrokeWidth/2)
            }

            statusData.add(Item(statusItemText,circleItem,lineItem))


        }


    }

    private fun containsFlag(flagSet: Int, flag: Int): Boolean {
        return flagSet or flag == flagSet
    }

    private fun addFlag(flagSet: Int, flag: Int): Int {
        return flagSet or flag
    }
}