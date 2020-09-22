package com.custom.app.ui.farm.farmList

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.agnext.qualixfarmer.farmList.farmList.FieldInteractor
import com.agnext.qualixfarmer.farmList.farmList.FieldListState
import com.agnext.qualixfarmer.farmList.farmList.FieldListViewModel
import com.agnext.qualixfarmer.farmList.farmList.FieldListViewModelFactory
import com.base.app.ui.base.BaseActivity
import com.core.app.util.AlertUtil
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.farm.addFarm.AddFarmActivity
import com.custom.app.ui.farm.farmDetail.FarmDetailActivity
import com.custom.app.ui.farm.updateFarm.UpdateFarmActivity
import com.custom.app.util.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_field_list.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.layout_progress.*

class FieldListActivity : BaseActivity(), View.OnClickListener, FarmListCallback {


    var viewModel: FieldListViewModel? = null
    var fieldListAdapter: FieldListAdapter? = null
    val REQUEST_FARM: Int = 111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_field_list)
        initView()
    }

    fun initView() {

        //setting toolbar
        toolbar.title = getString(R.string.farmer_field)
        setSupportActionBar(toolbar)
        //enabling back button
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)


        //VM
        viewModel = ViewModelProvider(
            this, FieldListViewModelFactory(
                FieldInteractor()
            )
        )[FieldListViewModel::class.java]
        viewModel!!.fieldListState.observe(::getLifecycle, ::updateUI)

        //click register
        fbAdd.setOnClickListener(this)
        fbAdd.visibility = View.VISIBLE


        progress.visibility = View.VISIBLE
        //action
        viewModel!!.getAllFarmVM(Constants.TOKEN)

    }

    private fun updateUI(screenState: ScreenState<FieldListState>?) {
        when (screenState) {
            ScreenState.Loading -> progress.visibility = View.VISIBLE
            is ScreenState.Render -> displayData(screenState.renderState)
        }
    }

    private fun displayData(renderState: FieldListState) {
        progress.visibility = View.GONE
        when (renderState) {
            FieldListState.FarmListFailure -> {
                AlertUtil.showToast(this, "Error to get farm")
                tvNoData.visibility = View.VISIBLE
            }

            FieldListState.FarmListSuccess -> {
                tvNoData.visibility = View.GONE
                updateList()
            }

            FieldListState.FarmNoRecord -> {
                tvNoData.visibility = View.VISIBLE
                AlertUtil.showToast(this, "No farm")
            }

            FieldListState.FarmDeleteFailure -> {
                AlertUtil.showToast(this, "Unable to delete the farm")
            }
            FieldListState.FarmDeleteSuccess -> {
                viewModel!!.getAllFarmVM(Constants.TOKEN)
            }

        }
    }

    fun updateList() {
        rvField.layoutManager = LinearLayoutManager(this)
        rvField.adapter =
            FieldListAdapter(this@FieldListActivity, viewModel!!.fieldList.value!!, this)
    }

    /**Override*/
    override fun onClick(view: View?) {
        when (view) {
            fbAdd -> {
                //  IntentUtil.moveNextScreen(this, AddFarmActivity::class.java)
                val intent = Intent(this, AddFarmActivity::class.java)
                startActivityForResult(intent, REQUEST_FARM)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FARM) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel!!.getAllFarmVM(Constants.TOKEN)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**Callback*/
    override fun onClickItem(position: Int) {
        val fieldData: FarmRes = viewModel!!.fieldList.value!![position]
        val gson = Gson()
        val type = object : TypeToken<FarmRes>() {}.type
        val json = gson.toJson(fieldData, type)
        var intentWithData = Intent(this, FarmDetailActivity::class.java)
        intentWithData.putExtra("jsonObject", json)
        startActivity(intentWithData)

    }

    override fun editFarmCallback(pos: Int) {
        var intentWithData = Intent(this, UpdateFarmActivity::class.java)
        intentWithData.putExtra("farmId", viewModel!!.fieldList.value!![pos].plot_id!!)
        startActivity(intentWithData)
    }

    override fun deleteFarmCallback(position: Int) {
        dialogConformDelete("Delete Farm", "Are you sure want to delete the farm", this, position)
    }

    /**  Alert Dialog*/

    private fun dialogConformDelete(
        title: String?,
        message: String?,
        context: Context,
        pos: Int
    ) {
        AlertDialog.Builder(context)
            .setTitle("$title")
            .setMessage("$message")

            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->

                viewModel!!.deleteFarm(Constants.TOKEN, viewModel!!.fieldList.value!![pos].plot_id!!)
            }
            .show()
    }


}

interface FarmListCallback {
    fun onClickItem(position: Int)
    fun editFarmCallback(position: Int)
    fun deleteFarmCallback(position: Int)
}
