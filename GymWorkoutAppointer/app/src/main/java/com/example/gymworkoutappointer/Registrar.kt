package com.example.gymworkoutappointer

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymworkoutappointer.databinding.ActivityRegistrarBinding

class Registrar : AppCompatActivity() {

    val enlaceRegistro:ActivityRegistrarBinding by lazy {
        ActivityRegistrarBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(enlaceRegistro.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val ejerciciosPorPartes = mapOf(
            "Pecho" to listOf("Press banca", "Aperturas", "Press superior (Barra)", "Press superior (Mancuernas)"),
            "Espalda" to listOf("Jalón con mancuernas", "Jalón al pecho", "Remo", "Pullover"),
            "Piernas" to listOf("Sentadillas", "Peso muerto", "Zancadas"),
            "Hombros" to listOf("Press militar", "Elevaciones laterales", "Pájaros"),
            "Brazos" to listOf("Curl de bíceps", "Extensión de tríceps", "Martillo"),
            "Abdominales" to listOf("Crunch", "Plancha", "Elevaciones de piernas")
        )

        // meto los keys en el spinner
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            ejerciciosPorPartes.keys.toList()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        enlaceRegistro.spnCuerpo.adapter = adapter

    }
}