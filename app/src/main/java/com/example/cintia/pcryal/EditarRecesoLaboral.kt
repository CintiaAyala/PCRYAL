package com.example.cintia.pcryal

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.layout_editarreceso.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EditarRecesoLaboral: AppCompatActivity() {
    var utility = Utility()
    var recesoAeditar = ""
    var Recesos = ""
    val categRecesos = ArrayList<Utility.StringWithTag>()
    var dataAdapterA: ArrayAdapter<Utility.StringWithTag>? = null
    var CodApp = ""
    var horayequipo = ""
    var nameEditFile = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_editarreceso)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
        getSupportActionBar()?.setLogo(R.drawable.iconapp)
        getSupportActionBar()?.setDisplayUseLogoEnabled(true)

        nameEditFile = intent.getStringExtra("nameFile")
        recesoAeditar = utility.leerArchivos("/Recesos/"+nameEditFile)
        Recesos = utility.leerArchivos("recesoslaborales.json")
        cargarSpinner()
        setFormulario()
        utility.getTime(edEditInicioReceso, this)
        utility.getTime(edEditFinReceso, this)
        btnGuardarEdicionReceso.setOnClickListener {
            guardar()
        }
    }

    fun cargarSpinner() {
        val jsonArrayRecesos = JSONArray(Recesos)
        categRecesos.add(utility.StringWithTag("-", "null"))
        for (i in 0 until jsonArrayRecesos.length()) {
            val row = jsonArrayRecesos.getJSONObject(i)
            var nomReceso = row.getString("descripcion")
            var idReceso = row.getString("id")
            categRecesos.add(utility.StringWithTag(nomReceso, idReceso))
        }
        dataAdapterA = ArrayAdapter(this, android.R.layout.simple_spinner_item, categRecesos)
        dataAdapterA!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spEditReceso.setAdapter(dataAdapterA)
    }

    fun setFormulario() {
        val jsonObject = JSONObject(recesoAeditar)
        CodApp = jsonObject.optString("codApp")
        horayequipo = jsonObject.optString("horayequipo")
        var datoIdReceso = jsonObject.optString("receso")
        var intReceso = utility.getPositionsp(datoIdReceso, categRecesos)
        spEditReceso.setSelection(intReceso)
        edEditInicioReceso.setText(jsonObject.optString("hrInicioReceso"))
        edEditFinReceso.setText(jsonObject.optString("hrFinReceso"))
        var obs = if (jsonObject.optString("observaciones").equals("-")) "" else jsonObject.optString("observaciones")
        edEditObsReceso.setText(obs)
    }

    fun guardar() {
        var idSelectReceso = categRecesos.get(spEditReceso.selectedItemId.toInt()).getid()
        var valhrInicioReceso = edEditInicioReceso.text.toString()
        var valhrFinReceso = edEditFinReceso.text.toString()
        var obs = if (edEditObsReceso.text.toString().equals("")) "-" else edEditObsReceso.text.toString()
        try {
            val strHorayEquipo = utility.getHorayEquipo()
            val wnFileName = "Receso_" + strHorayEquipo + "_" + CodApp + ".json"
            var f = File(Environment.getExternalStorageDirectory().absolutePath.toString() + "/Recesos")
            if (!f.isDirectory()) {
                val newFolder = "/Recesos"
                val myNewFolder = File(Environment.getExternalStorageDirectory().toString() + newFolder)
                myNewFolder.mkdir()
            }
            val file = File(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/Recesos/" + wnFileName)

            if (!(idSelectReceso.equals("null")) && !(valhrInicioReceso == "") && !(valhrFinReceso == "")) {
                if (utility.validDates(valhrInicioReceso,valhrFinReceso)) {
                    val osw = OutputStreamWriter(FileOutputStream(file))
                    osw.write("{")
                    osw.write(" \"codApp\": \"" + CodApp + "\","
                            + " \"horayequipo\": \"" + horayequipo + "\","
                            + " \"receso\": \"" + idSelectReceso + "\","
                            + " \"hrInicioReceso\": \"" + valhrInicioReceso + "\","
                            + " \"hrFinReceso\": \"" + valhrFinReceso + "\","
                            + " \"observaciones\": \"" + obs + "\""
                            + "}"
                    )
                    osw.flush()
                    osw.close()

                    //ELiminar el archivo editado
                    val filetoDelete = File(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/Recesos/" + nameEditFile)
                    if(filetoDelete.exists())
                        filetoDelete.delete()

                    val msj1 = "EDICION GUARDADA CORRECTAMENTE..."
                    val alertDialogBuilder = AlertDialog.Builder(this, R.style.Custom)
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
                } else {
                    val msj = "HORARIO DE FIN NO PUEDE SER MENOR QUE EL INICIO"
                    AlertDialog.Builder(this, R.style.Custom)
                            .setTitle("PetroApp")
                            .setMessage(msj)
                            .setPositiveButton(android.R.string.yes, null).show()
                }
            } else {
                val msj = "PROBLEMAS PARA GUARDAR..." + "\n" +
                        "FOMRULARIO INCOMPLETO"
                AlertDialog.Builder(this, R.style.Custom)
                        .setTitle("PetroApp")
                        .setMessage(msj)
                        .setPositiveButton(android.R.string.yes, null).show()
            }
        } catch (parseException: ParseException) {
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
                    val main = Intent(applicationContext, RecesosLaborales::class.java)
                    startActivity(main)
                    this@EditarRecesoLaboral.finish()
                })
        alertDialogBuilder.show()
    }
}