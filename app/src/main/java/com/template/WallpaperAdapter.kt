package com.template

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class WallpaperAdapter(private var images: List<MainActivity.ImgWithName>,
                       private var imagheNames: Array<MainActivity.ImgName>,
                       private var appName: String,
                       private var imageClickListener: ImageClickListener,
                       private var context: Context):
    RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewHolder {
        var itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.wallpaper_rv_item, parent, false)
        return WallpaperViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WallpaperViewHolder, position: Int) {
        var imgWithName = images[position]

        holder.imageView.setImageDrawable(imgWithName.img)

        var imageName = imagheNames.find { it.filename.equals(imgWithName.text)}
        var text: String = appName
        if(imageName != null) {
            text = imageName?.imgname
        }
        val img = images[position].img

        var gestureDetector = GestureDetector(context, object:
            GestureDetector.SimpleOnGestureListener(){
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                Log.i("TAG", "onSingleTapConfirmed: ")
                imageClickListener.onSingleClick(img, text)
                return true
            }
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                Log.i("TAG", "onDoubleTap: ")
                imageClickListener.onDoubleClick(img, text)
                return true
            }
        })

        holder.textView.setText(text)
        holder.imageView.setOnTouchListener({ view, event ->
            gestureDetector.onTouchEvent(event)
        })
    }

    override fun getItemCount(): Int {
        return images.size
    }


    class WallpaperViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var imageView = itemView.findViewById<ImageView>(R.id.imageButton)
        var textView = itemView.findViewById<TextView>(R.id.textView)
    }
}