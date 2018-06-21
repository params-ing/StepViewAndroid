/* Copyright (C) 2012 The Android Open Source Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*//*

package jugnoo.com.learningcustomvviews

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import android.os.Build
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Scroller

import java.util.ArrayList

*/
/**
 * Custom view that shows a pie chart and, optionally, a label.
 *//*

class PieChart : ViewGroup {
    private val mData = ArrayList<Item>()

    private var mTotal = 0.0f

    private var mPieBounds = RectF()

    private var mPiePaint: Paint? = null
    private var mTextPaint: Paint? = null
    private var mShadowPaint: Paint? = null

    private var mShowText = false

    private var mTextX = 0.0f
    private var mTextY = 0.0f
    private var mTextWidth = 0.0f
    private var mTextHeight = 0.0f
    private var mTextPos = TEXTPOS_LEFT

    private var mHighlightStrength = 1.15f

    private var mPointerRadius = 2.0f
    private var mPointerX: Float = 0.toFloat()
    private var mPointerY: Float = 0.toFloat()

    private var mPieRotation: Int = 0

    private var mCurrentItemChangedListener: OnCurrentItemChangedListener? = null

    private var mTextColor: Int = 0
    private var mPieView: PieView? = null
    private var mScroller: Scroller? = null
    private var mScrollAnimator: ValueAnimator? = null
    private var mDetector: GestureDetector? = null
    private var mPointerView: PointerView? = null

    // The angle at which we measure the current item. This is
    // where the pointer points.
    private var mCurrentItemAngle: Int = 0

    // the index of the current item.
    private var mCurrentItem = 0
    private var mAutoCenterInSlice: Boolean = false
    private var mAutoCenterAnimator: ObjectAnimator? = null
    private var mShadowBounds = RectF()

    */
/**
     * Returns true if the text label should be visible.
     *
     * @return True if the text label should be visible, false otherwise.
     *//*

    */
/**
     * Controls whether the text label is visible or not. Setting this property to
     * false allows the pie chart graphic to take up the entire visible area of
     * the control.
     *
     * @param showText true if the text label should be visible, false otherwise
     *//*

    var showText: Boolean
        get() = mShowText
        set(showText) {
            mShowText = showText
            invalidate()
        }

    */
/**
     * Returns the Y position of the label text, in pixels.
     *
     * @return The Y position of the label text, in pixels.
     *//*

    */
/**
     * Set the Y position of the label text, in pixels.
     *
     * @param textY the Y position of the label text, in pixels.
     *//*

    var textY: Float
        get() = mTextY
        set(textY) {
            mTextY = textY
            invalidate()
        }

    */
/**
     * Returns the width reserved for label text, in pixels.
     *
     * @return The width reserved for label text, in pixels.
     *//*

    */
/**
     * Set the width of the area reserved for label text. This width is constant; it does not
     * change based on the actual width of the label as the label text changes.
     *
     * @param textWidth The width reserved for label text, in pixels.
     *//*

    var textWidth: Float
        get() = mTextWidth
        set(textWidth) {
            mTextWidth = textWidth
            invalidate()
        }

    */
/**
     * Returns the height of the label font, in pixels.
     *
     * @return The height of the label font, in pixels.
     *//*

    */
/**
     * Set the height of the label font, in pixels.
     *
     * @param textHeight The height of the label font, in pixels.
     *//*

    var textHeight: Float
        get() = mTextHeight
        set(textHeight) {
            mTextHeight = textHeight
            invalidate()
        }

    */
/**
     * Returns a value that specifies whether the label text is to the right
     * or the left of the pie chart graphic.
     *
     * @return One of TEXTPOS_LEFT or TEXTPOS_RIGHT.
     *//*

    */
/**
     * Set a value that specifies whether the label text is to the right
     * or the left of the pie chart graphic.
     *
     * @param textPos TEXTPOS_LEFT to draw the text to the left of the graphic,
     * or TEXTPOS_RIGHT to draw the text to the right of the graphic.
     *//*

    var textPos: Int
        get() = mTextPos
        set(textPos) {
            if (textPos != TEXTPOS_LEFT && textPos != TEXTPOS_RIGHT) {
                throw IllegalArgumentException(
                        "TextPos must be one of TEXTPOS_LEFT or TEXTPOS_RIGHT")
            }
            mTextPos = textPos
            invalidate()
        }

    */
/**
     * Returns the strength of the highlighting applied to each pie segment.
     *
     * @return The highlight strength.
     *//*

    */
/**
     * Set the strength of the highlighting that is applied to each pie segment.
     * This number is a floating point number that is multiplied by the base color of
     * each segment to get the highlight color. A value of exactly one produces no
     * highlight at all. Values greater than one produce highlights that are lighter
     * than the base color, while values less than one produce highlights that are darker
     * than the base color.
     *
     * @param highlightStrength The highlight strength.
     *//*

    var highlightStrength: Float
        get() = mHighlightStrength
        set(highlightStrength) {
            if (highlightStrength < 0.0f) {
                throw IllegalArgumentException(
                        "highlight strength cannot be negative")
            }
            mHighlightStrength = highlightStrength
            invalidate()
        }

    */
/**
     * Returns the radius of the filled circle that is drawn at the tip of the current-item
     * pointer.
     *
     * @return The radius of the pointer tip, in pixels.
     *//*

    */
/**
     * Set the radius of the filled circle that is drawn at the tip of the current-item
     * pointer.
     *
     * @param pointerRadius The radius of the pointer tip, in pixels.
     *//*

    var pointerRadius: Float
        get() = mPointerRadius
        set(pointerRadius) {
            mPointerRadius = pointerRadius
            invalidate()
        }

    */
/**
     * Returns the current rotation of the pie graphic.
     *
     * @return The current pie rotation, in degrees.
     *//*

    */
/**
     * Set the current rotation of the pie graphic. Setting this value may change
     * the current item.
     *
     * @param rotation The current pie rotation, in degrees.
     *//*

    var pieRotation: Int
        get() = mPieRotation
        set(rotation) {
            var rotation = rotation
            rotation = (rotation % 360 + 360) % 360
            mPieRotation = rotation
            mPieView!!.rotateTo(rotation.toFloat())

            calcCurrentItem()
        }

    */
/**
     * Returns the index of the currently selected data item.
     *
     * @return The zero-based index of the currently selected data item.
     *//*

    */
/**
     * Set the currently selected item. Calling this function will set the current selection
     * and rotate the pie to bring it into view.
     *
     * @param currentItem The zero-based index of the item to select.
     *//*

    var currentItem: Int
        get() = mCurrentItem
        set(currentItem) = setCurrentItem(currentItem, true)

    private val isAnimationRunning: Boolean
        get() = !mScroller!!.isFinished || Build.VERSION.SDK_INT >= 11 && mAutoCenterAnimator!!.isRunning

    */
/**
     * Interface definition for a callback to be invoked when the current
     * item changes.
     *//*

    interface OnCurrentItemChangedListener {
        fun OnCurrentItemChanged(source: PieChart, currentItem: Int)
    }

    */
/**
     * Class constructor taking only a context. Use this constructor to create
     * [PieChart] objects from your own code.
     *
     * @param context
     *//*

    constructor(context: Context) : super(context) {
        init()
    }

    */
/**
     * Class constructor taking a context and an attribute set. This constructor
     * is used by the layout engine to construct a [PieChart] from a set of
     * XML attributes.
     *
     * @param context
     * @param attrs   An attribute set which can contain attributes from
     * [com.example.android.customviews.R.styleable.PieChart] as well as attributes inherited
     * from [View].
     *//*

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        // attrs contains the raw values for the XML attributes
        // that were specified in the layout, which don't include
        // attributes set by styles or themes, and which may have
        // unresolved references. Call obtainStyledAttributes()
        // to get the final values for each attribute.
        //
        // This call uses R.styleable.PieChart, which is an array of
        // the custom attributes that were declared in attrs.xml.
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.PieChart,
                0, 0
        )

        try {
            // Retrieve the values from the TypedArray and store into
            // fields of this class.
            //
            // The R.styleable.PieChart_* constants represent the index for
            // each custom attribute in the R.styleable.PieChart array.
            mShowText = a.getBoolean(R.styleable.PieChart_showText, false)
            mTextY = a.getDimension(R.styleable.PieChart_labelY, 0.0f)
            mTextWidth = a.getDimension(R.styleable.PieChart_labelWidth, 0.0f)
            mTextHeight = a.getDimension(R.styleable.PieChart_labelHeight, 0.0f)
            mTextPos = a.getInteger(R.styleable.PieChart_labelPosition, 0)
            mTextColor = a.getColor(R.styleable.PieChart_labelColor, -0x1000000)
            mHighlightStrength = a.getFloat(R.styleable.PieChart_highlightStrength, 1.0f)
            mPieRotation = a.getInt(R.styleable.PieChart_pieRotation, 0)
            mPointerRadius = a.getDimension(R.styleable.PieChart_pointerRadius, 2.0f)
            mAutoCenterInSlice = a.getBoolean(R.styleable.PieChart_autoCenterPointerInSlice, false)
        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle()
        }

        init()
    }

    */
/**
     * Set the current item by index. Optionally, scroll the current item into view. This version
     * is for internal use--the scrollIntoView option is always true for external callers.
     *
     * @param currentItem    The index of the current item.
     * @param scrollIntoView True if the pie should rotate until the current item is centered.
     * False otherwise. If this parameter is false, the pie rotation
     * will not change.
     *//*

    private fun setCurrentItem(currentItem: Int, scrollIntoView: Boolean) {
        mCurrentItem = currentItem
        if (mCurrentItemChangedListener != null) {
            mCurrentItemChangedListener!!.OnCurrentItemChanged(this, currentItem)
        }
        if (scrollIntoView) {
            centerOnCurrentItem()
        }
        invalidate()
    }


    */
/**
     * Register a callback to be invoked when the currently selected item changes.
     *
     * @param listener Can be null.
     * The current item changed listener to attach to this view.
     *//*

    fun setOnCurrentItemChangedListener(listener: OnCurrentItemChangedListener) {
        mCurrentItemChangedListener = listener
    }

    */
/**
     * Add a new data item to this view. Adding an item adds a slice to the pie whose
     * size is proportional to the item's value. As new items are added, the size of each
     * existing slice is recalculated so that the proportions remain correct.
     *
     * @param label The label text to be shown when this item is selected.
     * @param value The value of this item.
     * @param color The ARGB color of the pie slice associated with this item.
     * @return The index of the newly added item.
     *//*

    fun addItem(label: String, value: Float, color: Int): Int {
        val it = Item()
        it.mLabel = label
        it.mColor = color
        it.mValue = value

        // Calculate the highlight color. Saturate at 0xff to make sure that high values
        // don't result in aliasing.
        it.mHighlight = Color.argb(
                0xff,
                Math.min((mHighlightStrength * Color.red(color).toFloat()).toInt(), 0xff),
                Math.min((mHighlightStrength * Color.green(color).toFloat()).toInt(), 0xff),
                Math.min((mHighlightStrength * Color.blue(color).toFloat()).toInt(), 0xff)
        )
        mTotal += value

        mData.add(it)

        onDataChanged()

        return mData.size - 1
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Let the GestureDetector interpret this event
        var result = mDetector!!.onTouchEvent(event)

        // If the GestureDetector doesn't want this event, do some custom processing.
        // This code just tries to detect when the user is done scrolling by looking
        // for ACTION_UP events.
        if (!result) {
            if (event.action == MotionEvent.ACTION_UP) {
                // User is done scrolling, it's now safe to do things like autocenter
                stopScrolling()
                result = true
            }
        }
        return result
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        // Do nothing. Do not call the superclass method--that would start a layout pass
        // on this view's children. PieChart lays out its children in onSizeChanged().
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the shadow
        canvas.drawOval(mShadowBounds, mShadowPaint!!)

        // Draw the label text
        if (showText) {
            canvas.drawText(mData[mCurrentItem].mLabel!!, mTextX, mTextY, mTextPaint!!)
        }

        // If the API level is less than 11, we can't rely on the view animation system to
        // do the scrolling animation. Need to tick it here and call postInvalidate() until the scrolling is done.
        if (Build.VERSION.SDK_INT < 11) {
            tickScrollAnimation()
            if (!mScroller!!.isFinished) {
                postInvalidate()
            }
        }
    }


    //
    // Measurement functions. This example uses a simple heuristic: it assumes that
    // the pie chart should be at least as wide as its label.
    //
    override fun getSuggestedMinimumWidth(): Int {
        return mTextWidth.toInt() * 2
    }

    override fun getSuggestedMinimumHeight(): Int {
        return mTextWidth.toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Try for a width based on our minimum
        val minw = paddingLeft + paddingRight + suggestedMinimumWidth

        val w = Math.max(minw, View.MeasureSpec.getSize(widthMeasureSpec))

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        val minh = w - mTextWidth.toInt() + paddingBottom + paddingTop
        val h = Math.min(View.MeasureSpec.getSize(heightMeasureSpec), minh)

        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        //
        // Set dimensions for text, pie chart, etc
        //
        // Account for padding
        var xpad = (paddingLeft + paddingRight).toFloat()
        val ypad = (paddingTop + paddingBottom).toFloat()

        // Account for the label
        if (mShowText) xpad += mTextWidth

        val ww = w.toFloat() - xpad
        val hh = h.toFloat() - ypad

        // Figure out how big we can make the pie.
        val diameter = Math.min(ww, hh)
        mPieBounds = RectF(
                0.0f,
                0.0f,
                diameter,
                diameter)
        mPieBounds.offsetTo(paddingLeft.toFloat(), paddingTop.toFloat())

        mPointerY = mTextY - mTextHeight / 2.0f
        var pointerOffset = mPieBounds.centerY() - mPointerY

        // Make adjustments based on text position
        if (mTextPos == TEXTPOS_LEFT) {
            mTextPaint!!.textAlign = Paint.Align.RIGHT
            if (mShowText) mPieBounds.offset(mTextWidth, 0.0f)
            mTextX = mPieBounds.left

            if (pointerOffset < 0) {
                pointerOffset = -pointerOffset
                mCurrentItemAngle = 225
            } else {
                mCurrentItemAngle = 135
            }
            mPointerX = mPieBounds.centerX() - pointerOffset
        } else {
            mTextPaint!!.textAlign = Paint.Align.LEFT
            mTextX = mPieBounds.right

            if (pointerOffset < 0) {
                pointerOffset = -pointerOffset
                mCurrentItemAngle = 315
            } else {
                mCurrentItemAngle = 45
            }
            mPointerX = mPieBounds.centerX() + pointerOffset
        }

        mShadowBounds = RectF(
                mPieBounds.left + 10,
                mPieBounds.bottom + 10,
                mPieBounds.right - 10,
                mPieBounds.bottom + 20)

        // Lay out the child view that actually draws the pie.
        mPieView!!.layout(mPieBounds.left.toInt(),
                mPieBounds.top.toInt(),
                mPieBounds.right.toInt(),
                mPieBounds.bottom.toInt())
        mPieView!!.setPivot(mPieBounds.width() / 2, mPieBounds.height() / 2)

        mPointerView!!.layout(0, 0, w, h)
        onDataChanged()
    }

    */
/**
     * Calculate which pie slice is under the pointer, and set the current item
     * field accordingly.
     *//*

    private fun calcCurrentItem() {
        val pointerAngle = (mCurrentItemAngle + 360 + mPieRotation) % 360
        for (i in mData.indices) {
            val it = mData[i]
            if (it.mStartAngle <= pointerAngle && pointerAngle <= it.mEndAngle) {
                if (i != mCurrentItem) {
                    setCurrentItem(i, false)
                }
                break
            }
        }
    }

    */
/**
     * Do all of the recalculations needed when the data array changes.
     *//*

    private fun onDataChanged() {
        // When the data changes, we have to recalculate
        // all of the angles.
        var currentAngle = 0
        for (it in mData) {
            it.mStartAngle = currentAngle
            it.mEndAngle = (currentAngle.toFloat() + it.mValue * 360.0f / mTotal).toInt()
            currentAngle = it.mEndAngle


            // Recalculate the gradient shaders. There are
            // three values in this gradient, even though only
            // two are necessary, in order to work around
            // a bug in certain versions of the graphics engine
            // that expects at least three values if the
            // positions array is non-null.
            //
            it.mShader = SweepGradient(
                    mPieBounds.width() / 2.0f,
                    mPieBounds.height() / 2.0f,
                    intArrayOf(it.mHighlight, it.mHighlight, it.mColor, it.mColor),
                    floatArrayOf(0f, (360 - it.mEndAngle).toFloat() / 360.0f, (360 - it.mStartAngle).toFloat() / 360.0f, 1.0f)
            )
        }
        calcCurrentItem()
        onScrollFinished()
    }

    */
/**
     * Initialize the control. This code is in a separate method so that it can be
     * called from both constructors.
     *//*

    private fun init() {
        // Force the background to software rendering because otherwise the Blur
        // filter won't work.
        setLayerToSW(this)

        // Set up the paint for the label text
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint!!.color = mTextColor
        if (mTextHeight == 0f) {
            mTextHeight = mTextPaint!!.textSize
        } else {
            mTextPaint!!.textSize = mTextHeight
        }

        // Set up the paint for the pie slices
        mPiePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPiePaint!!.style = Paint.Style.FILL
        mPiePaint!!.textSize = mTextHeight

        // Set up the paint for the shadow
        mShadowPaint = Paint(0)
        mShadowPaint!!.color = -0xefeff0
        mShadowPaint!!.maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)

        // Add a child view to draw the pie. Putting this in a child view
        // makes it possible to draw it on a separate hardware layer that rotates
        // independently
        mPieView = PieView(context)
        addView(mPieView)
        mPieView!!.rotateTo(mPieRotation.toFloat())

        // The pointer doesn't need hardware acceleration, but in order to show up
        // in front of the pie it also needs to be on a separate view.
        mPointerView = PointerView(context)
        addView(mPointerView)

        // Set up an animator to animate the PieRotation property. This is used to
        // correct the pie's orientation after the user lets go of it.
        if (Build.VERSION.SDK_INT >= 11) {
            mAutoCenterAnimator = ObjectAnimator.ofInt(this@PieChart, "PieRotation", 0)

            // Add a listener to hook the onAnimationEnd event so that we can do
            // some cleanup when the pie stops moving.
            mAutoCenterAnimator!!.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}

                override fun onAnimationEnd(animator: Animator) {
                    mPieView!!.decelerate()
                }

                override fun onAnimationCancel(animator: Animator) {}

                override fun onAnimationRepeat(animator: Animator) {}
            })
        }


        // Create a Scroller to handle the fling gesture.
        if (Build.VERSION.SDK_INT < 11) {
            mScroller = Scroller(context)
        } else {
            mScroller = Scroller(context, null, true)
        }
        // The scroller doesn't have any built-in animation functions--it just supplies
        // values when we ask it to. So we have to have a way to call it every frame
        // until the fling ends. This code (ab)uses a ValueAnimator object to generate
        // a callback on every animation frame. We don't use the animated value at all.
        if (Build.VERSION.SDK_INT >= 11) {
            mScrollAnimator = ValueAnimator.ofFloat(0, 1)
            mScrollAnimator!!.addUpdateListener { tickScrollAnimation() }
        }

        // Create a gesture detector to handle onTouch messages
        mDetector = GestureDetector(this@PieChart.context, GestureListener())

        // Turn off long press--this control doesn't use it, and if long press is enabled,
        // you can't scroll for a bit, pause, then scroll some more (the pause is interpreted
        // as a long press, apparently)
        mDetector!!.setIsLongpressEnabled(false)


        // In edit mode it's nice to have some demo data, so add that here.
        if (this.isInEditMode) {
            val res = resources
            addItem("Annabelle", 3f, res.getColor(R.color.bluegrass))
            addItem("Brunhilde", 4f, res.getColor(R.color.chartreuse))
            addItem("Carolina", 2f, res.getColor(R.color.emerald))
            addItem("Dahlia", 3f, res.getColor(R.color.seafoam))
            addItem("Ekaterina", 1f, res.getColor(R.color.slate))
        }

    }

    private fun tickScrollAnimation() {
        if (!mScroller!!.isFinished) {
            mScroller!!.computeScrollOffset()
            pieRotation = mScroller!!.currY
        } else {
            if (Build.VERSION.SDK_INT >= 11) {
                mScrollAnimator!!.cancel()
            }
            onScrollFinished()
        }
    }

    private fun setLayerToSW(v: View) {
        if (!v.isInEditMode && Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
    }

    private fun setLayerToHW(v: View) {
        if (!v.isInEditMode && Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
    }

    */
/**
     * Force a stop to all pie motion. Called when the user taps during a fling.
     *//*

    private fun stopScrolling() {
        mScroller!!.forceFinished(true)
        if (Build.VERSION.SDK_INT >= 11) {
            mAutoCenterAnimator!!.cancel()
        }

        onScrollFinished()
    }

    */
/**
     * Called when the user finishes a scroll action.
     *//*

    private fun onScrollFinished() {
        if (mAutoCenterInSlice) {
            centerOnCurrentItem()
        } else {
            mPieView!!.decelerate()
        }
    }

    */
/**
     * Kicks off an animation that will result in the pointer being centered in the
     * pie slice of the currently selected item.
     *//*

    private fun centerOnCurrentItem() {
        val current = mData[currentItem]
        var targetAngle = current.mStartAngle + (current.mEndAngle - current.mStartAngle) / 2
        targetAngle -= mCurrentItemAngle
        if (targetAngle < 90 && mPieRotation > 180) targetAngle += 360

        if (Build.VERSION.SDK_INT >= 11) {
            // Fancy animated version
            mAutoCenterAnimator!!.setIntValues(targetAngle)
            mAutoCenterAnimator!!.setDuration(AUTOCENTER_ANIM_DURATION.toLong()).start()
        } else {
            // Dull non-animated version
            //mPieView.rotateTo(targetAngle);
        }
    }

    */
/**
     * Internal child class that draws the pie chart onto a separate hardware layer
     * when necessary.
     *//*

    private inner class PieView
    */
/**
     * Construct a PieView
     *
     * @param context
     *//*

    (context: Context) : View(context) {
        // Used for SDK < 11
        private var mRotation = 0f
        private val mTransform = Matrix()
        private val mPivot = PointF()

        internal var mBounds: RectF

        */
/**
         * Enable hardware acceleration (consumes memory)
         *//*

        fun accelerate() {
            setLayerToHW(this)
        }

        */
/**
         * Disable hardware acceleration (releases memory)
         *//*

        fun decelerate() {
            setLayerToSW(this)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            if (Build.VERSION.SDK_INT < 11) {
                mTransform.set(canvas.matrix)
                mTransform.preRotate(mRotation, mPivot.x, mPivot.y)
                canvas.matrix = mTransform
            }

            for (it in mData) {
                mPiePaint!!.shader = it.mShader
                canvas.drawArc(mBounds,
                        (360 - it.mEndAngle).toFloat(),
                        (it.mEndAngle - it.mStartAngle).toFloat(),
                        true, mPiePaint!!)
            }
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            mBounds = RectF(0f, 0f, w.toFloat(), h.toFloat())
        }

        fun rotateTo(pieRotation: Float) {
            mRotation = pieRotation
            if (Build.VERSION.SDK_INT >= 11) {
                rotation = pieRotation
            } else {
                invalidate()
            }
        }

        fun setPivot(x: Float, y: Float) {
            mPivot.x = x
            mPivot.y = y
            if (Build.VERSION.SDK_INT >= 11) {
                pivotX = x
                pivotY = y
            } else {
                invalidate()
            }
        }
    }

    */
/**
     * View that draws the pointer on top of the pie chart
     *//*

    private inner class PointerView
    */
/**
     * Construct a PointerView object
     *
     * @param context
     *//*

    (context: Context) : View(context) {

        override fun onDraw(canvas: Canvas) {
            canvas.drawLine(mTextX, mPointerY, mPointerX, mPointerY, mTextPaint!!)
            canvas.drawCircle(mPointerX, mPointerY, mPointerRadius, mTextPaint!!)
        }
    }

    */
/**
     * Maintains the state for a data item.
     *//*

    private inner class Item {
        var mLabel: String? = null
        var mValue: Float = 0.toFloat()
        var mColor: Int = 0

        // computed values
        var mStartAngle: Int = 0
        var mEndAngle: Int = 0

        var mHighlight: Int = 0
        var mShader: Shader? = null
    }

    */
/**
     * Extends [GestureDetector.SimpleOnGestureListener] to provide custom gesture
     * processing.
     *//*

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            // Set the pie rotation directly.
            val scrollTheta = vectorToScalarScroll(
                    distanceX,
                    distanceY,
                    e2.x - mPieBounds.centerX(),
                    e2.y - mPieBounds.centerY())
            pieRotation = pieRotation - scrollTheta.toInt() / FLING_VELOCITY_DOWNSCALE
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            // Set up the Scroller for a fling
            val scrollTheta = vectorToScalarScroll(
                    velocityX,
                    velocityY,
                    e2.x - mPieBounds.centerX(),
                    e2.y - mPieBounds.centerY())
            mScroller!!.fling(
                    0,
                    pieRotation,
                    0,
                    scrollTheta.toInt() / FLING_VELOCITY_DOWNSCALE,
                    0,
                    0,
                    Integer.MIN_VALUE,
                    Integer.MAX_VALUE)

            // Start the animator and tell it to animate for the expected duration of the fling.
            if (Build.VERSION.SDK_INT >= 11) {
                mScrollAnimator!!.duration = mScroller!!.duration.toLong()
                mScrollAnimator!!.start()
            }
            return true
        }

        override fun onDown(e: MotionEvent): Boolean {
            // The user is interacting with the pie, so we want to turn on acceleration
            // so that the interaction is smooth.
            mPieView!!.accelerate()
            if (isAnimationRunning) {
                stopScrolling()
            }
            return true
        }
    }

    companion object {

        */
/**
         * Draw text to the left of the pie chart
         *//*

        val TEXTPOS_LEFT = 0

        */
/**
         * Draw text to the right of the pie chart
         *//*

        val TEXTPOS_RIGHT = 1

        */
/**
         * The initial fling velocity is divided by this amount.
         *//*

        val FLING_VELOCITY_DOWNSCALE = 4

        */
/**
         *
         *//*

        val AUTOCENTER_ANIM_DURATION = 250

        */
/**
         * Helper method for translating (x,y) scroll vectors into scalar rotation of the pie.
         *
         * @param dx The x component of the current scroll vector.
         * @param dy The y component of the current scroll vector.
         * @param x  The x position of the current touch, relative to the pie center.
         * @param y  The y position of the current touch, relative to the pie center.
         * @return The scalar representing the change in angular position for this scroll.
         *//*

        private fun vectorToScalarScroll(dx: Float, dy: Float, x: Float, y: Float): Float {
            // get the length of the vector
            val l = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

            // decide if the scalar should be negative or positive by finding
            // the dot product of the vector perpendicular to (x,y).
            val crossX = -y

            val dot = crossX * dx + x * dy
            val sign = Math.signum(dot)

            return l * sign
        }
    }


}
*/
