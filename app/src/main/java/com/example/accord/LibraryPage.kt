package com.example.accord

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.ViewModelProvider
import com.example.accord.ui.theme.AccordTheme

class LibraryPage : BaseNavActivity() {
    override val selectedNavItemId = R.id.nav_library
    override val pageTitle = "Library"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<TextView>(R.id.page_title).visibility = View.GONE
        val viewModel = ViewModelProvider(this)[LibraryViewModel::class.java]

        val composeView = ComposeView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }
        findViewById<FrameLayout>(R.id.page_content).addView(composeView)
        composeView.setContent {
            AccordTheme {
                LibraryScreen(viewModel)
            }
        }
    }
}
