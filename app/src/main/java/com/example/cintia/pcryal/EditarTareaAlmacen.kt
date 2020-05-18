package com.example.cintia.pcryal

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import kotlinx.android.synthetic.main.layout_editartarea.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EditarTareaAlmacen: AppCompatActivity() {
    var utility = Utility()
    var CodApp = ""
    var horayequipo = ""
    var tareaAeditar = ""
    var nameEditFile = ""
    var Tareas = ""
    var Materiales = ""
    var ArrayMatyId: Array<Utility.StringWithTag>?=null
    var ArrayCant: Array<String>? =null
    var str_MaterialesId = ""
    var str_Materiales = ""
    var sizeMateriales = 0
    var scrollView : ScrollView? = null
    var strMaterialesEditar = ""
    val categTareas = ArrayList<Utility.StringWithTag>()
    var dataAdapterA: ArrayAdapter<Utility.StringWithTag>? = null
    var alertDialog: AlertDialog.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_editartarea)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
        getSupportActionBar()?.setLogo(R.drawable.iconapp)
        getSupportActionBar()?.setDisplayUseLogoEnabled(true)

        nameEditFile = intent.getStringExtra("nameFile")
        tareaAeditar = utility.leerArchivos("/Almacen/"+nameEditFile)
        Tareas = utility.leerArchivos("tareasalmacen.json")
        cargarSpinner()
        setFormulario()
        utility.getTime(edEditInicioTarea, this)
        utility.getTime(edEditFinTarea, this)

        btnEditMateriales.setOnClickListener {
            listarMateriales()
            alertMateriales()
        }

        btnGuardarEdicionTarea.setOnClickListener {
           guardar()
        }
    }
    fun cargarSpinner() {
        val jsonArrayRecesos = JSONArray(Tareas)
        categTareas.add(utility.StringWithTag("-", "null"))
        for (i in 0 until jsonArrayRecesos.length()) {
            val row = jsonArrayRecesos.getJSONObject(i)
            var nomReceso = row.getString("descripcion")
            var idReceso = row.getString("id")
            categTareas.add(utility.StringWithTag(nomReceso, idReceso))
        }
        dataAdapterA = ArrayAdapter(this, android.R.layout.simple_spinner_item, categTareas)
        dataAdapterA!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spEditTarea.setAdapter(dataAdapterA)
    }

    fun setFormulario(){
        val jsonObject = JSONObject(tareaAeditar)
        CodApp = jsonObject.optString("codApp")
        horayequipo = jsonObject.optString("horayequipo")
        strMaterialesEditar = jsonObject.optString("materiales")
        var datoIdReceso = jsonObject.optString("tarea")
        var intReceso = utility.getPositionsp(datoIdReceso, categTareas)
        spEditTarea.setSelection(intReceso)
        edEditInicioTarea.setText(jsonObject.optString("hrInicioTarea"))
        edEditFinTarea.setText(jsonObject.optString("hrFinTarea"))
        var obs = if(jsonObject.optString("observaciones").equals("-")) "" else jsonObject.optString("observaciones")
        edEditObsTarea.setText(obs)

        if(datoIdReceso.equals("1")){
            rowEditMateriales.visibility = View.VISIBLE
            Materiales = utility.leerArchivos("materiales.json")
            llenarItems()
            llenarConMateriales()
        }
    }

    fun llenarItems(){
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

    fun llenarConMateriales(){
        var arrMat = strMaterialesEditar.split("|")
        for (str in arrMat){
            if(!str.equals("")) {
                var idMatyCant = str.split(" ")
                var idMaterial = idMatyCant[0]
                var cant = idMatyCant[1]
                setInArrayCant(idMaterial, cant)
            }
        }

    }

    fun setInArrayCant(idToFind: String, cant:String){
        for (j in 0 until sizeMateriales) {
            if((ArrayMatyId!![j].getid()).equals(idToFind)){
                ArrayCant!!.set(j, cant)
                break
            }
        }
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
            edCantidad.setHint(wcantP)
            edCantidad.setHintTextColor(Color.WHITE)
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
                    //***********************************************
                    wValor = s.toString()
                    if((wValor.contains(".")||(wValor.contains(","))) && s[s.length - 1] != '.') {
                        wValor = wValor.replace(",",".")
                        if(wValor.indexOf(".") + 2 <= wValor.length - 1) {
                            val formatted = wValor.substring(0, wValor.indexOf(".") + 2)
                            edCantidad.setText(formatted)
                            edCantidad.setSelection(formatted.length)
                        }
                    }
                    wCan = utility.parseStringtoFloat(wValor)
                    if(wCan < 0) wCan = 0F

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
        var idSelectTarea = categTareas.get(spEditTarea.selectedItemId.toInt()).getid()
        var strTarea = categTareas.get(spEditTarea.selectedItemId.toInt()).toString()
        if(!strTarea.contains("MATERIALES")) str_MaterialesId = "-"
        var valhrInicioTarea = edEditInicioTarea.text.toString()
        var valhrFinTarea = edEditFinTarea.text.toString()
        var obs = if(edEditObsTarea.text.toString().equals("")) "-" else edEditObsTarea.text.toString()
        try {
            val strHorayEquipo = utility.getHorayEquipo()
            val wnFileName = "Almacen_" + strHorayEquipo + "_" + CodApp + ".json"
            var f = File(Environment.getExternalStorageDirectory().absolutePath.toString() + "/Almacen")
            if(!f.isDirectory()) {
                val newFolder = "/Almacen"
                val myNewFolder = File(Environment.getExternalStorageDirectory().toString() + newFolder);
                myNewFolder.mkdir() //creamos la carpeta
            }
            val file = File(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/Almacen/" + wnFileName)

            if(!idSelectTarea.equals("-") && !(valhrInicioTarea == "") && !(valhrFinTarea == "") && !(str_MaterialesId == "")){
                if(utility.validDates(valhrInicioTarea,valhrFinTarea)){
                    val osw = OutputStreamWriter(FileOutputStream(file))
                    osw.write("{")
                    osw.write(" \"codApp\": \"" + CodApp + "\","
                            + " \"horayequipo\": \"" + horayequipo + "\","
                            + " \"tarea\": \"" + idSelectTarea + "\","
                            + " \"hrInicioTarea\": \"" + valhrInicioTarea + "\","
                            + " \"hrFinTarea\": \"" + valhrFinTarea + "\","
                            + " \"materiales\": \"" + str_MaterialesId + "\","
                            + " \"observaciones\": \"" + obs + "\""
                            + "}"
                    )
                    osw.flush()
                    osw.close()

                    //Delete the editted file
                    val filetoDelete = File(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/Almacen/" + nameEditFile)
                    if(filetoDelete.exists())
                        filetoDelete.delete()

                    val msj1 = "EDICION GUARDADA CORRECTAMENTE..."
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
                .setTitle("PetroApp")
                .setMessage("No ha guardado la edición. Seguro desea salir?")
                .setPositiveButton("SI", { dialog, id ->
                    val main = Intent(applicationContext, TareasAlmacen::class.java)
                    startActivity(main)
                    this.finish()
                })
        alertDialogBuilder.show()
    }
}