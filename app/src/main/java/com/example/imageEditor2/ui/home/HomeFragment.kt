package com.example.imageEditor2.ui.home

import android.content.Intent
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imageEditor2.base.BaseFragment
import com.example.imageEditor2.base.BaseViewModel
import com.example.imageEditor2.databinding.FragmentHomeBinding
import com.example.imageEditor2.ui.detail.ImageDetailActivity
import com.example.imageEditor2.ui.home.adapter.CollectionAdapter
import com.example.imageEditor2.ui.home.adapter.OnClickImage
import com.example.imageEditor2.utils.URL
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>(), OnClickImage {
    private val viewModel: HomeViewModel by viewModel()
    private val mAdapter by lazy { CollectionAdapter(this) }
    private var mPageQuery = 0

    override fun getViewBinding(inflater: LayoutInflater): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater)
    }

    override fun initViewModel(): BaseViewModel {
        return viewModel
    }

    override fun initView() {
        binding?.recycleView?.adapter = mAdapter
    }

    override fun initListener() {
        binding?.recycleView?.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int,
                ) {
                    super.onScrolled(recyclerView, dx, dy)
                    val linearLayoutManager: LinearLayoutManager =
                        recyclerView.layoutManager as LinearLayoutManager
                    if (dy > 0 && linearLayoutManager.findLastCompletelyVisibleItemPosition() + 1 == mAdapter.currentList.size - 1) {
                        mPageQuery++
                        viewModel.getCollections(mPageQuery)
                    }
                }
            },
        )
    }

    override fun observeData() {
        viewModel.collectionsLiveData.observe(viewLifecycleOwner) {
            val newList = mAdapter.currentList.toMutableList()
            newList.addAll(it)
            mAdapter.submitList(newList)
        }
    }

    override fun clickImage(url: String) {
        val intent = Intent(requireContext(), ImageDetailActivity::class.java)
        intent.putExtra(URL, url)
        startActivity(intent)
    }

    override fun doubleTapForLikeImage(id: String) {
        viewModel.likeImage(id)
    }

    override fun clickLike(index: Int) {
        mAdapter.currentList.toMutableList().apply {
            this[index].coverPhoto.likedByUser = !this[index].coverPhoto.likedByUser
            if (this[index].coverPhoto.likedByUser) {
                this[index].coverPhoto.likes++
            } else {
                this[index].coverPhoto.likes--
            }
            mAdapter.notifyItemChanged(index)
        }
    }
}
