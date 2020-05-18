package com.example.cintia.pcryal

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import kotlinx.android.synthetic.main.layout_recesoslaborales.*

import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.ParseException
import java.util.*
import android.widget.LinearLayout
import android.support.design.widget.FloatingActionButton

class RecesosLaborales: AppCompatActivity()  {
    var scrollView : ScrollView? = null
    val categRecesos = ArrayList<Utility.StringWithTag>()
    var dataAdapterA: ArrayAdapter<Utility.StringWithTag>? = null
    var utility:Utility = Utility()
    var Recesos : String = ""
    var CodApp : String = ""
    var texto = ""
    var jsonjObject: JSONObject? = null
    var listRecesos: MutableList<RecesoLaboral>? = mutableListOf<RecesoLaboral>()
    var alert: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_recesoslaborales)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
        getSupportActionBar()?.setLogo(R.drawable.iconapp)
        getSupportActionBar()?.setDisplayUseLogoEnabled(true)

        CodApp = utility.getFromJornada("codApp")
        Recesos = utility.leerArchivos("recesoslaborales.json")
        cargarSpinner()
        edInicioReceso.setText(utility.getHora())
        utility.getTime(edInicioReceso,this)
        utility.getTime(edFinReceso,this)

        visibilityBntListaRecesos()

        btnGuardarReceso.setOnClickListener{
            guardar()
        }
        btnListaReceso.setOnClickListener{
            leerListaRecesos()
            alertRecesos()
        }
    }

    fun alertRecesos(){
        var alertDialog = AlertDialog.Builder(this, R.style.Custom)
        alertDialog!!.setView(scrollView)
        alertDialog!!.setNegativeButton("Cancel",

                { dialog, which ->
                    dialog.dismiss()
                    listRecesos!!.clear()
                    // TODO Auto-generated method stub
                })


        alert = alertDialog!!.create()
        alert!!.show()
        alert!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        alert!!.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    fun visibilityBntListaRecesos(){
        if(utility.existFiles("/Recesos/") > 0)
            btnListaReceso.visibility = View.VISIBLE
        else
            btnListaReceso.visibility = View.INVISIBLE
    }

    fun leerListaRecesos(){
        var archivo = ""
        listRecesos!!.clear()
        var f = File(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/Recesos/")
        if(f.exists()) {
            var files = f.listFiles()
            for (file in files) {
                texto = ""
                if (file.isFile() && file.name.contains(utility.getFromJornada("codApp"))){
                    archivo = file.getName()
                    texto = utility.leerArchivos("Recesos/"+archivo)
                    jsonjObject = JSONObject(texto)
                    listRecesos!!.add(RecesoLaboral(archivo,jsonjObject!!.optString("horayequipo"), jsonjObject!!.optString("receso"),jsonjObject!!.optString("hrInicioReceso"), jsonjObject!!.optString("hrFinReceso")))
                }
            }
            listarRecesosLaborales()
        }
    }

    fun listarRecesosLaborales(){
        scrollView = ScrollView(this)
        val layoutParamsScroll =  LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        scrollView!!.setLayoutParams(layoutParamsScroll)
        scrollView!!.setBackgroundColor(Color.parseColor("#1b2024"))


        var LxP = LinearLayout(this)
        LxP.orientation = LinearLayout.VERTICAL

        for(z in 0 until listRecesos!!.size){
            val layoutDesc = LinearLayout(this)
            layoutDesc.orientation = LinearLayout.HORIZONTAL

            var rl = listRecesos!!.get(z)

            val tvTitle = TextView(this)
            var nro = z + 1
            tvTitle.setText("RECESO LABORAL " + nro)
            tvTitle.textSize = 17f
            var tvTitleParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            tvTitleParam.gravity = Gravity.CENTER
            tvTitleParam.setMargins(0,10, 0, 5)
            tvTitle.setLayoutParams(tvTitleParam)

            val tvDescripcion = TextView(this)
            tvDescripcion.setText("MOTIVO: " + (rl.descripcion))
            tvDescripcion.textSize = 17f
            val tvParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            tvParams.setMargins(20, 0, 10, 0)
            tvDescripcion.layoutParams = tvParams

            layoutDesc.addView(tvDescripcion)

            val layoutHorarios = LinearLayout(this)
            layoutDesc.orientation = LinearLayout.HORIZONTAL

            val LayoutHoraiosParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            LayoutHoraiosParams.setMargins(20, 10, 25, 5)

            val tvHrInicio = TextView(this)
            tvHrInicio.setText("INICIO: " + rl.hrInicio)
            tvHrInicio.setLayoutParams(LayoutHoraiosParams);
            layoutHorarios.addView(tvHrInicio)

            val tvHrFin = TextView(this)
            tvHrFin.setText("FIN: " + rl.hrFin)
            tvHrFin.setLayoutParams(LayoutHoraiosParams)
            layoutHorarios.addView(tvHrFin)

            val btnEditar = FloatingActionButton(this)
            val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.gravity = Gravity.RIGHT
            layoutParams.setMargins(0, 0, 50, 20)
            btnEditar.layoutParams = layoutParams
            btnEditar.size = FloatingActionButton.SIZE_MINI
            btnEditar.setImageResource(android.R.drawable.ic_menu_edit)
            btnEditar.setOnClickListener {
                val orIntent = Intent(this, EditarRecesoLaboral::class.java)
                orIntent.putExtra("nameFile", rl.nameFile!!)
                this.startActivity(orIntent)
                this.finish()
            }

            if(utility.par(z)){
                tvTitle.setTextColor(Color.WHITE);
                tvDescripcion.setTextColor(Color.WHITE);
                tvHrInicio.setTextColor(Color.WHITE);
                tvHrFin.setTextColor(Color.WHITE);
            }else{
                tvTitle.setTextColor(Color.parseColor("#ed7a0d"));
                tvDescripcion.setTextColor(Color.parseColor("#ed7a0d"));
                tvHrInicio.setTextColor(Color.parseColor("#ed7a0d"));
                tvHrFin.setTextColor(Color.parseColor("#ed7a0d"));
            }
            LxP.addView(tvTitle)
            LxP.addView(layoutDesc)
            LxP.addView(layoutHorarios)
            LxP.addView(btnEditar)
            LxP.setOnLongClickListener{
                alertEliminarReceso(rl.nameFile!!)
                true
            }
        }
        scrollView!!.addView(LxP)
    }

    fun alertEliminarReceso(NameFile: String){
        var dialogo1 =  AlertDialog.Builder(this, R.style.Custom)
        dialogo1.setTitle("PetroApp: IMPORTANTE")
        dialogo1.setMessage("¿ Eliminar este receso laboral ?")
        dialogo1.setCancelable(false)
        dialogo1.setPositiveButton("Confirmar",  {dialogo1, id ->
            eliminarReceso(NameFile)
        })
        dialogo1.setNegativeButton("Cancelar", {dialogo1, id-> {} })
        dialogo1.show()
    }

   fun eliminarReceso(NameFile: String){
        val file = File(Environment.getExternalStorageDirectory().absolutePath + "/Recesos/" + NameFile)
        if(file.exists()){
            file.delete()
            scrollView!!.removeAllViews()
            if(utility.existFiles("/Recesos/") > 0){
                leerListaRecesos()
                alert!!.dismiss()
                alertRecesos()
            }else{
                alert!!.dismiss()
                visibilityBntListaRecesos()
            }
        }
    }

    fun cargarSpinner(){
        val jsonArrayEquipo = JSONArray(Recesos)
        categRecesos.add(utility.StringWithTag("-", "null"))
        for (i in 0 until jsonArrayEquipo.length()) {
            val row = jsonArrayEquipo.getJSONObject(i)
            var nomReceso = row.getString("descripcion")
            var idReceso = row.getString("id")
            categRecesos.add(utility.StringWithTag(nomReceso, idReceso))
        }
        dataAdapterA = ArrayAdapter(this, android.R.layout.simple_spinner_item, categRecesos)
        dataAdapterA!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spReceso.setAdapter(dataAdapterA)
    }

    fun guardar(){
        var idSelectReceso = categRecesos.get(spReceso.selectedItemId.toInt()).getid()
        var valhrInicioReceso = edInicioReceso.text.toString()
        var valhrFinReceso = edFinReceso.text.toString()
        var obs = if(edObsReceso.text.toString().equals("")) "-" else edObsReceso.text.toString()
        try {
            val strHorayEquipo = utility.getHorayEquipo()
            val wnFileName = "Receso_" + strHorayEquipo + "_" + CodApp + ".json";
            var f = File(Environment.getExternalStorageDirectory().absolutePath.toString() + "/Recesos")
            if (!f.isDirectory()) {
                val newFolder = "/Recesos"
                val myNewFolder = File(Environment.getExternalStorageDirectory().toString() + newFolder);
                myNewFolder.mkdir(); //creamos la carpeta
            }
            val file = File(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/Recesos/" + wnFileName)

            if(!(idSelectReceso.equals("null")) && !(valhrInicioReceso == "") && !(valhrFinReceso == "")){
                if(utility.validDates(valhrInicioReceso,valhrFinReceso)){
                    val osw = OutputStreamWriter(FileOutputStream(file))
                    osw.write("{")
                    osw.write(" \"codApp\": \"" + CodApp + "\","
                            + " \"horayequipo\": \"" + strHorayEquipo + "\","
                            + " \"receso\": \"" + idSelectReceso + "\","
                            + " \"hrInicioReceso\": \"" + valhrInicioReceso + "\","
                            + " \"hrFinReceso\": \"" + valhrFinReceso + "\","
                            + " \"observaciones\": \"" + obs + "\""
                            + "}"
                    )
                    osw.flush()
                    osw.close()
                    val msj1 = "DATOS GUARDADOS CORRECTAMENTE..."
                    val alertDialogBuilder = AlertDialog.Builder(this,R.style.Custom)
                    alertDialogBuilder
                            .setTitle("PetroApp")
                            .setMessage(msj1)
                            .setCancelable(false)
                            //.setIcon(R.drawable.ok)
                            .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, id ->
                                val MainAct = Intent(applicationContext, MainActivity::class.java)
                                startActivity(MainAct)
                                this.finish()
                            })
                    alertDialogBuilder.show()
                }else{
                    val msj = "HORARIO DE FIN NO PUEDE SER MENOR QUE EL INICIO"
                    AlertDialog.Builder(this,R.style.Custom)
                            .setTitle("PetroApp")
                            .setMessage(msj)
                            .setPositiveButton(android.R.string.yes, null).show()
                }
            }else{
                val msj =  "PROBLEMAS PARA GUARDAR..." + "\n" +
                        "FOMRULARIO INCOMPLETO"
                AlertDialog.Builder(this,R.style.Custom)
                        .setTitle("PetroApp")
                        .setMessage(msj)
                        .setPositiveButton(android.R.string.yes, null).show()
            }
        }catch( parseException: ParseException){
            parseException.printStackTrace()
        }
    }

    override fun onBackPressed() {
        val alertDialogBuilder = AlertDialog.Builder(this, R.style.Custom)
        alertDialogBuilder

                .setNegativeButton("NO", { dialog, id ->
                    dialog.cancel()
                })
                .setTitle("PetroApp: IMPORTANTE")
                .setMessage("No ha guardado el formulario. Seguro desea salir?")
                .setPositiveButton("SI", { dialog, id ->
                    val main = Intent(applicationContext, MainActivity::class.java)
                    startActivity(main)
                    this.finish()
                })
        alertDialogBuilder.show()
    }
}