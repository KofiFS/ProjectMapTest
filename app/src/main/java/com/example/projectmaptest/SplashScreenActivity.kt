package com.example.projectmaptest

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout for the splash screen activity
        setContentView(R.layout.activity_splash)

        val textView: TextView = findViewById(R.id.textView)

        // Get the height of the screen
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()

        // Define the animation: drop down effect
        val dropDownAnimation = ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, -screenHeight, 0f)
        dropDownAnimation.duration = 1000 // Duration of the drop down animation in milliseconds
        dropDownAnimation.interpolator = AccelerateDecelerateInterpolator() // Use AccelerateDecelerateInterpolator for smooth acceleration

        // Define the animation: bounce up effect
        val bounceUpAnimation = ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, 0f, -50f)
        bounceUpAnimation.duration = 200 // Duration of the bounce up animation in milliseconds
        bounceUpAnimation.interpolator = AccelerateDecelerateInterpolator() // Use AccelerateDecelerateInterpolator for smooth acceleration

        // Define the animation: bounce down effect
        val bounceDownAnimation = ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, -50f, 0f)
        bounceDownAnimation.duration = 500 // Duration of the bounce down animation in milliseconds
        bounceDownAnimation.interpolator = BounceInterpolator() // Use AccelerateDecelerateInterpolator for smooth acceleration

        // Define the animation: scale up effect
        val scaleUpX = ObjectAnimator.ofFloat(textView, View.SCALE_X, 1.0f, 1.1f)
        scaleUpX.duration = 200
        scaleUpX.interpolator = AccelerateDecelerateInterpolator()

        val scaleUpY = ObjectAnimator.ofFloat(textView, View.SCALE_Y, 1.0f, 1.1f)
        scaleUpY.duration = 200
        scaleUpY.interpolator = AccelerateDecelerateInterpolator()

        // Define the animation: scale down effect
        val scaleDownX = ObjectAnimator.ofFloat(textView, View.SCALE_X, 1.1f, 1.0f)
        scaleDownX.duration = 200
        scaleDownX.interpolator = AccelerateDecelerateInterpolator()

        val scaleDownY = ObjectAnimator.ofFloat(textView, View.SCALE_Y, 1.1f, 1.0f)
        scaleDownY.duration = 200
        scaleDownY.interpolator = AccelerateDecelerateInterpolator()

        // Create an AnimatorSet for the bounce animations
        val bounceAnimatorSet = AnimatorSet()
        bounceAnimatorSet.playSequentially(bounceUpAnimation, bounceDownAnimation)

        // Create an AnimatorSet for the entire sequence
        val animatorSet = AnimatorSet()
        animatorSet.play(dropDownAnimation).before(bounceAnimatorSet)
        animatorSet.play(scaleUpX).with(scaleUpY).before(scaleDownX).before(scaleDownY)

        // Start the animation sequence
        animatorSet.start()

        // Handler to delay opening the MainActivity
        Handler().postDelayed({
            // Start MainActivity after 2 seconds
            startActivity(Intent(this, MainActivity::class.java))
            // Finish the current activity to prevent the user from going back to the splash screen
            finish()
        }, 2500) // 2500 milliseconds = 2.5 seconds
    }
}
