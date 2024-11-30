package com.example.gymworkoutappointer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkoutappointer.databinding.ActivityRegistrarBinding

class Registrar : AppCompatActivity() {

    val enlaceRegistro:ActivityRegistrarBinding by lazy {
        ActivityRegistrarBinding.inflate(layoutInflater)
    }

    ////////////////////////////////////// Listas /////////////////////////////////////////////////////
    // Partes del cuerpo con sus ejercicios
    val ejerciciosPorPartes = mapOf(
        "Pecho" to listOf("Press banca", "Aperturas", "Press superior (Barra)", "Press superior (Mancuernas)"),
        "Espalda" to listOf("Jalón con mancuernas", "Jalón al pecho", "Remo", "Pullover"),
        "Piernas" to listOf("Sentadillas", "Peso muerto", "Zancadas"),
        "Hombros" to listOf("Press militar", "Elevaciones laterales", "Pájaros"),
        "Brazos" to listOf("Curl de bíceps", "Extensión de tríceps", "Martillo"),
        "Abdominales" to listOf("Crunch", "Plancha", "Elevaciones de piernas")
    )

    // Aqui meto los ejercicios que añado
    val ejerciciosPorDia = mutableMapOf(
        "Lunes" to mutableListOf<String>(),
        "Martes" to mutableListOf(),
        "Miércoles" to mutableListOf(),
        "Jueves" to mutableListOf(),
        "Viernes" to mutableListOf(),
        "Sábado" to mutableListOf(),
        "Domingo" to mutableListOf()
    )

    // Lista de los días
    val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

//    lateinit var adapterRcv:DiasAdapter

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(enlaceRegistro.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicio los spinners
        setupSpinners()

        // Agregar ejercicio seleccionado al día
        enlaceRegistro.btnRegistra.setOnClickListener {
            val parteSeleccionada = enlaceRegistro.spnCuerpo.selectedItem?.toString()
            val ejercicioSeleccionado = enlaceRegistro.spnEjercicios.selectedItem?.toString()
            val diaSeleccionado = enlaceRegistro.spnDias.selectedItem?.toString()

            if (parteSeleccionada != null && ejercicioSeleccionado != null && diaSeleccionado != null) {
                ejerciciosPorDia[diaSeleccionado]?.add("$parteSeleccionada: $ejercicioSeleccionado")
                Toast.makeText(
                    this,
                    "Ejercicio agregado al $diaSeleccionado",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Selecciona una parte, ejercicio y día", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setupSpinners() {
        // Meto los dias en Spinner "días"
        val adapterDias = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            diasSemana
        )
        adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        enlaceRegistro.spnDias.adapter = adapterDias

        // meto las partes del cuerpo en el spinner "objetivo"
        val adapterPartes = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            ejerciciosPorPartes.keys.toList()
        )
        adapterPartes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        enlaceRegistro.spnCuerpo.adapter = adapterPartes

        // El spinner "spnEjercicios" es variable según la parte del cuerpo indicada
        enlaceRegistro.spnCuerpo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Obtener la parte del cuerpo seleccionada
                val parteSeleccionada = ejerciciosPorPartes.keys.toList()[position]

                // Llenar el Spinner de Ejercicios con los ejercicios correspondientes
                val ejercicios = ejerciciosPorPartes[parteSeleccionada] ?: listOf()
                val adapterEjercicios = ArrayAdapter(
                    this@Registrar,
                    android.R.layout.simple_spinner_item,
                    ejercicios
                )
                adapterEjercicios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                enlaceRegistro.spnEjercicios.adapter = adapterEjercicios
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // si no hay elegido nada no muestro nada
            }
        }
    }
}

//// Modelo básico "Día : Lista de ejercicios"
//data class DiaConEjercicios(val nombre: String, val ejercicios: MutableList<String>)

// Aquí trabajo el modelo
//class DiasAdapter(private val dias: List<DiaConEjercicios>) :
//    RecyclerView.Adapter<DiasAdapter.DiaViewHolder>() {
//
//    class DiaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        private val binding = ItemDiaBinding.bind(view)
//
//        fun bind(dia: DiaConEjercicios) {
//            binding.txtDia.text = dia.nombre
//            binding.txtEjercicios.text = dia.ejercicios.joinToString("\n")
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dia, parent, false)
//        return DiaViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: DiaViewHolder, position: Int) {
//        holder.bind(dias[position])
//    }
//
//    override fun getItemCount() = dias.size
//}
