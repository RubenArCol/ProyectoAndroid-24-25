package com.example.gymworkoutappointer

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding
import com.example.gymworkoutappointer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    var doblePulsacion = false



    val enlace:ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(enlace.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        super.onStart()

        // navegar a Registrar
        enlace.btnCrear.setOnClickListener{
            val intento = Intent(this, Registrar::class.java)
            startActivity(intento)
        }

        // navegar a Historial
        enlace.btnVer.setOnClickListener{
            val intento = Intent(this, Ver::class.java)
            startActivity(intento)
        }

        // Doble pulsación
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (doblePulsacion){
                    finish()
                } else {
                    doblePulsacion = true
                    Toast.makeText(this@MainActivity, "Presiona de nuevo para salir", Toast.LENGTH_SHORT).show()

                    window.decorView.postDelayed({
                        doblePulsacion = false
                    }, 1000) // si en un segundo no ha pulsado el boton reinicio el estado
                }
            }
        })

    }
}