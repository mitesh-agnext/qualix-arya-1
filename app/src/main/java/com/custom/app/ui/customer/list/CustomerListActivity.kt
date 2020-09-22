package com.custom.app.ui.customer.list

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.core.app.util.AlertUtil
import com.core.app.util.RxUtils
import com.custom.app.CustomApp
import com.custom.app.R
import com.custom.app.ui.base.ScreenState
import com.custom.app.ui.customer.add.AddCustomerActivity
import com.custom.app.ui.customer.detail.CustomerDetailActivity
import com.custom.app.ui.customer.edit.EditCustomerActivity
import com.custom.app.util.Constants.REQUEST_ADD_CUSTOMER
import com.custom.app.util.Constants.REQUEST_EDIT_CUSTOMER
import com.custom.app.util.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.user.app.data.UserManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_customer_list.*
import kotlinx.android.synthetic.main.layout_progress.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CustomerListActivity : AppCompatActivity(), View.OnClickListener, CustomerCallback {

    @Inject
    lateinit var interactor: CustomerInteractor

    @Inject
    lateinit var userManager: UserManager

    private lateinit var disposable: Disposable
    private lateinit var viewModel: CustomerListVM
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CustomApp).homeComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_list)
        initView()
    }

    private fun initView() {
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.customer_management)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        fbAdd.setOnClickListener(this)

        viewModel = ViewModelProvider(this,
                CustomerListVM.CustomerListViewModelFactory(interactor))[CustomerListVM::class.java]
        viewModel.customerState.observe(::getLifecycle, ::updateUI)
        viewModel.getCustomerListVm()

        searchBar.isFocusable = false

        disposable = RxSearchObservable.fromView(searchBar)
                .debounce(1, TimeUnit.SECONDS)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ query ->
                    if (query.isEmpty()) {
                        viewModel.getCustomerListVm()
                    } else {
                        viewModel.getCustomerSearchVm(query!!)
                    }
                })
    }

    private fun updateUI(screenState: ScreenState<CustomerState>?) {
        when (screenState) {
            ScreenState.Loading -> progress.visibility = View.VISIBLE
            is ScreenState.Render -> processLoginState(screenState.renderState)
        }
    }

    private fun processLoginState(renderState: CustomerState) {
        when (renderState) {
            CustomerState.TokenExpire -> {
                AlertUtil.showToast(this, getString(R.string.token_expire))
            }
            CustomerState.CustomerListSuccess -> {
                if (!TextUtils.isEmpty(userManager.customerType)) {
                    val qualityAnalysisAdapter = CustomerListAdapter(this, viewModel.customerList.value!!, this, userManager.customerType)
                    rvCustomer.adapter = qualityAnalysisAdapter
                    rvCustomer.layoutManager = LinearLayoutManager(this)
                }
            }
            CustomerState.CustomerListFailure -> {
                AlertUtil.showToast(this, viewModel.errorMsg)
            }
            CustomerState.DeleteCustomerFailure -> {
                AlertUtil.showToast(this, viewModel.errorMsg)
            }
            CustomerState.DeleteCustomerSuccess -> {
                AlertUtil.showToast(this, "Successfully  deleted")
                viewModel.getCustomerListVm()
            }
            CustomerState.ApproveCustFailure -> {
                AlertUtil.showToast(this, viewModel.errorMsg)
            }
            CustomerState.ApproveCustSuccess -> {
                viewModel.getCustomerListVm()
            }
            CustomerState.TokenExpire -> {
                AlertUtil.showToast(this, getString(R.string.token_expire))
                Utils.tokenExpire(this@CustomerListActivity)
            }
        }
        progress.visibility = View.GONE
    }

    override fun onClick(view: View?) {
        when (view) {
            fbAdd -> {
                var intent = Intent(this, AddCustomerActivity::class.java)
                startActivityForResult(intent, REQUEST_ADD_CUSTOMER)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun editCustomerCallback(pos: Int) {
        var intent = Intent(this, EditCustomerActivity::class.java)
        val gson = Gson()
        val type = object : TypeToken<CustomerRes>() {}.type
        val json = gson.toJson(viewModel.customerList.value!![pos], type)
        intent.putExtra("selectObject", json)
        startActivityForResult(intent, REQUEST_EDIT_CUSTOMER)
    }

    override fun approveClick(pos: Int) {
        showFeedbackDialog(this, pos)
    }

    override fun deleteCustomerCallback(pos: Int) {
        dialogConfirmDelete("Delete Customer", "Are you sure you want to delete customer", this, pos)
    }

    private fun dialogConfirmDelete(title: String?, message: String?, context: Context, pos: Int) {
        AlertDialog.Builder(context)
                .setTitle("$title")
                .setMessage("$message")
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    viewModel.deleteCustomer(pos)
                }
                .show()
    }

    private fun showFeedbackDialog(activity: Activity, pos: Int) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cutsom_approve_dailog)
        val etFeedback = dialog.findViewById(R.id.etFeedback) as EditText
        val btSubmit = dialog.findViewById(R.id.btSubmit) as TextView
        val btCancel = dialog.findViewById(R.id.btCancel) as TextView
        val params: ViewGroup.LayoutParams = dialog.window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window.attributes = params as WindowManager.LayoutParams?
        btSubmit.setOnClickListener {

            viewModel.approveCustomer(pos, etFeedback.text.toString())
            dialog.dismiss()
        }

        btCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    /**Override*/
    override fun itemClick(pos: Int) {
        var intent = Intent(this, CustomerDetailActivity::class.java)
        val gson = Gson()
        val type = object : TypeToken<CustomerRes>() {}.type
        val json = gson.toJson(viewModel.customerList.value!![pos], type)
        intent.putExtra("selectObject", json)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_CUSTOMER || requestCode == REQUEST_EDIT_CUSTOMER) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.getCustomerListVm()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        RxUtils.dispose(disposable)
    }
}

interface CustomerCallback {

    fun editCustomerCallback(pos: Int)
    fun deleteCustomerCallback(pos: Int)
    fun approveClick(pos: Int)
    fun itemClick(pos: Int)

}

object RxSearchObservable {
    fun fromView(searchView: SearchView): Observable<String> {
        val subject: PublishSubject<String> = PublishSubject.create()
        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                subject.onComplete()
                searchView.clearFocus();
                return false
            }

            override fun onQueryTextChange(text: String): Boolean {
                subject.onNext(text)
                return false
            }
        })
        return subject
    }
}