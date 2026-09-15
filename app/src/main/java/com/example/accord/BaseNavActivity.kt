package com.example.accord

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
}
