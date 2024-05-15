package com.example.imageEditor2.custom

import android.content.Context
import android.graphics.ColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.RecyclerView
import com.example.imageEditor.custom.OnFilterPicked
import com.example.imageEditor2.R
import com.example.imageEditor2.utils.colorFilterList
import com.example.imageEditor2.utils.displayImage

class FilterAdapter(
    private val url: String,
    private val context: Context,
    private val onFilterPicked: OnFilterPicked,
) :
    RecyclerView.Adapter<FilterAdapter.ViewHolder>() {
    private val mFilterOptions = colorFilterList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mImgPreview: ImageFilterView = view.findViewById(R.id.imgPreview)

        fun bind(
            colorFilter: ColorFilter,
            url: String,
            onFilterPicked: OnFilterPicked,
        ) {
            mImgPreview.displayImage(url)
            mImgPreview.colorFilter = colorFilter
            mImgPreview.setOnClickListener { onFilterPicked.filterPicked(colorFilter) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_filter_preview, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mFilterOptions.size
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(mFilterOptions[position], url, onFilterPicked)
    }
}
