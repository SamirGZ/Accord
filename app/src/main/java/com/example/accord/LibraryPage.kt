package com.example.accord

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider

class LibraryPage : BaseNavActivity() {
    override val selectedNavItemId = R.id.nav_library
    override val pageTitle = "Library"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this)[LibraryViewModel::class.java]
        setPageComposeContent {
            LibraryScreen(viewModel)
        }
    }
}
