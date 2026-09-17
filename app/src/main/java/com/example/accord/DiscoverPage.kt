package com.example.accord

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider

class DiscoverPage : BaseNavActivity() {
    override val selectedNavItemId = R.id.nav_discover
    override val pageTitle = "Discover"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this)[DiscoverViewModel::class.java]
        setPageComposeContent {
            DiscoverScreen(viewModel)
        }
    }
}
