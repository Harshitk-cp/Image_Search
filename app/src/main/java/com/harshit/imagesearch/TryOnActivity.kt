package com.harshit.imagesearch

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide

class TryOnActivity : AppCompatActivity() {

    lateinit var tryOnImage: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_try_on)

        tryOnImage = findViewById(R.id.tryOnImage)

        val gettingTryOnImage = intent
        val a = gettingTryOnImage.extras

        if (a != null) {
            val i = a["TryOnImage"] as String?
            val imgUri = i.toString()
            Glide.with(tryOnImage).load(imgUri).into(tryOnImage)
        }

    }
}