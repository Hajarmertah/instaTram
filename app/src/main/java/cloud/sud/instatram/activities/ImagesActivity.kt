package cloud.sud.instatram.activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import cloud.sud.instatram.HomeAdapter.Companion.mtitle
import cloud.sud.instatram.HomeAdapter.Companion.stationID
import cloud.sud.instatram.IMAGE_SOURCE_LOCAL
import cloud.sud.instatram.ImageViewModel
import cloud.sud.instatram.ImageViewModelFactory
import cloud.sud.instatram.R
import cloud.sud.instatram.application.ImApplication
import cloud.sud.instatram.databinding.ActivityImagesBinding
import cloud.sud.instatram.databinding.ImageSelectionDialogBinding
import cloud.sud.instatram.databinding.PostImageBinding
import cloud.sud.instatram.fragments.ImageFragment
import cloud.sud.instatram.model.ImageEntity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*




class ImagesActivity : BaseActivity() {

    private var mImagePath: String = ""
    private val picFragment = ImageFragment()
    private lateinit var stationName: String
    var stationId: String = "21"
    private lateinit var mBinding: ActivityImagesBinding
    private lateinit var postBinding: PostImageBinding
    private val mPicViewModel: ImageViewModel by viewModels {
        ImageViewModelFactory((application as ImApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        checkTheme()
        loadLocate()
        super.onCreate(savedInstanceState)
        mBinding = ActivityImagesBinding.inflate(layoutInflater)
        postBinding = PostImageBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        stationName = intent.getStringExtra(mtitle).toString()
        stationId = intent.getStringExtra(stationID).toString()

        val bundle = Bundle()
        bundle.putString(STATION_ID_KEY, stationId)
        picFragment.arguments = bundle


        supportActionBar?.title = stationName
        mBinding.addPic.setOnClickListener {
            imageSelectionDialog()

        }
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_pic_fragment, picFragment)
            commit()
        }

    }

    companion object {
        private const val CAMERA_CODE = 1
        private const val GALLERY_CODE = 2
        private const val IMAGE_DIRECTORY = "Pictures" //InstTramImages
        const val STATION_ID_KEY = "station id key"   //send stationId to PicFragment
        const val HOME_OR_MAP_FRAGMENT =
            "map/home Fragment"       //data:  0=homeFragment  1=mapFragment

    }


    private fun imageSelectionDialog() {
        val dialog = Dialog(this)
        val selBinding: ImageSelectionDialogBinding =
            ImageSelectionDialogBinding.inflate(layoutInflater)
        dialog.setContentView(selBinding.root)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 350)
        dialog.window?.setBackgroundDrawableResource(R.color.white_black)
        selBinding.isCamera.setOnClickListener {
            validatePermission()
            dialog.dismiss()
        }

        selBinding.isGallery.setOnClickListener {
            openImageGallery()
            dialog.dismiss()
        }
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun postImageDialog() {
        setContentView(postBinding.root)
//   dialog.setCanceledOnTouchOutside(false)
        postBinding.btnEdit.setOnClickListener {
            imageSelectionDialog()
        }
        postBinding.btnCancel.setOnClickListener {

            AlertDialog.Builder(this@ImagesActivity)
                .setTitle(getString(R.string.cancel_post_title))
                .setMessage(getString(R.string.cancel_post_message))
                .setPositiveButton(getString(R.string.keep_editing)) { iDialog, _ ->
                    iDialog.dismiss()
                }
                .setNegativeButton(getString(R.string.discard_post)) { iDialog, _ ->
                    setContentView(mBinding.root)
                    Toast.makeText(this, getString(R.string.canceled), Toast.LENGTH_SHORT).show()
                    iDialog.dismiss()
                }.show()
                .let {
                    it.setCanceledOnTouchOutside(false)
                    it.window?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    it.window?.setBackgroundDrawableResource(R.color.white_black)
                }
        }

        postBinding.btnPost.setOnClickListener {
            val imageTitle = postBinding.etTitle.text.toString().trim { it <= ' ' }
            val dateTime = LocalDateTime.now()
            val date: String = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            when {
                TextUtils.isEmpty(mImagePath) -> {
                    Toast.makeText(this, getString(R.string.no_picture), Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(imageTitle) -> {
                    Toast.makeText(this, getString(R.string.no_title), Toast.LENGTH_LONG).show()
                }
                else -> {
                    val picDetails: ImageEntity = ImageEntity(
                        mImagePath,
                        IMAGE_SOURCE_LOCAL,
                        date,
                        imageTitle,
                        stationName,
                        stationId,
                    )
                    mPicViewModel.insert(picDetails)
                    Toast.makeText(
                        this,
                        getString(R.string.you_successfully_added_your_image),
                        Toast.LENGTH_LONG
                    ).show()
                    Log.i("insertion", "success")
                    setContentView(mBinding.root)
                }
            }
        }
    }


    private fun validatePermission() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(intent, CAMERA_CODE)
                        }
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread().check()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this@ImagesActivity)
            .setMessage(getString(R.string.permission_off))
            .setPositiveButton(getString(R.string.go_to_settings)) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }.show()
            .setCanceledOnTouchOutside(false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_CODE) {
                data?.extras?.let {
                    val thumbnail: Bitmap = data.extras?.get("data") as Bitmap
                    //   ivImage.setImageBitmap(thumbnail)
                    Glide.with(this)
                        .load(thumbnail)
                        .into(postBinding.picView)
                    mImagePath = saveImageToInternalStorage(thumbnail)
                    Log.i("imagePath", mImagePath)
                    postImageDialog()
                }

            }
            if (requestCode == GALLERY_CODE) {
                data?.let {
                    val selectedPhotoUri = data.data
                    Glide.with(this)
                        .load(selectedPhotoUri)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?, model: Any?,
                                target: Target<Drawable>?, isFirstResource: Boolean
                            ): Boolean {
                                Log.e("TAG", "Error Loading image", e)
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                resource?.let {
                                    val bitmap: Bitmap = resource.toBitmap()
                                    mImagePath = saveImageToInternalStorage(bitmap)
                                }
                                return false
                            }

                        })
                        .into(postBinding.picView)
                }
                postImageDialog()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("cancelled", "User cancelled image selection")
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private fun openImageGallery() {
        Dexter.withContext(this)
            .withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            ).withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val galleryIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(galleryIntent, GALLERY_CODE)

                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(
                        this@ImagesActivity,
                        getString(R.string.permission_gallery_denied),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread().check()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.image_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemHome -> {
                val i = Intent(this@ImagesActivity, HomeActivity::class.java)
                i.putExtra(HOME_OR_MAP_FRAGMENT, 0)
                startActivity(i)
                finish()
            }
            R.id.itemMap -> {
                val i = Intent(this@ImagesActivity, HomeActivity::class.java)
                i.putExtra(HOME_OR_MAP_FRAGMENT, 1)
                startActivity(i)
                finish()
            }
            R.id.itemLanguage -> {
                languageDialog()
            }
            R.id.itemTheme -> {
                themeDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
