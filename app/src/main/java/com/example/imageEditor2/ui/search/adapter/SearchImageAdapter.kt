package com.example.imageEditor2.ui.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.imageEditor2.R
import com.example.imageEditor2.model.PhotoModel
import com.example.imageEditor2.model.Urls
import com.example.imageEditor2.utils.displayImage

class SearchImageAdapter(private val onClickImage: (String) -> Unit) :
    ListAdapter<PhotoModel, SearchImageAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<PhotoModel>() {
            override fun areItemsTheSame(
                oldItem: PhotoModel,
                newItem: PhotoModel,
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: PhotoModel,
                newItem: PhotoModel,
            ): Boolean {
                return oldItem.id == newItem.id
            }
        },
    ) {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val img: ImageView = view.findViewById(R.id.img)

        fun bind(
            url: Urls,
            onClickImage: (String) -> Unit,
        ) {
            img.displayImage(url.regular)
            img.setOnClickListener {
                onClickImage(url.regular)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position).urls, onClickImage)
    }
}
