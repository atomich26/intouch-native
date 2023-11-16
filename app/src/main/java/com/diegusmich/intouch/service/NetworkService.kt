package com.diegusmich.intouch.service

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

object NetworkService {

    private var connectivityManager: ConnectivityManager? = null
    private lateinit var networkRequest: NetworkRequest
    private lateinit var networkCallback: NetworkCallback
    private val onNetworkAvailableObservers = mutableListOf<NetworkStateObserver>()

    fun buildService(context: Context): NetworkService {

        //Get a connectivity manager API to observe network state
        connectivityManager =
            context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager

        //Create a network request with custom capabilities
        networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        networkCallback = object : NetworkCallback() {

            override fun onAvailable(network: Network) {
                super.onAvailable(network)

                for (observer in onNetworkAvailableObservers){
                    observer.run()
                }

                showNetworkStatus(context, R.string.internet_online)
            }
        }
        return this
    }

    fun observe() {
        connectivityManager!!.requestNetwork(networkRequest, networkCallback)
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