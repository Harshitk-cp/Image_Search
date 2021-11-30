package com.harshit.imagesearch.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.harshit.imagesearch.R

class TryOnActivity : AppCompatActivity() {

    lateinit var tryOnImage: ImageView
    lateinit var imgUser: ImageView
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_try_on)

        tryOnImage = findViewById(R.id.tryOnImage)
        imgUser = findViewById(R.id.imgUser)

        val gettingTryOnImage = intent
        val a = gettingTryOnImage.extras

        if (a != null) {
            val i = a["userPassImage"] as Bitmap?
            tryOnImage.setImageBitmap(i)
        }

        val gettingGalleryUserImage = intent
        val b = gettingGalleryUserImage.extras

        if (b != null) {
            val j = b["GalleryUserImage"] as Uri?
            val imgUri = Uri.parse(j.toString())
            Glide.with(imgUser).load(imgUri).into(imgUser)
        }

        val gettingCameraUserImage = intent
        val c = gettingCameraUserImage.extras

        if (c != null) {
            val k = c["CameraUserImage"] as Bitmap?
            imgUser.setImageBitmap(k)
        }


        tryOnImage.setOnTouchListener(OnTouchListener { view, event ->
            var xDown = 0f
            var yDown = 0f
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    xDown = event.x
                    yDown = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val movedX: Float
                    val movedY: Float
                    movedX = event.x
                    movedY = event.y
                    val distanceX = movedX - xDown
                    val distanceY = movedY - yDown
                    tryOnImage.setX(tryOnImage.getX() + distanceX)
                    tryOnImage.setY(tryOnImage.getY() + distanceY)

                    xDown = movedX
                    yDown - movedY
                }
            }
            true
        })

    }
}