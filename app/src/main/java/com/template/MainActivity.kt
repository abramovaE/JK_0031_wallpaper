package com.template

import android.app.WallpaperManager

import android.content.res.Resources
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.RecyclerView
import android.view.WindowManager

import android.util.DisplayMetrics
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.LinearLayout

import android.graphics.BitmapFactory

import android.graphics.Bitmap

import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.widget.ImageView
import com.google.android.material.appbar.CollapsingToolbarLayout


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
        Log.d("TAG", "images: $images")
        loadNames()


        var rv = binding.rv

        var appName = resources.getString(R.string.app_name)


        var adapter = WallpaperAdapter(images, imageNames, appName, this, this)
        rv.adapter = adapter

        var listener = object: View.OnTouchListener{
            override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                if(event?.actionMasked == MotionEvent.ACTION_POINTER_DOWN){
                    var  count = event?.pointerCount
                    Log.d("TAG", "count: $count")
                    showToast("After 1 second you will exit the application")
                    val timer = object: CountDownTimer(1000, 1000){
                        override fun onTick(p0: Long) {}
                        override fun onFinish() {
                            finish()
                        }
                    }
                    timer.start()
                }
                return view?.onTouchEvent(event) ?: true
            }
        }

        binding.rv.setOnTouchListener(listener)
        setContentView(binding.root)
    }

    private fun loadImages(){
        var files = assets.list(wallpapersFolder)

        if (files != null) {
            files.forEach {
                var file = assets.open("$wallpapersFolder/$it")
                var drawable = Drawable.createFromStream(file, null)
                var imgWithName = ImgWithName(it, drawable)
                images.add(imgWithName)
            }
        }
    }

    private fun loadNames(){

        val sb = StringBuilder()

        assets.open(namesFile).bufferedReader().forEachLine {
            sb.append(it)
        }
        Log.d("TAG", "names: ${sb.toString()}")

        val typeToken = object : TypeToken<Array<ImgName>>(){}.type
        imageNames = Gson().fromJson<Array<ImgName>>(sb.toString(), typeToken)

//        Log.d("TAG", "gson: $gson")


    }

    class ImgWithName(var text: String, var img: Drawable){
        override fun toString(): String {
            return text
        }
    }

    class ImgName(val filename: String, val imgname: String){

    }

    fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    }

    override fun onSingleClick(img: Drawable, imageName: String){


        var dialogBinding = DialogSelectWallpaperBinding.inflate(layoutInflater)
        dialogBinding.imageView.setImageDrawable(img)
        dialogBinding.textView2.setText(imageName)

        var dialog = AlertDialog.Builder(this).setView(dialogBinding.root).create()
        dialog.window?.setLayout(400, 400)

        dialogBinding.button.setOnClickListener({v->
            var wallpaperManager = WallpaperManager.getInstance(this)
            val displayMetrics = Resources.getSystem().displayMetrics
            val displayWidth = displayMetrics.widthPixels
            val displayHeight = displayMetrics.heightPixels

            var bitmap = (img as BitmapDrawable).bitmap


            wallpaperManager.suggestDesiredDimensions(displayWidth, displayHeight)
            wallpaperManager.setBitmap(bitmap)
            showToast("Обои установлены")
            dialog.dismiss()
        })



        dialog.show()

        dialogBinding.root.height

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window!!.attributes)
        val displayMetrics = Resources.getSystem().displayMetrics
        val displayWidth = displayMetrics.widthPixels * 0.7

        layoutParams.width = displayWidth.toInt()
//        layoutParams.height = dialogBinding.root.height
        dialog.window!!.attributes = layoutParams



    }


    override fun onDoubleClick(img: Drawable, imageName: String) {
        var doubleTapDialog=  AlertDialog.Builder(this)
            .setTitle(imageName)
            .setPositiveButton("OK",
            { dialogInterface, i ->
                finish()})
        doubleTapDialog.show()
    }
}

interface ImageClickListener{
    fun onSingleClick(img: Drawable, imageName: String)
    fun onDoubleClick(img: Drawable, imageName: String)
}
