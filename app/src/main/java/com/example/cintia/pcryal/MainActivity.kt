package com.example.cintia.pcryal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    lateinit var mRecyclerView : RecyclerView
    val mAdapter : RecyclerAdapter = RecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
        getSupportActionBar()?.setLogo(R.drawable.iconapp)
        getSupportActionBar()?.setDisplayUseLogoEnabled(true)
        setUpRecyclerView();
    }

    fun getFunciones(): MutableList<ListaFuncionalidades>{
        var funciones:MutableList<ListaFuncionalidades> = mutableListOf()
        funciones.add(ListaFuncionalidades("Jornada", "Jornada.kt", "Registración de Jornada","Acceso Permitido", "login"))
        funciones.add(ListaFuncionalidades("Almacen", "MenuAlmacen.kt", "Registrar movimientos en almacen","Acceso Denegado", "almacen"))
        funciones.add(ListaFuncionalidades("Orden de Trabajo", "MenuOt.kt", "Generar nueva OT, Editar dato de un motor","Acceso Denegado", "ordentrabajo"))
        funciones.add(ListaFuncionalidades("Recesos Laborales", "MenuRecesos.kt", "Registración de Recesos Laborales","Acceso Denegado", "recesoslaborales"))
        funciones.add(ListaFuncionalidades("Descargar Novedades", "DescargaNovedades.kt", "Descarga Novedades","Acceso Permitido", "novedadesdescargar"))
        funciones.add(ListaFuncionalidades("Enviar Novedades", "EnviarNovedades.kt", "Enviar Novedades","Acceso Permitido", "novedadesenviar"))
        return funciones
    }

    fun setUpRecyclerView(){
        mRecyclerView = findViewById(R.id.rvFuncionalidadesList) as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter.RecyclerAdapter(getFunciones(), this)
        mRecyclerView.adapter = mAdapter
    }
}
