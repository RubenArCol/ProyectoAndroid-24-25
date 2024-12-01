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
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymworkoutappointer.databinding.ActivityRegistrarBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class DiaConEjercicios(
    val nombre: String,
    val ejercicios: MutableList<String> = mutableListOf()
)

private lateinit var progreso: ProgressBar

class Registrar : AppCompatActivity() {

    private val enlaceRegistro: ActivityRegistrarBinding by lazy {
        ActivityRegistrarBinding.inflate(layoutInflater)
    }

    private val preferencias by lazy {
        getSharedPreferences("preferencias", MODE_PRIVATE)
    }

    private val ejerciciosPorPartes = mapOf(
        "Pecho" to listOf(
            "Press banca",
            "Aperturas",
            "Press superior (Barra)",
            "Press superior (Mancuernas)"
        ),
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

        // arranco los spinners, la listview, la barra de progresos y los datos que estuvieran guardados
        progreso = enlaceRegistro.pbDias
        setupSpinners()
        setupListView()
        cargarDatosJson()

        //al principio la lista es invisible
        enlaceRegistro.listDias.visibility = View.GONE

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

                //actualizao visiblilidad, barra de progreso y almaceno datos para persistencia
                actualizarVisibilidadListView()
                actualizarProgressBar()
                guardarDatosJson()

                Toast.makeText(this, "Ejercicio agregado al $diaSeleccionado", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Selecciona una parte, ejercicio y día", Toast.LENGTH_SHORT)
                    .show()
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
        enlaceRegistro.spnCuerpo.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
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
                val view = LayoutInflater.from(parent?.context)
                    .inflate(android.R.layout.simple_list_item_1, parent, false)
                val dia = getItem(position)
                view.findViewById<TextView>(android.R.id.text1).text =
                    "${dia.nombre}: ${dia.ejercicios.joinToString(", ")}"
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
            listViewAdapter.notifyDataSetChanged() // Notificar al adaptador que los datos han cambiado

            // Actualizar la visibilidad del ListView
            actualizarVisibilidadListView()
            actualizarProgressBar()

            Toast.makeText(this, "Lista de ejercicios limpiada", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("No") { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
        }

        builder.show()
    }

    // lista invisible si no tiene nada
    private fun actualizarVisibilidadListView() {
        val listView: ListView = enlaceRegistro.listDias
        // si tiene algo se vé y si no la oculto
        listView.visibility =
            if (ejerciciosPorDia.any { it.ejercicios.isNotEmpty() }) View.VISIBLE else View.GONE
    }

    // actualiza la barra de progresos
    private fun actualizarProgressBar() {
        val diasConEjercicios = ejerciciosPorDia.count { it.ejercicios.isNotEmpty() }
        progreso.progress = diasConEjercicios
    }

    // almaceno los datos de la lista para añadir persistencia
    private fun guardarDatosJson() {
        val editor = preferencias.edit()
        // lista a json
        val jsonDatos = Gson().toJson(ejerciciosPorDia)
        editor.putString("ejercicios_por_dia", jsonDatos) // Clave corregida
        editor.apply()
    }

    // cargo los datos almacenados en la lista
    private fun cargarDatosJson() {
        val jsonDatos = preferencias.getString("ejercicios_por_dia", null) // Clave corregida
        if (jsonDatos != null) {
            val listaType = object : TypeToken<MutableList<DiaConEjercicios>>() {}.type
            ejerciciosPorDia.clear()
            ejerciciosPorDia.addAll(Gson().fromJson(jsonDatos, listaType))
        }
    }
}