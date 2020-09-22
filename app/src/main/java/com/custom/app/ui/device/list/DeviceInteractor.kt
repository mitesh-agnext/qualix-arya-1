package com.custom.app.ui.device.list

import android.content.Context
import com.custom.app.network.ApiClient
import com.custom.app.network.ApiInterface
import com.custom.app.ui.customer.list.CustomerRes
import com.custom.app.ui.device.add.DeviceGroupRes
import com.custom.app.ui.device.add.DeviceSubTypeRes
import com.custom.app.ui.device.add.DeviceTypeRes
import com.custom.app.ui.device.add.SensorProfileRes
import com.custom.app.ui.device.assign.DeviationProfileRes
import com.custom.app.ui.device.assign.InstallationCenterRes
import com.custom.app.ui.user.list.UserDataRes
import com.google.gson.JsonObject
import com.user.app.data.UserManager
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class DeviceInteractor(val userManager: UserManager) {

    fun allDevices(listener: ListDeviceInteractorCallback, keyword: String, listStatus: String) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.searchDevices("Bearer ${userManager.token}", keyword, listStatus)
            call.enqueue(object : Callback<ArrayList<DevicesData>> {
                override fun onResponse(
                        call: Call<ArrayList<DevicesData>>, response: Response<ArrayList<DevicesData>>) {


                    when {
                        response.isSuccessful -> listener.allDevicesApiSuccess(response.body()!!)
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            when {
                                else -> listener.allDevicesApiError(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<DevicesData>>, t: Throwable) {
                    listener.allDevicesApiError(t.message.toString())
                }
            })

        } catch (e: Exception) {
            Timber.e(e);
        }
    }

    fun deleteDevices(listener: ListDeviceInteractorCallback, deviceId: String, deletedPostion: Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)

            val call = apiService.deleteDevice("Bearer ${userManager.token}", deviceId)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>) {

                    when {
                        response.isSuccessful -> listener.deleteDeviceSuccess(deletedPostion)
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())

                            when {
                                else -> listener.deleteDeviceFailure(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.deleteDeviceFailure(t.message.toString())
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addDevices(serial_nummber: String, device_type: Int, hw_revision: String, fw_revision: String, start_of_life: String, end_of_life: String, sensor_profile_id: Int,
                   device_sub_type_id: Int, device_group_id: Int, vendor: String, listener: AddDeviceListener, context: Context) {
        when {
            serial_nummber.isEmpty() -> {
                listener.onSerialNumberEmpty()
            }
            start_of_life.isEmpty() -> {
                listener.onStartLifeEmpty()
            }
            end_of_life.isEmpty() -> {
                listener.onEndLifeEmpty()
            }
            vendor.isEmpty() -> {
                listener.onVendorEmpty()
            }
            else -> {

                val options = JsonObject()
                options.addProperty("serial_number", serial_nummber)
                options.addProperty("device_type_id", device_type)
                options.addProperty("hw_revision", hw_revision)
                options.addProperty("fw_revision", fw_revision)
                options.addProperty("start_of_life", start_of_life)
                options.addProperty("end_of_life", end_of_life)
                options.addProperty("sensor_profile_id", sensor_profile_id)
                options.addProperty("device_sub_type_id", device_sub_type_id)
                options.addProperty("device_group_id", device_group_id)
                options.addProperty("vendor_name", vendor)

                submitAddDevices(listener, options)
            }
        }
    }

    private fun submitAddDevices(listener: AddDeviceListener, options: JsonObject) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.addDevices("Bearer ${userManager.token}", options)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onAddDevicesFailure(t!!.message.toString())
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {

                if (response.isSuccessful) {
                    listener.onAddDevicesSuccess(response.body()!!)
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())

                    if (response.code() == 401) {
                        listener.onAddDevicesFailure(jObjError.getString("error-message"))
                    } else {
                        listener.onAddDevicesFailure(jObjError.getString("error-message"))
                    }
                }
            }
        })
    }

    fun getDevicesType(listener: AddDeviceListener) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getDeviceType("Bearer ${userManager.token}")
            call.enqueue(object : Callback<ArrayList<DeviceTypeRes>> {
                override fun onResponse(
                        call: Call<ArrayList<DeviceTypeRes>>, response: Response<ArrayList<DeviceTypeRes>>) {

                    when {
                        response.isSuccessful -> {
                            listener.onGetDeviceTypeSuccess(response.body()!!)
                        }
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())

                            when {
                                response.code() == 401 ->
                                    listener.onGetDeviceTypeError(jObjError.getString("error-message"))
                                else -> listener.onGetDeviceTypeError(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<DeviceTypeRes>>, t: Throwable) {
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun getDevicesSubType(listener: AddDeviceListener) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getDeviceSubType("Bearer ${userManager.token}")
            call.enqueue(object : Callback<ArrayList<DeviceSubTypeRes>> {
                override fun onResponse(
                        call: Call<ArrayList<DeviceSubTypeRes>>,
                        response: Response<ArrayList<DeviceSubTypeRes>>) {
                    when {
                        response.isSuccessful -> {
                            listener.onGetDeviceSubTypeSuccess(response.body()!!)
                        }
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())

                            when {
                                response.code() == 401 -> listener.onGetDeviceSubTypeError(jObjError.getString("error-message"))
                                else -> listener.onGetDeviceSubTypeError(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<DeviceSubTypeRes>>, t: Throwable) {
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun getDevicesTypeGroup(listener: AddDeviceListener) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getDeviceGroup("Bearer ${userManager.token}")
            call.enqueue(object : Callback<ArrayList<DeviceGroupRes>> {
                override fun onResponse(
                        call: Call<ArrayList<DeviceGroupRes>>, response: Response<ArrayList<DeviceGroupRes>>) {

                    when {
                        response.isSuccessful -> {
                            listener.onGetDeviceTypeGroupSuccess(response.body()!!)
                        }
                        else -> {
                            val jObjError = JSONObject(response.errorBody()!!.string())

                            when {
                                response.code() == 401 -> listener.onGetDeviceTypeGroupError(jObjError.getString("error-message"))
                                else -> listener.onGetDeviceTypeGroupError(jObjError.getString("error-message"))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<DeviceGroupRes>>, t: Throwable) {
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun getSensorProfile(listener: AddDeviceListener) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getSensorProfile("Bearer ${userManager.token}")
            call.enqueue(object : Callback<ArrayList<SensorProfileRes>> {
                override fun onResponse(
                        call: Call<ArrayList<SensorProfileRes>>, response: Response<ArrayList<SensorProfileRes>>) {


                    if (response.isSuccessful) {
                        listener.onGetSensorProfileSuccess(response.body()!!)
                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())

                        when {
                            response.code() == 401 -> listener.onGetSensorProfileError(jObjError.getString("error-message"))
                            else -> listener.onGetSensorProfileError(jObjError.getString("error-message"))
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<SensorProfileRes>>, t: Throwable) {
                    listener.onGetSensorProfileError(t.message.toString())
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
            listener.onGetSensorProfileError(e.message.toString())
        }
    }

    fun updateDevices(serial_nummber: String, device_type: Int, hw_revision: String, fw_revision: String, start_of_life: String,
                      end_of_life: String, sensor_profile_id: Int, device_sub_type_id: Int, device_group_id: Int, vendor: String,
                      device_Id: Int, listener: UpdateDevicesListener, context: Context) {

        when {
            serial_nummber.isEmpty() -> {
                listener.onSerialNumberEmpty()
            }
            start_of_life.isEmpty() -> {
                listener.onStartLifeEmpty()
            }
            end_of_life.isEmpty() -> {
                listener.onEndLifeEmpty()
            }
            vendor.isEmpty() -> {
                listener.onVendorEmpty()
            }
            else -> {

                val options = JsonObject()
                options.addProperty("serial_number", serial_nummber)
                options.addProperty("device_type_id", device_type)
                options.addProperty("hw_revision", hw_revision)
                options.addProperty("fw_revision", fw_revision)
                options.addProperty("start_of_life", start_of_life)
                options.addProperty("end_of_life", end_of_life)
                options.addProperty("sensor_profile_id", sensor_profile_id)
                options.addProperty("device_sub_type_id", device_sub_type_id)
                options.addProperty("device_group_id", device_group_id)
                options.addProperty("vendor_name", vendor)

                submitUpdateDevices(listener, options, device_Id)

            }
        }
    }

    private fun submitUpdateDevices(listener: UpdateDevicesListener, options: JsonObject, device_id: Int) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.updateDevices("Bearer ${userManager.token}", options, device_id)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onUpdateDevicesFailure(t!!.message.toString())
            }

            override fun onResponse(
                    call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onUpdateDevicesSuccess(response.body()!!)
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())

                    if (response.code() == 401) {
                        listener.onUpdateDevicesFailure(jObjError.getString("error-message"))
                    } else {
                        listener.onUpdateDevicesFailure(jObjError.getString("error-message"))
                    }
                }
            }
        })
    }

    fun deviceProvisioning(hw_revision: String, fw_revision: String, start_of_service: String, end_of_service: String,
                           customer_id: String, installation_center_id: String, deviceId: Int, coldstoreId: String,
                           deviation_id: String, user_id: String, listener: ProvisioningDevicesListener) {
        when {
            start_of_service.isEmpty() -> {
                listener.onStartServiceEmpty()
            }
            end_of_service.isEmpty() -> {
                listener.onEndServiceEmpty()
            }
            else -> {

                val options = JsonObject()

                options.addProperty("commercial_location_id", installation_center_id)
                options.addProperty("hw_revision", hw_revision)
                options.addProperty("fw_revision", fw_revision)
                options.addProperty("start_of_service", start_of_service)
                options.addProperty("end_of_service", end_of_service)
                options.addProperty("customer_id", customer_id)
                options.addProperty("cold_store_id", coldstoreId)
                options.addProperty("user_id", user_id)
                options.addProperty("deviation_id", deviation_id)

                submitDevicesProvisioning(listener, options, deviceId)
            }
        }
    }

    private fun submitDevicesProvisioning(listener: ProvisioningDevicesListener, options: JsonObject, deviceId: Int) {

        val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
        val call = apiService.deviceProvisioning("Bearer ${userManager.token}", options, deviceId)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                listener.onProvisioningDevicesFailure(t!!.message.toString())
            }

            override fun onResponse(
                    call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onProvisioningDevicesSuccess(response.body()!!)
                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())

                    if (response.code() == 401) {
                        listener.onProvisioningDevicesFailure(jObjError.getString("error-message"))
                    } else {
                        listener.onProvisioningDevicesFailure(jObjError.getString("error-message"))
                    }
                }
            }
        })
    }

    fun getDeviceCustomers(listener: ProvisioningDevicesListener) {
        try {
            val apiService = ApiClient.getClient().create(ApiInterface::class.java)
            val call = apiService.getCustomers("Bearer ${userManager.token}")

            call.enqueue(object : Callback<ArrayList<CustomerRes>> {
                override fun onResponse(call: Call<ArrayList<CustomerRes>>,
                                        response: Response<ArrayList<CustomerRes>>) {
                    if (response.isSuccessful) {
                        if (response.code() == 204) {
                            listener.onGetCustomerEmpty("No record found")
                        } else {
                            listener.onGetCustomerSuccess(response.body()!!)
                        }

                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())

                        when {
                            response.code() == 401 -> listener.onGetCustomerFailure(jObjError.getString("error-message"))
                            else -> listener.onGetCustomerFailure(jObjError.getString("error-message"))
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<CustomerRes>>, t: Throwable) {
                    listener.onGetCustomerFailure(t.message.toString())
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
            listener.onGetCustomerFailure(e.message.toString())
        }
    }

    fun getDeviceUsers(listener: ProvisioningDevicesListener, customer_id: String) {
        try {
            val apiService = ApiClient.getClient().create(ApiInterface::class.java)
            val call = apiService.getUsers("Bearer ${userManager.token}", customer_id)

            call.enqueue(object : Callback<ArrayList<UserDataRes>> {
                override fun onResponse(call: Call<ArrayList<UserDataRes>>, response: Response<ArrayList<UserDataRes>>) {

                    if (response.isSuccessful) {

                        if (response.code() == 204) {
                            listener.onGetUserEmpty("No record found")
                        } else {
                            listener.onGetUserSuccess(response.body()!!)
                        }

                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())

                        when {
                            response.code() == 401 -> listener.onGetUserFailure(jObjError.getString("error-message"))
                            else -> listener.onGetUserFailure(jObjError.getString("error-message"))
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<UserDataRes>>, t: Throwable) {
                    listener.onGetCustomerFailure(t.message.toString())
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
            listener.onGetUserFailure(e.message.toString())
        }
    }

    fun getInstallationCenters(listener: ProvisioningDevicesListener, customer_id: Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getInstallationCenters("Bearer ${userManager.token}", "", customer_id.toString(), "")

            call.enqueue(object : Callback<ArrayList<InstallationCenterRes>> {
                override fun onResponse(call: Call<ArrayList<InstallationCenterRes>>,
                                        response: Response<ArrayList<InstallationCenterRes>>) {
                    if (response.isSuccessful) {

                        if (response.code() == 204) {
                            listener.onGetInstallationCentersEmpty("No record found")
                        } else {
                            listener.onGetInstallationCentersSuccess(response.body()!!)
                        }

                    } else {

                        val jObjError = JSONObject(response.errorBody()!!.string())

                        when {
                            response.code() == 401 -> listener.onGetInstallationCentersFailure(jObjError.getString("error-message"))
                            else -> listener.onGetInstallationCentersFailure(jObjError.getString("error-message"))
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<InstallationCenterRes>>, t: Throwable) {
                    listener.onGetInstallationCentersFailure(t.message.toString())
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
            listener.onGetInstallationCentersFailure(e.message.toString())
        }
    }

    fun getDeviationProfiles(listener: ProvisioningDevicesListener, customer_id: Int) {
        try {
            val apiService = ApiClient.getDcmClient().create(ApiInterface::class.java)
            val call = apiService.getDeviationProfiles("Bearer ${userManager.token}")

            call.enqueue(object : Callback<ArrayList<DeviationProfileRes>> {
                override fun onResponse(call: Call<ArrayList<DeviationProfileRes>>, response: Response<ArrayList<DeviationProfileRes>>) {

                    if (response.isSuccessful) {
                        if (response.code() == 204) {
                            listener.onGetDeviationProfileEmpty("No record found")
                        } else {
                            listener.onGetDeviationProfileSuccess(response.body()!!)

                        }


                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())

                        when {
                            response.code() == 401 -> listener.onGetDeviationProfileFailure(jObjError.getString("error-message"))
                            else -> listener.onGetDeviationProfileFailure(jObjError.getString("error-message"))
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<DeviationProfileRes>>, t: Throwable) {
                    listener.onGetDeviationProfileFailure(t.message.toString())
                }
            })
        } catch (e: Exception) {
            Timber.e(e)
            listener.onGetDeviationProfileFailure(e.message.toString())
        }
    }

}

interface AddDeviceListener {

    fun onAddDevicesSuccess(body: ResponseBody)
    fun onAddDevicesFailure(msg: String)

    fun onSerialNumberEmpty()
    fun onVendorEmpty()
    fun onStartLifeEmpty()
    fun onEndLifeEmpty()

    fun onGetSensorProfileSuccess(body: ArrayList<SensorProfileRes>)
    fun onGetSensorProfileError(msg: String)
    fun onGetSensorProfileEmpty(msg: String)

    fun onGetDeviceTypeSuccess(body: ArrayList<DeviceTypeRes>)
    fun onGetDeviceTypeError(msg: String)
    fun onGetDeviceTypeEmpty(msg: String)

    fun onGetDeviceTypeGroupSuccess(body: ArrayList<DeviceGroupRes>)
    fun onGetDeviceTypeGroupError(msg: String)
    fun onGetDeviceTypeGroupEmpty(msg: String)

    fun onGetDeviceSubTypeSuccess(body: ArrayList<DeviceSubTypeRes>)
    fun onGetDeviceSubTypeError(msg: String)
    fun onGetDeviceSubTypeEmpty(msg: String)
}

interface ListDeviceInteractorCallback {
    fun allDevicesApiSuccess(body: ArrayList<DevicesData>)
    fun allDevicesApiError(msg: String)
    fun allDevicesApiEmpty(msg: String)

    fun deleteDeviceSuccess(deletedPostion: Int)
    fun deleteDeviceFailure(msg: String)
}

interface UpdateDevicesListener {

    fun onUpdateDevicesSuccess(body: ResponseBody)
    fun onUpdateDevicesFailure(msg: String)

    fun onSerialNumberEmpty()
    fun onVendorEmpty()
    fun onStartLifeEmpty()
    fun onEndLifeEmpty()

    fun onGetSensorProfileSuccess(body: ArrayList<SensorProfileRes>)
    fun onGetSensorProfileError(msg: String)
    fun onGetSensorProfileEmpty(msg: String)

    fun onGetDeviceTypeSuccess(body: ArrayList<DeviceTypeRes>)
    fun onGetDeviceTypeError(msg: String)
    fun onGetDeviceTypeEmpty(msg: String)

    fun onGetDeviceTypeGroupSuccess(body: ArrayList<DeviceGroupRes>)
    fun onGetDeviceTypeGroupError(msg: String)
    fun onGetDeviceTypeGroupEmpty(msg: String)

    fun onGetDeviceSubTypeSuccess(body: ArrayList<DeviceSubTypeRes>)
    fun onGetDeviceSubTypeError(msg: String)
    fun onGetDeviceSubTypeEmpty(msg: String)
}

interface ProvisioningDevicesListener {

    fun onProvisioningDevicesSuccess(body: ResponseBody)
    fun onProvisioningDevicesFailure(msg: String)

    fun onGetCustomerSuccess(body: ArrayList<CustomerRes>)
    fun onGetCustomerFailure(msg: String)
    fun onGetCustomerEmpty(msg: String)

    fun onGetUserSuccess(body: ArrayList<UserDataRes>)
    fun onGetUserFailure(msg: String)
    fun onGetUserEmpty(msg: String)

    fun onGetInstallationCentersSuccess(body: ArrayList<InstallationCenterRes>)
    fun onGetInstallationCentersFailure(msg: String)
    fun onGetInstallationCentersEmpty(msg: String)

    fun onGetDeviationProfileSuccess(body: ArrayList<DeviationProfileRes>)
    fun onGetDeviationProfileFailure(msg: String)
    fun onGetDeviationProfileEmpty(msg: String)

    fun onStartServiceEmpty()
    fun onEndServiceEmpty()

}

