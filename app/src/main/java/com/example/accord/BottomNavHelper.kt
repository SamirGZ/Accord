package com.example.accord

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity

object BottomNavHelper {

    private data class NavItem(
        val viewId: Int,
        val destination: Class<out AppCompatActivity>
    )

    private val navItems = listOf(
        NavItem(R.id.nav_home, HomePage::class.java),
        NavItem(R.id.nav_library, LibraryPage::class.java),
        NavItem(R.id.nav_discover, DiscoverPage::class.java),
        NavItem(R.id.nav_profile, ProfilePage::class.java),
    )

    fun setup(activity: AppCompatActivity, selectedItemId: Int) {
        navItems.forEach { item ->
            activity.findViewById<View>(item.viewId).apply {
                isSelected = item.viewId == selectedItemId
                setOnClickListener {
                    navigateTo(activity, item.destination)
                }
            }
        }
    }

    fun navigateTo(activity: AppCompatActivity, destination: Class<out AppCompatActivity>) {
        if (activity::class.java == destination) return
        activity.startActivity(Intent(activity, destination))
        activity.overridePendingTransition(0, 0)
        activity.finish()
        activity.overridePendingTransition(0, 0)
    }
}
