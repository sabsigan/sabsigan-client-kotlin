package com.android.sabsigan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ArtistCard()
        }

    }

    @Preview
    @Composable
    fun ArtistCard() {
        Text(text = "ddddd",
            color = Color(250, 0, 0)
        )
    }
}