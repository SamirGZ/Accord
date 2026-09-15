package com.example.accord

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.accord.ui.theme.AccordTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AccordTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AccordTheme {
        Greeting("Android")
    }
}