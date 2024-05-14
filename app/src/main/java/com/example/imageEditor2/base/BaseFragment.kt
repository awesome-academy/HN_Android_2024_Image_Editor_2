package com.example.imageEditor.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.imageEditor2.R
import com.example.imageEditor2.base.BaseViewModel

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    private var _binding: VB? = null
    val binding get() = _binding

    lateinit var viewModel: BaseViewModel

    private val loadingDialog by lazy { setupProgressDialog() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = getViewBinding(inflater)
        initView()
        viewModel = initViewModel()
        return _binding?.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        defaultObserve()
        initData()
        initListener()
    }

    abstract fun getViewBinding(inflater: LayoutInflater): VB

    abstract fun initViewModel(): BaseViewModel

    abstract fun initView()

    abstract fun initData()

    abstract fun initListener()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun defaultObserve() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), getString(it), Toast.LENGTH_SHORT).show()
        }
        viewModel.responseMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupProgressDialog(): AlertDialog {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(requireContext(), R.style.CustomDialog)
        builder.setCancelable(false)

        val myLayout = LayoutInflater.from(requireContext())
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
