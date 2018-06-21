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
    todo   1. Account for padding and
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:padding="10dp"
 */



    private lateinit var circlePaint:Paint
    private lateinit var textPaint:Paint
    private lateinit var linePaint:Paint
    private var mStrokeWidth:Float = 2.0f;


    private var statusData = mutableListOf<Item>()
    private val statusCount:Int = 10;
    private val circleRadius:Float = 50.0f
    private val lineLength:Float = 50.0f
    private val lastPoint = PointF();
    private var lineRatio = 0.0f;

    //To store the data of each circle
    private data class Item(val textData:StatusItemText, val circleItem: CircleItem, val lineItem: LineItem?)
    private data class StatusItemText(val text: String, val paint: Paint, val x:Float, val y:Float)
    private data class CircleItem(val center: PointF,val radius:Float,val paint: Paint)
    private data class LineItem(val start: PointF,val end: PointF,val paint: Paint)



    init {
        init()
    }


    private fun init() {
        circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        circlePaint.style = Paint.Style.STROKE
        circlePaint.strokeWidth = mStrokeWidth
        circlePaint.color = ContextCompat.getColor(context,R.color.colorAccent)
        linePaint = circlePaint;

        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.style = Paint.Style.FILL
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.strokeWidth = mStrokeWidth
        textPaint.color = ContextCompat.getColor(context,R.color.colorAccent)
        textPaint.textSize = 20.0f
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


        var measuredWidth = resolveSize(desiredWidth,widthMeasureSpec)
        var measuredHeight = resolveSize(desiredHeight,heightMeasureSpec)

        val heightMode =   MeasureSpec.getMode(heightMeasureSpec);
        val widthMode =   MeasureSpec.getMode(widthMeasureSpec);
        if(measuredWidth<desiredWidth && heightMode==MeasureSpec.AT_MOST){
            val actualWidth = measuredWidth-(paddingLeft+paddingRight)
            val maxHorizontalRadius = ((actualWidth)/(((statusCount-1)*lineRatio)+(2*statusCount))) ;
            measuredHeight = (maxHorizontalRadius*2.0f).toInt();
        }else if(measuredHeight<desiredHeight && widthMode==MeasureSpec.AT_MOST){
            val verticalRadius = measuredHeight/2.0f;
            val lineLength = lineRatio * verticalRadius;
            measuredWidth = ((statusCount * (2 * (verticalRadius))) + ((statusCount - 1) * lineLength)).toInt()

        }

        setMeasuredDimension(measuredWidth,measuredHeight)


    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for(item in statusData){

            canvas?.drawCircle(item.circleItem.center.x,item.circleItem.center.y,item.circleItem.radius,item.circleItem.paint)
            canvas?.drawText(item.textData.text,item.textData.x,item.textData.y,item.textData.paint)
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
            val circleItem  = CircleItem(PointF((lastPoint.x+circleRadius),lastPoint.y),circleRadius,circlePaint)
            lastPoint.x += ((circleRadius) * 2.0f)+ (mStrokeWidth/2)

            val text:String = (i+1).toString();
            val measuringRect = Rect();
            textPaint.getTextBounds(text,0,text.length,measuringRect)
            val statusItemText = StatusItemText(text,textPaint,circleItem.center.x,circleItem.center.y-measuringRect.exactCenterY())

            if (i!=statusCount-1) {
                lineItem = LineItem(PointF(lastPoint.x,lastPoint.y), PointF(lastPoint.x+lineLength,lastPoint.y), linePaint)
                lastPoint.x = lineItem.end.x + (mStrokeWidth/2)
            }

            statusData.add(Item(statusItemText,circleItem,lineItem))


        }


    }
}