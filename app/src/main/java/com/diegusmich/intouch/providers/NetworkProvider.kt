package com.diegusmich.intouch.providers

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.widget.Toast
import com.diegusmich.intouch.IntouchApplication
import com.diegusmich.intouch.R
import com.diegusmich.intouch.network.NetworkStateObserver

object NetworkProvider {

    private var _isNetworkAvailable = false
    val isNetworkAvailable get() = _isNetworkAvailable

    private val onNetworkAvailableObservers = mutableListOf<NetworkStateObserver>()

    fun build(context: Context){

        //Get a connectivity manager API to observe network state
        val connectivityManager =
            context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager

        //Create a network request with custom capabilities
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        val networkCallback = object : NetworkCallback() {

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                _isNetworkAvailable = true

                for (observer in onNetworkAvailableObservers){
                    observer.run()
                }

                showNetworkStatus(context, R.string.internet_online)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                _isNetworkAvailable = false
            }
        }

        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    fun addOnNetworkAvailableObserver(observer : NetworkStateObserver){
        onNetworkAvailableObservers.add(observer)
    }

    fun removeOnNetworkAvailableObserver(observer : NetworkStateObserver){
        onNetworkAvailableObservers.remove(observer)
    }

    private fun showNetworkStatus(ctx: Context, strRes: Int) {
        if (IntouchApplication.isAppForeground)
            Toast.makeText(ctx, ctx.getString(strRes), Toast.LENGTH_SHORT).show()
    }
}