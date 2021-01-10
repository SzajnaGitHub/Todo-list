package com.example.todolist.utils

import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.Network
import android.net.NetworkCapabilities.*
import android.net.NetworkRequest
import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Email.TYPE_MOBILE
import com.example.todolist.di.annotations.NetworkAvailable
import io.reactivex.subjects.Subject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkManager @Inject constructor(
    private val connectivityManager: ConnectivityManager,
    @NetworkAvailable private val internetAvailableSubject: Subject<Boolean>
) : ConnectivityManager.NetworkCallback() {

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        internetAvailableSubject.onNext(true)
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        internetAvailableSubject.onNext(false)
    }

    fun hasInternetConnection(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    fun checkInternetConnection() {
        internetAvailableSubject.onNext(hasInternetConnection())
    }

    fun register() {
        connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), this)
    }

    fun unregister() {
        connectivityManager.unregisterNetworkCallback(this)
    }
}
