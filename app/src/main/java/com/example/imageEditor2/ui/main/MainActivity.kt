package com.example.imageEditor2.ui.main

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import com.example.imageEditor.base.BaseActivity
import com.example.imageEditor2.R
import com.example.imageEditor2.base.BaseViewModel
import com.example.imageEditor2.databinding.ActivityMainBinding
import com.example.imageEditor2.ui.create.CreateImageFragment
import com.example.imageEditor2.ui.favorite.FavoriteFragment
import com.example.imageEditor2.ui.home.HomeFragment
import com.example.imageEditor2.ui.search.SearchFragment
import com.example.imageEditor2.utils.AUTHORIZE_DATA
import com.example.imageEditor2.utils.CREATE_INDEX
import com.example.imageEditor2.utils.FAVORITE_INDEX
import com.example.imageEditor2.utils.HOME_INDEX
import com.example.imageEditor2.utils.SEARCH_INDEX
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val viewModel: MainViewModel by viewModel()

    private val mMainViewPagerAdapter by lazy {
        MainViewPagerAdapter(
            supportFragmentManager,
            lifecycle,
        )
    }
    private val mAuthorizeData by lazy {
        intent.getStringExtra(AUTHORIZE_DATA)
    }

    private val mHomeFragment by lazy { HomeFragment() }
    private val mSearchFragment by lazy { SearchFragment() }
    private val mCreateImageFragment by lazy { CreateImageFragment() }
    private val mFavouriteFragment by lazy { FavoriteFragment.newInstance(mAuthorizeData ?: "") }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): BaseViewModel {
        return viewModel
    }

    override fun initView() {
        val launcher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!permissions.getOrDefault(Manifest.permission.POST_NOTIFICATIONS, false)) {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        startActivity(intent)
                    }
                }
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.CAMERA,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                ),
            )
        } else {
            launcher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ),
            )
        }
        setupPager()
    }

    override fun initListener() {
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    binding.pager2.currentItem = HOME_INDEX
                    true
                }

                R.id.search -> {
                    binding.pager2.currentItem = SEARCH_INDEX
                    true
                }

                R.id.add -> {
                    binding.pager2.currentItem = CREATE_INDEX
                    true
                }

                R.id.favourite -> {
                    binding.pager2.currentItem = FAVORITE_INDEX
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    override fun observeData() {
        // No-op
    }

    private fun setupPager() {
        mMainViewPagerAdapter.addFragment(mHomeFragment)
        mMainViewPagerAdapter.addFragment(mSearchFragment)
        mMainViewPagerAdapter.addFragment(mCreateImageFragment)
        mMainViewPagerAdapter.addFragment(mFavouriteFragment)
        binding.pager2.offscreenPageLimit = mMainViewPagerAdapter.itemCount
        binding.pager2.adapter = mMainViewPagerAdapter
        binding.pager2.isUserInputEnabled = false // disable swiping
    }
}
