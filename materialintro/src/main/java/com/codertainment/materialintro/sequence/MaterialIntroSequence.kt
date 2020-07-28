package com.codertainment.materialintro.sequence

import android.app.Activity
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import com.codertainment.materialintro.MaterialIntroConfiguration
import com.codertainment.materialintro.animation.MaterialIntroListener
import com.codertainment.materialintro.utils.materialIntro
import com.codertainment.materialintro.utils.preferencesManager
import com.codertainment.materialintro.view.MaterialIntroView

/*
 * Created by Shripal Jain
 * on 11/02/2020
 */

class MaterialIntroSequence private constructor(private val activity: Activity) {

  companion object {
    private val sequences = ArrayList<MaterialIntroSequence>()

    fun getInstance(activity: Activity): MaterialIntroSequence {
      val found = sequences.filter { it.activity == activity }
      return if (found.isEmpty()) {
        val mis = MaterialIntroSequence(activity)
        sequences.add(mis)
        mis
      } else {
        found[0]
      }
    }
  }

  private var mivs = ArrayList<MaterialIntroView>()
  private var interceptingLayer: View? = null
  private val decorViewGroup get() = activity.window.decorView as ViewGroup

  private var counter = 0
  private var isMivShowing = false
  private val handler by lazy {
    Handler()
  }

  /**
   * Whether to show the skip button
   */
  var showSkip = false

  /**
   * If enabled, once the user clicks on skip button, all new MIVs will be skipped too
   * If disabled, even after the user clicks on skip button and new MIVs are added after that, for e.g. for another fragment, the new MIVs will be shown
   */
  var persistSkip = false
  private var isSkipped = false

  private var materialIntroListener = object : MaterialIntroListener {
    override fun onIntroDone(onUserClick: Boolean, viewId: String) {
      materialIntroSequenceListener?.onProgress(onUserClick, viewId, counter, mivs.size)
      isMivShowing = false
      if (counter == mivs.size) {
        materialIntroSequenceListener?.onCompleted()
        removeClicksInterceptingLayer()
      } else {
        nextIntro()
      }
    }
  }

  /**
   * Delay (in ms) for first MIV to be shown
   */
  var initialDelay: Long = 500
  var materialIntroSequenceListener: MaterialIntroSequenceListener? = null

  fun add(config: MaterialIntroConfiguration) {
    val found = mivs.find { it.viewId == config.viewId || it.viewId == config.targetView?.tag?.toString() }
    if (found != null && activity.preferencesManager.isDisplayed(config.viewId ?: config.targetView?.tag?.toString())) return
    mivs.add(activity.materialIntro(config = config) {
      showSkip = this@MaterialIntroSequence.showSkip
      delayMillis = if (mivs.isEmpty()) initialDelay else 0
      materialIntroListener = this@MaterialIntroSequence.materialIntroListener
      skipButton.setOnClickListener {
        skip()
      }
    })
  }

  fun addConfig(func: MaterialIntroConfiguration.() -> Unit) {
    add(MaterialIntroConfiguration().apply {
      func()
    })
  }

  private fun skip() {
    isSkipped = true
    mivs[counter - 1].dismiss()
    for (i in 0 until mivs.size) {
      if (mivs[i].showOnlyOnce) {
        activity.preferencesManager.setDisplayed(mivs[i].viewId)
      }
    }
    counter = mivs.size
    materialIntroSequenceListener?.onCompleted()
    removeClicksInterceptingLayer()  
  }

  fun start() {
    if (isSkipped && persistSkip) {
      skip()
    } else {
      if (!isMivShowing) {
        nextIntro()
      }
    }
  }

  private fun nextIntro() {
    if (isSkipped && persistSkip) {
      skip()
    } else if (counter < mivs.size) {
      if (counter == 0) {
        addClicksInterceptingLayer()
      }
      isMivShowing = true
      handler.post {
        mivs[counter++].show(activity)
      }
    }
  }

  private fun addClicksInterceptingLayer() {
    interceptingLayer = View(activity).apply {
      isClickable = true
    }

    decorViewGroup.addView(interceptingLayer, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
}

  private fun removeClicksInterceptingLayer() {
     decorViewGroup.removeView(interceptingLayer)
  }
}

interface MaterialIntroSequenceListener {
  /**
   * @param onUserClick if the MIV was dismissed by the user on click or it was auto-dismissed because it was set as displayed
   * @param viewId viewId for the dismissed MIV
   * @param current index of the dismissed MIV
   * @param total Total number of MIVs in the current MaterialIntroSequence
   */
  fun onProgress(onUserClick: Boolean, viewId: String, current: Int, total: Int)

  /**
   * Called when all MIVs in the current MaterialIntroSequence have been dismissed
   */
  fun onCompleted()
}