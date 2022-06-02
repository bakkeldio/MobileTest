package com.edu.common.navigation

import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import com.edu.common.R

object Navigation {

    private const val baseUri = "mobileTest://"
    private const val navigation_tests = "navigationTests/"
    private const val navigation_test = "navigationCompletedTest/"
    private const val navigation_group = "navigationGroup/"

    object Tests {
        fun navigateToTests(groupId: String): Pair<NavDeepLinkRequest, NavOptions> {
            val deepLink = buildNavDeepLinkRequest("$baseUri$navigation_tests$groupId")
            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.nav_graph_enter)
                .setExitAnim(R.anim.nav_graph_exit)
                .build()

            return Pair(deepLink, navOptions)

        }

        fun navigateToCompletedTest(
            groupId: String,
            testId: String
        ): Pair<NavDeepLinkRequest, NavOptions> {
            val deepLink = buildNavDeepLinkRequest("$baseUri$navigation_test$groupId;$testId")
            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.nav_graph_enter)
                .setExitAnim(R.anim.nav_graph_exit)
                .build()
            return Pair(deepLink, navOptions)
        }

    }

    object Groups {
        fun navigateToGroup(groupId: String): Pair<NavDeepLinkRequest, NavOptions> {
            val deepLinkRequest = buildNavDeepLinkRequest("$baseUri$navigation_group$groupId")
            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.nav_graph_enter)
                .setExitAnim(R.anim.nav_graph_exit)
                .build()
            return Pair(deepLinkRequest, navOptions)
        }
    }

    fun buildNavDeepLinkRequest(link: String): NavDeepLinkRequest {
        return NavDeepLinkRequest.Builder.fromUri(link.toUri()).build()
    }


}