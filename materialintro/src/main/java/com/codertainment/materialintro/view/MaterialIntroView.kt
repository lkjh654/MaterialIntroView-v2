package com.codertainment.materialintro.view

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.cardview.widget.CardView
import com.codertainment.materialintro.MaterialIntroConfiguration
import com.codertainment.materialintro.R
import com.codertainment.materialintro.animation.AnimationFactory
import com.codertainment.materialintro.animation.AnimationListener
import com.codertainment.materialintro.animation.MaterialIntroListener
import com.codertainment.materialintro.sequence.SkipLocation
import com.codertainment.materialintro.shape.*
import com.codertainment.materialintro.shape.Circle
import com.codertainment.materialintro.shape.Rect
import com.codertainment.materialintro.target.Target
import com.codertainment.materialintro.target.ViewTarget
import com.codertainment.materialintro.utils.Constants
import com.codertainment.materialintro.utils.Utils
import com.codertainment.materialintro.utils.preferencesManager
import com.google.android.material.button.MaterialButton

class MaterialIntroView : RelativeLayout {

  /**
   * Mask color
   */
  var maskColor = Constants.DEFAULT_MASK_COLOR

  /**
   * MaterialIntroView will start
   * showing after delayMillis seconds
   * passed
   */
  var delayMillis = Constants.DEFAULT_DELAY_MILLIS

  /**
   * We don't draw MaterialIntroView
   * until isReady field set to true
   */
  private var isReady = false

  /**
   * Show MaterialIntroView
   * with fade in animation if
   * this is enabled.
   */
  var isFadeInAnimationEnabled = true

  /**
   * Dismiss MaterialIntroView
   * with fade out animation if
   * this is enabled.
   */
  var isFadeOutAnimationEnabled = true

  /**
   * Animation duration
   */
  var fadeAnimationDurationMillis = Constants.DEFAULT_FADE_DURATION

  /**
   * targetShape focus on target
   * and clear circle to focus
   */
  private lateinit var targetShape: Shape

  /**
   * Focus Type
   */
  var focusType = Focus.ALL

  /**
   * FocusGravity type
   */
  var focusGravity = FocusGravity.CENTER

  /**
   * Target View
   */
  private lateinit var myTargetView: Target

  /**
   * Eraser
   */
  private lateinit var eraser: Paint

  /**
   * Handler will be used to
   * delay MaterialIntroView
   */
  private lateinit var myHandler: Handler

  /**
   * All views will be drawn to
   * this bitmap and canvas then
   * bitmap will be drawn to canvas
   */
  private var bitmap: Bitmap? = null
  private var canvas: Canvas? = null

  /**
   * Circle padding
   */
  var padding = Constants.DEFAULT_TARGET_PADDING

  /**
   * Layout myWidth/myHeight
   */
  private var myWidth = 0
  private var myHeight = 0

  /**
   * Dismiss on touch any where
   */
  var dismissOnTouch = false

  /**
   * Info card view container
   */
  private lateinit var infoView: RelativeLayout

  /**
   * Info CardView
   */
  private lateinit var infoCardView: CardView

  /**
   * Info TextView
   */
  private lateinit var infoTextView: TextView

  /**
   * Info dialog will be shown
   * If this value true
   */
  var isInfoEnabled = true

  /**
   * Info Text
   */
  var infoText: CharSequence = ""

  /**
   * Info Text Color
   */
  @ColorInt
  var infoTextColor: Int? = null

  /**
   * Info Text Size in sp
   */
  var infoTextSize: Float? = null

  /**
   * Info Text Alignment, Use View.TEXT_ALIGNMENT_
   */
  var infoTextAlignment: Int = View.TEXT_ALIGNMENT_CENTER

  /**
   * Info Text Custom Typeface
   */
  var infoTextTypeface: Typeface? = null

  /**
   * Card View Background Color
   */
  @ColorInt
  var infoCardBackgroundColor: Int? = null

  /**
   * Help Dialog Icon
   */
  private lateinit var helpIconView: ImageView

  /**
   * Help Icon will be shown if this is true
   */
  var isHelpIconEnabled = true

  /**
   * Drawable resource to set as help icon
   */
  @DrawableRes
  var helpIconResource: Int? = null

  /**
   * Drawable to set as help icon
   */
  var helpIconDrawable: Drawable? = null

  /**
   * Tint Help Icon
   */
  @ColorInt
  var helpIconColor: Int? = null

  /**
   * Custom View for info card
   */
  var infoCustomView: View? = null

  /**
   * Layout Resource for custom view
   */
  @LayoutRes
  var infoCustomViewRes: Int? = null

  /**
   * Dot view will appear center of
   * cleared target area
   */
  private lateinit var dotView: ImageView

  /**
   * Dot View will be shown if
   * this is true
   */
  var isDotViewEnabled = true

  /**
   * Dot View animated with zoom in & zoom out animation if this is true
   */
  var isDotAnimationEnabled = true

  /**
   * Tint Dot Icon
   */
  @ColorInt
  var dotIconColor: Int? = null

  /**
   * Check using this Id whether user learned
   * or not.
   */
  var viewId: String = ""

  /**
   * When layout completed, we set this true
   * Otherwise onGlobalLayoutListener stuck on loop.
   */
  private var isLayoutCompleted = false

  /**
   * Notify user when MaterialIntroView is dismissed
   */
  var materialIntroListener: MaterialIntroListener? = null

  /**
   * Perform click operation to target
   * if this is true
   */
  var isPerformClick = false

  /**
   * Show MIV only once
   */
  var showOnlyOnce = true

  /**
   * Mark view as displayed only when user clicks
   */
  var userClickAsDisplayed = true

  /**
   * Shape of target
   */
  var shapeType = ShapeType.CIRCLE

  /**
   * Use custom shape
   */
  var customShape: Shape? = null

  internal var showSkip = false

  /**
   * Location of the skip button
   */
  var skipLocation: SkipLocation = SkipLocation.BOTTOM_LEFT

  /**
   * Text for skip button
   */
  private var skipText: CharSequence = "Skip"

  /**
   * Apply custom styling to the skip button
   */
  private var skipButtonStyling: MaterialButton.() -> Unit = {}

  lateinit var skipButton: MaterialButton

  private var statusBarHeight = 0
  private var myVisibleBottom = 0

  var skipButtonMargin = Utils.dpToPx(16)

  private var dismissed = false

  constructor(context: Context) : super(context) {
    init()
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    init()
  }

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    init()
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
    init()
  }

  private fun init() {
    setWillNotDraw(false)
    visibility = INVISIBLE
    /**
     * initialize objects
     */
    skipButton = MaterialButton(context)
    myHandler = Handler()
    eraser = Paint().apply {
      color = -0x1
      xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
      flags = Paint.ANTI_ALIAS_FLAG
    }
    fitsSystemWindows = true
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    myWidth = measuredWidth
    myHeight = measuredHeight
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    if (!isReady) return
    if (bitmap == null) {
      bitmap?.recycle()
      bitmap = Bitmap.createBitmap(myWidth, myHeight, Bitmap.Config.ARGB_8888)
      this.canvas = Canvas(bitmap!!)
    }
    /**
     * Draw mask
     */
    this.canvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    this.canvas?.drawColor(maskColor)
    /**
     * Clear focus area
     */
    targetShape.draw(this.canvas!!, eraser, padding)
    canvas.drawBitmap(bitmap!!, 0f, 0f, null)
  }

  /**
   * Perform click operation when user
   * touches on target circle.
   *
   * @param event
   * @return
   */
  override fun onTouchEvent(event: MotionEvent): Boolean {
    val xT = event.x
    val yT = event.y
    val isTouchOnFocus = targetShape.isTouchOnFocus(xT.toDouble(), yT.toDouble())
    when (event.action) {
      MotionEvent.ACTION_DOWN -> {
        if (isTouchOnFocus && isPerformClick) {
          myTargetView.view.apply {
            isPressed = true
            invalidate()
          }
        }
        return true
      }
      MotionEvent.ACTION_UP -> {
        if (isTouchOnFocus || dismissOnTouch) {
          dismiss()
        }
        if (isTouchOnFocus && isPerformClick) {
          myTargetView.view.apply {
            performClick()
            isPressed = true
            invalidate()
            isPressed = false
            invalidate()
          }
        }
        return true
      }
    }
    return super.onTouchEvent(event)
  }

  var targetView
    get() = myTargetView.view
    set(value) {
      if (value.tag is String) {
        value.tag?.toString()?.let {
          viewId = it
        }
      }
      myTargetView = ViewTarget(value)
    }

  /**
   * Shows material view with fade in
   * animation
   *
   * @param activity
   */
  fun show(activity: Activity) {
    if (context.preferencesManager.isDisplayed(viewId)) {
      materialIntroListener?.onIntroDone(false, viewId)
      return
    }
    if (!::targetShape.isInitialized) {
      targetShape = when {
        customShape != null -> {
          customShape!!
        }
        shapeType == ShapeType.CIRCLE -> {
          Circle(myTargetView, focusType, focusGravity, padding)
        }
        else -> {
          Rect(myTargetView, focusType, focusGravity, padding)
        }
      }
    }

    if (isInfoEnabled) {
      infoView = LayoutInflater.from(context).inflate(R.layout.material_intro_card, null) as RelativeLayout
      infoCardView = infoView.findViewById(R.id.info_card_view)
      infoTextView = infoView.findViewById(R.id.info_text)
      helpIconView = infoView.findViewById(R.id.info_icon)
      if (infoCustomViewRes != null || infoCustomView != null) {
        infoCustomViewRes?.let {
          infoCustomView = LayoutInflater.from(context).inflate(it, infoCardView, false)
        }
        infoCardView.removeAllViews()
        infoCardView.addView(infoCustomView)
      } else {
        infoCardBackgroundColor?.let {
          infoCardView.setCardBackgroundColor(it)
        }
        infoTextView.text = infoText
        infoTextView.textAlignment = infoTextAlignment
        infoTextView.typeface = infoTextTypeface

        infoTextSize?.let {
          infoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, it)
        }

        infoTextColor?.let {
          infoTextView.setTextColor(it)
        }
        if (isHelpIconEnabled) {
          helpIconResource?.let {
            helpIconView.setImageResource(it)
          }
          helpIconDrawable?.let {
            helpIconView.setImageDrawable(it)
          }
          helpIconColor?.let {
            helpIconView.setColorFilter(it)
          }
        }
      }
    }

    if (isDotViewEnabled) {
      dotView = LayoutInflater.from(context).inflate(R.layout.dot_view, null) as ImageView
      dotView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
      dotIconColor?.let {
        dotView.setColorFilter(it, PorterDuff.Mode.SRC_IN)
      }
    }

    if (showSkip) {
      val rect = android.graphics.Rect()
      activity.window.decorView.getWindowVisibleDisplayFrame(rect)
      statusBarHeight = rect.top
      myVisibleBottom = rect.bottom
    }

    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
      override fun onGlobalLayout() {
        targetShape.reCalculateAll()
        if (targetShape.point.y != 0 && !isLayoutCompleted) {
          if (isInfoEnabled) {
            setInfoLayout()
          }
          if (isDotViewEnabled) {
            setDotViewLayout()
          }
          if (showSkip) {
            setSkipButtonLayout()
          }
          removeOnGlobalLayoutListener(this@MaterialIntroView, this)
        }
      }
    })

    (activity.window.decorView as ViewGroup).addView(this)
    isReady = true
    myHandler.postDelayed(
      {
        if (isFadeInAnimationEnabled)
          AnimationFactory.animateFadeIn(
            this@MaterialIntroView,
            fadeAnimationDurationMillis,
            object : AnimationListener.OnAnimationStartListener {
              override fun onAnimationStart() {
                visibility = VISIBLE
              }
            })
        else
          visibility = VISIBLE
      }, delayMillis
    )
    if (showOnlyOnce && !userClickAsDisplayed) {
      context.preferencesManager.setDisplayed(viewId)
    }
  }

  /**
   * Dismiss Material Intro View
   */
  fun dismiss() {
    //prevent from firing dismiss() method multiple times when quickly clicking the layer
    if (dismissed) {
      return
    }
    dismissed = true
    if (showOnlyOnce && userClickAsDisplayed) {
      context.preferencesManager.setDisplayed(viewId)
    }
    if (isFadeOutAnimationEnabled) {
      AnimationFactory.animateFadeOut(this, fadeAnimationDurationMillis, object : AnimationListener.OnAnimationEndListener {
        override fun onAnimationEnd() {
          removeSelf()
        }
      })
    } else {
      removeSelf()
    }
  }

  private fun removeSelf() {
    visibility = GONE
    removeMaterialView()
    materialIntroListener?.onIntroDone(true, viewId)
  }

  private fun removeMaterialView() {
    if (parent != null)
      (parent as ViewGroup).removeView(this)
  }

  /**
   * locate info card view above/below the
   * circle. If circle's Y coordinate is bigger than
   * Y coordinate of root view, then locate cardview
   * above the circle. Otherwise locate below.
   */
  private fun setInfoLayout() {
    myHandler.post {
      isLayoutCompleted = true
      infoParent?.removeView(infoView)
      val infoDialogParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.FILL_PARENT)
      if (targetShape.point.y < myHeight / 2) {
        infoView.gravity = Gravity.TOP
        infoDialogParams.setMargins(
          0,
          targetShape.point.y + targetShape.height / 2,
          0,
          0
        )
      } else {
        infoView.gravity = Gravity.BOTTOM
        infoDialogParams.setMargins(
          0,
          0,
          0,
          myHeight - (targetShape.point.y + targetShape.height / 2) + 2 * targetShape.height / 2
        )
      }
      infoView.layoutParams = infoDialogParams
      infoView.postInvalidate()
      addView(infoView)
      if (!isHelpIconEnabled) {
        helpIconView.visibility = GONE
      }
      infoView.visibility = VISIBLE
    }
  }

  private fun setDotViewLayout() {
    myHandler.post {
      dotParent?.removeView(dotView)
      val dotViewLayoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
      dotViewLayoutParams.height = Utils.dpToPx(Constants.DEFAULT_DOT_SIZE)
      dotViewLayoutParams.width = Utils.dpToPx(Constants.DEFAULT_DOT_SIZE)
      dotViewLayoutParams.setMargins(
        targetShape.point.x - (dotViewLayoutParams.width / 2),
        targetShape.point.y - (dotViewLayoutParams.height / 2),
        0,
        0
      )
      dotView.layoutParams = dotViewLayoutParams
      dotView.postInvalidate()
      addView(dotView)
      dotView.visibility = VISIBLE
      if (isDotAnimationEnabled) {
        AnimationFactory.performAnimation(dotView)
      }
    }
  }

  private fun setSkipButtonLayout() {
    myHandler.post {
      val s = Point()
      skipButton.text = skipText
      skipButton.apply {
        skipButtonStyling()
      }
      display.getSize(s)
      skipButton.measure(s.x, s.y)
      val skipButtonLayoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
      var topMargin = 0
      var leftMargin = 0

      val defaultMargin = skipButtonMargin

      when (skipLocation) {
        SkipLocation.BOTTOM_LEFT -> {
          leftMargin = defaultMargin
          topMargin = myVisibleBottom - skipButton.measuredHeight - defaultMargin
        }
        SkipLocation.BOTTOM_RIGHT -> {
          leftMargin = s.x - skipButton.measuredWidth - defaultMargin
          topMargin = myVisibleBottom - skipButton.measuredHeight - defaultMargin
        }
        SkipLocation.TOP_LEFT -> {
          leftMargin = defaultMargin
          topMargin = defaultMargin + statusBarHeight
        }
        SkipLocation.TOP_RIGHT -> {
          leftMargin = s.x - skipButton.measuredWidth - defaultMargin
          topMargin = defaultMargin + statusBarHeight
        }
      }
      skipButtonLayoutParams.setMargins(leftMargin, topMargin, 0, 0)
      skipButton.layoutParams = skipButtonLayoutParams
      skipButton.postInvalidate()
      addView(skipButton)
    }
  }

  fun withConfig(config: MaterialIntroConfiguration?) {
    if (config == null) return
    this.maskColor = config.maskColor

    this.delayMillis = config.delayMillis

    this.isFadeInAnimationEnabled = config.isFadeInAnimationEnabled
    this.isFadeOutAnimationEnabled = config.isFadeOutAnimationEnabled
    this.fadeAnimationDurationMillis = config.fadeAnimationDurationMillis

    this.focusType = config.focusType
    this.focusGravity = config.focusGravity

    this.padding = config.padding

    this.dismissOnTouch = config.dismissOnTouch

    this.isInfoEnabled = config.isInfoEnabled
    this.infoText = config.infoText
    this.infoTextColor = config.infoTextColor
    this.infoTextSize = config.infoTextSize
    this.infoTextAlignment = config.infoTextAlignment
    this.infoTextTypeface = config.infoTextTypeface
    this.infoCardBackgroundColor = config.infoCardBackgroundColor

    this.isHelpIconEnabled = config.isHelpIconEnabled
    this.helpIconResource = config.helpIconResource
    this.helpIconDrawable = config.helpIconDrawable
    this.helpIconColor = config.helpIconColor

    this.infoCustomView = config.infoCustomView
    this.infoCustomViewRes = config.infoCustomViewRes

    this.isDotViewEnabled = config.isDotViewEnabled
    this.isDotAnimationEnabled = config.isDotAnimationEnabled
    this.dotIconColor = config.dotIconColor

    config.viewId?.let {
      this.viewId = it
    }
    config.targetView?.let {
      this.targetView = it
    }

    this.isPerformClick = config.isPerformClick

    this.showOnlyOnce = config.showOnlyOnce
    this.userClickAsDisplayed = config.userClickAsDisplayed

    this.shapeType = config.shapeType
    this.customShape = config.customShape

    this.materialIntroListener = config.materialIntroListener

    this.skipLocation = config.skipLocation
    this.skipText = config.skipText
    this.skipButtonStyling = config.skipButtonStyling
  }

  private val infoParent
    get() = infoView.parent as ViewGroup?

  private val dotParent
    get() = dotView.parent as ViewGroup?

  companion object {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun removeOnGlobalLayoutListener(v: View, listener: ViewTreeObserver.OnGlobalLayoutListener) {
      v.viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }
  }
}
