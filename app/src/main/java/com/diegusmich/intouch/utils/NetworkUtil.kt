package com.diegusmich.intouch.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.widget.Toast
import com.diegusmich.intouch.IntouchApplication
import com.diegusmich.intouch.R

class NetworkUtil {

    companion object {
        private var connectivityManager: ConnectivityManager? = null
        private lateinit var networkRequest: NetworkRequest
        private lateinit var networkCallback: NetworkCallback

        fun buildService(context: Context): Companion {

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
                    showNetworkStatus(context, R.string.internet_online)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    showNetworkStatus(context, R.string.internet_connection_lost)
                }
            }

            return Companion
        }

        fun observe() {
            connectivityManager!!.requestNetwork(networkRequest, networkCallback)
        }

        private fun showNetworkStatus(ctx: Context, strRes: Int) {
            if (IntouchApplication.isAppForeground)
                Toast.makeText(ctx, ctx.getString(strRes), Toast.LENGTH_SHORT).show()
        }
    }
}