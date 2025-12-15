package com.example.habittracker.ui.settings

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.habittracker.R
import com.google.android.material.card.MaterialCardView

class SettingsActivity : AppCompatActivity() {

    private var selectedTheme = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        val saveButton = findViewById<TextView>(R.id.saveButton)
        val cardLight = findViewById<MaterialCardView>(R.id.cardLight)
        val cardDark = findViewById<MaterialCardView>(R.id.cardDark)
        val cardSystem = findViewById<MaterialCardView>(R.id.cardSystem)

        backButton.setOnClickListener {
            finish()
        }

        saveButton.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(selectedTheme)
        }

        cardLight.setOnClickListener {
            selectedTheme = AppCompatDelegate.MODE_NIGHT_NO
            updateCardSelection(cardLight, listOf(cardDark, cardSystem))
        }

        cardDark.setOnClickListener {
            selectedTheme = AppCompatDelegate.MODE_NIGHT_YES
            updateCardSelection(cardDark, listOf(cardLight, cardSystem))
        }

        cardSystem.setOnClickListener {
            selectedTheme = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            updateCardSelection(cardSystem, listOf(cardLight, cardDark))
        }

        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_NO -> cardLight.isChecked = true
            AppCompatDelegate.MODE_NIGHT_YES -> cardDark.isChecked = true
            else -> cardSystem.isChecked = true
        }
    }

    private fun updateCardSelection(selectedCard: MaterialCardView, otherCards: List<MaterialCardView>) {
        selectedCard.isChecked = true
        otherCards.forEach { it.isChecked = false }
    }
}