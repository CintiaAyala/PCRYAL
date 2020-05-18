package com.example.cintia.pcryal

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.os.Environment
import android.text.Editable
import android.widget.EditText
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import org.json.JSONArray
import org.json.JSONObject

class Utility: Activity() {
    var Jornada = ""
    var EnvironmentHelper = EnvironmentHelper()
    fun getTime(editText: EditText, context: Context){

        val cal = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            editText.text = (SimpleDateFormat("HH:mm").format(cal.time)).toEditable()

        }

        editText.setOnClickListener {
            TimePickerDialog(context, TimePickerDialog.THEME_HOLO_DARK, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
    }
    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    fun validDates(dateI: String, dateF: String):Boolean{
        try {
            val dateForm = SimpleDateFormat("HH:mm", Locale.US)
            val dInicio = dateForm.parse(dateI)
            val dFin = dateForm.parse(dateF)
            return (dFin.compareTo(dInicio) >= 0)
        }catch(e:Exception){return false}
    }

    fun getFromJornada(key: String):String{
        Jornada = leerArchivos(getFecha()+".json")
        val jsonObject = JSONObject(Jornada)
        return jsonObject.optString(key)
    }

    fun getFecha() : String{
        var date = Date();
        val formatter = SimpleDateFormat("yyyyMMdd")
        val answer = formatter.format(date)
        return answer
    }

    fun getHora(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd HH:mm:ss")
        val date = Date()
        return dateFormat.format(date).substring(9,14)
    }

    fun getHorayEquipo(): String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = Date()
        return ((dateFormat.format(date)).substring(11,19)).replace(":","") + getFromJornada("equipo")
    }

    fun dateTimeReverse(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd HH:mm:ss")
        val date = Date()
        return dateFormat.format(date)
    }

    fun parseStringtoFloat(wStr: String): Float {
        var wValor = 0f
        try {
            wValor = java.lang.Float.parseFloat(wStr)
        } catch (e: NumberFormatException) {
            wValor = 0f
        }

        return wValor
    }

    fun leerArchivos(ArchNombre :String) :String{
        var strRta = ""
        try {
            val f = File(EnvironmentHelper.storageDirectory + "/"+ArchNombre)
            if(f.exists()){
                var fin: BufferedReader?
                fin = BufferedReader(InputStreamReader(FileInputStream(f)))
                strRta = fin.readLine()
                fin.close()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return strRta
    }

    fun getPositionsp(id : String, categoria : ArrayList<Utility.StringWithTag>) : Int{
        var position = -1
        for (i in 0 until categoria.size){
            var item = categoria.get(i)
            var idCat = item.getid()
            if(idCat == id) position = i
        }
        return position
    }
    fun existFiles(directory : String) : Int{
        var cont = 0
        var f = File(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + directory)
        if(f.exists()) {
            var files = f.listFiles()
            for (file in files) {
                if(file.isFile() && file.name.contains(getFromJornada("codApp"))){
                    cont++
                }
            }
        }
        return cont
    }

    fun par(numero: Int): Boolean {return numero % 2 == 0 }

    fun getStringFromJson(jsonArrayStr:String, keyfind:String, equalTo:String?, returnkey:String) : String? {
        try {
            val jsonArray = JSONArray(jsonArrayStr)

            for (index in 0 until jsonArray.length()) {

                if(equalTo.equals((jsonArray.getJSONObject(index).getString(keyfind)))) {
                    return jsonArray.getJSONObject(index).getString(returnkey)
                    break
                }
            }
        }catch(e:Exception){
            return null
        }
        return null
    }
    inner class StringWithTag(var string: String, var tag: String) {

        override fun toString(): String {
            return string
        }
        fun getid(): String {
            return tag
        }
    }
}