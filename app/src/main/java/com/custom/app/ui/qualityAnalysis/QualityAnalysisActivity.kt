package com.custom.app.ui.qualityAnalysis

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.agnext.sensenextmyadmin.ui.auth.login.RefreshInteractor
import com.base.app.ui.base.BaseActivity
import com.core.app.util.ActivityUtil
import com.core.app.util.AlertUtil
import com.custom.app.R
import com.custom.app.data.model.scanhistory.ScanHistoryResT
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.farm.farmList.FieldListActivity
import com.custom.app.ui.login.LoginActivity
import com.custom.app.ui.logout.LogoutDialog
import com.custom.app.ui.scan.list.history.ListCallBack
import com.custom.app.ui.user.detail.UserDetailActivity
import com.custom.app.util.Constants
import com.custom.app.util.Utils
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.WriterException
import kotlinx.android.synthetic.main.activity_quality_analysis.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.text.SimpleDateFormat
import java.util.*

class QualityAnalysisActivity : BaseActivity(), View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        AdapterView.OnItemSelectedListener,
        LogoutDialog.Callback,
        ListCallBack
 {

    private lateinit var actionBar: ActionBar
    private lateinit var viewModel: QualityAnaViewModel
    private var doubleBackToExitPressedOnce = false
    lateinit var qualityAnalysisAdapter: QualityAnalysisAdapter
    lateinit var token: String
    lateinit var currentDay: String
    lateinit var weekDay: String
    lateinit var monthDay: String
    var tokenHard="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2VtYWlsIjoidGVtcHJvYmluMzMxQGdtYWlsLmNvbSIsInVzZXJfZm5hbWUiOiJEZW1vIiwidXNlcl9uYW1lIjoidGVtcHJvYmluMzMxQGdtYWlsLmNvbSIsImN1c3RvbWVyX3V1aWQiOiI4YTVhNTZhMC00ZjQxLTRhMWMtYWIxNC02ZDUxYWU2MjJkMGIiLCJyb2xlcyI6WyJjdXN0b21lcl9hZG1pbiJdLCJpc3MiOiJRdWFsaXgiLCJ1c2VyX2xuYW1lIjoidXNlciIsImNsaWVudF9pZCI6ImNsaWVudElkIiwidXNlcl91dWlkIjpudWxsLCJ1c2VyX3R5cGUiOiJDVVNUT01FUiIsInVzZXJfaWQiOjEyOCwidXNlcl9tb2JpbGUiOiIwMTctMjc4OTA3MiIsInNjb3BlIjpbImFsbCJdLCJ1c2VyX2hpZXJhcmNoeSI6ImFkbWluIiwiY3VzdG9tZXJfbmFtZSI6IkRlbW8gY3VzdG9tZXIiLCJleHAiOjE1OTkxNzAxNDYsImN1c3RvbWVyX2lkIjo5MSwianRpIjoiNGU4MWE4ZDItZDMzOS00OGNhLTg5MTQtM2MwYWRhOTdkMzIxIn0.HlDebSYh-Ut5oHEWV_kQHEOLwGiae9teDdYaXKYdsG7j6DAswGNgnuz9awxY9XPvHrWYdMPGxMAmS_mIULkdaWIrO5Hk00k5tiqPpx3F0EpT0uwq_P1OdxjUJOCmwzm0xo2dS4D3eyK2VakOUalnkKLnCY5S9dC9QTMPU_NS4g-VKwy0BtdD45qjzCznb7kXSbNDIBmVMljercYjVuBJRe4G_4ffT64Fibb1KNhoMGLLGEro4sh2OjGvU7VK930m7hRJiNPv_8G1Rr3-midXjhR_t_Nfg2fNVq7mR_vaxy3BpD9v_jHqW8bvURBhVfhGyP8JDqrwWhfJF_O-gPYHBA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quality_analysis)
        //  token = "Bearer " + SessionClass(this).getUserToken()

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, 0, 0)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        navigation.setNavigationItemSelectedListener(this)

        viewModel = ViewModelProvider(
                this,
                QAViewModelFactory(QualityAnaInterceptor(this), RefreshInteractor(this))
        )[QualityAnaViewModel::class.java]
        viewModel!!.qualityAnaState.observe(::getLifecycle, ::updateUI)

        initView()
        rvScanHistory.isFocusable = false

    }


    /**Init View*/
    fun initView() {
        setSupportActionBar(toolbar)
        actionBar = supportActionBar!!
        actionBar.title = "Qualix Farmer"



        spFilter.onItemSelectedListener = this

        llProfile.setOnClickListener(this)
        viewAllPayment.setOnClickListener(this)
        ivQrImage.setOnClickListener(this)
        ivQrImage.setImageBitmap(Utils.generateQR("123456", this))

//        tvUserName.text=SessionClass(this).getUserName()
//        tvUserId.text="${ SessionClass(this).getUserId()}"

        var calendar = Calendar.getInstance()
        val date = calendar.time
        val dateFormat = SimpleDateFormat("MM/dd/yyyy")

        currentDay = dateFormat.format(date)
        weekDay = getCalculatedDate(currentDay!!, "MM/dd/yyyy", -7)
        monthDay = getCalculatedDate(currentDay!!, "MM/dd/yyyy", -30)
        token = Constants.TOKEN
        //Api for Avg Data
        if (hasConnection(this))
            viewModel.getAvgScanVM(token)
        else
            AlertUtil.showToast(this, getString(R.string.internet_issue))
    }


    /**UI  State Observer*/
    private fun updateUI(screenState: ScreenState<QualityState>?) {
        when (screenState) {
            ScreenState.Loading -> progress.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenState.renderState)
        }
    }

    private fun processLoginState(renderState: QualityState) {
        progress.visibility = View.GONE
        viewAllPayment.visibility = View.VISIBLE

        when (renderState) {
            QualityState.scansListSuccess -> {
                //setting the recycleView
                tvNoRecord.visibility = View.GONE
                viewAllPayment.visibility = View.VISIBLE
                rvScanHistory.visibility = View.VISIBLE

                rvScanHistory.layoutManager = LinearLayoutManager(this)
                qualityAnalysisAdapter =
                        QualityAnalysisAdapter(this, viewModel.scanList.value!!, this)
                rvScanHistory.adapter = qualityAnalysisAdapter
            }
            QualityState.noScanListSuccess -> {
                tvNoRecord.visibility = View.VISIBLE
//                viewAllPayment.visibility = View.GONE
                rvScanHistory.visibility = View.GONE

            }
            QualityState.scansListFailure -> {
                AlertUtil.showToast(this, "Error to get data")
            }

            QualityState.avgScanDataFaliure -> {
                pager.visibility = View.GONE
            }

            QualityState.monthFlcDataSuccess -> {
                //setting data in Graph
                //  graph()
            }
            QualityState.monthFlcDataFailure -> {
                AlertUtil.showToast(this, "Error to get data")

            }


        }
    }


    /**User define Callback*/
    override fun onItemClick(position: Int) {
        val intent = Intent(this, QualityDetailActivity::class.java)
        val gsonObj = Gson()
        val type = object : TypeToken<ScanHistoryResT>() {}.type
        val json = gsonObj.toJson(viewModel.scanList.value!![position], type)
        intent.putExtra("selectObject", json)
        startActivity(intent)
    }

    override fun editItem(pos: Int) {
    }


    //2 onClick Listener
    override fun onClick(view: View?) {
        when (view) {
            ivQrImage -> Utils.imageDialog(this, generateQR("123456", this))
//            llProfile -> IntentUtil.moveScreenIntent(this, ProfileActivity::class.java, false)
//            viewAllPayment -> IntentUtil.moveScreenIntent(
//                    this,
//                    PaymentHistoryActivity::class.java,
//                    false
//            )
        }
    }

    //3 Back Pressed
//    override fun onBackPressed() {
//        if (doubleBackToExitPressedOnce) {
//            finish()
//            super.onBackPressed()
//            return
//        }
//        this.doubleBackToExitPressedOnce = true
//        AlertUtil.showToast(this, getString(R.string.double_click_to_exit))
//
//    }

    //4 Spinner Callback
    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        when (pos) {
            // 0 -> viewModel.getScansListVm(token, currentDay, currentDay)
            0 -> viewModel.getScansListVm(tokenHard, currentDay, weekDay)
            1 -> viewModel.getScansListVm(tokenHard, currentDay, monthDay)
            2 -> viewModel.getScansListVm(tokenHard, "", "")
        }
    }

    private fun getCalculatedDate(date: String, dateFormat: String, days: Int): String {
        val cal = Calendar.getInstance()
        val s = SimpleDateFormat(dateFormat)
        if (date.isNotEmpty()) {
            cal.time = s.parse(date)
        }
        cal.add(Calendar.DAY_OF_YEAR, days)
        val date = cal.timeInMillis
        return s.format(Date(date))
        // return  Date(cal.timeInMillis).time
    }

    private fun hasConnection(context: Context): Boolean {
        val cm = context.getSystemService(
                Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (wifiNetwork != null && wifiNetwork.isConnected) {
            return true
        }
        val mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (mobileNetwork != null && mobileNetwork.isConnected) {
            return true
        }
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    /**Generate the QR and display*/
    private fun generateQR(input: String, context: Context): Bitmap {
        var bitmap: Bitmap? = null

        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay
        val point = Point()
        display.getSize(point)
        val width = point.x
        val height = point.y
        var smallerDimension = if (width < height) width else height
        smallerDimension = smallerDimension * 3 / 4
        val qrgEncoder = QRGEncoder(input, null, QRGContents.Type.TEXT, smallerDimension)

        try {
            bitmap = qrgEncoder.encodeAsBitmap()
            // ivQR.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            Log.v("Error", e.toString())
        }

        return bitmap!!
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_quality_ana -> {
                drawer_layout.closeDrawer(GravityCompat.START)
            }
            R.id.action_farm -> {
                closeDrawer()
                ActivityUtil.startActivity(this, FieldListActivity::class.java, false)
            }
            R.id.action_profile -> {
                closeDrawer()
                ActivityUtil.startActivity(this, UserDetailActivity::class.java, false)
            }
            R.id.action_logout -> {
                closeDrawer()
                LogoutDialog.newInstance().show(supportFragmentManager, Constants.LOGOUT_DIALOG)
            }
        }
        return true
    }

    override fun showLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        ActivityUtil.startActivity(this, intent, true)
    }

     override fun onBackPressed() {
         if (this.drawer_layout.isDrawerOpen(GravityCompat.START)) {
             this.drawer_layout.closeDrawer(GravityCompat.START)
         } else {
             super.onBackPressed()
         }
     }

     private fun closeDrawer() {
         if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
             drawer_layout.closeDrawer(GravityCompat.START)
         }
     }
}
