package com.example.accord

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.accord.ui.theme.AccordTheme

abstract class BaseNavActivity : AppCompatActivity() {

    abstract val selectedNavItemId: Int
    abstract val pageTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_page_template)

        findViewById<TextView>(R.id.page_title).text = pageTitle

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            findViewById<View>(R.id.bottom_nav)?.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        BottomNavHelper.setup(this, selectedNavItemId)
    }

    protected fun setPageComposeContent(content: @Composable () -> Unit) {
        findViewById<TextView>(R.id.page_title).visibility = View.GONE
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
                content()
            }
        }
    }
}
