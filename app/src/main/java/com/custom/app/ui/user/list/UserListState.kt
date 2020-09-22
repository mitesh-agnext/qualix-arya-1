package com.custom.app.ui.user.list

sealed class UserListState
object Loading : UserListState()
data class Error(val message: String?) : UserListState()
data class List(val users: ArrayList<UserDataRes>) : UserListState()
object Delete : UserListState()
object TokenExpire : UserListState()

object CustomerList : UserListState()
object CustomerError : UserListState()