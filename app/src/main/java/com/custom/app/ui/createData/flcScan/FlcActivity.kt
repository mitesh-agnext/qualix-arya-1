package com.custom.app.ui.createData.flcScan

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.base.app.ui.base.BaseActivity
import com.custom.app.R
import com.custom.app.ui.createData.flcScan.adapter.AdapterHorizontalPick
import kotlinx.android.synthetic.main.custom_toolbar.*

class FlcActivity : BaseActivity(), View.OnClickListener, AdapterHorizontalPick.HorizontalCallback {

//    private lateinit var viewModel: FlcViewModel
//    var rvSection: RecyclerView? = null
//    var sectionAdapter: AdapterHorizontalPick? = null
//    var imageDone: Int = 0
//    var imageNeed: Int = 0
//    val MY_PERMISSION_REQUEST_CAMERA = 0
//    var MY_PERMISSION_REQUEST_GALLERY = 0
//    val PICK_IMAGE = 1
//
//    var newTextQuery: String? = ""
//    var tvx: TextView? = null
//    var tvoneBud: TextView? = null
//    var tvoneLeaf: TextView? = null
//    var tvoneLeafBud: TextView? = null
//    var tvtwoLeafBud: TextView? = null
//    var tvthreeLeafBud: TextView? = null
//    var tvoneBanjhi: TextView? = null
//    var tvoneLeafBunjhi: TextView? = null
//    var tvtwoLeafBunjhi: TextView? = null
//    var tvCoarseCount: TextView? = null
//    var tvtwoLeaf: TextView? = null
//    var tvthreeLeaf: TextView? = null
//    var dialogLayout: View? = null
//    var builder: AlertDialog.Builder? = null
//    var alertDialog: AlertDialog? = null
//    var tvflcPercent: TextView? = null
//    var flcHeading: TextView? = null
//    var oneBud_Count: Int? = null
//    var oneLeaf_Count: Int? = null
//    var twoLeaf_Count: Int? = null
//    var threeLeaf_Count: Int? = null
//    var oneLeafBud: Int? = null
//    var twoLeafBud: Int? = null
//    var threeLeafBud: Int? = null
//    var oneBanjhi_Count: Int? = null
//    var oneLeafBanji: Int? = null
//    var twoLeafBanji: Int? = null
//    var totalLeafCount: Int? = null
//    var totalWeight: Double? = null
//    var fineCount: Int? = null
//    var seasonId: Long? = 0
//    var areaCovered: Double? = 0.0
//
//    var mCurrentPhotoPath: String? = ""
//    var sectionCodeNameList: ArrayList<String> = ArrayList()
//    var sectionCodeIdList: ArrayList<String> = ArrayList()
//    var sectionNameList: ArrayList<String> = ArrayList()
//    var sectionIdList: ArrayList<Int> = ArrayList()
//    var sectionImageList: ArrayList<Int> = ArrayList()
//
//    object AllSections {
//        var secId: Int? = 27
//    }
//
//    var img: Bitmap? = null
//    var userId: String? = "62"
//    var agent_code: String? = ""
//    var searchBar_flc: AutoCompleteTextView? = null
//
//    var myDialog: Dialog? = null

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fine_leaf_count)
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.flc)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
//        rvSection = findViewById(R.id.rvSection)
//
//        viewModel = ViewModelProvider(this, FlcViewModelFactory(FlcInteractor()))[FlcViewModel::class.java]
//        viewModel.flcState.observe(::getLifecycle, ::updateUI)
//
//        viewModel.ClearData(secId.toString(), userId!!)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            init()
//        }
//
//        etImageNumber.setOnFocusChangeListener { v, hasFocus ->
//            if (!TextUtils.isEmpty(etImageNumber.text.toString())) {
//
//                imageNeed = Integer.parseInt(etImageNumber.text.toString())
//                lnImageLayout!!.visibility = View.VISIBLE
//                tvCaptureImageCount.text = "" + imageDone + "/" + imageNeed
//
//            } else {
//                lnImageLayout!!.visibility = View.GONE
//                showToast(this, getString(R.string.add_image_count))
//            }
//        }
//        etImageNumber.isClickable = true
//
//        myDialog = Dialog(this);
    }

//    @SuppressLint("NewApi")
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun init() {
//
//        galleryLayout!!.visibility = View.VISIBLE
//        expiredAdd!!.visibility = View.GONE
//        parentLayout!!.visibility = View.VISIBLE
//        lnImageLayout!!.visibility = View.GONE
//
//        confirm!!.isClickable = true
//        click_image!!.setOnClickListener(this)
//        confirm!!.setOnClickListener(this)
//        tv_gallery!!.setOnClickListener(this)
//
//        searchBar_flc = findViewById(R.id.searchBar_flc)
//
//            searchBar_flc!!.addTextChangedListener(object : TextWatcher {
//                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//
//                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
////                        sectionCode_list.visibility = View.VISIBLE
//
//                        newTextQuery = s.toString()
//
//                        if (newTextQuery.equals("")){
//
//                        }
//                        else {
//                            viewModel.FetchSectionCode(newTextQuery!!)
//                        }
//
//                    }
//
//                    override fun afterTextChanged(s: Editable) {
//
//                    }
//                })
//
//
//        selectionLayout_flc.visibility = View.VISIBLE
//        sectionCode.visibility = View.VISIBLE
//        section_selection.visibility = View.VISIBLE
//        agentCode.visibility = View.GONE
//
//        viewModel.FetchLocations()
//
//        if (viaGarden.isChecked) {
//            selectionLayout_flc.visibility = View.VISIBLE
//            sectionCode.visibility = View.GONE
//        } else {
//            selectionLayout_flc.visibility = View.GONE
//            sectionCode.visibility = View.VISIBLE
//        }
//
////        sectionCode_list.setOnItemClickListener { parent, view, position, id ->
////            secId = sectionCodeIdList.get(position).toInt()
////
////                searchBar_flc.setText(sectionCodeNameList.get(position))
////                sectionCode_list.visibility = View.GONE
////
////        }
//
//        boughtLeaf.setOnCheckedChangeListener { buttonView, isChecked ->
//            selectionLayout_flc.visibility = View.VISIBLE
//            sectionCode.visibility = View.VISIBLE
//            section_selection.visibility = View.VISIBLE
//            agentCode.visibility = View.GONE
//
//            viewModel.FetchLocations()
//
//            if (viaGarden.isChecked) {
//                selectionLayout_flc.visibility = View.VISIBLE
//                sectionCode.visibility = View.GONE
//            } else {
//                selectionLayout_flc.visibility = View.GONE
//                sectionCode.visibility = View.VISIBLE
//            }
//
//        }
//        ownLeaf.setOnCheckedChangeListener { buttonView, isChecked ->
//            selectionLayout_flc.visibility = View.GONE
//            sectionCode.visibility = View.GONE
//            section_selection.visibility = View.GONE
//            agentCode.visibility = View.VISIBLE
//        }
//
//        if (fullyCovered.isChecked){
//            areaCOveredLayout.visibility = View.VISIBLE
//            areaCovered = 100.0
//        }
//        else {
//            areaCovered = etAreaCovered!!.text.toString().toDoubleOrNull()
//            areaCOveredLayout.visibility = View.GONE
//        }
//        areaCOveredLayout.visibility = View.GONE
//
//        fullyCovered.setOnCheckedChangeListener { buttonView, isChecked ->
//            areaCOveredLayout.visibility = View.VISIBLE
//            areaCovered = 100.0
//        }
//        partiallyCovered.setOnCheckedChangeListener { buttonView, isChecked ->
//            areaCovered = etAreaCovered!!.text.toString().toDoubleOrNull()
//            areaCOveredLayout.visibility = View.GONE
//        }
//
//        viaGarden.setOnCheckedChangeListener { buttonView, isChecked ->
//            selectionLayout_flc.visibility = View.GONE
//            sectionCode.visibility = View.VISIBLE
//        }
//        viaSectionCode.setOnCheckedChangeListener { buttonView, isChecked ->
//            selectionLayout_flc.visibility = View.VISIBLE
//            sectionCode.visibility = View.GONE
//        }
//        upload_next_image.visibility = View.GONE
//
//    }
//
//    private fun updateUI(screenState: ScreenState<FlcState>?) {
//        when (screenState) {
//            ScreenState.Loading -> {
//                progressFLC.visibility = View.VISIBLE
//            }
//            is ScreenState.Render -> processLoginState(screenState.renderState)
//        }
//    }
//
//    private fun processLoginState(renderState: FlcState) {
//
//        progressFLC.visibility = View.GONE
//        when (renderState) {
//
//            FlcState.ClearDataFailure -> {
//                AlertUtil.showSnackBar(parentLayout, viewModel.errorMessage, 2000)
//            }
//            FlcState.ClearDataSuccess -> {
//            }
//            FlcState.UploadImageFailure -> {
//                AlertUtil.showSnackBar(parentLayout, viewModel.errorMessage, 2000)
//            }
//            FlcState.UploadImageSuccess -> {
//                if (viewModel.uploadImage.value!!.status.equals("fail")) {
//                    tv_gallery!!.isClickable = true
//                    click_image!!.isClickable = true
//                    nextimagetext!!.visibility = View.GONE
//                    showdialog()
//                } else if (viewModel.uploadImage.value!!.status.equals("pass")) {
//                    showToast(this, "Upoaded Successfuly")
//                    setUploadImageData()
//                }
//            }
//            FlcState.SaveFLCTokenExpire -> {
//                val intent = Intent(this, LoginActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)
//                this.finish()
//                this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//            }
//            FlcState.FetchFLCLocationFailure -> {
//                AlertUtil.showSnackBar(parentLayout, viewModel.errorMessage, 2000)
//            }
//            FlcState.FetchFLCLocationSuccess -> {
//                updateLocationSpinner(viewModel.fetchFlcLocations.value!!)
//            }
//            FlcState.FetchFLCGardenFailure -> {
//                AlertUtil.showSnackBar(parentLayout, viewModel.errorMessage, 2000)
//            }
//            FlcState.FetchFLCGardenSuccess -> {
//                updateGardenSpinner(viewModel.fetchFlcGardens.value!!)
//            }
//            FlcState.FetchFLCDivisionFailure -> {
//                AlertUtil.showSnackBar(parentLayout, viewModel.errorMessage, 2000)
//            }
//            FlcState.FetchFLCDivisionSuccess -> {
//                updateDivisionSpinner(viewModel.fetchFlcDivision.value!!)
//            }
//            FlcState.FetchFLCSectionFailure -> {
//                AlertUtil.showSnackBar(parentLayout, viewModel.errorMessage, 2000)
//            }
//            FlcState.FetchFLCSectionSuccess -> {
//                updateSections(viewModel.fetchFlcSections.value!!)
//            }
//            FlcState.FetchFLCSectionCodeFailure -> {
//                AlertUtil.showSnackBar(parentLayout, viewModel.errorMessage, 2000)
//            }
//            FlcState.FetchFLCSectionCodeSuccess -> {
//
//                sectionCodeNameList.clear()
//                sectionCodeIdList.clear()
//                for (i in viewModel.fetchFlcSectionCode.value!!.indices) {
//
//                    sectionCodeNameList.add(viewModel.fetchFlcSectionCode.value!!.get(i).name!!)
//                    sectionCodeIdList.add(viewModel.fetchFlcSectionCode.value!!.get(i).section_id!!.toString())
//
//                }
//
////                val sectionCodeAdapter = ArrayAdapter(this, android.R.layout.select_dialog_item, sectionCodeNameList)
//////                sectionCode_list.adapter = sectionCodeAdapter
////                searchBar_flc!!.threshold = 1
////                searchBar_flc!!.setAdapter(sectionCodeAdapter)
////
////                searchBar_flc!!.isPopupShowing
//
//                if (sectionCodeNameList.size!=0) {
//                    showPopupWindow(searchBar_flc!!,sectionCodeNameList)
//                }
//            }
//            FlcState.SaveFLCFailure -> {
//                tvx!!.visibility = View.VISIBLE
//            }
//            FlcState.SaveFLCSuccess -> {
//                tvx!!.visibility = View.VISIBLE
//            }
//            FlcState.FetchFLCResultFailure -> {
//                showDialogresult(0)
//            }
//            FlcState.FetchFLCResultSuccess -> {
//                showDialogresult(1)
//            }
//        }
//    }
//
//    fun setUploadImageData() {
//        preview_image.setImageResource(0)
//        imageDone += 1
//
//        if (imageDone == imageNeed) {
//            confirm!!.visibility = View.VISIBLE
//            confirm!!.visibility = View.GONE
//            tv_gallery!!.isClickable = false
//            click_image!!.isClickable = false
//            nextimagetext!!.visibility = View.GONE
////            showResult(secId.toString(), userId!!)
//            viewModel.FetchFlc(secId.toString(), userId!!)
//
//        } else {
//            tv_gallery!!.isClickable = true
//            click_image!!.isClickable = true
//            nextimagetext!!.visibility = View.VISIBLE
//        }
//        img = null
//        tvCaptureImageCount.text = "" + imageDone + "/" + imageNeed
//        confirm!!.isClickable = true
//
//    }

    @RequiresApi(Build.VERSION_CODES.FROYO)
    override fun onClick(view: View?) {
//        when (view) {
//
//            click_image -> {
//
//                if (!TextUtils.isEmpty(etImageNumber.text.toString())) {
//
//                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//
//                        imageNeed = Integer.parseInt(etImageNumber.text.toString())
//                        lnImageLayout!!.visibility = View.VISIBLE
//                        tvCaptureImageCount.text = "" + imageDone + "/" + imageNeed
//
//                        if (imageNeed != 0) {
//                            if (imageDone == imageNeed) {
//                                confirm!!.text = this.resources.getString(R.string.upload_image)
//                                tv_gallery!!.isClickable = false
//                                upload_next_image!!.visibility = View.INVISIBLE
//                            } else {
//                                confirm!!.text = this.resources.getString(R.string.upload_image)
//                                upload_next_image!!.visibility = View.INVISIBLE
//                            }
//                        }
//
//                        takeCamerIntent()
//
//                    } else {
//                        requestCameraPermission()
//                    }
//
//                } else {
//                    lnImageLayout!!.visibility = View.GONE
//                    showToast(
//                            this,
//                            getString(R.string.add_image_count)
//                    )
//                }
//
//            }
//            confirm -> {
//
//                if (img != null) {
//                    confirm!!.visibility = View.GONE
//                    val file = File(getPathFromURI(getImageUri(this, img!!)))
//
//                    Log.e("file", " " + file)
//
//                    etImageNumber.keyListener = null
//                    etImageNumber.isClickable = false
//                    etImageNumber.isFocusable = false
//
//                    scrollAddleaves!!.fullScroll(View.FOCUS_DOWN)
//
//                    var leavesClass = etweight_addLeave.text.toString()
//                    if (leavesClass.equals("")) {
//                        showToast(this, "Please add weight of leaves")
//                    } else {
//                        viewModel.UploadImage(file, userId!!, secId.toString())
//                    }
//                } else {
//                    showToast(this, "Please select image first")
//                }
//
//            }
//            tv_gallery -> {
//
//                if (!TextUtils.isEmpty(etImageNumber.text.toString())) {
//
//                    imageNeed = Integer.parseInt(etImageNumber.text.toString())
//                    lnImageLayout!!.visibility = View.VISIBLE
//                    tvCaptureImageCount.text = "" + imageDone + "/" + imageNeed
//
//                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                        val intent = Intent(Intent.ACTION_GET_CONTENT)
//                        intent.addCategory(Intent.CATEGORY_OPENABLE)
//                        intent.type = "image/*"
//                        startActivityForResult(Intent.createChooser(intent, "" + resources.getString(R.string.select_picture)), PICK_IMAGE)
//
//                    } else {
//                        checkAndRequestPermissions()
//                    }
//
//
//                    if (imageNeed != 0) {
//                        if (imageDone == imageNeed) {
//                            confirm!!.text = this.resources.getString(R.string.upload_image)
//                            tv_gallery!!.isClickable = false
//                            upload_next_image!!.visibility = View.INVISIBLE
//                        } else {
//                            confirm!!.text = this.resources.getString(R.string.upload_image)
//                            upload_next_image!!.visibility = View.INVISIBLE
//                        }
//                    }
//
//                } else {
//                    lnImageLayout!!.visibility = View.GONE
//                    showToast(this, getString(R.string.add_image_count))
//                }
//
//            }
//        }
    }

//    private fun requestCameraPermission() {
//
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
//            Snackbar.make(
//                    parentLayout!!,
//                    "" + resources.getString(R.string.camera_access_is_required_to_display_the_camera_preview),
//                    Snackbar.LENGTH_INDEFINITE
//            ).setAction("" + resources.getString(R.string.ok)) {
//                ActivityCompat.requestPermissions(
//                        this,
//                        arrayOf(Manifest.permission.CAMERA),
//                        MY_PERMISSION_REQUEST_CAMERA
//                )
//            }.show()
//        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
//                        this,
//                        Manifest.permission.CAMERA
//                )
//        ) {
//            Snackbar.make(
//                    parentLayout!!,
//                    "" + resources.getString(R.string.camera_access_is_required_to_display_the_camera_preview),
//                    Snackbar.LENGTH_INDEFINITE
//            ).setAction("" + resources.getString(R.string.ok)) {
//                ActivityCompat.requestPermissions(
//                        this,
//                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                        MY_PERMISSION_REQUEST_CAMERA
//                )
//            }.show()
//
//        } else {
//            Snackbar.make(
//                    parentLayout!!,
//                    "" + resources.getString(R.string.permission_is_not_available_Requesting_camera_permission),
//                    Snackbar.LENGTH_SHORT
//            ).show()
//            ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.CAMERA),
//                    MY_PERMISSION_REQUEST_CAMERA
//            )
//        }
//    }

    @SuppressLint("MissingSuperCall")
    @RequiresApi(Build.VERSION_CODES.DONUT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

//        try {
//
//            if (resultCode == Activity.RESULT_OK && requestCode == MY_PERMISSION_REQUEST_CAMERA) {
//
//                val bmOptions = BitmapFactory.Options().apply {
//                    // Get the dimensions of the bitmap
//                    inJustDecodeBounds = true
//                    BitmapFactory.decodeFile(mCurrentPhotoPath, this)
//
//                    // Decode the image file into a Bitmap sized to fill the View
//                    inJustDecodeBounds = false
//
//                    inPurgeable = true
//                }
//
//                BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)?.also { bitmap ->
//
//                    img = bitmap
//                    if (img != null) {
//                        confirm!!.visibility = View.VISIBLE
//                        imageframe.visibility = View.VISIBLE
//                        nextimagetext.visibility = View.GONE
//                        upload_next_image.visibility = View.INVISIBLE
//
//                    } else {
//                        confirm!!.visibility = View.GONE
//                        imageframe.visibility = View.GONE
//                        nextimagetext.visibility = View.VISIBLE
//                    }
//                    preview_image.setImageBitmap(bitmap)
////                    confirm!!.text = this.resources.getString(R.string.upload_image)
//                    if (imageNeed != 0) {
//                        if (imageDone == imageNeed) {
//                            confirm!!.text = this.resources.getString(R.string.upload_image)
//                        } else {
//                            confirm!!.text = this.resources.getString(R.string.upload_image)
//                        }
//                    }
//
//                }
//
//            } else if (resultCode == Activity.RESULT_OK) {
//                if (requestCode === PICK_IMAGE) {
//                    var selectedImageUri = data!!.data
//                    // Get the path from the Uri
//                    val path = getPathFromURI(selectedImageUri)
//
//                    if (path != null) {
//                        val f = File(path)
//                        selectedImageUri = Uri.fromFile(f)
//                    }
//
//                    img = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
//                    // Set the image in ImageView
//                    preview_image.setImageURI(selectedImageUri)
//
//                    if (img != null) {
//                        confirm!!.visibility = View.VISIBLE
//                        imageframe.visibility = View.VISIBLE
//                        nextimagetext.visibility = View.GONE
//                        upload_next_image.visibility = View.INVISIBLE
//
//                    } else {
//                        confirm!!.visibility = View.GONE
//                        imageframe.visibility = View.GONE
//                        nextimagetext.visibility = View.VISIBLE
//                    }
//
//                    if (imageNeed != 0) {
//                        if (imageDone == imageNeed) {
//                            confirm!!.text = this.resources.getString(R.string.upload_image)
//                        } else {
//                            confirm!!.text = this.resources.getString(R.string.upload_image)
//                        }
//                    }
//                    tvCaptureImageCount.text = "" + imageDone + "/" + imageNeed
//
//                }
//            } else if (requestCode == Activity.RESULT_CANCELED || resultCode != Activity.RESULT_OK) {
//
//                if (img != null) {
//
//                    confirm!!.visibility = View.VISIBLE
//                    imageframe.visibility = View.GONE
//                    if (imageDone == 0) {
//
//                        nextimagetext.visibility = View.GONE
//                        upload_next_image.visibility = View.INVISIBLE
//
//                    } else {
//
//                        nextimagetext.visibility = View.GONE
//                        upload_next_image.visibility = View.INVISIBLE
//
//                    }
//
//                } else {
//                    confirm!!.visibility = View.GONE
//                    imageframe.visibility = View.GONE
//                    if (imageDone == 0) {
//
//                        nextimagetext.visibility = View.GONE
//                        upload_next_image.visibility = View.INVISIBLE
//
//                    } else {
//
//                        nextimagetext.visibility = View.VISIBLE
//                        upload_next_image.visibility = View.INVISIBLE
//
//                    }
//
//                }
//
////                confirm!!.visibility = View.GONE
//                tvCaptureImageCount.text = "" + imageDone + "/" + imageNeed
//
//            }
//
//        } catch (e: Exception) {
//
//            Log.e("exception", " " + e.toString())
//        }
//
////        scrollAddleaves!!.fullScroll(View.FOCUS_DOWN)
//        loadScreen()

    }

//    private fun loadScreen() {
//
//        //intent after delay
//        val handlerIntent = Handler()
//        handlerIntent.postDelayed({
//
//            scrollAddleaves!!.fullScroll(View.FOCUS_DOWN)
//
//        }, 200)
//    }
//
//    fun getPathFromURI(contentUri: Uri): String? {
//        var res: String? = null
//        val proj = arrayOf(MediaStore.Images.Media.DATA)
//        val cursor = contentResolver.query(contentUri, proj, null, null, null)
//        if (cursor!!.moveToFirst()) {
//            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            res = cursor.getString(column_index)
//        }
//        cursor.close()
//        return res
//    }
//
//    fun getImageUri(context: Context, inImage: Bitmap): Uri {
//        val bytes = ByteArrayOutputStream()
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//        val path = MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "Title", null)
//        return Uri.parse(path)
//    }

//    fun explain(msg: String) {
//        val dialog = AlertDialog.Builder(this)
//        dialog.setMessage(msg)
//                .setPositiveButton("" + resources.getString(R.string.yes)) { paramDialogInterface, paramInt ->
//                    //  permissionsclass.requestPermission(type,code);
//                    startActivity(
//                            Intent(
//                                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                                    Uri.parse("package:com.example.parsaniahardik.kotlin_marshmallowpermission")
//                            )
//                    )
//                }
//                .setNegativeButton("" + resources.getString(R.string.cancel)) { paramDialogInterface, paramInt -> this.finish() }
//        dialog.show()
//    }
//
//    fun checkAndRequestPermissions() {
//
//        if (ActivityCompat.shouldShowRequestPermissionRationale(
//                        this,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE
//                )
//        ) {
//            Snackbar.make(
//                    parentLayout!!, "" + resources.getString(R.string.gallery_access_is_required),
//                    Snackbar.LENGTH_INDEFINITE
//            ).setAction("" + resources.getString(R.string.ok)) {
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSION_REQUEST_GALLERY)
//            }.show()
//        } else {
//            Snackbar.make(parentLayout!!, "" + resources.getString(R.string.permission_is_not_available), Snackbar.LENGTH_SHORT).show()
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSION_REQUEST_GALLERY)
//        }
//    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    @RequiresApi(Build.VERSION_CODES.FROYO)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        if (requestCode != MY_PERMISSION_REQUEST_GALLERY || requestCode != MY_PERMISSION_REQUEST_CAMERA) {
//            return
//        }
//
//        if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            val view: View? = null
//            if (view == click_image) {
//
//                takeCamerIntent()
//
//            } else {
//
//                val intent = Intent(Intent.ACTION_GET_CONTENT)
//                intent.addCategory(Intent.CATEGORY_OPENABLE)
//                intent.type = "image/*"
//                startActivityForResult(Intent.createChooser(intent, "" + resources.getString(R.string.select_picture)), PICK_IMAGE)
//            }
//        } else {
//            explain("" + resources.getString(R.string.you_need_to_give_some_mandatory_permissions))
//        }
    }

//    @RequiresApi(Build.VERSION_CODES.FROYO)
//    fun createImageFile(): File {
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val imageFileName = "JPEG_" + timeStamp + "_"
//        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.absolutePath
//        return image
//    }
//
//    @RequiresApi(Build.VERSION_CODES.FROYO)
//    fun takeCamerIntent() {
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        if (takePictureIntent.resolveActivity(packageManager) != null) {
//            var photoFile: File? = null
//            try {
//                photoFile = createImageFile()
//            } catch (ex: IOException) {
//            }
//            if (photoFile != null) {
//                val photoURI: Uri = FileProvider.getUriForFile(this, applicationContext.packageName, photoFile)
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                startActivityForResult(takePictureIntent, MY_PERMISSION_REQUEST_CAMERA)
//            }
//        }
//    }
//
//    private fun updateLocationSpinner(locationList: java.util.ArrayList<LocationList>) {
//
//        val adapter = FlcLocationAdapter(this, locationList)
//        spLocation.adapter = adapter
//        spLocation.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(
//                            parent: AdapterView<*>,
//                            view: View,
//                            position: Int,
//                            id: Long) {
//
//                        val selectedLocationId = locationList[position].location_id!!.toInt()
//                        viewModel.FetchGardens(selectedLocationId)
//                    }
//
//                    override fun onNothingSelected(parent: AdapterView<*>) {
//                    }
//                }
//    }

//    private fun updateGardenSpinner(gardenList: java.util.ArrayList<GardenList>) {
//
//        val adapter = FlcGardenAdapter(this, gardenList)
//        spGarden.adapter = adapter
//        spGarden.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//
//                        val selectedGardenId = gardenList[position].garden_id!!.toInt()
//                        viewModel.FetchDivisions(selectedGardenId)
//                    }
//                    override fun onNothingSelected(parent: AdapterView<*>) {
//                    }
//                }
//    }

//    private fun updateDivisionSpinner(divisionList: java.util.ArrayList<DevisionList>) {
//
//        val adapter = FlcDevisionAdapter(this, divisionList)
//        spDevesion.adapter = adapter
//        spDevesion.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//
//                        val selectedDivisionId = divisionList[position].division_id!!.toInt()
//                        viewModel.FetchSections(selectedDivisionId)
//                    }
//
//                    override fun onNothingSelected(parent: AdapterView<*>) {
//                    }
//                }
//    }

//    fun updateSections(sectionList: ArrayList<SectionData>) {
//
//        sectionNameList.clear()
//        sectionIdList.clear()
////        secId = 0
//
//        for (i in sectionList.indices) {
//            sectionNameList.add(sectionList[i].name!!)
//            sectionIdList.add(sectionList[i].section_id!!)
//        }
//
//        sectionAdapter = AdapterHorizontalPick(sectionNameList, sectionImageList, this@FlcActivity, this@FlcActivity)
//        rvSection!!.adapter = sectionAdapter
//
//        rvSection?.layoutManager = LinearLayoutManager(this@FlcActivity, LinearLayoutManager.HORIZONTAL, false)
////        secId = sectionIdList[position]
//
//    }

    override fun onResume() {
        super.onResume()
//        text_package_expired.text = resources.getString(R.string.package_expired)
//        upload_next_image!!.text = resources.getString(R.string.please_upload_image)
//        nextimagetext!!.text = resources.getString(R.string.please_upload_next_image)
    }

//    fun showdialog() {
//        val dialog = Dialog(this)
//        dialog.setContentView(R.layout.wrongimagedialog)
//        dialog.setTitle("Title...")
//        dialog.setCanceledOnTouchOutside(false)
//        val yesButton = dialog.findViewById(R.id.btn_yes) as Button
//        yesButton.setOnClickListener(View.OnClickListener {
//
//            dialog.dismiss()
//        })
//        dialog.show()
//    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun horizontalItem(position: Int) {
//        secId = sectionIdList.get(position)
    }

//    @RequiresApi(Build.VERSION_CODES.ECLAIR)
//    fun showDialogresult(status: Int) {
//        val inflater: LayoutInflater = LayoutInflater.from(this)
//        dialogLayout = inflater.inflate(R.layout.dialog_flc, null)
//        builder = AlertDialog.Builder(this)
//        builder!!.setView(dialogLayout)
//        alertDialog = builder!!.create()
//        alertDialog!!.setCanceledOnTouchOutside(false)
//
//        tvx = dialogLayout!!.findViewById(R.id.tvx)
//        tvoneBud = dialogLayout!!.findViewById(R.id.tvOneBudCount)
//        tvoneLeaf = dialogLayout!!.findViewById(R.id.tvOneLeafCount)
//        tvtwoLeaf = dialogLayout!!.findViewById(R.id.tvTwoLeafCount)
//        tvthreeLeaf = dialogLayout!!.findViewById(R.id.tvThreeLeafCount)
//        tvoneLeafBud = dialogLayout!!.findViewById(R.id.tvOneLeafBud)
//        tvtwoLeafBud = dialogLayout!!.findViewById(R.id.tvTwoLeafBud)
//        tvthreeLeafBud = dialogLayout!!.findViewById(R.id.tvThreeLeafBud)
//
//        tvoneBanjhi = dialogLayout!!.findViewById(R.id.tvOneBanjhi)
//        tvoneLeafBunjhi = dialogLayout!!.findViewById(R.id.tvOneLeafBunjhi)
//        tvtwoLeafBunjhi = dialogLayout!!.findViewById(R.id.tvTwoLeafBunjhi)
//
//        tvflcPercent = dialogLayout!!.findViewById(R.id.tvflcPercent)
//        flcHeading = dialogLayout!!.findViewById(R.id.flcHeading)
//        tvCoarseCount = dialogLayout!!.findViewById(R.id.tvCoarseCount_flc)
//
//        val showFlc: LinearLayout = dialogLayout!!.findViewById(R.id.showFLC)
//        val add_flc_dialog_result: TextView = dialogLayout!!.findViewById(R.id.add_flc_dialog)
//
//        add_flc_dialog_result.visibility = View.GONE
//        add_flc_dialog_result.setOnClickListener {
//            if (!add_flc_dialog_result.text.equals(resources.getString(R.string.clear_data))) {
//                alertDialog!!.hide()
//                finish()
//                // move back to same screen by intent data
//                val intent = Intent(this, FlcActivity::class.java)
//                intent.putExtra("SCREEN", "ADDFLC")
//
//                startActivity(intent)
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//                viewModel.ClearData(secId.toString(), userId!!)
//            }
//        }
//
//        if (status == 0) {
//            showFlc.visibility = View.GONE
//            add_flc_dialog_result.text = resources.getString(R.string.clear_data)
//            confirm!!.isClickable = true
//            add_flc_dialog_result.visibility = View.VISIBLE
//            tvx!!.visibility = View.VISIBLE
//            tvx!!.text = resources.getString(R.string.something_went_wrong) + "\n"
//
//        } else if (status == 1) {
//            confirm!!.isClickable = true
//            tvx!!.visibility = View.VISIBLE
//            showFlc.visibility = View.GONE
//            if (viewModel.fetchFlcResult.value != null) {
//
//                if (viewModel.fetchFlcResult.value!!.Total_Bunches.toString().equals("null") || viewModel.fetchFlcResult.value!!.Total_Bunches == null) {
//                    tvx!!.text =
//                            resources.getString(R.string.something_went_wrong) + "\n"
//                }
//                else {
//                    if (viewModel.fetchFlcResult.value!!.fine_count == null) {
//                        fineCount = 0
//                    }
//                    if (viewModel.fetchFlcResult.value!!.OneBud_Count == null) {
//                        oneBud_Count = 0
//                    } else {
//                        oneBud_Count = viewModel.fetchFlcResult.value!!.OneBud_Count
//                    }
//                    if (viewModel.fetchFlcResult.value!!.OneLeaf_Count == null) {
//                        oneLeaf_Count = 0
//                    } else {
//                        oneLeaf_Count = viewModel.fetchFlcResult.value!!.OneLeaf_Count
//                    }
//                    if (viewModel.fetchFlcResult.value!!.TwoLeaf_Count == null) {
//                        twoLeaf_Count = 0
//                    } else {
//                        twoLeaf_Count = viewModel.fetchFlcResult.value!!.TwoLeaf_Count
//                    }
//                    if (viewModel.fetchFlcResult.value!!.ThreeLeaf_Count == null) {
//                        threeLeaf_Count = 0
//                    } else {
//                        threeLeaf_Count = viewModel.fetchFlcResult.value!!.ThreeLeaf_Count
//                    }
//                    if (viewModel.fetchFlcResult.value!!.OneLeafBud_Count == null) {
//                        oneLeafBud = 0
//                    } else {
//                        oneLeafBud = viewModel.fetchFlcResult.value!!.OneLeafBud_Count
//                    }
//                    if (viewModel.fetchFlcResult.value!!.TwoLeafBud_Count == null) {
//                        twoLeafBud = 0
//                    } else {
//                        twoLeafBud = viewModel.fetchFlcResult.value!!.TwoLeafBud_Count
//                    }
//                    if (viewModel.fetchFlcResult.value!!.ThreeLeafBud_Count == null) {
//                        threeLeafBud = 0
//                    } else {
//                        threeLeafBud = viewModel.fetchFlcResult.value!!.ThreeLeafBud_Count
//                    }
//                    if (viewModel.fetchFlcResult.value!!.OneBanjhi_Count == null) {
//                        oneBanjhi_Count = 0
//                    } else {
//                        oneBanjhi_Count = viewModel.fetchFlcResult.value!!.OneBanjhi_Count
//                    }
//                    if (viewModel.fetchFlcResult.value!!.OneLeafBanjhi_Count == null) {
//                        oneLeafBanji = 0
//                    } else {
//                        oneLeafBanji = viewModel.fetchFlcResult.value!!.OneLeafBanjhi_Count
//                    }
//                    if (viewModel.fetchFlcResult.value!!.TwoLeafBanjhi_Count == null) {
//                        twoLeafBanji = 0
//                    } else {
//                        twoLeafBanji = viewModel.fetchFlcResult.value!!.TwoLeafBanjhi_Count
//                    }
//                    if (viewModel.fetchFlcResult.value!!.Total_Bunches == null) {
//                        totalLeafCount = 0
//                    } else {
//                        totalLeafCount = viewModel.fetchFlcResult.value!!.Total_Bunches
//                    }
//
//                    val A = oneLeafBud!!
//                    val B = twoLeafBud!!
//                    val C = threeLeafBud!! / 2
//                    val D = oneLeafBanji!!
//                    val E = twoLeafBanji!! + oneBanjhi_Count!! + oneBud_Count!! + oneLeaf_Count!! + twoLeaf_Count!! + threeLeaf_Count!!
//
//                    val sumx = (A + B + C + D).toDouble()
//                    var xpercent = (sumx / totalLeafCount!!) * 100
//
//                    flcHeading!!.visibility = View.GONE
//
//                    if (xpercent.isNaN()) {
//                        tvx!!.visibility = View.VISIBLE
//                        xpercent = 0.0
//                    } else {
//                        tvx!!.visibility = View.VISIBLE
//                        tvx!!.text = resources.getString(R.string.fine_shoot) + " " + String.format("%.2f", xpercent) + " %"
//                    }
//                    tvoneBud!!.visibility = View.VISIBLE
//                    tvoneLeaf!!.visibility = View.VISIBLE
//                    tvtwoLeaf!!.visibility = View.VISIBLE
//                    tvthreeLeaf!!.visibility = View.VISIBLE
//                    tvCoarseCount!!.visibility = View.VISIBLE
//                    tvoneLeafBud!!.visibility = View.VISIBLE
//                    tvtwoLeafBud!!.visibility = View.VISIBLE
//                    tvthreeLeafBud!!.visibility = View.VISIBLE
//
//                    tvoneLeafBunjhi!!.visibility = View.VISIBLE
//                    tvtwoLeafBunjhi!!.visibility = View.VISIBLE
//
//                    tvoneLeaf!!.text = resources.getString(R.string.one_leaf) + oneLeaf_Count.toString()
//                    tvtwoLeaf!!.text = resources.getString(R.string.two_leaves) + twoLeaf_Count.toString()
//                    tvoneBud!!.text = resources.getString(R.string.one_bud) + oneBud_Count.toString()
//                    tvthreeLeaf!!.text = resources.getString(R.string.three_leaves) + threeLeaf_Count.toString()
//                    tvoneLeafBud!!.text = resources.getString(R.string.one_leaf_bud) + A
//                    tvtwoLeafBud!!.text = resources.getString(R.string.two_leaves_bud) + B
//                    tvthreeLeafBud!!.text = resources.getString(R.string.three_leaves_bud) + C
//                    tvCoarseCount!!.text = "Coarse Count : " + E
//                    tvoneBanjhi!!.text = resources.getString(R.string.one_banjhi) + oneBanjhi_Count.toString()
//                    if (D > 20) {
//                        tvoneLeafBunjhi!!.setTextColor(
//                                ContextCompat.getColor(applicationContext, R.color.red)
//                        )
//                    }
//                    tvoneLeafBunjhi!!.text = resources.getString(R.string.one_leaf_banjhi) + D
//                    tvtwoLeafBunjhi!!.text = resources.getString(R.string.two_leaves_banjhi) + twoLeafBanji.toString()
//
//                    if (totalWeight != etweight_addLeave!!.text.toString().toDoubleOrNull()) {
//                        totalWeight = etweight_addLeave!!.text.toString().toDoubleOrNull()
//                    } else {
//                        totalWeight = 0.0
//                    }
//                    if (areaCovered == null) {
//                        areaCovered = 0.0
//                    }
//                    if (seasonId == null) {
//                        seasonId = 1
//                    }
//                    if (!etagent_code.text.toString().isNullOrEmpty()) {
//                        agent_code = etagent_code.text.toString()
//                    }
//                    viewModel.SaveFlc(A.toString(), B.toString(), C.toString(), D.toString(), twoLeafBanji.toString(), oneBanjhi_Count.toString(),
//                            oneBud_Count.toString(), oneLeaf_Count.toString(), twoLeaf_Count.toString(), threeLeaf_Count.toString(), xpercent,
//                            totalLeafCount!!.toDouble(), secId.toString(), agent_code.toString(), totalWeight!!, areaCovered!!)
//
//                    add_flc_dialog_result.text = resources.getString(R.string.ok)
//                }
//            } else {
//                add_flc_dialog_result.text = resources.getString(R.string.ok)
//            }
//            add_flc_dialog_result.visibility = View.VISIBLE
//        }
//        alertDialog!!.show()
//    }

//    fun ShowPopup(str: ArrayList<String>) {
//
//        myDialog!!.setContentView(R.layout.custompopup)
//
//        val searchedData: ListView = myDialog!!.findViewById(R.id.searchedData)
//        val sectionCodeAdapter = ArrayAdapter(this, android.R.layout.select_dialog_item, str)
//        searchedData.adapter = sectionCodeAdapter
//
//        searchedData.setOnItemClickListener { parent, view, position, id ->
//            secId = sectionCodeIdList.get(position).toInt()
//
//                searchBar_flc!!.setText(sectionCodeNameList.get(position))
//                searchedData.visibility = View.GONE
//            myDialog!!.dismiss()
//
//        }
//
//        myDialog!!.show()
//
//    }
//
//    fun showPopupWindow(view: View, str: ArrayList<String>) {
//        val inflater = view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val popupView = inflater.inflate(R.layout.custompopup, null)
//
//        val width = LinearLayout.LayoutParams.WRAP_CONTENT
//        val height = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        val popupWindow = PopupWindow(popupView, width, height)
//
//        popupWindow.showAtLocation(view, Gravity.HORIZONTAL_GRAVITY_MASK, 0, 0)
//        popupView.setOnTouchListener { v, event ->
//
//            popupWindow.dismiss()
//            true
//        }
//        val searchedData: ListView = popupView.findViewById(R.id.searchedData)
//        val sectionCodeAdapter = ArrayAdapter(this, android.R.layout.select_dialog_item, str)
//        searchedData.adapter = sectionCodeAdapter
//
//        searchedData.setOnItemClickListener { parent, view, position, id ->
//            secId = sectionCodeIdList.get(position).toInt()
//
//            searchBar_flc!!.setText(sectionCodeNameList.get(position))
//            searchedData.visibility = View.GONE
//            myDialog!!.dismiss()
//
//        }
//
//
//    }
}

//    fun showResult(sectionId: String, userId: String) {
//
//        val intent = Intent(this, ShowFlcResult::class.java)
//        intent.putExtra("secId", sectionId)
//        intent.putExtra("userId", userId)
//        startActivityForResult(intent, 1)
//    }