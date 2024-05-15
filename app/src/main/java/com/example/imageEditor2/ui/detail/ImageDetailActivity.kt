package com.example.imageEditor2.ui.detail

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.drawToBitmap
import com.example.imageEditor.base.BaseActivity
import com.example.imageEditor.custom.CropSuccessCallback
import com.example.imageEditor.custom.OnFilterPicked
import com.example.imageEditor.custom.OnSwipeTouchListener
import com.example.imageEditor2.R
import com.example.imageEditor2.base.BaseViewModel
import com.example.imageEditor2.custom.FilterAdapter
import com.example.imageEditor2.custom.ImageListener
import com.example.imageEditor2.databinding.ActivityDetailImageBinding
import com.example.imageEditor2.utils.DEFAULT_EMOJI_SIZE
import com.example.imageEditor2.utils.DEFAULT_PROGRESS_VALUE
import com.example.imageEditor2.utils.RANGE_CONTRAST_AND_BRIGHTNESS
import com.example.imageEditor2.utils.URL
import com.example.imageEditor2.utils.displayImage
import com.example.imageEditor2.utils.displayImageWithBitmap
import com.example.imageEditor2.utils.dpToPx
import com.example.imageEditor2.utils.emojiToDrawable
import com.example.imageEditor2.utils.getEmojiDrawable
import org.koin.androidx.viewmodel.ext.android.viewModel

class ImageDetailActivity :
    BaseActivity<ActivityDetailImageBinding>(),
    CropSuccessCallback,
    OnFilterPicked {
    private val mUrl by lazy { intent.getStringExtra(URL) }
    private val mImageListener by lazy { ImageListener() }
    private var mScaleGestureDetector: GestureDetector? = null
    private var mIsFiltering = false
    private var mChangeImg = false
    private val mPath = Path()
    private var mCanvas: Canvas? = null
    private var mMutableBitmap: Bitmap? = null
        set(value) {
            field = value
            mCanvas = value?.let { Canvas(it) }
        }

    private var mPaint =
        Paint().apply {
            color = Color.BLACK
            strokeWidth = 5f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

    private var mLastTouchX = 0f
    private var mLastTouchY: Float = 0f
    private var mCroppedBitmap: Bitmap? = null
    private val mListEmoji = mutableListOf<ImageView>()
    private val mViewModel: DetailViewModel by viewModel()
    private lateinit var mEmoji: String
    private lateinit var mFilterAdapter: FilterAdapter

    override fun getViewBinding(): ActivityDetailImageBinding {
        return ActivityDetailImageBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): BaseViewModel {
        return mViewModel
    }

    override fun initView() {
        mScaleGestureDetector =
            GestureDetector(
                this,
                mImageListener,
            )
        mUrl?.let {
            binding.img.displayImage(
                it,
            ) { bitmap ->
                mMutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            }
            mFilterAdapter = FilterAdapter(it, this, this)
            binding.recycleViewFilterOption.adapter = mFilterAdapter
        }
        binding.sbContrast.progress = DEFAULT_PROGRESS_VALUE
        binding.sbBrightness.progress = DEFAULT_PROGRESS_VALUE
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        mImageListener.addListenerOnImageView(binding.img)
        binding.imgCrop.setOnClickListener {
            binding.cropView.visibility = View.VISIBLE
            binding.cropView.setCropSuccessCallback(this)
            binding.constrainConfirm.visibility = View.VISIBLE
            binding.constrainOption.visibility = View.GONE
        }
        mScaleGestureDetector?.setOnDoubleTapListener(mImageListener)
        binding.imgDownLoad.setOnClickListener {
            if (mChangeImg) {
                mListEmoji.forEach {
                    getEmojiDrawable(it, binding.img)
                }
                mViewModel.saveImage(
                    bitmap = binding.img.drawToBitmap(),
                    onSaved = { setupWhenSaved() },
                )
            } else {
                mUrl?.let {
                    mViewModel.downloadImage(it)
                }
            }
        }

        drawListener()

        binding.imgDone.setOnClickListener {
            if (binding.cropView.hasInstanceListener()) {
                mCroppedBitmap?.let { it1 ->
                    binding.img.displayImageWithBitmap(it1) { bitmap ->
                        mMutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                    }
                }
                binding.cropView.removeCropCallback()
                binding.cropView.visibility = View.GONE
            }
            if (mIsFiltering) {
                binding.img.displayImageWithBitmap(binding.img.drawToBitmap()) {
                    mMutableBitmap = it.copy(Bitmap.Config.ARGB_8888, true)
                }
                binding.sbContrast.progress = DEFAULT_PROGRESS_VALUE
                binding.sbBrightness.progress = DEFAULT_PROGRESS_VALUE
                mIsFiltering = false
            }
            if (binding.imgEmojiPreview.visibility == View.VISIBLE) {
                if (mEmoji.isNotBlank()) {
                    mChangeImg = true
                    val layoutParams =
                        ConstraintLayout.LayoutParams(
                            DEFAULT_EMOJI_SIZE.dpToPx(this),
                            DEFAULT_EMOJI_SIZE.dpToPx(this),
                        )
                    layoutParams.startToStart = R.id.img
                    layoutParams.topToTop = R.id.img
                    layoutParams.endToEnd = R.id.img
                    layoutParams.bottomToBottom = R.id.img
                    val imageView =
                        ImageView(this).apply {
                            this.layoutParams = layoutParams
                            setImageDrawable(emojiToDrawable(mEmoji, this@ImageDetailActivity))
                            setOnTouchListener(OnSwipeTouchListener(this))
                        }
                    mListEmoji.add(imageView)
                    binding.root.addView(imageView)
                }
            }
            hideAndShowElement()
            mImageListener.addListenerOnImageView(binding.img)
        }
        binding.imgCancel.setOnClickListener {
            setupDefaultView()
        }
        binding.imgFilter.setOnClickListener {
            binding.recycleViewFilterOption.visibility = View.VISIBLE
            binding.constrainConfirm.visibility = View.VISIBLE
            binding.constrainOption.visibility = View.GONE
        }
        contrastAndBrightness()
        binding.imgIcon.setOnClickListener {
            binding.parentEmoji.visibility = View.VISIBLE
            binding.constrainConfirm.visibility = View.VISIBLE
            binding.constrainOption.visibility = View.GONE
            binding.imgEmojiPreview.visibility = View.VISIBLE
        }
        binding.emojiPicker.setOnEmojiPickedListener {
            mEmoji = it.emoji
            binding.imgEmojiPreview.setImageDrawable(null)
            binding.imgEmojiPreview.setImageDrawable(emojiToDrawable(it.emoji, this))
        }
    }

    override fun observeData() {
        mViewModel.error.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        mViewModel.message.observe(this) {
            Toast.makeText(this, getString(it), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        mImageListener.removeListener()
        mScaleGestureDetector = null
        super.onDestroy()
    }

    override fun onCropSuccess(
        startPointF: PointF,
        endPointF: PointF,
    ) {
        mMutableBitmap?.let {
            mChangeImg = true
            // Tính toán hình chữ nhật cắt (crop rectangle) từ startPointF và endPointF
            val left = minOf(startPointF.x, endPointF.x)
            val top = minOf(startPointF.y, endPointF.y)
            val right = maxOf(startPointF.x, endPointF.x)
            val bottom = maxOf(startPointF.y, endPointF.y)
            val cropRect = RectF(left, top, right, bottom)

            // Tạo một bitmap mới từ phần đã cắt (crop) của bitmap gốc
            if (cropRect.left > 0 && cropRect.top > 0) {
                mCroppedBitmap =
                    Bitmap.createBitmap(
                        it,
                        cropRect.left.toInt(),
                        cropRect.top.toInt(),
                        cropRect.width().toInt(),
                        cropRect.height().toInt(),
                    )
            }
        }
    }

    override fun filterPicked(colorFilter: ColorFilter) {
        mChangeImg = true
        mIsFiltering = true
        binding.img.colorFilter = colorFilter
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun drawListener() {
        binding.img.setOnTouchListener { view, event ->
            if (mImageListener.hasListener()) {
                mScaleGestureDetector?.onTouchEvent(event)
            } else {
                val touchX = event.x
                val touchY = event.y
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        mLastTouchX = touchX
                        mLastTouchY = touchY
                        mPath.reset()
                        mPath.moveTo(touchX, touchY)
                        binding.rgColor.visibility = View.GONE
                    }

                    MotionEvent.ACTION_MOVE -> {
                        mPath.quadTo(
                            mLastTouchX,
                            mLastTouchY,
                            (touchX + mLastTouchX) / 2,
                            (touchY + mLastTouchY) / 2,
                        )
                        mLastTouchX = touchX
                        mLastTouchY = touchY
                        mCanvas?.drawPath(mPath, mPaint)
                        binding.img.setImageBitmap(mMutableBitmap)
                        mChangeImg = true
                    }

                    MotionEvent.ACTION_UP -> {
                        binding.rgColor.visibility = View.VISIBLE
                    }
                }
            }
            view.performClick()
            true
        }
        binding.imgDraw.setOnClickListener {
            mImageListener.removeListener()
            binding.constrainConfirm.visibility = View.VISIBLE
            binding.constrainOption.visibility = View.GONE
            binding.rgColor.visibility = View.VISIBLE
        }
        binding.rgColor.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbBlack -> mPaint.color = Color.BLACK
                R.id.rbRed -> mPaint.color = Color.RED
                R.id.rbBlue -> mPaint.color = Color.BLUE
                R.id.rbYellow -> mPaint.color = Color.YELLOW
                R.id.rbOrange -> mPaint.color = getColor(R.color.orange)
                R.id.rbPink -> mPaint.color = getColor(R.color.pink)
                R.id.rbWhite -> mPaint.color = Color.WHITE
            }
        }
    }

    private fun setupDefaultView() {
        mIsFiltering = false
        mChangeImg = false
        mUrl?.let {
            binding.img.displayImage(it) { bitmap ->
                mMutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            }
            binding.img.clearColorFilter()
        }
        hideAndShowElement()
        binding.cropView.removeCropCallback()
        mImageListener.addListenerOnImageView(binding.img)
        binding.sbContrast.progress = DEFAULT_PROGRESS_VALUE
        binding.sbBrightness.progress = DEFAULT_PROGRESS_VALUE
        binding.parentEmoji.visibility = View.GONE
        binding.imgEmojiPreview.setImageDrawable(null)
        binding.imgEmojiPreview.visibility = View.GONE
        if (mListEmoji.isNotEmpty()) {
            mListEmoji.forEach {
                binding.root.removeView(it)
            }
            mListEmoji.clear()
        }
    }

    private fun hideAndShowElement() {
        binding.recycleViewFilterOption.visibility = View.GONE
        binding.constraintContrast.visibility = View.GONE
        binding.constrainConfirm.visibility = View.GONE
        binding.constrainOption.visibility = View.VISIBLE
        binding.rgColor.visibility = View.GONE
        binding.cropView.visibility = View.GONE
        binding.parentEmoji.visibility = View.GONE
        binding.imgEmojiPreview.setImageDrawable(null)
        binding.imgEmojiPreview.visibility = View.GONE
    }

    private fun setupWhenSaved() {
        mUrl?.let {
            binding.img.displayImage(it) { bitmap ->
                mMutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            }
            binding.img.clearColorFilter()
            if (mListEmoji.isNotEmpty()) {
                mListEmoji.forEach {
                    binding.root.removeView(it)
                }
                mListEmoji.clear()
            }
        }
    }

    private fun contrastAndBrightness() {
        binding.imgContrast.setOnClickListener {
            binding.constraintContrast.visibility = View.VISIBLE
            binding.constrainConfirm.visibility = View.VISIBLE
            binding.constrainOption.visibility = View.GONE
        }
        binding.sbContrast.setOnSeekBarChangeListener(
            object : OnSeekBarChangeListener {
                override fun onProgressChanged(
                    p0: SeekBar?,
                    value: Int,
                    p2: Boolean,
                ) {
                    if (value > 0) {
                        val contrast = value.toFloat() / RANGE_CONTRAST_AND_BRIGHTNESS
                        binding.img.contrast = contrast
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    p0?.let {
                        if (p0.progress > 0) {
                            val contrast = p0.progress.toFloat() / RANGE_CONTRAST_AND_BRIGHTNESS
                            binding.img.contrast = contrast
                            mChangeImg = true
                        }
                    }
                }
            },
        )
        binding.sbBrightness.setOnSeekBarChangeListener(
            object : OnSeekBarChangeListener {
                override fun onProgressChanged(
                    p0: SeekBar?,
                    value: Int,
                    p2: Boolean,
                ) {
                    if (value > 0) {
                        val brightness = value.toFloat() / RANGE_CONTRAST_AND_BRIGHTNESS
                        binding.img.brightness = brightness
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    p0?.let {
                        if (p0.progress > 0) {
                            val brightness = p0.progress.toFloat() / RANGE_CONTRAST_AND_BRIGHTNESS
                            binding.img.brightness = brightness
                            mChangeImg = true
                        }
                    }
                }
            },
        )
    }
}
