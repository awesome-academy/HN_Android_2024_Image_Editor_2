package com.example.imageEditor2.ui.favorite

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imageEditor2.base.BaseFragment
import com.example.imageEditor2.base.BaseViewModel
import com.example.imageEditor2.databinding.FragmentFavouriteBinding
import com.example.imageEditor2.model.response.AuthorizeResponse
import com.example.imageEditor2.ui.detail.ImageDetailActivity
import com.example.imageEditor2.ui.favorite.adapter.FavoriteAdapter
import com.example.imageEditor2.ui.favorite.adapter.OnClickImage
import com.example.imageEditor2.utils.URL
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment :
    BaseFragment<FragmentFavouriteBinding>(),
    OnClickImage {
    private val mViewModel: FavoriteViewModel by viewModel()
    private val mAdapter by lazy { FavoriteAdapter(this) }
    private var mPageQuery = 1
    private val mNameUser by lazy {
        Gson().fromJson(
            this.arguments?.getString(DATA),
            AuthorizeResponse::class.java,
        ).username
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentFavouriteBinding {
        return FragmentFavouriteBinding.inflate(inflater)
    }

    override fun initViewModel(): BaseViewModel {
        return mViewModel
    }

    override fun initView() {
        binding?.recycleViewFavorite?.adapter = mAdapter
    }

    override fun initListener() {
        binding?.recycleViewFavorite?.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int,
                ) {
                    super.onScrolled(recyclerView, dx, dy)
                    val linearLayoutManager: LinearLayoutManager =
                        recyclerView.layoutManager as LinearLayoutManager
                    if (dy > 0 && linearLayoutManager.findLastCompletelyVisibleItemPosition() == mAdapter.currentList.size - 1) {
                        mPageQuery++
                        mViewModel.getFavoriteList(mNameUser, mPageQuery)
                    }
                }
            },
        )
    }

    override fun observeData() {
        mViewModel.photosLiveData.observe(viewLifecycleOwner) {
            val newList = mAdapter.currentList.toMutableList()
            newList.addAll(it)
            if (newList.isEmpty()) {
                binding?.tvEmpty?.visibility = View.VISIBLE
            } else {
                mAdapter.submitList(newList)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.getFavoriteList(mNameUser)
    }

    override fun onPause() {
        super.onPause()
        mPageQuery = 1
        mAdapter.submitList(null)
        mAdapter.resetStateList()
    }

    override fun likeImage(id: String) {
        mViewModel.likeImage(id)
    }

    override fun dislikeImage(id: String) {
        mViewModel.dislikeImage(id)
    }

    override fun clickDetailImage(url: String) {
        val intent = Intent(requireContext(), ImageDetailActivity::class.java)
        intent.putExtra(URL, url)
        startActivity(intent)
    }

    companion object {
        private const val DATA = "AuthorizeData"

        @JvmStatic
        fun newInstance(authorizeResponse: String): FavoriteFragment =
            FavoriteFragment().apply {
                arguments = bundleOf(Pair(DATA, authorizeResponse))
            }
    }
}
