/*
 * Copyright © 2020 - present | Notes by Javinator9889
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 *
 * Created by Javinator9889 on 20/06/20 - Notes.
 */
package com.javinator9889.notes.utils.animator

import android.animation.Animator
import android.animation.ValueAnimator
import com.airbnb.lottie.LottieAnimationView

class LoopAnimationListener(
    private val lottieAnimation: LottieAnimationView,
    private val minFrame: Int = -1,
    private val maxFrame: Int = -1
) : AnimationEndListener {
    override fun onAnimationEnd(animation: Animator?) {
        lottieAnimation.removeAllAnimatorListeners()
        lottieAnimation.repeatCount = ValueAnimator.INFINITE
        if (minFrame != -1)
            lottieAnimation.setMinFrame(minFrame)
        if (maxFrame != -1)
            lottieAnimation.setMaxFrame(maxFrame)
        lottieAnimation.playAnimation()
    }
}