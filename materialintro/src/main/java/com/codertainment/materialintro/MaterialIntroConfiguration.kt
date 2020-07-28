package com.codertainment.materialintro

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import com.codertainment.materialintro.animation.MaterialIntroListener
import com.codertainment.materialintro.sequence.SkipLocation
import com.codertainment.materialintro.shape.Focus
import com.codertainment.materialintro.shape.FocusGravity
import com.codertainment.materialintro.shape.Shape
import com.codertainment.materialintro.shape.ShapeType
import com.codertainment.materialintro.utils.Constants
import com.codertainment.materialintro.utils.Utils
import com.google.android.material.button.MaterialButton

data class MaterialIntroConfiguration(
  var maskColor: Int = Constants.DEFAULT_MASK_COLOR,
  var delayMillis: Long = Constants.DEFAULT_DELAY_MILLIS,

  var isFadeInAnimationEnabled: Boolean = true,
  var isFadeOutAnimationEnabled: Boolean = true,
  var fadeAnimationDurationMillis: Long = Constants.DEFAULT_FADE_DURATION,

  var focusType: Focus = Focus.ALL,
  var focusGravity: FocusGravity = FocusGravity.CENTER,

  var padding: Int = Constants.DEFAULT_TARGET_PADDING,

  var dismissOnTouch: Boolean = false,

  var isInfoEnabled: Boolean = true,
  var infoText: CharSequence = "",
  var infoTextColor: Int? = null,
  var infoTextSize: Float? = null,
  var infoTextAlignment: Int = View.TEXT_ALIGNMENT_CENTER,
  var infoTextTypeface: Typeface? = null,
  @ColorInt
  var infoCardBackgroundColor: Int? = null,

  var isHelpIconEnabled: Boolean = true,
  @DrawableRes
  var helpIconResource: Int? = null,
  var helpIconDrawable: Drawable? = null,
  @ColorInt
  var helpIconColor: Int? = null,

  var infoCustomView: View? = null,
  @LayoutRes
  var infoCustomViewRes: Int? = null,

  var isDotViewEnabled: Boolean = true,
  var isDotAnimationEnabled: Boolean = true,
  @ColorInt
  var dotIconColor: Int? = null,

  var viewId: String? = null,
  var targetView: View? = null,

  var isPerformClick: Boolean = false,

  var showOnlyOnce: Boolean = true,
  var userClickAsDisplayed: Boolean = true,

  var shapeType: ShapeType = ShapeType.CIRCLE,
  var customShape: Shape? = null,
  var materialIntroListener: MaterialIntroListener? = null,

  var skipLocation: SkipLocation = SkipLocation.BOTTOM_LEFT,
  var skipButtonMargin: Int = Utils.dpToPx(16),
  var skipText: CharSequence = "Skip",
  var skipButtonStyling: MaterialButton.() -> Unit = {}
)