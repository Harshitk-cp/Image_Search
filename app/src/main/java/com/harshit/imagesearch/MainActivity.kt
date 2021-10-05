package com.harshit.imagesearch

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import android.content.DialogInterface
import android.view.View
import androidx.core.view.isVisible


class MainActivity : AppCompatActivity() {

    private lateinit var txtResult: TextView
    private lateinit var btnGallery: Button
    private lateinit var btnCamera: Button
    private lateinit var txtDisplay: TextView
    lateinit var imgImage: ImageView
    lateinit var imgDisplay: ImageView

    private val CAMERA_PERMISSION_CODE = 123
    private val READ_STORAGE_PERMISSION_CODE = 123
    private val WRITE_STORAGE_PERMISSION_CODE = 123

    private val TAG = "MyTag"

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    lateinit var inputImage: InputImage
    lateinit var imageLabeler: ImageLabeler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgImage = findViewById(R.id.imgImage)
        imgDisplay = findViewById(R.id.imgDisplay)
        txtResult = findViewById(R.id.txtResult)
        btnGallery = findViewById(R.id.btnGallery)
        btnCamera = findViewById(R.id.btnCamera)
        txtDisplay = findViewById(R.id.txtDisplay)

        imgImage.visibility = View.GONE
        txtResult.visibility = View.GONE

        imageLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data = result?.data
            try {
                val photo = data?.extras?.get("data") as Bitmap
                imgImage.setImageBitmap(photo)
                inputImage = InputImage.fromBitmap(photo, 0)
                imgImage.visibility = View.VISIBLE
                txtResult.visibility = View.VISIBLE
                txtDisplay.visibility = View.INVISIBLE
                imgDisplay.visibility = View.INVISIBLE
                processImage()
            } catch (e: Exception) {
                Log.d(TAG, "onActivityResult: ${e.message}")
            }
        }

        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data = result?.data
            try {
                inputImage = InputImage.fromFilePath(this@MainActivity, data?.data)
                imgImage.setImageURI(data?.data)
                imgImage.visibility = View.VISIBLE
                txtResult.visibility = View.VISIBLE
                imgDisplay.visibility = View.INVISIBLE
                txtDisplay.visibility = View.INVISIBLE
                    processImage()
            } catch (e: Exception) {

            }
        }


        btnGallery.setOnClickListener {
            val storageIntent = Intent()
            storageIntent.type = "image/*"
            storageIntent.action = Intent.ACTION_GET_CONTENT
            galleryLauncher.launch(storageIntent)
        }

        btnCamera.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(cameraIntent)
        }



    }

    private fun processImage() {

        imageLabeler.process(inputImage)
            .addOnSuccessListener {
                var result = ""
                for (label in it) {
                    result = result + " " + label.text
                }

                txtResult.text = result

            }.addOnFailureListener{
                Log.d(TAG, "processImage: ${it.message}")
            }

    }


    private fun checkPermission(permission:String, requestCode: Int){
        if(ContextCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.CAMERA)==PackageManager.PERMISSION_DENIED){

            showMessageOKCancel(
                "You need to allow camera usage"
            ) { dialog, which ->
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
            }



        }else(
                null
        )

    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode== CAMERA_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                checkPermission(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    READ_STORAGE_PERMISSION_CODE
                )
            }
            else{
                Toast.makeText(this@MainActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }else if(requestCode== READ_STORAGE_PERMISSION_CODE){
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    checkPermission(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        WRITE_STORAGE_PERMISSION_CODE
                    )
                }
                else{
                    Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
                }
        }
        else if(requestCode== WRITE_STORAGE_PERMISSION_CODE)
            {
                if(!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
                }

            }

    }

    override fun onBackPressed() {
        if(txtResult.isVisible || imgImage.isVisible){ 
            imgImage.visibility = View.GONE
            txtResult.visibility = View.GONE
            txtDisplay.visibility = View.VISIBLE
            imgDisplay.visibility = View.VISIBLE
        }else{
            super.onBackPressed()
        }
    }

    override fun onStart() {
        checkPermission( android.Manifest.permission.CAMERA,
            CAMERA_PERMISSION_CODE)
        super.onStart()
    }



}