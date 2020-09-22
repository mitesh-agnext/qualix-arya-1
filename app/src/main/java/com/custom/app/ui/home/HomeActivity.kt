package com.custom.app.ui.home

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.ListPopupWindow
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager.OnBackStackChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.base.app.ui.base.BaseHome
import com.core.app.BuildConfig
import com.core.app.ui.custom.SpannyText
import com.core.app.util.ActivityUtil
import com.core.app.util.AlertUtil
import com.core.app.util.Constants.BLUETOOTH_SETTINGS_REQUEST
import com.core.app.util.Constants.LOCATION_SETTINGS_REQUEST
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.databinding.ActivityHomeBinding
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.createData.instlCenter.centerList.InstallationCenterListActivity
import com.custom.app.ui.createData.region.list.RegionListActivity
import com.custom.app.ui.createData.region.site.list.SiteListActivity
import com.custom.app.ui.customer.list.CustomerListActivity
import com.custom.app.ui.device.list.DeviceListActivity
import com.custom.app.ui.farmer.detail.FarmerDetailActivity
import com.custom.app.ui.farmer.scan.FarmerScanFragment
import com.custom.app.ui.landing.LandingFragment
import com.custom.app.ui.login.LoginActivity
import com.custom.app.ui.logout.LogoutDialog
import com.custom.app.ui.password.change.ChangePasswordFragment
import com.custom.app.ui.permission.PermissionActivity
import com.custom.app.ui.sample.SampleFragment
import com.custom.app.ui.scan.list.history.ScanHistoryActivity
import com.custom.app.ui.scan.list.scanFrg.ScanHistoryFragment
import com.custom.app.ui.scan.select.EmptyFragment
import com.custom.app.ui.scan.select.SelectScanFragment
import com.custom.app.ui.setting.SettingActivity
import com.custom.app.ui.user.detail.UserDetailActivity
import com.custom.app.ui.user.list.UserListActivity
import com.custom.app.util.Constants.*
import com.custom.app.util.Permissions
import com.data.app.db.table.ResultTable
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.media.app.GlideApp
import com.specx.device.ble.NIRScanSDK
import com.specx.device.ble.NanoBleService
import com.specx.device.ui.select.SelectDeviceDialog
import com.specx.device.util.Constants.*
import com.specx.scan.data.model.commodity.CommodityItem
import com.specx.scan.data.model.sample.SampleItem
import com.specx.scan.ui.chemical.result.ChemicalResultActivity
import com.specx.scan.ui.chemical.scan.ScanDialog
import com.specx.scan.ui.reference.ReferenceDialog
import com.specx.scan.ui.result.base.ResultActivity
import com.specx.scan.ui.result.search.SearchResultActivity
import com.specx.scan.util.Constants.*
import com.user.app.data.UserData
import de.hdodenhof.circleimageview.CircleImageView
import org.parceler.Parcels
import java.util.*
import javax.inject.Inject

class HomeActivity : BaseHome(), NavigationView.OnNavigationItemSelectedListener, OnBackStackChangedListener,
        FarmerScanFragment.Callback, SampleFragment.Callback, SelectDeviceDialog.Callback,
        ReferenceDialog.Callback, ScanDialog.Callback, LogoutDialog.Callback {

    @Inject
    lateinit var homeInteractor: HomeInteractor

    lateinit var binding: ActivityHomeBinding

    private var selectedMenuItem: MenuItem? = null
    private var drawerToggle: ActionBarDrawerToggle? = null

    private var serviceIntent: Intent? = null
    private val disconnectReceiver: BroadcastReceiver = DisconnectReceiver()
    private val disconnectFilter = IntentFilter(NIRScanSDK.ACTION_GATT_DISCONNECTED)

    private var scanId: String? = null
    private var device: DeviceItem? = null
    private var devices = ArrayList<DeviceItem>()
    private lateinit var viewModel: HomeViewModel

    var toolbarTitle: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.includeAppbar.includeToolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportFragmentManager.addOnBackStackChangedListener(this)

        viewModel = ViewModelProvider(this, HomeDevicesViewModelFactory(homeInteractor))[HomeViewModel::class.java]
        viewModel.homeState.observe(::getLifecycle, ::updateUI)

        val switchNight = binding.navView.menu.findItem(R.id.nav_menu_night).actionView as SwitchCompat
        switchNight.isChecked = userManager.isNightMode
        switchNight.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (!isChecked) {
                userManager.isNightMode = false
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                userManager.isNightMode = true
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
        drawerToggle = object : ActionBarDrawerToggle(this, binding.drawerLayout,
                binding.includeAppbar.includeToolbar.toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                if (selectedMenuItem != null) {
                    when (selectedMenuItem!!.itemId) {
                        R.id.nav_menu_home -> showHomeScreen(device?.device_id, device?.device_name)
                        R.id.nav_menu_permission ->
                            ActivityUtil.startActivity(this@HomeActivity, PermissionActivity::class.java, false)
                        R.id.nav_scan_history ->
                            ActivityUtil.startActivityResult(this@HomeActivity,
                                    ScanHistoryActivity::class.java, REQ_EDIT_SCAN_HISTORY, false)
                        R.id.nav_customer_list -> ActivityUtil.startActivity(this@HomeActivity, CustomerListActivity::class.java, false)
                        R.id.nav_user_list -> ActivityUtil.startActivity(this@HomeActivity, UserListActivity::class.java, false)
                        R.id.nav_menu_change_pwd -> fragmentTransition(ChangePasswordFragment.newInstance(), CHANGE_PWD_FRAGMENT)
                        R.id.nav_add_center -> ActivityUtil.startActivity(this@HomeActivity, InstallationCenterListActivity::class.java, false)
                        R.id.nav_add_region -> ActivityUtil.startActivity(this@HomeActivity, RegionListActivity::class.java, false)
                        R.id.nav_add_site -> ActivityUtil.startActivity(this@HomeActivity, SiteListActivity::class.java, false)
/*
                        R.id.nav_cold_store -> ActivityUtil.startActivity(this@HomeActivity, SNDeviceListActivity::class.java, false)
                        R.id.nav_add_coldstore -> ActivityUtil.startActivity(this@HomeActivity, ColdstoreListActivity::class.java, false)
                        R.id.nav_farm_management -> ActivityUtil.startActivity(this@HomeActivity, SectionListActivity::class.java, false)
                        R.id.nav_payment_history -> ActivityUtil.startActivity(this@HomeActivity, PaymentHistoryActivity::class.java, false)
                        R.id.nav_add_profile -> ActivityUtil.startActivity(this@HomeActivity, ProfileListActivity::class.java, false)
                        R.id.nav_add_profile_type -> ActivityUtil.startActivity(this@HomeActivity, ProfileTypeListActivity::class.java, false)
                        R.id.nav_add_food_type -> ActivityUtil.startActivity(this@HomeActivity, FoodTypeListActivity::class.java, false)
                        R.id.nav_add_ruleConfig -> ActivityUtil.startActivity(this@HomeActivity, RuleConfigListActivity::class.java, false)
                        R.id.nav_add_season -> ActivityUtil.startActivity(this@HomeActivity, SeasonListActivity::class.java, false)
*/
                        R.id.nav_add_devices -> {
                            val intent1 = Intent(this@HomeActivity, DeviceListActivity::class.java)
                            intent1.putExtra("activityStatus", 0)
                            ActivityUtil.startActivity(this@HomeActivity, intent1, false)
                        }
                        R.id.nav_device_provisioning -> {
                            val intent2 = Intent(this@HomeActivity, DeviceListActivity::class.java)
                            intent2.putExtra("activityStatus", 1)
                            ActivityUtil.startActivity(this@HomeActivity, intent2, false)
                        }
                        R.id.nav_menu_night -> if (switchNight.isChecked) {
                            switchNight.isChecked = false
                        } else {
                            switchNight.isChecked = true
                        }
                        R.id.nav_menu_settings -> ActivityUtil.startActivity(this@HomeActivity, SettingActivity::class.java, false)
                        R.id.nav_menu_logout -> LogoutDialog.newInstance().show(supportFragmentManager, LOGOUT_DIALOG)
                    }
                    selectedMenuItem = null
                }
            }
        }
        binding.drawerLayout.addDrawerListener(drawerToggle!!)
        drawerToggle!!.setDrawerIndicatorEnabled(true)
        drawerToggle!!.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
        binding.navView.itemIconTintList = null

        binding.includeFab.fabGhost.setOnClickListener { view -> showGhostMenu(view) }

        binding.includeAppbar.includeToolbar.toolbar.setOnLongClickListener {
            if (BuildConfig.DEBUG) {
                if (binding.includeFab.fabGhost.visibility == View.VISIBLE) {
                    binding.includeFab.fabGhost.hide()
                } else {
                    binding.includeFab.fabGhost.show()
                }
            }
            false
        }

        binding.includeAppbar.includeToolbar.toolbar.setNavigationOnClickListener { v: View? ->
            val currentFragment = supportFragmentManager.findFragmentById(R.id.layout_main)
            if (currentFragment is HomeFragment
                    || currentFragment is LandingFragment
                    || currentFragment is ScanHistoryFragment) {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            } else {
                onBackPressed()
            }
        }

        toolbarTitle = binding.includeAppbar.includeToolbar.toolbar.findViewById(R.id.title)
        toolbarTitle!!.setOnClickListener {
            if (devices.size > 0 && devices.size != 1) {
                showDeviceMenu(devices)
            }
        }

        if (!TextUtils.isEmpty(userManager.userData)) {
            val user = Gson().fromJson(userManager.userData, UserData::class.java)
            if (user != null) {
                val headerLayout = binding.navView.getHeaderView(0)
                headerLayout.setOnClickListener { showProfileScreen() }
                val civProfile: CircleImageView = headerLayout.findViewById(R.id.civ_profile)
                GlideApp.with(this).load(user.profile).error(R.drawable.ic_profile).into(civProfile)
                val tvName = headerLayout.findViewById<TextView>(R.id.tv_name)
                val tvEmail = headerLayout.findViewById<TextView>(R.id.tv_email)
                if (!TextUtils.isEmpty(user.name)) {
                    tvName.text = user.name
                }
                tvEmail.text = user.email
            }
        }

        if (!TextUtils.isEmpty(userManager.permissions)) {
            val permissions = Gson().fromJson<List<String>>(userManager.permissions,
                    object : TypeToken<List<String?>?>() {}.type)
            if (permissions != null && !permissions.isEmpty()) {
                val drawerMenu = binding.navView.menu

                val dashboard = drawerMenu.findItem(R.id.title_dashboard)
                dashboard.title = SpannyText(dashboard.title, StyleSpan(BOLD),
                        ForegroundColorSpan(ContextCompat.getColor(this, R.color.medium_grey)))

                val scan = drawerMenu.findItem(R.id.title_scan)
                scan.title = SpannyText(scan.title, StyleSpan(BOLD),
                        ForegroundColorSpan(ContextCompat.getColor(this, R.color.medium_grey)))

                val inventory = drawerMenu.findItem(R.id.title_inventory)
                inventory.title = SpannyText(inventory.title, StyleSpan(BOLD),
                        ForegroundColorSpan(ContextCompat.getColor(this, R.color.medium_grey)))

                val store = drawerMenu.findItem(R.id.title_store)
                store.title = SpannyText(store.title, StyleSpan(BOLD),
                        ForegroundColorSpan(ContextCompat.getColor(this, R.color.medium_grey)))

                val settings = drawerMenu.findItem(R.id.title_settings)
                settings.title = SpannyText(settings.title, StyleSpan(BOLD),
                        ForegroundColorSpan(ContextCompat.getColor(this, R.color.medium_grey)))

                if (permissions.contains(Permissions.GET_USER)) {
                    drawerMenu.findItem(R.id.nav_user_list).isVisible = true
                } else {
                    drawerMenu.findItem(R.id.nav_user_list).isVisible = false
                }
                if (permissions.contains(Permissions.GET_CUSTOMER)) {
                    drawerMenu.findItem(R.id.nav_customer_list).isVisible = true
                } else {
                    drawerMenu.findItem(R.id.nav_customer_list).isVisible = false
                }
                if (permissions.contains(Permissions.DEVICE_PROVISIONING)) {
                    drawerMenu.findItem(R.id.nav_device_provisioning).isVisible = true
                } else {
                    drawerMenu.findItem(R.id.nav_device_provisioning).isVisible = false
                }
                if (permissions.contains(Permissions.CREATE_DEVICE)) {
                    drawerMenu.findItem(R.id.nav_add_devices).isVisible = true
                } else {
                    drawerMenu.findItem(R.id.nav_add_devices).isVisible = false
                }
                if (permissions.contains(Permissions.CREATE_INSTALLATION)) {
                    drawerMenu.findItem(R.id.nav_add_center).isVisible = true
                } else {
                    drawerMenu.findItem(R.id.nav_add_center).isVisible = false
                }
                if (permissions.contains(Permissions.CREATE_REGION)) {
                    drawerMenu.findItem(R.id.nav_add_region).isVisible = true
                } else {
                    drawerMenu.findItem(R.id.nav_add_region).isVisible = false
                }
                if (permissions.contains(Permissions.CREATE_SITE)) {
                    drawerMenu.findItem(R.id.nav_add_site).isVisible = true
                } else {
                    drawerMenu.findItem(R.id.nav_add_site).isVisible = false
                }
                if (permissions.contains(Permissions.VIEW_SCAN_HISTORY)) {
                    drawerMenu.findItem(R.id.nav_scan_history).isVisible = true
                } else {
                    drawerMenu.findItem(R.id.nav_scan_history).isVisible = false
                }
            }
        }

        if (savedInstanceState == null) {
            if (userManager.customerType == "OPERATOR") {
                viewModel.onGetDevices()
            } else {
                showHomeScreen(device?.device_id, device?.device_name)
            }
            userManager.clearSavedDevice()
            serviceIntent = Intent(this, NanoBleService::class.java)
            startService(serviceIntent)
            LocalBroadcastManager.getInstance(this).registerReceiver(disconnectReceiver, disconnectFilter)
        }
    }

    fun showDeviceMenu(devices: ArrayList<DeviceItem>) {
        val listPopupWindow = ListPopupWindow(this, null, R.attr.listPopupWindowStyle)
        val adapter = ArrayAdapter(this, R.layout.item_device_type,
                devices.map { it.device_name }.toList())
        listPopupWindow.setAdapter(adapter)
        listPopupWindow.anchorView = binding.includeAppbar.appbarLayout
        listPopupWindow.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
            device = devices[position]
            toolbarTitle!!.text = device?.device_name
            listPopupWindow.dismiss()
            showHomeScreen(device?.device_id, device?.device_name)
        }
        listPopupWindow.show()
    }

    private fun updateUI(screenState: ScreenState<HomeDeviceState>?) {
        when (screenState) {
            ScreenState.Loading -> binding.progressBar.visibility = View.VISIBLE
            is ScreenState.Render -> processState(screenState.renderState)
        }
    }

    inner class DisconnectReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            showMessage("Device is disconnected!")
            userManager.clearSavedDevice()
        }
    }

    override fun showMessage(msg: String) {
        AlertUtil.showToast(this, msg)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        if (selectedMenuItem !== menuItem) {
            selectedMenuItem = menuItem
            return true
        }
        return false
    }

    override fun onBackStackChanged() {
        invalidateOptionsMenu()

        val currentFragment = supportFragmentManager.findFragmentById(R.id.layout_main)
        if (currentFragment is HomeFragment
                || currentFragment is LandingFragment
                || currentFragment is ScanHistoryFragment) {
            drawerToggle!!.syncState()
            binding.navView.setCheckedItem(R.id.nav_menu_home)
        }
    }

    private fun processState(renderState: HomeDeviceState) {
        binding.progressBar.visibility = View.GONE

        when (renderState) {
            HomeDeviceState.SubscribeDeviceSuccess -> {
                devices = viewModel.homeList.value!!.devices!!
                if (devices.size > 0) {
                    device = devices[0]
                    toolbarTitle!!.text = device!!.device_name
                    if (devices.size > 1) {
                        toolbarTitle!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_black, 0);
                    }
                    navigateScreen(intent.extras)
                } else {
                    fragmentTransition(EmptyFragment.newInstance("No device allocated"), EMPTY_FRAGMENT)
//                    AlertUtil.showSnackBar(binding.layoutMain, viewModel.errorMessage)
                }
            }
            HomeDeviceState.SubscribeDeviceFailure -> {
                AlertUtil.showSnackBar(binding.progressBar, viewModel.errorMessage)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.appbar_menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.layout_main)
        if (currentFragment is ScanHistoryFragment) {
            val skip = menu.findItem(R.id.item_skip)
            if (skip != null) {
                skip.isVisible = false
            }
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.layout_main)
                if (currentFragment is HomeFragment
                        || currentFragment is LandingFragment
                        || currentFragment is ScanHistoryFragment) {
                    return false
                }
            }
            R.id.item_search -> ActivityUtil.startActivity(this, SearchResultActivity::class.java, false)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showHomeScreen(deviceId: Int?, deviceName: String?) {
        if (userManager.customerType == "OPERATOR") {
            if (device?.device_id == 2) {
                fragmentTransition(SelectScanFragment
                        .newInstance(scanId, deviceId!!, deviceName), SELECT_SCAN_FRAGMENT)
            } else {
                fragmentTransition(ScanHistoryFragment
                        .newInstance(deviceId!!, deviceName!!), SCAN_HISTORY_FRAGMENT)
            }
        } else if (userManager.customerType == "CUSTOMER") {
            fragmentTransition(LandingFragment.newInstance(), LANDING_FRAGMENT)
        } else if (userManager.customerType == "SERVICE_PROVIDER") {
            fragmentTransition(HomeFragment.newInstance(), HOME_FRAGMENT)
        }
    }

    private fun showProfileScreen() {
        ActivityUtil.startActivity(this, UserDetailActivity::class.java, false)
    }

    override fun showAddFarmerScreen() {
        ActivityUtil.startActivity(this, FarmerDetailActivity::class.java, false)
    }

    override fun showSelectDeviceDialog() {
        SelectDeviceDialog.newInstance().show(supportFragmentManager, SELECT_DEVICE_DIALOG)
    }

    override fun showScanDialog(farmerCode: String, batchId: String, sample: SampleItem, commodity: CommodityItem) {
        ScanDialog.newInstance(farmerCode, batchId, sample, commodity).show(supportFragmentManager, SCAN_DIALOG)
    }

    override fun showBluetoothDialog() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(intent, BLUETOOTH_SETTINGS_REQUEST)
    }

    override fun showWarmupScreen(deviceMac: String) {
        val intent = Intent(this, ChemicalResultActivity::class.java)
        intent.putExtra(KEY_WARMUP, true)
        intent.putExtra(KEY_DEVICE_MAC, deviceMac)
        ActivityUtil.startActivity(this, intent, false)
    }

    override fun showReferenceScanScreen() {
        val intent = Intent(this, ChemicalResultActivity::class.java)
        intent.putExtra(KEY_REFERENCE, true)
        ActivityUtil.startActivity(this, intent, false)
    }

    override fun showScanScreen(batchId: String, location: String, farmerCode: String, sample: SampleItem, commodity: CommodityItem) {
        val intent = Intent(this, ChemicalResultActivity::class.java)
        intent.putExtra(ResultTable.BATCH_ID, batchId)
        intent.putExtra(KEY_LOCATION, location)
        intent.putExtra(KEY_FARMER, farmerCode)
        intent.putExtra(KEY_SAMPLE, Parcels.wrap(sample))
        intent.putExtra(KEY_COMMODITY, Parcels.wrap(commodity))
        intent.putExtra(KEY_APP_VERSION, getString(R.string.app_version))
        ActivityUtil.startActivity(this, intent, false)
    }

    private fun navigateScreen(bundle: Bundle?) {
        if (bundle != null)
            when (bundle.getString(FLOW)) {
                NAV_SPLASH, NAV_LOGIN -> {
                    showHomeScreen(device?.device_id, device?.device_name)
                }
                NAV_NOTIFICATION, NAV_SCAN_HISTORY_ACTIVITY -> {
                    scanId = bundle.getString(KEY_SCAN_ID)
                    val deviceId = bundle.getString(KEY_DEVICE_ID)!!.toInt()
                    val deviceName = bundle.getString(KEY_DEVICE_NAME)

                    showHomeScreen(deviceId, deviceName)

                    fragmentTransition(SelectScanFragment
                            .newInstance(scanId, deviceId, deviceName), SELECT_SCAN_FRAGMENT)
                }
            }
    }

    override fun showResultScreen(batchId: String, deviceId: Int) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultTable.BATCH_ID, batchId)
        intent.putExtra(KEY_DEVICE_ID, deviceId)
        ActivityUtil.startActivity(this, intent, false)
    }

    override fun showLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        ActivityUtil.startActivity(this, intent, true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragmentManager = supportFragmentManager
        when (requestCode) {
            LOCATION_SETTINGS_REQUEST, BLUETOOTH_SETTINGS_REQUEST -> {
                var currentFragment = fragmentManager.findFragmentByTag(SCAN_DIALOG)
                if (currentFragment == null) {
                    currentFragment = fragmentManager.findFragmentByTag(SELECT_DEVICE_DIALOG)
                }
                currentFragment?.onActivityResult(requestCode, resultCode, data)
            }
            REQ_EDIT_SCAN_HISTORY -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    navigateScreen(data.extras)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (serviceIntent != null) {
            stopService(serviceIntent)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(disconnectReceiver)
        }
        (application as CustomApp).releaseHomeComponent()
    }

}