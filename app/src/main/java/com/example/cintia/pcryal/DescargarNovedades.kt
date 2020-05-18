package com.example.cintia.pcryal

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import dmax.dialog.SpotsDialog;
import kotlinx.android.synthetic.main.layout_descargarnov.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class DescargarNovedades : AppCompatActivity(), View.OnClickListener {
    val REQUEST_EXTERNAL_STORAGE : Int = 1
    val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val UrlArray = arrayOf("aplicaciones","bombasaib","compresiones","equipos","maquinas","materiales","moviles","pozos","serviciosmantenimiento","zonas","tipoot","recesoslaborales","tareasalmacen")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_descargarnov)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
        getSupportActionBar()?.setLogo(R.drawable.iconapp)
        getSupportActionBar()?.setDisplayUseLogoEnabled(true)
        verifyStoragePermissions(this)
        btnActualizar.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnActualizar -> {

                DescargaAsync(this, UrlArray).execute()
            }
            else -> {
            }
        }
    }

    fun verifyStoragePermissions(activity:Activity){
        var permission = ActivityCompat.checkSelfPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE)
        }
    }

    inner class DescargaAsync(activ: Activity, urlArray: Array<String>) : AsyncTask<Void, Integer, Boolean>() {
        var activity: Activity = activ
        var arrTables = urlArray
        var SalidaOk : String = "Actualizado exitosamente "
        var SalidaError : String = "Error al descargar "
        var texto : String = ""
        var hostname = "192.168.1.19"
        var wUrl : String = "http://"+hostname+"/SGPDM/ArchJson/solicitar_json.php?NomTabla=";
        var dialog= SpotsDialog.Builder().setContext(activity)
                .setMessage("Actualizando...")
                .setTheme(R.style.Custom)
                .build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            texto = ""
            var Error = 0
            for(table in arrTables){
                if(!descargar(table)) {
                    Error ++
                    break
                }
            }
            if(Error>0) return false
            else return true
        }

        private fun descargar(tabla:String):Boolean {
                var exitosa: Boolean? = true
                try {
                    val url = URL(wUrl+tabla)
                    val urlConnection = url.openConnection() as HttpURLConnection
                    urlConnection.requestMethod = "GET"
                    urlConnection.doOutput = true
                    urlConnection.connect()
                    val SDCardRoot = Environment.getExternalStorageDirectory().toString()
                    val file = File(SDCardRoot +"/"+ tabla+".json")
                    val fileOutput = FileOutputStream(file)
                    val inputStream = urlConnection.inputStream
                    var downloadedSize = 0

                    val buffer = ByteArray(1024)
                    var bufferLength: Int
                    bufferLength = inputStream.read(buffer)
                    while (bufferLength > 0) {
                        fileOutput.write(buffer, 0, bufferLength)
                        downloadedSize += bufferLength
                        bufferLength = inputStream.read(buffer)
                    }
                    fileOutput.close()
                    texto += SalidaOk+ tabla + "\n"
                } catch (e: MalformedURLException) {
                    exitosa = false
                    texto += SalidaError + tabla + "\n"

                } catch (e: IOException) {
                    e.printStackTrace()
                    exitosa = false
                    texto += SalidaError + tabla + "\n"

                }
                return exitosa!!
        }

        override fun onProgressUpdate(vararg values: Integer?) {

        }

        override fun onPreExecute() {
            super.onPreExecute()
            tvRta?.text = "Actualizando"
            dialog.show()
            tvRta?.text = "Actualizando"
        }

        override fun onPostExecute(result: Boolean?) {
        //    super.onPostExecute(result)
            dialog.dismiss()
            tvRta?.text = texto
            if(result == false) tvDescarga?.text = "INTENTE ACTUALIZAR NUEVAMENTE"
            else {
                tvDescarga?.text = "ACTUALIZACION EXITOSA"

            }
            tvDescarga?.visibility = View.VISIBLE
        }

        override fun onCancelled() {
            super.onCancelled()
        }

    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}