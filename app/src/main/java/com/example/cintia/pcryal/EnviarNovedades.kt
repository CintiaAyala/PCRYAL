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
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.layout_enviarnov.*
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply
import java.io.*
import java.net.SocketException


class EnviarNovedades:AppCompatActivity(), View.OnClickListener {
    val REQUEST_EXTERNAL_STORAGE : Int = 1
    val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    var hostname = "192.168.1.19"
    var port = 51518
    var user = "geek"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_enviarnov)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
        getSupportActionBar()?.setLogo(R.drawable.iconapp)
        getSupportActionBar()?.setDisplayUseLogoEnabled(true)
        verifyStoragePermissions(this)
        btnEnviar.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnEnviar -> {

                EnvioAsync(this).execute()
            }
            else -> {
                // else condition
            }
        }
    }

    fun verifyStoragePermissions(activity: Activity){
        var permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE)
        }
    }

    inner class EnvioAsync(activ: Activity) : AsyncTask<Void, Integer, Boolean>() {
        var activity: Activity = activ
        var str_print = ""
        var texto : String = ""
        var dialog = SpotsDialog.Builder().setContext(activity)
                .setMessage("Actualizando...")
                .setTheme(R.style.Custom)
                .build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            texto = ""
            val dirArray = arrayOf("Jornada", "OT", "Almacen", "Receso", "imgPDM")
            var cant : Int
            for(dir in dirArray){
                if(dir.equals("imgPDM")) cant = enviarImg()
                else cant = enviar(dir)
                if(cant == -1)
                    return false
                else
                    str_print += "Se enviaron " + cant + " archivos de " + dir + " exitosamente" + "\n"
            }
            return true
        }

        private fun enviarImg():Int {
            var cantFile: Int = 0
            val remotoDir = "/imgPDM"
            var mFTPClient: FTPClient?
            val path = Environment.getExternalStorageDirectory().toString()+"/"+ remotoDir
            try {
                mFTPClient = FTPClient()
                mFTPClient.connect(hostname, port)
                mFTPClient.login(user,"")
                if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                    mFTPClient.changeWorkingDirectory(remotoDir)
                    mFTPClient.setFileType(FTP.BINARY_FILE_TYPE)
                    val f = File(path)
                    if (f.exists()) {
                        val file = f.listFiles()
                        for (i in file.indices) {
                            val archivo = file[i]
                            if (archivo.isFile()) {
                                var nameFile = archivo.getName()
                                var BuffIn: BufferedInputStream?
                                BuffIn = BufferedInputStream(FileInputStream(path + "/" + nameFile))
                                mFTPClient.enterLocalPassiveMode()
                                mFTPClient.storeFile(nameFile, BuffIn)
                                BuffIn.close()
                                archivo.delete()
                                cantFile++

                            }
                        }
                    }
                    mFTPClient.logout()
                    mFTPClient.disconnect()
                }
            } catch (e: SocketException) {
                e.printStackTrace()
                cantFile = -1
            } catch (e: IOException) {
                e.printStackTrace()
                cantFile = -1
            }
            return cantFile
        }

        private fun enviar(nameF:String):Int {
            var cantFile = 0
            var nameFile: String
            var dir =  nameF+"s"
            if(nameF.equals("Almacen")) dir = nameF
            val remotoDir = "/" + dir
            var mFTPClient: FTPClient?
            val path = Environment.getExternalStorageDirectory().toString()+"/"+ dir
            try {
                mFTPClient = FTPClient()
                mFTPClient.connect(hostname, port)
                mFTPClient.login(user, "")
                if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                    mFTPClient.changeWorkingDirectory(remotoDir)
                    mFTPClient.setFileType(FTP.BINARY_FILE_TYPE)
                    val f = File(path)
                    if (f.exists()) {
                        val file = f.listFiles()
                        for (i in file.indices) {
                            val archivo = file[i]
                            if (archivo.isFile()) {
                                nameFile = archivo.getName()
                                if (nameFile.indexOf(nameF) != -1) {
                                    var BuffIn: BufferedInputStream?
                                    BuffIn = BufferedInputStream(FileInputStream(path + "/" + nameFile))
                                    mFTPClient.enterLocalPassiveMode()
                                    mFTPClient.storeFile(nameFile, BuffIn)
                                    BuffIn.close()
                                    moverArchivo(path, archivo.getName(), path + "/Enviados")
                                    cantFile++
                                }
                            }
                        }
                    }
                    mFTPClient.logout()
                    mFTPClient.disconnect()
                }
            } catch (e: SocketException) {
                e.printStackTrace()
                 cantFile = -1
            } catch (e: IOException) {
                e.printStackTrace()
                cantFile = -1
            }
            return cantFile
        }

        private fun moverArchivo(inputPath: String, inputFile: String, outputPath: String) {
            var inp: InputStream?
            var out: OutputStream?
            try {
                //crear la carpeta de salida si no existe
                val dir = File(outputPath)
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                inp = FileInputStream(inputPath + "/" + inputFile)
                out = FileOutputStream(outputPath +"/" + inputFile)

                val buffer = ByteArray(1024)
                var read: Int = inp.read(buffer)
                while (read != -1) {
                    out.write(buffer, 0, read)
                    read = inp.read(buffer)
                }
                inp.close()
                inp = null
                // escribe el archivo de salida
                out.flush()
                out.close()
                out = null

                // elimina el archivo original file
                File(inputPath+"/"+inputFile).delete()
            } catch (fnfe1: FileNotFoundException) {
                // Log.e("tag", fnfe1.getMessage());
            } catch (e: Exception) {
                // Log.e("tag", e.getMessage());
            }
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
            if(result == false) tvEnvio?.text = "INTENTE ENVIAR NUEVAMENTE"
            else {
                tvEnvio?.text = "ENVIO EXITOSO"
                tvRta?.text = str_print
            }
            tvEnvio?.visibility = View.VISIBLE
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