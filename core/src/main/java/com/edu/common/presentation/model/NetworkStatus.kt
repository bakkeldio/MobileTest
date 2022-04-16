package com.edu.common.presentation.model

sealed class NetworkStatus{
    object Available: NetworkStatus()
    object UnAvailable: NetworkStatus()
}
