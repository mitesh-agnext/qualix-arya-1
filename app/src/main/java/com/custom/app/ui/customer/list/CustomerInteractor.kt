package com.custom.app.ui.customer.list

import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.ui.createData.analytics.analyticsScreen.CategoryTabsRes
import com.user.app.data.UserManager
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class CustomerInteractor(val apiService: ApiInterface,val userManager: UserManager) {

    fun search(keyword: String, mCallback: CustomerListListener) {
        val call = apiService.getCustomersSearch("Bearer ${userManager.token}", keyword)
        call.enqueue(object : Callback<ArrayList<CustomerRes>> {
            override fun onFailure(call: Call<ArrayList<CustomerRes>>, t: Throwable) {
                mCallback.onCustomerListFailure(t.message.toString())
            }

            override fun onResponse(call: Call<ArrayList<CustomerRes>>, response: Response<ArrayList<CustomerRes>>) {
                when (response.code()) {
                    200 -> {
                        mCallback.onCustomerListSuccess(response.body()!!)
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.onCustomerListFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.onCustomerListFailure(e.message.toString())
                        }
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                }
            }
        })
    }

    fun list(mCallback: CustomerListListener) {
        val call = apiService.getCustomers("Bearer ${userManager.token}")
        call.enqueue(object : Callback<ArrayList<CustomerRes>> {
            override fun onFailure(call: Call<ArrayList<CustomerRes>>, t: Throwable) {
                mCallback.onCustomerListFailure("Failed to fetch list")
            }

            override fun onResponse(call: Call<ArrayList<CustomerRes>>, response: Response<ArrayList<CustomerRes>>) {
                when (response.code()) {
                    200 -> {
                        mCallback.onCustomerListSuccess(response.body()!!)
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.onCustomerListFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.onCustomerListFailure(e.message.toString())
                        }
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                }
            }
        })
    }

    fun delete(customerID: String, mCallback: DeleteCustomerListener) {
        val call = apiService.deleteCustomer("Bearer ${userManager.token}", customerID)
        call.enqueue(object : Callback<CustomerRes> {
            override fun onFailure(call: Call<CustomerRes>, t: Throwable) {
                mCallback.onDeleteCustomerFailure("Error to delete")
            }

            override fun onResponse(call: Call<CustomerRes>, response: Response<CustomerRes>) {
                when (response.code()) {
                    204 -> {
                        mCallback.onDeleteCustomerSuccess()
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.onDeleteCustomerFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.onDeleteCustomerFailure(e.message.toString())
                        }
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                }
            }
        })
    }

    fun approve(body: HashMap<String, Any>, mCallback: DeleteCustomerListener) {

        val call = apiService.approvePartner("Bearer ${userManager.token}", body)
        call.enqueue(object : Callback<CustomerRes> {
            override fun onFailure(call: Call<CustomerRes>, t: Throwable) {
                mCallback.onApproveCustFailure("Error to approve")
            }

            override fun onResponse(call: Call<CustomerRes>, response: Response<CustomerRes>) {
                when (response.code()) {
                    200 -> {
                        mCallback.onApproveCustSuccess()
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.onApproveCustFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.onApproveCustFailure(e.message.toString())
                        }
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                }
            }
        })
    }

    fun add(requestData: HashMap<String, Any>, mCallback: AddCustomerListener) {
        mCallback.onAddCustomerSuccess()
    }

    fun edit(customer_id: String, requestData: HashMap<String, Any>, mCallback: EditCustomerListener) {

        val call = apiService.updateCustomer("Bearer ${userManager.token}", customer_id, requestData)
        call.enqueue(object : Callback<CustomerRes> {

            override fun onFailure(call: Call<CustomerRes>, t: Throwable) {
                mCallback.onEditCustomerFailure("Error to update")
            }

            override fun onResponse(call: Call<CustomerRes>, response: Response<CustomerRes>) {
                when (response.code()) {
                    200 -> {
                        mCallback.onEditCustomerSuccess()
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.onEditCustomerFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.onEditCustomerFailure(e.message.toString())
                        }
                    }
                    else -> {
                        mCallback.onEditCustomerFailure("Error to update")
                    }
                }
            }
        })
    }

    fun getPartner( mCallback: AddCustomerListener) {
        val call = apiService.getPartner("Bearer ${userManager.token}")
        call.enqueue(object : Callback<ArrayList<CustomerRes>> {

            override fun onFailure(call: Call<ArrayList<CustomerRes>>, t: Throwable) {
                mCallback.onGetPartnerFailure("Error to get partner")
            }

            override fun onResponse(call: Call<ArrayList<CustomerRes>>, response: Response<ArrayList<CustomerRes>>) {
                when (response.code()) {
                    200 -> {
                        mCallback.onGetPartnerSuccess(response.body()!!)
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.onGetPartnerFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.onGetPartnerFailure(e.message.toString())
                        }
                    }
                    else -> {
                        mCallback.onGetPartnerFailure("Error to get partner")
                    }
                }
            }
        })
    }

    fun allCategories(listener: AddCustomerListener, customer_id: String) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getCategoriesLists("Bearer ${userManager.token}",customer_id)
            call.enqueue(object : Callback<ArrayList<CategoryTabsRes>> {
                override fun onResponse(
                        call: Call<ArrayList<CategoryTabsRes>>, response: Response<ArrayList<CategoryTabsRes>>) {

                    when {
                        response.isSuccessful -> listener.allCategoryApiSuccess(response.body()!!)
                        else -> {
                            when {
                                else -> {
                                    val jObjError = JSONObject(response.errorBody()!!.string())
                                    listener.allCategoryApiError(jObjError.getString("error-message"))
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<CategoryTabsRes>>, t: Throwable) {
                    listener.allCategoryApiError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}

interface CustomerListListener {

    fun onCustomerListSuccess(response: ArrayList<CustomerRes>)
    fun onCustomerListFailure(msg: String)
    fun onTokenExpire()
}

interface DeleteCustomerListener {

    fun onApproveCustSuccess()
    fun onApproveCustFailure(s: String)
    fun onDeleteCustomerSuccess()
    fun onDeleteCustomerFailure(msg: String)
    fun onTokenExpire()

}

interface AddCustomerListener {
    fun onGetPartnerSuccess(response:ArrayList<CustomerRes>)
    fun onGetPartnerFailure(msg: String)
    fun onAddCustomerSuccess()
    fun onAddCustomerFailure()
    fun allCategoryApiSuccess(body: ArrayList<CategoryTabsRes>)
    fun allCategoryApiError(msg: String)
    fun onTokenExpire()
}

interface EditCustomerListener {
    fun onEditCustomerSuccess()
    fun onEditCustomerFailure(msg: String)
    fun onTokenExpire()
}