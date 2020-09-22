package com.custom.app.ui.user.list

import com.custom.app.data.model.role.RoleRes
import com.custom.app.network.ApiInterface
import com.custom.app.network.RestService
import com.custom.app.ui.customer.list.CustomerRes
import com.user.app.data.UserManager
import io.reactivex.Single
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class UserInteractor(val userManager: UserManager, val apiService: ApiInterface, val restService: RestService) {

    fun getList(): Single<ArrayList<UserDataRes>> {
        return restService.users()
    }

    fun getUserForCustomer(customerId:String): Single<ArrayList<UserDataRes>> {
        return restService.usersForCustomer(customerId)
    }

    fun search(keyword: String, mCallback: UserListListener) {
        val call = apiService.getUsersSearch("Bearer ${userManager.token}", keyword)
        call.enqueue(object : Callback<ArrayList<UserDataRes>> {
            override fun onFailure(call: Call<ArrayList<UserDataRes>>, t: Throwable) {
                mCallback.onError(t.message)
            }

            override fun onResponse(call: Call<ArrayList<UserDataRes>>, response: Response<ArrayList<UserDataRes>>) {
                when (response.code()) {
                    200 -> {
                        mCallback.onUserListSuccess(response.body()!!)
                    }
                    else -> {
                        mCallback.onError("Error to fetch data")
                    }
                }
            }
        })
    }

    fun addCustomer(requestData: HashMap<String, Any>, mCallback: AddUserListener) {
        val call = apiService.addCustomerUser("Bearer ${userManager.token}", requestData)
        call.enqueue(object : Callback<CustomerRes> {
            override fun onFailure(call: Call<CustomerRes>, t: Throwable) {
                mCallback.onAddUserFailure("Error to add User")
            }

            override fun onResponse(call: Call<CustomerRes>, response: Response<CustomerRes>) {
                    when (response.code()) {
                        200 -> {
                            mCallback.onAddUserSuccess(response)
                        }
                        201 -> {
                            mCallback.onAddUserSuccess(response)
                        }
                        401 -> {
                            mCallback.onTokenExpire()
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                mCallback.onAddUserFailure(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                Timber.e(e)
                                mCallback.onAddUserFailure(e.message.toString())
                            }
                        }
                        else -> {
                            mCallback.onAddUserFailure("Error to add User")
                        }

                    }
            }
        })
    }

    fun add(requestData: HashMap<String, Any>, mCallback: AddUserListener) {
        val call = apiService.addUser("Bearer ${userManager.token}", requestData)
        call.enqueue(object : Callback<UserDataRes> {
            override fun onFailure(call: Call<UserDataRes>, t: Throwable) {
                mCallback.onAddUserFailure("Error to add User")
            }

            override fun onResponse(call: Call<UserDataRes>, response: Response<UserDataRes>) {
                    when (response.code()) {
                        201 -> {
                            mCallback.onAddUserSingleSuccess()
                        }
                        401 -> {
                            mCallback.onTokenExpire()
                        }
                        400 -> {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                mCallback.onAddUserFailure(jObjError.getString("error-message"))
                            } catch (e: Exception) {
                                Timber.e(e)
                                mCallback.onAddUserFailure(e.message.toString())
                            }
                        }
                        else -> {
                            mCallback.onAddUserFailure("Error to add User")
                        }
                    }
                }
        })
    }

    fun edit(user_id: String, requestData: HashMap<String, Any>, mCallback: EditUserListener) {
        val call = apiService.updateUser("Bearer ${userManager.token}", user_id, requestData)
        call.enqueue(object : Callback<UserDataRes> {

            override fun onFailure(call: Call<UserDataRes>, t: Throwable) {
                mCallback.onEditUserFailure("Error to update")
            }

            override fun onResponse(call: Call<UserDataRes>, response: Response<UserDataRes>) {
                when (response.code()) {
                    200 -> {
                        mCallback.onEditUserSuccess()
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.onEditUserFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.onEditUserFailure(e.message.toString())
                        }
                    }
                    else -> {
                        mCallback.onEditUserFailure("Error to update")
                    }
                }
            }
        })
    }

    fun delete(user: UserDataRes, mCallback: UserListListener) {
        val call = apiService.deleteUser("Bearer ${userManager.token}", user.user_id.toString())
        call.enqueue(object : Callback<UserDataRes> {
            override fun onFailure(call: Call<UserDataRes>, t: Throwable) {
                mCallback.onError("Error to delete")
            }

            override fun onResponse(call: Call<UserDataRes>, response: Response<UserDataRes>) {
                when (response.code()) {
                    204 -> {
                        mCallback.onDeleteUserSuccess(user)
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.onError(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.onError(e.message.toString())
                        }
                    }
                }
            }
        })
    }

    fun role(mCallback: UserRoleListener) {
        val call = apiService.getRole("Bearer ${userManager.token}")
        call.enqueue(object : Callback<ArrayList<RoleRes>> {
            override fun onFailure(call: Call<ArrayList<RoleRes>>, t: Throwable) {
                mCallback.onUserRoleFailure("Error to get user role")
            }

            override fun onResponse(call: Call<ArrayList<RoleRes>>, response: Response<ArrayList<RoleRes>>) {
                when (response.code()) {
                    200 -> {
                        if (response.body()!!.size > 0)
                            mCallback.onUserRoleSuccess(response)
                        else mCallback.onUserRoleFailure("Error to get user role")
                    }
                    401 -> {
                        mCallback.onTokenExpire()
                    }
                    400 -> {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            mCallback.onUserRoleFailure(jObjError.getString("error-message"))
                        } catch (e: Exception) {
                            mCallback.onUserRoleFailure(e.message.toString())
                        }
                    }
                    else -> {
                        mCallback.onUserRoleFailure("Error to get user role")
                    }
                }
            }

        })
    }
}

interface UserListListener {

    fun onUserListSuccess(users: ArrayList<UserDataRes>)
    fun onDeleteUserSuccess(user: UserDataRes)
    fun onError(msg: String?)

}

interface AddUserListener {

    fun onAddUserSuccess(response: Response<CustomerRes>)
    fun onAddUserFailure(msg: String)
    fun onAddUserSingleSuccess()
    fun onTokenExpire()

}

interface EditUserListener {

    fun onEditUserSuccess()
    fun onEditUserFailure(msg: String)
    fun onTokenExpire()

}

interface UserRoleListener {

    fun onUserRoleSuccess(response: Response<ArrayList<RoleRes>>)
    fun onUserRoleFailure(msg: String)
    fun onTokenExpire()

}