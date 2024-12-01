package com.example.gymworkoutappointer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymworkoutappointer.databinding.ActivityRegistrarBinding
import com.example.gymworkoutappointer.databinding.ActivityVerBinding

class Ver : AppCompatActivity() {

    private val enlaceSalud: ActivityVerBinding by lazy {
        ActivityVerBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(enlaceSalud.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        enlaceSalud.btnCalculaImc.setOnClickListener {
            calcularIMC()
        }
    }

    private fun calcularIMC() {
        val alturaStr = enlaceSalud.txtAltura.text.toString()
        val pesoStr = enlaceSalud.txtPeso.text.toString()

        // Verificar si se seleccionó un RadioButton en el RadioGroup
        if (!enlaceSalud.rdCm.isChecked && !enlaceSalud.rdM.isChecked) {
            Toast.makeText(this, "Selecciona una unidad de altura (metros o centímetros)", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar si los campos de altura y peso están vacíos
        if (alturaStr.isEmpty() || pesoStr.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa tu peso y altura", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener y validar los valores ingresados
        val altura: Float = try {
            alturaStr.toFloat()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Altura inválida", Toast.LENGTH_SHORT).show()
            return
        }

        val peso: Float = try {
            pesoStr.toFloat()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Peso inválido", Toast.LENGTH_SHORT).show()
            return
        }

        if (altura <= 0 || peso <= 0) {
            Toast.makeText(this, "Altura y peso deben ser mayores a cero", Toast.LENGTH_SHORT).show()
            return
        }


        val alturaEnMetros = if (enlaceSalud.rdCm.isChecked) {
            altura / 100 // cm a m
        } else {
            altura // Está en metros
        }

        // Calcular el IMC
        val imc = peso / (alturaEnMetros * alturaEnMetros)
        mostrarResultadoIMC(imc)
    }

    private fun mostrarResultadoIMC(imc: Float) {
        val categoria = when {
            imc < 18.5 -> "Bajo peso"
            imc < 24.9 -> "Peso normal"
            imc < 29.9 -> "Sobrepeso"
            else -> "Obesidad"
        }

        enlaceSalud.txtIMC.text = "Tu IMC es: %.2f (%s)".format(imc, categoria)
        enlaceSalud.txtIMC.visibility = View.VISIBLE

        if (categoria == "Sobrepeso" || categoria == "Obesidad") {
            mostrarDialogoObesidad(imc)
        }
    }

    // si tiene sobrepeso le puedo sugerir ir a una página web de ayuda
    private fun mostrarDialogoObesidad(imc: Float) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cuidado con tu peso")
        builder.setMessage("Tienes sobrepeso u obesidad (IMC: %.2f). ¿Quieres más información para mejorar tu salud?".format(imc))

        builder.setPositiveButton("Sí, saber más") { _, _ ->
            val url = "https://www.centerwellprimarycare.com/es/resources/body-mass-index-health-tips.html"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        builder.setNegativeButton("No, gracias") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }
}