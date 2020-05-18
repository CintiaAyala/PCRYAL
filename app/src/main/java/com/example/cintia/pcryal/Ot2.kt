package com.example.cintia.pcryal

import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_ot2.*
import org.json.JSONArray
import android.graphics.Color
import android.os.Environment
import android.support.design.widget.FloatingActionButton
import android.widget.*
import android.widget.TextView
import android.widget.EditText
import android.widget.LinearLayout
import android.view.WindowManager
import android.text.InputType
import android.view.Gravity
import android.text.Editable
import android.text.TextWatcher
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class Ot2: Fragment(), View.OnClickListener {
    var scrollView : ScrollView? = null
    var utility = Utility()

    var alertDialog: AlertDialog.Builder? = null
    var btn_Servicios: Button? = null
    var btn_Materiales: Button? = null
    var btnGuardar:FloatingActionButton? = null

    var edHrFinOt:  EditText? = null

    var tvServicios:TextView? = null

    var Servicios : String = ""
    var Materiales : String = ""

    var ADItemsServ: Array<String>? = null
    var ADItemsServId: Array<String>? = null
    var ArrayTrueFalseServ: BooleanArray? = null

    var ArrayMatyId: Array<Utility.StringWithTag>?=null
    var ArrayCant: Array<String>? =null
    var sizeMateriales=0

    var str_ServiciosOk = ""
    var str_ServiciosOkId = ""

    var str_MaterialesId = ""
    var str_Materiales = ""

    var arrDatos: Array<String>? = null

    var codCompresion = "-"
    var CodApp = ""
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view: View = inflater!!.inflate(R.layout.fragment_ot2, container, false)

        val myTag = tag

        (activity as OrdenTrabajo).setTabFragmentB(myTag)
        edHrFinOt = view.findViewById(R.id.edHrFinOt) as EditText
        btn_Servicios = view.findViewById(R.id.btnServ) as Button
        btn_Materiales = view.findViewById(R.id.btnMateriales) as Button
        btnGuardar = view.findViewById(R.id.btnGuardar) as FloatingActionButton

        tvServicios = view.findViewById(R.id.tvServicios) as TextView
        btn_Servicios!!.setOnClickListener(this)
        btn_Materiales!!.setOnClickListener(this)
        btnGuardar!!.setOnClickListener(this)
        inicializar()

        edHrFinOt!!.setText(utility.getHora())
        utility.getTime(edHrFinOt!!, context)

        return view
    }

    fun b_updateText(t: Array<String>) {
        arrDatos = t
    }

    fun inicializar(){
        CodApp = utility.getFromJornada("codApp")
        Servicios = utility.leerArchivos("serviciosmantenimiento.json")
        Materiales = utility.leerArchivos("materiales.json")
        llenarArrayItems()
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btnServ -> {
                alertServicios()
            }
            R.id.btnMateriales ->{
                listarMateriales()
                alertMateriales()
            }
            R.id.btnGuardar->{
                guardar()
            }
            else -> {
                //else condition
            }
        }
    }

    fun guardar(){
        var widTipoOt = arrDatos?.get(0) ?: "1"
        var wMM = arrDatos?.get(1)
        var wPozo = arrDatos?.get(2)?.trim()?.toUpperCase()
        var wHrInicioOt = arrDatos?.get(3) ?: "-"
        var wPresion = arrDatos?.get(4) ?: "-"
        var wTemperatura = arrDatos?.get(5) ?: "-"
        var wGolpesXmin = arrDatos?.get(6) ?: "-"
        var wPresionGas = arrDatos?.get(7) ?: "-"
        var wStr_Fotos = arrDatos?.get(8) ?: "-"
        var widMaquina = utility.getStringFromJson(utility.leerArchivos("maquinas.json"), "mm", wMM, "id")
        var widPozo = utility.getStringFromJson(utility.leerArchivos("pozos.json"), "nombre", wPozo, "id")
        if(str_MaterialesId == "") str_MaterialesId = "-"
        if(str_ServiciosOkId == "") str_ServiciosOkId = "-"
        if(wStr_Fotos == "") wStr_Fotos = "-"
        var wHrFinOt = edHrFinOt!!.getText().toString()

        val wnFileName = "OT_" + utility.getHorayEquipo() + "_" + CodApp + ".json";

        var f = File(Environment.getExternalStorageDirectory().absolutePath.toString() + "/OTs")
        if(!f.isDirectory()) {
            val newFolder = "/OTs"
            val myNewFolder = File(Environment.getExternalStorageDirectory().toString() + newFolder);
            myNewFolder.mkdir(); //creamos la carpeta
        }
        if(widMaquina!=null && widPozo!=null &&  wHrInicioOt!="" && wHrFinOt!="" && widTipoOt!="null") {
            if(utility.validDates(wHrInicioOt,wHrFinOt)){
                val file = File(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/OTs/" + wnFileName)
                var osw = OutputStreamWriter(FileOutputStream(file))
                var latitud = (activity as OrdenTrabajo).latitude
                var longitud = (activity as OrdenTrabajo).longitude
                osw.write("{" + "\n");
                osw.write(" \"codApp\": \"" + CodApp + "\"," + "\n"
                        + " \"latitud\": \"" + latitud + "\"," + "\n"
                        + " \"longitud\": \"" + longitud + "\"," + "\n"
                        + " \"pozo\": \"" + widPozo + "\"," + "\n"
                        + " \"mm\": \"" + widMaquina + "\"," + "\n"
                        + " \"tipoOt\": \"" + widTipoOt + "\"," + "\n"
                        + " \"inicio\": \"" + wHrInicioOt + "\"," + "\n"
                        + " \"acciones\": \"" + str_ServiciosOkId + "\"," + "\n"
                        + " \"compresion\": \"" + getcbCL("id") + "\"," + "\n"
                        + " \"presion\": \"" + wPresion + "\"," + "\n"
                        + " \"temperatura\": \"" + wTemperatura + "\"," + "\n"
                        + " \"golpesXmin\": \"" + wGolpesXmin + "\"," + "\n"
                        + " \"presionGas\": \"" + wPresionGas + "\"," + "\n"
                        + " \"final\": \"" + wHrFinOt +"\"," + "\n"
                        + " \"materiales\": \"" + str_MaterialesId + "\"," + "\n"
                        + " \"imagenes\": \"" + wStr_Fotos + "\"" + "\n"
                        + "}");
                osw.flush();
                osw.close();
                val alertDialogBuilder = android.support.v7.app.AlertDialog.Builder(context,R.style.Custom)
                alertDialogBuilder
                        .setTitle("PetroApp")
                        .setMessage("DATOS GUARDADOS CORRECTAMENTE...")
                        //  .setIcon(R.drawable.ok)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes, { dialog, id ->
                            val MainAct = Intent(context, MainActivity::class.java)
                            startActivity(MainAct)
                            this.activity.finish()
                        })
                alertDialogBuilder.show()
        }else{
                val msj = "HORARIO DE FIN NO PUEDE SER MENOR QUE EL INICIO"
                AlertDialog.Builder(context,R.style.Custom)
                        .setTitle("PetroApp")
                        .setMessage(msj)
                        .setPositiveButton(android.R.string.yes, null).show()
            }
        }else{
            val msj1 = "PROBLEMAS PARA GUARDAR..." + "\n" +
                    "ORDEN DE TRABAJO INCOMPLETA , FALTAN ALGUNOS DATOS"
            val alertDialogBuilder = android.support.v7.app.AlertDialog.Builder(context,R.style.Custom)
            alertDialogBuilder
                    .setTitle("PetroApp")
                    .setMessage(msj1)
                    .setCancelable(false)
                    //.setIcon(R.drawable.ok)
                    .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, id ->

                    })
            alertDialogBuilder.show()
        }
    }

    fun llenarArrayItems() {
        val jsonArrayServicios = JSONArray(Servicios)
        val sizeServicios = jsonArrayServicios.length()
        ADItemsServ = Array(sizeServicios, { "" })
        ADItemsServId = Array(sizeServicios, { "" })
        ArrayTrueFalseServ = BooleanArray(sizeServicios, { false })
        ArrayTrueFalseServ?.fill(false, 0, sizeServicios)
        for (i in 0 until sizeServicios) {
            val row = jsonArrayServicios.getJSONObject(i)
            var nomServicio = row.getString("descripcion")
            var idServicio = row.getString("id")
            ADItemsServ?.set(i, nomServicio)
            ADItemsServId?.set(i, idServicio)
        }

        val jsonArrayMateriales = JSONArray(Materiales)
        sizeMateriales = jsonArrayMateriales.length()
        ArrayMatyId = Array(sizeMateriales, { utility.StringWithTag("-", "null") })
        ArrayCant = Array(sizeMateriales, { "" })
        ArrayCant?.fill("0.0", 0, sizeMateriales)

        for (j in 0 until sizeMateriales) {
            val row = jsonArrayMateriales.getJSONObject(j)
            var nomMaterial = row.getString("descripcion")
            var idMaterial = row.getString("id")
            ArrayMatyId?.set(j,utility.StringWithTag(nomMaterial, idMaterial))
        }
    }

    fun listarMateriales(){
        scrollView = ScrollView(context);
        val layoutParamsScroll =  LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollView!!.setLayoutParams(layoutParamsScroll);

        var LxP = LinearLayout(context)
        LxP.orientation = LinearLayout.VERTICAL
        val LinearParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        LinearParams.setMargins(20, 0, 0, 0)
        LxP.layoutParams = LinearParams

        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(0, 0, 0, 15)

        for(z in 0 until sizeMateriales){
            val layoutCant = LinearLayout(context)
            layoutCant.orientation = LinearLayout.HORIZONTAL

            val tvDescripcion = TextView(context)
            tvDescripcion.setText(ArrayMatyId!!.get(z).toString())
            tvDescripcion.textSize = 17f
            val tvParams = LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.WRAP_CONTENT)
            tvParams.setMargins(10, 0, 50, 0)
            tvDescripcion.layoutParams = tvParams

            val edCantidad = EditText(context)
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
        val textView = TextView(this.context)
        textView.text = " Materiales utilizados:"
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 20f
        textView.setBackgroundColor(Color.parseColor("#ed7a0d"))

        var alertDialog = AlertDialog.Builder(this.context, R.style.CustomList)
        alertDialog.setView(scrollView)
        alertDialog.setNegativeButton("Cancel",

                { dialog, which ->
                    // TODO Auto-generated method stub
                })
        alertDialog.setPositiveButton("Ok", { _, _ ->
            // TODO Auto-generated method stub
            str_MaterialesId = ""
            str_Materiales = ""
            for (i in 0 until sizeMateriales) {
                if(!ArrayCant?.get(i).equals("0.0")){
                    str_MaterialesId = str_MaterialesId + ArrayMatyId?.get(i)?.getid() + " " + ArrayCant?.get(i) + "|"
                    str_Materiales = str_Materiales + ArrayMatyId?.get(i)?.toString() + " " + ArrayCant?.get(i) + "|"
                }
            }
            tvMateriales.text = str_Materiales
        })

        val alert = alertDialog.create()
        alert.setCustomTitle(textView)
        alert.show()
        alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    fun alertServicios(){
        val textView = TextView(this.context)
        textView.text = "Selecciona los servicios:"
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 20f
        textView.setBackgroundColor(Color.parseColor("#ed7a0d"))

        var alertdialogbuilder = AlertDialog.Builder(this.context, R.style.CustomList)
        alertdialogbuilder.setMultiChoiceItems(ADItemsServ, ArrayTrueFalseServ, {dialog,which,isChecked->
            // Actualizar el estado del item checked
                  ArrayTrueFalseServ?.set(which,isChecked)

        })
        alertdialogbuilder.setCancelable(false)

        alertdialogbuilder.setCustomTitle(textView)

        alertdialogbuilder.setPositiveButton("OK", { dialog, which ->
            var a = 0
            str_ServiciosOk = ""
            while (a < ArrayTrueFalseServ!!.size) {
                val value = ArrayTrueFalseServ!![a]
                if(value){
                    str_ServiciosOk = str_ServiciosOk + ADItemsServ!!.get(a) + "|"
                    str_ServiciosOkId = str_ServiciosOkId + ADItemsServId!!.get(a) + "|"
                }
                a++
            }
            tvServicios!!.setText(str_ServiciosOk)
        })

        alertdialogbuilder.setNeutralButton("Cancel", { dialog, which -> })

        val dialog = alertdialogbuilder.create()
        dialog.show()
    }

     fun getcbCL(wRtaValue : String) : String {
        var strCompresion=""
        var j = 1
        var cb : EditText
        var tv : TextView
        var resourceIDL : Int
        var resourceIDR : Int
        var resourceTV : Int
        var valorL : String
        var valorR : String
        var valortv :String
        while(j <= 6) {
            valorL = "cbCL" + j + "L"
            valorR = "cbCL" + j + "R"
            valortv = "tvCL" + j
            resourceTV = getResources().getIdentifier(valortv,"id",activity.packageName)
            resourceIDL = getResources().getIdentifier(valorL, "id", activity.packageName)
            resourceIDR = getResources().getIdentifier(valorR,"id",activity.packageName)
            if(resourceIDL != 0) {
                tv = view?.findViewById(resourceTV) as TextView
                cb = view?.findViewById(resourceIDL)as EditText
                if(!cb.getText().toString().equals("")){
                    if(strCompresion.equals("")){
                        strCompresion = tv.getText().toString() + " " + "L" + " " + cb.getText()
                        codCompresion = j.toString() + " " + "L" + " " + cb.getText()
                    }else {
                        strCompresion = strCompresion + "|" + tv.getText().toString() + " " + "L" + " " + cb.getText()
                        codCompresion = codCompresion + "|" + j.toString() + " " + "L" + " " + cb.getText()
                    }
                }
            }
            if(resourceIDR != 0){
                tv = view?.findViewById(resourceTV) as TextView
                cb = view?.findViewById(resourceIDR) as EditText
                if(!cb.getText().toString().equals("")){
                    if(strCompresion.equals("")){
                        strCompresion = tv.getText().toString() + " " + "R" + " " + cb.getText()
                        codCompresion = j.toString() + " " + "R" + " " + cb.getText()
                    }else {
                        strCompresion = strCompresion + "|" + tv.getText().toString() + " " + "R" + " " + cb.getText()
                        codCompresion = codCompresion + "|" + j.toString() + " " + "R" + " " + cb.getText()
                    }
                }
            }
            j++
        }
        if(strCompresion.equals("")){
            strCompresion = "-"
            codCompresion = "-"
        }
        return if(wRtaValue.equals("id")) codCompresion else strCompresion
    }
}