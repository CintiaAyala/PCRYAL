package com.example.cintia.pcryal

import android.support.v7.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import kotlinx.android.synthetic.main.layout_tareasalmacen.*
import org.json.JSONArray
import android.widget.TextView
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TareasAlmacen : AppCompatActivity() {
    var utility = Utility()
    var scrollView : ScrollView? = null
    var svAlmacen : ScrollView? = null
    var CodApp = ""
    var Tareas = ""
    var Materiales = ""
    var sizeMateriales = 0
    var ArrayMatyId: Array<Utility.StringWithTag>?=null
    var ArrayCant: Array<String>? =null
    val categTareas = ArrayList<Utility.StringWithTag>()
    var dataAdapterA: ArrayAdapter<Utility.StringWithTag>? = null
    var alertDialog: AlertDialog.Builder? = null
    var str_MaterialesId = ""
    var str_Materiales = ""
    var listTareas: MutableList<TareaAlmacen>? = mutableListOf()
    var jsonjObject: JSONObject? = null
    var alert: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_tareasalmacen)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
        getSupportActionBar()?.setLogo(R.drawable.iconapp)
        getSupportActionBar()?.setDisplayUseLogoEnabled(true)

        CodApp = utility.getFromJornada("codApp")
        Tareas = utility.leerArchivos("tareasalmacen.json")
        Materiales = utility.leerArchivos("materiales.json")
        llenarArrayItems()
        edInicioTarea.setText(utility.getHora())
        utility.getTime(edInicioTarea,this)
        utility.getTime(edFinTarea,this)
        cargarSpinner()
        spTareas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent!!.getItemAtPosition(position).toString()
                if (selectedItem.contains("MATERIALES")) {
                    rowCargaMateriales.visibility = View.VISIBLE
                }else{
                    rowCargaMateriales.visibility = View.GONE
                }
            }
        }
        visibilityBntListaTareas()

        btnCargarMateriales.setOnClickListener{
            listarMateriales()
            alertMateriales()
        }

        btnGuardarTareas.setOnClickListener{
            guardar()
        }

        btnListaTareas.setOnClickListener{
            leerListaTareas()
            alertTareas()
        }
    }

    fun alertTareas(){
        var alertDialog = AlertDialog.Builder(this,R.style.Custom)
        alertDialog.setView(svAlmacen)
        alertDialog.setNegativeButton("Cancel",

                { dialog, which ->
                    dialog.dismiss()
                    listTareas!!.clear()
                    // TODO Auto-generated method stub
                })


        alert = alertDialog.create()
        alert!!.show()
        alert!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        alert!!.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    fun leerListaTareas(){
        var archivo : String
        listTareas!!.clear()
        var f = File(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/Almacen/")
        if(f.exists()) {
            var files = f.listFiles()
            for (file in files) {
                var texto: String
                if (file.isFile() && file.name.contains(utility.getFromJornada("codApp"))){
                    archivo = file.getName()
                    texto = utility.leerArchivos("Almacen/"+archivo)
                    jsonjObject = JSONObject(texto)
                    listTareas!!.add(TareaAlmacen(archivo,jsonjObject!!.optString("horayequipo"), jsonjObject!!.optString("tarea"),jsonjObject!!.optString("hrInicioTarea"), jsonjObject!!.optString("hrFinTarea"), jsonjObject!!.optString("materiales")))
                }
            }
            listarTareasAlmacen()
        }
    }

    fun listarTareasAlmacen(){
        svAlmacen = ScrollView(this)
        val layoutParamsScroll =  LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        svAlmacen!!.setLayoutParams(layoutParamsScroll)
        svAlmacen!!.setBackgroundColor(Color.parseColor("#1b2024"))


        var LxP = LinearLayout(this)
        LxP.orientation = LinearLayout.VERTICAL

        for(z in 0 until listTareas!!.size){
            val layoutDesc = LinearLayout(this)
            layoutDesc.orientation = LinearLayout.HORIZONTAL

            var rl = listTareas!!.get(z)

            val tvTitle = TextView(this)
            var nro = z + 1
            tvTitle.setText("TAREA ALMACEN " + nro)
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
                val orIntent = Intent(this, EditarTareaAlmacen::class.java)
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
                alertEliminarTarea(rl.nameFile!!)
                true
            }
        }
        svAlmacen!!.addView(LxP)
    }

    fun alertEliminarTarea(NameFile: String){
        var dialogo1 =  AlertDialog.Builder(this, R.style.Custom)
        dialogo1.setTitle("PetroApp: IMPORTANTE")
        dialogo1.setMessage("¿ Eliminar este tarea de almacen ?")
        dialogo1.setCancelable(false)
        dialogo1.setPositiveButton("Confirmar",  {dialogo1, id ->
            eliminarTarea(NameFile)
        })
        dialogo1.setNegativeButton("Cancelar", {dialogo1, id-> {} })
        dialogo1.show()
    }

    fun eliminarTarea(NameFile: String){
        val file = File(Environment.getExternalStorageDirectory().absolutePath + "/Almacen/" + NameFile)
        if(file.exists()){
            file.delete()
            svAlmacen!!.removeAllViews()
            if(utility.existFiles("/Almacen/") > 0){
                leerListaTareas()
                alert!!.dismiss()
                alertTareas()
            }else{
                alert!!.dismiss()
                visibilityBntListaTareas()
            }
        }
    }

    fun visibilityBntListaTareas(){
        if(utility.existFiles("/Almacen/") > 0)
            btnListaTareas.visibility = View.VISIBLE
        else
            btnListaTareas.visibility = View.INVISIBLE
    }

    fun llenarArrayItems(){
        val jsonArrayMateriales = JSONArray(Materiales)
        sizeMateriales = jsonArrayMateriales.length()
        ArrayMatyId = Array(sizeMateriales, { utility.StringWithTag("-", "null") })
        ArrayCant = Array(sizeMateriales, { "" })
        ArrayCant!!.fill("0.0", 0, sizeMateriales)

        for (j in 0 until sizeMateriales) {
            val row = jsonArrayMateriales.getJSONObject(j)
            var nomMaterial = row.getString("descripcion")
            var idMaterial = row.getString("id")
            ArrayMatyId!![j] = utility.StringWithTag(nomMaterial, idMaterial)
        }
    }

    fun cargarSpinner(){
        val jsonArrayEquipo = JSONArray(Tareas)
        categTareas.add(utility.StringWithTag("-", "null"))
        for (i in 0 until jsonArrayEquipo.length()) {
            val row = jsonArrayEquipo.getJSONObject(i)
            var nomReceso = row.getString("descripcion")
            var idReceso = row.getString("id")
            categTareas.add(utility.StringWithTag(nomReceso, idReceso))
        }
        dataAdapterA = ArrayAdapter(this, android.R.layout.simple_spinner_item, categTareas)
        dataAdapterA!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTareas.setAdapter(dataAdapterA)
    }

    fun listarMateriales(){
        scrollView = ScrollView(this);
        val layoutParamsScroll =  LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        scrollView!!.setLayoutParams(layoutParamsScroll)

        var LxP = LinearLayout(this)
        LxP.orientation = LinearLayout.VERTICAL
        val LinearParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        LinearParams.setMargins(20, 0, 0, 0)
        LxP.layoutParams = LinearParams

        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(0, 0, 0, 15)

        for(z in 0 until sizeMateriales){
            val layoutCant = LinearLayout(this)
            layoutCant.orientation = LinearLayout.HORIZONTAL

            val tvDescripcion = TextView(this)
            tvDescripcion.setText(ArrayMatyId!!.get(z).toString())
            tvDescripcion.textSize = 17f
            val tvParams = LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.WRAP_CONTENT)
            tvParams.setMargins(10, 0, 50, 0)
            tvDescripcion.layoutParams = tvParams

            val edCantidad = EditText(this)
            val wcantP = ArrayCant!!.get(z)
            edCantidad.setHintTextColor(Color.WHITE)
            edCantidad.setHint(wcantP)
            edCantidad.gravity = Gravity.RIGHT
            edCantidad.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            val edparam = LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.WRAP_CONTENT)
            edparam.gravity = RelativeLayout.ALIGN_PARENT_RIGHT
            edCantidad.layoutParams = edparam
            val finalI = z
            edCantidad.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    var wCan: Float
                    var wValor: String
                    wValor = s.toString()
                    if ((wValor.contains(".")||(wValor.contains(","))) && s[s.length - 1] != '.') {
                        wValor = wValor.replace(",",".")
                        if (wValor.indexOf(".") + 2 <= wValor.length - 1) {
                            val formatted = wValor.substring(0, wValor.indexOf(".") + 2)
                            edCantidad.setText(formatted)
                            edCantidad.setSelection(formatted.length)
                        }
                    }
                    wCan = utility.parseStringtoFloat(wValor)
                    if (wCan < 0) wCan = 0F

                    ArrayCant!!.set(finalI, wCan.toString())

                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })
            layoutCant.addView(tvDescripcion)
            layoutCant.addView(edCantidad)
            layoutCant.layoutParams = params
            LxP.addView(layoutCant)
        }
        scrollView!!.addView(LxP)
    }

    fun alertMateriales(){
        val textView = TextView(this)
        textView.text = "RECEPCION DE MATERIALES:"
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 20f
        textView.setBackgroundColor(Color.parseColor("#ed7a0d"))
        alertDialog = AlertDialog.Builder(this, R.style.CustomList)
        alertDialog!!.setView(scrollView)
        alertDialog!!.setNegativeButton("Cancel",

                { dialog, which ->
                    // TODO Auto-generated method stub
                })
        alertDialog!!.setPositiveButton("Ok", { dialog, which ->
            // TODO Auto-generated method stub
            str_MaterialesId = ""
            str_Materiales = ""
            for (i in 0 until sizeMateriales) {
                if(ArrayCant!![i] != "0.0"){
                    str_MaterialesId = str_MaterialesId + ArrayMatyId!!.get(i).getid() + " " + ArrayCant!![i] + "|"
                    str_Materiales = str_Materiales + ArrayMatyId!!.get(i).toString() + " " + ArrayCant!![i] + "|"
                }
            }

        })

        val alert = alertDialog!!.create()
        alert.setCustomTitle(textView)
        alert.show()
        alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    fun guardar(){
        var idSelectTarea = categTareas.get(spTareas.selectedItemId.toInt()).getid()
        var strTarea = categTareas.get(spTareas.selectedItemId.toInt()).toString()
        if(!strTarea.contains("MATERIALES")) str_MaterialesId = "-"
        var valhrInicioTarea = edInicioTarea.text.toString()
        var valhrFinTarea = edFinTarea.text.toString()
        var obs = if(edObsTarea.text.toString().equals("")) "-" else edObsTarea.text.toString()
        try {
            val strHorayEquipo = utility.getHorayEquipo()
            val wnFileName = "Almacen_" + strHorayEquipo + "_" + CodApp + ".json"
            var f = File(Environment.getExternalStorageDirectory().absolutePath.toString() + "/Almacen")
            if (!f.isDirectory()) {
                val newFolder = "/Almacen"
                val myNewFolder = File(Environment.getExternalStorageDirectory().toString() + newFolder);
                myNewFolder.mkdir(); //creamos la carpeta
            }
            val file = File(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/Almacen/" + wnFileName)

            if(!idSelectTarea.equals("-") && !(valhrInicioTarea == "") && !(valhrFinTarea == "") && !(str_MaterialesId == "")){
                if(utility.validDates(valhrInicioTarea,valhrFinTarea)){
                    val osw = OutputStreamWriter(FileOutputStream(file))
                    osw.write("{")
                    osw.write(" \"codApp\": \"" + CodApp + "\","
                            + " \"horayequipo\": \"" + strHorayEquipo + "\","
                            + " \"tarea\": \"" + idSelectTarea + "\","
                            + " \"hrInicioTarea\": \"" + valhrInicioTarea + "\","
                            + " \"hrFinTarea\": \"" + valhrFinTarea + "\","
                            + " \"materiales\": \"" + str_MaterialesId + "\","
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