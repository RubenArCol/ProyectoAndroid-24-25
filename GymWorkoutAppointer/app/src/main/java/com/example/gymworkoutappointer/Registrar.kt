package com.example.gymworkoutappointer

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymworkoutappointer.databinding.ActivityRegistrarBinding

data class DiaConEjercicios(
    val nombre: String,
    val ejercicios: MutableList<String> = mutableListOf()
)

class Registrar : AppCompatActivity() {

    private val enlaceRegistro: ActivityRegistrarBinding by lazy {
        ActivityRegistrarBinding.inflate(layoutInflater)
    }

    private val ejerciciosPorPartes = mapOf(
        "Pecho" to listOf("Press banca", "Aperturas", "Press superior (Barra)", "Press superior (Mancuernas)"),
        "Espalda" to listOf("Jalón con mancuernas", "Jalón al pecho", "Remo", "Pullover"),
        "Piernas" to listOf("Sentadillas", "Peso muerto", "Zancadas"),
        "Hombros" to listOf("Press militar", "Elevaciones laterales", "Pájaros"),
        "Brazos" to listOf("Curl de bíceps", "Extensión de tríceps", "Martillo"),
        "Abdominales" to listOf("Crunch", "Plancha", "Elevaciones de piernas")
    )

    // lista de dias a los que se les añade ejercicios
    private val diasSemana: List<String> by lazy {
        listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    }

    // lista de dia con ejercicios
    private val ejerciciosPorDia: MutableList<DiaConEjercicios> by lazy {
        diasSemana.map { DiaConEjercicios(it) }.toMutableList()
    }

    private lateinit var listViewAdapter: BaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(enlaceRegistro.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // arranco los spinners y la listview
        setupSpinners()
        setupListView()

        // Agregar ejercicio seleccionado al día
        enlaceRegistro.btnRegistra.setOnClickListener {
            val parteSeleccionada = enlaceRegistro.spnCuerpo.selectedItem?.toString()
            val ejercicioSeleccionado = enlaceRegistro.spnEjercicios.selectedItem?.toString()
            val diaSeleccionado = enlaceRegistro.spnDias.selectedItem?.toString()

            if (parteSeleccionada != null && ejercicioSeleccionado != null && diaSeleccionado != null) {
                // Encontrar el día correspondiente
                val dia = ejerciciosPorDia.find { it.nombre == diaSeleccionado }
                dia?.ejercicios?.add("$parteSeleccionada: $ejercicioSeleccionado")
                listViewAdapter.notifyDataSetChanged() // Notificar al adaptador que los datos han cambiado
                Toast.makeText(this, "Ejercicio agregado al $diaSeleccionado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Selecciona una parte, ejercicio y día", Toast.LENGTH_SHORT).show()
            }
        }

        enlaceRegistro.btnLimpia.setOnClickListener {
            limpiaLista()
        }
    }

    private fun setupSpinners() {
        // Meto los días en Spinner "días"
        val adapterDias = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            diasSemana
        )
        adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        enlaceRegistro.spnDias.adapter = adapterDias

        // Meto las partes del cuerpo en el spinner "objetivo"
        val adapterPartes = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            ejerciciosPorPartes.keys.toList()
        )
        adapterPartes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        enlaceRegistro.spnCuerpo.adapter = adapterPartes

        // El spinner "spnEjercicios" es variable según la parte del cuerpo indicada
        enlaceRegistro.spnCuerpo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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

    private fun setupListView() {
        val listView: ListView = enlaceRegistro.listDias
        listViewAdapter = object : BaseAdapter() {
            override fun getCount(): Int {
                return ejerciciosPorDia.size
            }

            override fun getItem(position: Int): DiaConEjercicios {
                return ejerciciosPorDia[position]
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = LayoutInflater.from(parent?.context).inflate(android.R.layout.simple_list_item_1, parent, false)
                val dia = getItem(position)
                view.findViewById<TextView>(android.R.id.text1).text = "${dia.nombre}: ${dia.ejercicios.joinToString(", ")}"
                return view
            }
        }
        listView.adapter = listViewAdapter
    }

    // funcion de limpiar la listView
    private fun limpiaLista() {
        // dialogo modal
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar")
        builder.setMessage("¿Estás seguro de que deseas limpiar la lista de ejercicios?")

        builder.setPositiveButton("Sí") { dialog: DialogInterface, which: Int ->
            // Limpiar la lista de ejercicios
            ejerciciosPorDia.forEach { it.ejercicios.clear() }
            Toast.makeText(this, "Lista de ejercicios limpiada", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("No") { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
        }

        builder.show()
    }
}