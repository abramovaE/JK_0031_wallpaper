package com.template

import android.content.Context
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WallpaperAdapter(private var images: List<MainActivity.ImgWithName>,
                       private var imageNames: Array<MainActivity.ImgName>,
                       private var appName: String,
                       private var imageClickListener: ImageClickListener,
                       private var context: Context):
    RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.wallpaper_rv_item, parent, false)
        return WallpaperViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WallpaperViewHolder, position: Int) {
        val imgWithName = images[position]

        holder.imageView.setImageDrawable(imgWithName.img)

        val imageName = imageNames.find { it.fileName == imgWithName.fileName }
        var text: String = appName
        if(imageName != null) {
            text = imageName.imgName
        }
        val img = images[position].img

        val gestureDetector = GestureDetector(context, object:
            GestureDetector.SimpleOnGestureListener(){
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                imageClickListener.onSingleClick(img, text)
                return true
            }
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                imageClickListener.onDoubleClick(img, text)
                return true
            }
        })

        holder.textView.text = text
        holder.imageView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }


    class WallpaperViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var imageView = itemView.findViewById<ImageView>(R.id.imageButton)
        var textView = itemView.findViewById<TextView>(R.id.textView)
    }
}