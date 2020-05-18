package com.example.cintia.pcryal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import java.io.File
import android.support.v7.app.AlertDialog

class MainActivity : AppCompatActivity() {

    lateinit var wRecyclerView : RecyclerView
    val mAdapter : RecyclerAdapter = RecyclerAdapter()
    var utility = Utility()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
        getSupportActionBar()?.setLogo(R.drawable.iconapp)
        getSupportActionBar()?.setDisplayUseLogoEnabled(true)
        setRecyclerViewFuncionalidades();
    }

    fun getFunciones(): MutableList<ListaFuncionalidades>{
        var strAcceso = "Acceso Denegado"
        var strFinJornada = "Acceso Concedido"
        if(registrado()) strFinJornada = "Acceso Denegado"
        if(registrado())  strAcceso = "Acceso Concedido"
        var funciones:MutableList<ListaFuncionalidades> = mutableListOf()
        funciones.add(ListaFuncionalidades("Jornada", "Jornada", "Registración de Jornada",archivosDescargados(), "login"))
        funciones.add(ListaFuncionalidades("Almacen", "TareasAlmacen", "Registrar movimientos en almacen"+ "\n" + "Editar/Elminar",strAcceso, "almacen"))
        funciones.add(ListaFuncionalidades("Orden de Trabajo", "MenuOt", "Generar nueva OT",strAcceso, "ordentrabajo"))
        funciones.add(ListaFuncionalidades("Recesos Laborales", "RecesosLaborales", "Registración de Recesos Laborales" + "\n" + "Editar/Elminar",strAcceso, "recesoslaborales"))
        funciones.add(ListaFuncionalidades("Descargar Novedades", "DescargarNovedades", "Descarga Novedades","Acceso Concedido", "novedadesdescargar"))
        funciones.add(ListaFuncionalidades("Enviar Novedades", "EnviarNovedades", "Enviar Novedades", strFinJornada, "novedadesenviar"))
        funciones.add(ListaFuncionalidades("Finalizar Jornada", "FinalizarJornada", "Finalizar la jornada laboral",strAcceso, "finalizarjornada"))
        return funciones
    }

    fun registrado():Boolean{
        var acceso = false
        val SDCardRoot = Environment.getExternalStorageDirectory()
        var date = utility.getFecha()
        val fJornada = File(SDCardRoot, date+".json")

        if(fJornada.exists()) acceso = true
        return acceso
    }

    fun archivosDescargados(): String{
        var existe = "Acceso Concedido"
        var wTableAplic:String = "aplicaciones"
        var wTableBombas:String = "bombasaib"
        var wTableCompresiones:String = "compresiones"
        var wTableEquipos:String = "equipos"
        var wTableMaquinas:String = "maquinas"
        var wTableMateriales:String = "materiales"
        var wTableMoviles:String = "moviles"
        var wTablePozos:String = "pozos"
        var wTableServMantenimiento: String = "serviciosmantenimiento"
        var wTableZonas: String = "zonas"
        var wTableTipoOt: String = "tipoot"
        var wTableRecesosLaborales: String = "recesoslaborales"
        var wTableTareasAlmacen: String = "tareasalmacen"
        val SDCardRoot = Environment.getExternalStorageDirectory()
        val UrlArray = arrayOf(wTableAplic,wTableBombas,wTableCompresiones,wTableEquipos,wTableMaquinas,wTableMateriales,wTableMoviles,wTablePozos,wTableServMantenimiento,wTableZonas,wTableTipoOt,wTableRecesosLaborales,wTableTareasAlmacen)
        for(table in UrlArray){
            val f = File(SDCardRoot, table + ".json")
            if(!f.exists()){
                existe = "Acceso Denegado"
                break
            }
        }
        return existe
    }

    fun setRecyclerViewFuncionalidades(){
        wRecyclerView = findViewById(R.id.rvFuncionalidadesList) as RecyclerView
        wRecyclerView.setHasFixedSize(true)
        wRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter.RecyclerAdapter(getFunciones(), this)
        wRecyclerView.adapter = mAdapter
    }

    override fun onBackPressed() {
        val alertDialogBuilder = AlertDialog.Builder(this, R.style.Custom)
        alertDialogBuilder
                .setNegativeButton("NO", { dialog, id ->
                    dialog.cancel()
                })
                .setTitle("PetroApp")
                .setMessage("Seguro desea salir?")
                .setPositiveButton("SI", { dialog, id ->
                    this@MainActivity.finish()
                    finishAffinity()
                })
        alertDialogBuilder.show()
    }
}
