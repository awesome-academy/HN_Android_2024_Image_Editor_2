package com.example.imageEditor2.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import androidx.core.view.forEachIndexed
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imageEditor2.R
import com.example.imageEditor2.base.BaseFragment
import com.example.imageEditor2.base.BaseViewModel
import com.example.imageEditor2.databinding.FragmentSearchBinding
import com.example.imageEditor2.model.QueryModel
import com.example.imageEditor2.ui.detail.ImageDetailActivity
import com.example.imageEditor2.ui.search.adapter.SearchImageAdapter
import com.example.imageEditor2.ui.search.adapter.SearchItemCallback
import com.example.imageEditor2.ui.search.adapter.SearchTextAdapter
import com.example.imageEditor2.utils.ALL
import com.example.imageEditor2.utils.URL
import com.example.imageEditor2.utils.setSpanForString
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment :
    BaseFragment<FragmentSearchBinding>(),
    SearchItemCallback {
    private val mViewModel: SearchViewModel by viewModel()
    private val mAdapter by lazy {
        SearchImageAdapter(onClickImage = {
            val intent = Intent(requireContext(), ImageDetailActivity::class.java)
            intent.putExtra(URL, it)
            startActivity(intent)
        })
    }

    private var mQueryList = mutableListOf<QueryModel>()
    private val mSearchTextAdapter by lazy {
        SearchTextAdapter(this, mQueryList)
    }
    private var mPageQuery = 0

    override fun getViewBinding(inflater: LayoutInflater): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater)
    }

    override fun initViewModel(): BaseViewModel {
        return mViewModel
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView() {
        binding?.rbShop?.text =
            setSpanForString(
                getString(R.string.shop),
                resources.getDrawable(R.drawable.ic_shop, null),
            )

        binding?.recycleView?.adapter = mAdapter
        binding?.recycleViewSearch?.adapter = mSearchTextAdapter
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
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount &&
                        firstVisibleItemPosition >= 0 && totalItemCount >= mAdapter.currentList.size
                    ) {
                        mPageQuery++
                        mViewModel.searchPhotos(page = mPageQuery)
                    }
                }
            },
        )
        binding?.radioGroup?.setOnCheckedChangeListener { group, checkedId ->
            setBackgroundRadioButton(checkedId)
            mAdapter.submitList(null)
            when (checkedId) {
                R.id.rbIgtv -> {
                    mViewModel.getAllQueryFromLocal()
                    mViewModel.searchPhotos(query = getString(R.string.igtv))
                }

                R.id.rbShop -> {
                    mViewModel.searchPhotos(query = getString(R.string.shop))
                }

                R.id.rbStyle -> {
                    mViewModel.searchPhotos(query = getString(R.string.style))
                }

                R.id.rbSports -> {
                    mViewModel.searchPhotos(query = getString(R.string.sports))
                }

                R.id.rbAuto -> {
                    mViewModel.searchPhotos(query = getString(R.string.auto))
                }
            }
        }

        binding?.searchView?.setOnQueryTextListener(
            object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!query.isNullOrBlank()) {
                        mAdapter.submitList(null)
                        mViewModel.searchPhotos(query = query)
                        mViewModel.insertQuery(query)
                        binding?.searchView?.isIconified = true
                        val inputMethodManager =
                            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(
                            binding?.searchView?.windowToken,
                            0,
                        )
                        binding?.searchView?.clearFocus()
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            },
        )

        binding?.searchView?.setOnQueryTextFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                binding?.recycleViewSearch?.visibility = View.GONE
            } else {
                mSearchTextAdapter.updateDataList(mViewModel.listQuery)
                binding?.recycleViewSearch?.visibility = View.VISIBLE
            }
        }
        binding?.refreshLayout?.setOnRefreshListener {
            mPageQuery = 0
            binding?.radioGroup?.forEachIndexed { index, view ->
                view.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_radio_unpick)
            }
            binding?.searchView?.setQuery("", false)
            mAdapter.submitList(null)
            mViewModel.searchPhotos(query = ALL)
            binding?.refreshLayout?.isRefreshing = false
        }
    }

    override fun observeData() {
        mViewModel.photosLiveData.observe(viewLifecycleOwner) {
            val newList = mAdapter.currentList.toMutableList()
            newList.addAll(it.photoModels)
            mAdapter.submitList(newList)
        }
        mViewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun deleteItemQuery(
        queryModel: QueryModel,
        position: Int,
    ) {
        mQueryList.removeAt(position)
        mSearchTextAdapter.notifyDataSetChanged()
        mViewModel.deleteQuery(queryModel)
    }

    override fun selectQuery(query: String) {
        mAdapter.submitList(null)
        mViewModel.searchPhotos(query = query)
        binding?.searchView?.isIconified = true
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            binding?.searchView?.windowToken,
            0,
        )
        binding?.searchView?.clearFocus()
    }

    private fun setBackgroundRadioButton(id: Int) {
        binding?.radioGroup?.forEachIndexed { index, view ->
            view.background =
                if (view.id == id) {
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_radio_picking,
                    )
                } else {
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_radio_unpick)
                }
        }
    }
}
