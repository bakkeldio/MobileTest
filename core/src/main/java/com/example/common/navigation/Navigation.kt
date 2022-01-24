package com.example.common.navigation

import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import com.example.common.R

object Navigation {

    private const val baseUri = "mobileTest://"
    private const val navigation_tests = "navigationTests/"

    object Tests {
        fun navigateToTests(groupId: String): Pair<NavDeepLinkRequest, NavOptions> {
            val deepLink = buildNavDeepLinkRequest("$baseUri$navigation_tests$groupId")
            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.nav_graph_enter)
                .setExitAnim(R.anim.nav_graph_exit)
                .build()

            return Pair(deepLink, navOptions)

        }
    }

    fun buildNavDeepLinkRequest(link: String): NavDeepLinkRequest{
        return NavDeepLinkRequest.Builder.fromUri(link.toUri()).build()
    }


}