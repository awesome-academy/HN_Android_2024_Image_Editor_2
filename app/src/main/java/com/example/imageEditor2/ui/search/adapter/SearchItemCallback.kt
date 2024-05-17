package com.example.imageEditor2.ui.search.adapter

import com.example.imageEditor2.model.QueryModel

interface SearchItemCallback {
    fun deleteItemQuery(
        queryModel: QueryModel,
        position: Int,
    )

    fun selectQuery(query: String)
}
