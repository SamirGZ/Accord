package com.example.accord

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider

class HomePage : BaseNavActivity() {
    override val selectedNavItemId = R.id.nav_home
    override val pageTitle = "Home"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        setPageComposeContent {
            HomeScreen(viewModel)
        }
    }
}
