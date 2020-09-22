package com.custom.app.ui.user.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.core.app.util.RxUtils
import com.custom.app.ui.customer.list.CustomerInteractor
import com.custom.app.ui.customer.list.CustomerListListener
import com.custom.app.ui.customer.list.CustomerRes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class UserListViewModel(val userInteractor: UserInteractor, val customerInteractor: CustomerInteractor) : ViewModel(), UserListListener, CustomerListListener {

    private var disposable: Disposable? = null

    val userListStateLiveData: MutableLiveData<UserListState> = MutableLiveData()

    private val userListLiveData: MutableLiveData<ArrayList<UserDataRes>> = MutableLiveData()
    val userList: LiveData<ArrayList<UserDataRes>> get() = userListLiveData
    val customerList = ArrayList<CustomerRes>()

    var deletePosition: Int? = null

    fun getCustomerList() {
        userListStateLiveData.value = Loading
        customerInteractor.list(this)
    }

    fun getUserListVm(customerId: String) {
        userListStateLiveData.value = Loading

        disposable = userInteractor.getUserForCustomer(customerId)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe({ users: ArrayList<UserDataRes> ->
                    onUserListSuccess(users)
                }, { error: Throwable ->
                    onError(error.message)
                })
    }

    fun getUserListSearchVm(keyword: String) {
        userListStateLiveData.value = Loading
        userInteractor.search(keyword, this)
    }

    fun deleteUser(pos: Int) {
        deletePosition = pos
        userInteractor.delete(userList.value!![pos], this)
        userListStateLiveData.value = Delete
    }

    override fun onUserListSuccess(users: ArrayList<UserDataRes>) {
        userListLiveData.value = users
        userListStateLiveData.value = List(users)
    }

    override fun onDeleteUserSuccess(user: UserDataRes) {
        userList.value!!.remove(user)
        userListStateLiveData.value = Delete
    }

    override fun onError(msg: String?) {
        userListStateLiveData.value = Error(msg)
    }

    fun destroy() {
        RxUtils.dispose(disposable)
    }


    override fun onCustomerListSuccess(response: ArrayList<CustomerRes>) {
        customerList.clear()
        customerList.addAll(response)
        userListStateLiveData.value = CustomerList
    }

    override fun onCustomerListFailure(msg: String) {
        customerList.clear()
        userListStateLiveData.value = CustomerError    }

    override fun onTokenExpire() {
        userListStateLiveData.value = TokenExpire
    }
}

class UserListViewModelFactory(private val userInteractor: UserInteractor, private val customerInteractor: CustomerInteractor) :
        ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserListViewModel(userInteractor, customerInteractor) as T
    }
}