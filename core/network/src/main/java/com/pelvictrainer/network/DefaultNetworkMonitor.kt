package com.pelvictrainer.network


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

import javax.inject.Inject


class DefaultNetworkMonitor @Inject constructor(
    private val context: Context
) : NetworkMonitor {


    override fun isConnected(): Boolean {


        val connectivityManager =
            context.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager


        val network =
            connectivityManager.activeNetwork
                ?: return false


        val capabilities =
            connectivityManager.getNetworkCapabilities(network)
                ?: return false


        return capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )

    }

}