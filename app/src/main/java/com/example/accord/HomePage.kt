package com.example.accord

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class HomePage : BaseNavActivity() {
    override val selectedNavItemId = R.id.nav_home
    override val pageTitle = "Home"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Temporary test call — proves the Retrofit connection works.
        // We'll remove this once we wire up a real ViewModel + UI.
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getPerfumes(limit = 5)
                Log.d("PerfumeTest", "Got ${response.perfumes.size} perfumes")
                response.perfumes.forEach {
                    Log.d("PerfumeTest", "${it.name} — ${it.brand}")
                }
            } catch (e: Exception) {
                Log.e("PerfumeTest", "Failed to fetch perfumes", e)
            }
        }
    }
}