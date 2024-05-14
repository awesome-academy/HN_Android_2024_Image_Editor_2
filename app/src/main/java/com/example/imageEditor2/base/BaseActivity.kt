package com.example.imageEditor.base

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.imageEditor2.R
import com.example.imageEditor2.base.BaseViewModel

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    private lateinit var _binding: VB
    val binding get() = _binding

    private lateinit var viewModel: BaseViewModel

    private val loadingDialog by lazy { setupProgressDialog() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        setContentView(_binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decor = window.decorView
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        viewModel = initViewModel()
        defaultObserve()
        initView()
        initListener()
        observeData()
    }

    abstract fun getViewBinding(): VB

    abstract fun initViewModel(): BaseViewModel

    abstract fun initView()

    abstract fun initListener()

    abstract fun observeData()

    private fun defaultObserve() {
        viewModel.isLoading.observe(this) {
            if (it) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        }
        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, getString(it), Toast.LENGTH_SHORT).show()
        }
        viewModel.responseMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupProgressDialog(): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.CustomDialog)
        builder.setCancelable(false)

        val myLayout = LayoutInflater.from(this)
        val dialogView: View = myLayout.inflate(R.layout.fragment_progress_dialog, null)

        builder.setView(dialogView)

        val dialog: AlertDialog = builder.create()
        val window: Window? = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
        }
        return dialog
    }
}
