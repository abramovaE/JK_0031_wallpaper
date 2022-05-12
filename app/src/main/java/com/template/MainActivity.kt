package com.template

import android.app.WallpaperManager

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.template.databinding.ActivityMainBinding
import com.template.databinding.DialogSelectWallpaperBinding
import java.lang.StringBuilder

import android.graphics.drawable.BitmapDrawable
import android.os.CountDownTimer
import android.view.*
import android.widget.Toast
import android.view.MotionEvent
import android.view.WindowManager

class MainActivity : AppCompatActivity(), ImageClickListener {

    private lateinit var binding: ActivityMainBinding
    private var images = mutableListOf<ImgWithName>()
    private lateinit var imageNames: Array<ImgName>
    private val wallpapersFolder = "wallpapers"
    private val namesFile: String = "names"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        loadImages()
        loadNames()

        val rv = binding.rv
        val appName = resources.getString(R.string.app_name)
        val adapter = WallpaperAdapter(images, imageNames, appName, this, this)

        val listener = View.OnTouchListener { view, event ->
            if(event?.actionMasked == MotionEvent.ACTION_POINTER_DOWN){
                val count = event.pointerCount
                if(count == 3) {
                    showToast(getString(R.string.exit_1_sec))
                    val timer = object : CountDownTimer(1000, 1000) {
                        override fun onTick(p0: Long) {}
                        override fun onFinish() {
                            finish()
                        }
                    }
                    timer.start()
                }
            }
            view?.onTouchEvent(event) ?: false
        }

        rv.setOnTouchListener(listener)
        rv.adapter = adapter

        setContentView(binding.root)
    }

    private fun loadImages(){
        val files = assets.list(wallpapersFolder)
        files?.forEach {
            val file = assets.open("$wallpapersFolder/$it")
            val drawable = Drawable.createFromStream(file, null)
            val imgWithName = ImgWithName(it, drawable)
            images.add(imgWithName)
        }
    }

    private fun loadNames(){
        val sb = StringBuilder()
        assets.open(namesFile).bufferedReader().forEachLine {
            sb.append(it)
        }
        val typeToken = object : TypeToken<Array<ImgName>>(){}.type
        imageNames = Gson().fromJson(sb.toString(), typeToken)
    }

    class ImgWithName(var fileName: String, var img: Drawable){
        override fun toString(): String {
            return fileName
        }
    }

    class ImgName(val fileName: String, val imgName: String)

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onSingleClick(img: Drawable, imageName: String){


        val dialogBinding = DialogSelectWallpaperBinding.inflate(layoutInflater)
        dialogBinding.imageView.setImageDrawable(img)
        dialogBinding.textView2.text = imageName

        val dialog = AlertDialog.Builder(this).setView(dialogBinding.root).create()
        dialog.window?.setLayout(400, 400)

        dialogBinding.button.setOnClickListener {
            val wallpaperManager = WallpaperManager.getInstance(this)

            val displayMetrics = Resources.getSystem().displayMetrics
            val displayWidth = displayMetrics.widthPixels
            val displayHeight = displayMetrics.heightPixels

            val bitmap = (img as BitmapDrawable).bitmap

            wallpaperManager.suggestDesiredDimensions(displayWidth, displayHeight)
            wallpaperManager.setBitmap(bitmap)
            showToast(getString(R.string.walpaper_set))
            dialog.dismiss()
        }

        dialog.show()

        dialogBinding.root.height

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window!!.attributes)
        val displayMetrics = Resources.getSystem().displayMetrics
        val displayWidth = displayMetrics.widthPixels * 0.7
        layoutParams.width = displayWidth.toInt()
        dialog.window!!.attributes = layoutParams
    }


    override fun onDoubleClick(img: Drawable, imageName: String) {
        val doubleTapDialog=  AlertDialog.Builder(this)
            .setTitle(imageName)
            .setPositiveButton("OK") { _, _ ->
                finish()
            }
        doubleTapDialog.show()
    }
}

interface ImageClickListener{
    fun onSingleClick(img: Drawable, imageName: String)
    fun onDoubleClick(img: Drawable, imageName: String)
}
