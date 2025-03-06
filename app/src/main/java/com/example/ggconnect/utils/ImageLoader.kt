package com.example.ggconnect.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.ggconnect.R
import com.google.firebase.storage.FirebaseStorage
import java.lang.ref.WeakReference

class ImageLoader private constructor(context: Context) {
    private val contextRef = WeakReference(context)

    fun loadImage(source: String, imageView: ImageView, placeholder: Int = R.drawable.unavailable_photo) {
        contextRef.get()?.let { context ->
            if (source.startsWith("gs://")) {
                // Handle Firebase Storage references
                val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(source)
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    loadImageFromUrl(uri, imageView, placeholder)
                }.addOnFailureListener {
                    imageView.setImageResource(placeholder)
                }
            } else {
                loadImageFromUrl(Uri.parse(source), imageView, placeholder)
            }
        }
    }

    private fun loadImageFromUrl(uri: Uri, imageView: ImageView, placeholder: Int) {
        Glide.with(imageView.context)
            .load(uri)
            .centerCrop()
            .placeholder(placeholder)
            .into(imageView)
    }

    companion object {
        @Volatile
        private var instance: ImageLoader? = null

        fun init(context: Context): ImageLoader {
            return instance ?: synchronized(this) {
                instance ?: ImageLoader(context).also { instance = it }
            }
        }

        fun getInstance(): ImageLoader {
            return instance ?: throw IllegalStateException(
                "ImageLoader must be initialized by calling init(context) before use."
            )
        }
    }
}
