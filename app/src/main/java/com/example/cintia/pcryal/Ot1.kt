package com.example.cintia.pcryal

import android.Manifest
import android.support.v4.app.Fragment
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_ot1.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.provider.MediaStore
import android.net.Uri
import android.os.Environment
import java.io.*
import android.support.v7.app.AlertDialog
import android.support.design.widget.FloatingActionButton
import android.widget.*
import org.json.JSONArray


class Ot1: Fragment(), AdapterView.OnItemLongClickListener {
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    var adaptador1: ArrayAdapter<String>? = null
    var cantFotos = 0
    var botonFotos: FloatingActionButton? = null
    var botonSend: FloatingActionButton? = null
    var spTipo: Spinner? = null
    var edMM: EditText? = null
    var edPozo: EditText? = null
    var edHrInicioOt: EditText? = null
    var edPresion : EditText? = null
    var edTemp: EditText? = null
    var edGolpes: EditText? = null
    var edPGC: EditText? = null
    var lvImagenes: ListView? = null
    var arrListFotos = ArrayList<String>()
    var TipoOt: String = ""
    val categTipoOt = ArrayList<Utility.StringWithTag>()
    var dataAdapterD: ArrayAdapter<Utility.StringWithTag>? = null
    var utility = Utility()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view: View = inflater!!.inflate(R.layout.fragment_ot1, container, false)

            lvImagenes = view.findViewById(R.id.lvFotos) as ListView
            edMM = view.findViewById(R.id.edMM) as EditText
            edPozo =  view.findViewById(R.id.edPozo) as EditText
            edHrInicioOt = view.findViewById(R.id.edHrInicioOt) as EditText
            edPresion = view.findViewById(R.id.edPresion) as EditText
            edTemp =  view.findViewById(R.id.edTemp) as EditText
            edGolpes = view.findViewById(R.id.edGolpes) as EditText
            edPGC = view.findViewById(R.id.edPGC) as EditText
            botonFotos = view.findViewById(R.id.btnFotos) as FloatingActionButton
            botonSend = view.findViewById(R.id.btnOk) as FloatingActionButton
            spTipo = view.findViewById(R.id.spTipoOt) as Spinner
            TipoOt = utility.leerArchivos("tipoot.json")
            edMM!!.setText((activity as OrdenTrabajo).getScannedMM())

            setTipoOtSpinner()
            edHrInicioOt!!.setText(utility.getHora())
            utility.getTime(edHrInicioOt!!,context)
            adaptador1= ArrayAdapter(context, android.R.layout.simple_list_item_1, arrListFotos)
            lvImagenes?.adapter = adaptador1
            lvImagenes?.onItemLongClickListener = this
            botonFotos!!.setOnClickListener{
                verifyStoragePermissions()
                seleccionarImagen()
            }

            botonSend!!.setOnClickListener{
               recuperarInfoOt1()
               (activity as OrdenTrabajo).selectIndex(1)
            }

        return view
    }

    fun setTipoOtSpinner(){
        val jsonArrayTipoOt = JSONArray(TipoOt)
        categTipoOt.add(utility.StringWithTag("-","null"))
        for (i in 0 until jsonArrayTipoOt.length()) {
            val row = jsonArrayTipoOt.getJSONObject(i)
            var nomTipo = row.getString("descripcion")
            var idTipo = row.getString("id")
            categTipoOt.add(utility.StringWithTag(nomTipo, idTipo))
        }
        dataAdapterD =  ArrayAdapter(context, android.R.layout.simple_spinner_item, categTipoOt)
        dataAdapterD!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTipo!!.adapter = dataAdapterD
    }

    override fun onItemLongClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long): Boolean {
        var posicion=p2

        var dialogo1: AlertDialog.Builder =  AlertDialog.Builder(context, R.style.Custom)
        dialogo1.setTitle("Importante")
        dialogo1.setMessage("¿ Eliminar esta foto ?")
        dialogo1.setCancelable(false)
        dialogo1.setPositiveButton("Confirmar",  {dialogo1, id ->
                eliminarImagen(posicion)
        })
        dialogo1.setNegativeButton("Cancelar", {dialogo1, id-> {
            }
        })
        dialogo1.show()

        return false
    }

    fun eliminarImagenes(){
        for (item in arrListFotos) {
            eliminarImagenxNombre(item)
        }
    }

    private fun eliminarImagenxNombre(name: String){
        val SDCardRoot = Environment.getExternalStorageDirectory().toString()+ "/imgPDM"
        val f = File(SDCardRoot)
        if(f.exists()) {
            val file = f.listFiles()
            for (i in file.indices) {
                val archivo = file[i]
                if(archivo.isFile() && archivo.getName().equals(name)) {
                    archivo.delete()
                    break
                }
            }
        }
    }

    fun eliminarImagen(posImg: Int){
        val SDCardRoot = Environment.getExternalStorageDirectory().toString()+ "/imgPDM"
        val f = File(SDCardRoot)
        if(f.exists()) {
           var file = f.listFiles()
            for(i in 0 until file.size){
                var archivo = file[i]
                if(archivo.isFile){
                    var name = arrListFotos.get(posImg)
                    if(archivo.name == name) archivo.delete()
                }
            }
            arrListFotos.removeAt(posImg)
            adaptador1!!.notifyDataSetChanged ()
        }
    }

    fun verifyStoragePermissions(){
        val permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    private fun seleccionarImagen() {
        //  final CharSequence[] options = { "Camara", "Galeria","Cancelar" };
        val options = arrayOf<CharSequence>("Camara", "Cancelar")

        val builder = android.app.AlertDialog.Builder(context, R.style.Custom)
        builder.setTitle("Tomar una Foto")
        builder.setItems(options) { dialog, item ->
            if(options[item] == "Camara") {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val f = File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg")
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f))
                startActivityForResult(intent, 1)
            } else if(options[item] == "Cancelar") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    fun recuperarInfoOt1() {
        var idSelectTipo = categTipoOt.get(spTipo!!.selectedItemId.toInt()).getid()
        var mm = edMM!!.text.toString()
        var pozo = edPozo!!.text.toString()
        var hrInicioOt = edHrInicioOt!!.text.toString()
        var presion = edPresion!!.text.toString()
        var temperatura = edTemp!!.text.toString()
        var golpesXmin = edGolpes!!.text.toString()
        var presionGas = edPGC!!.text.toString()
        var strFotos = ""
        for(item in arrListFotos)
            strFotos = strFotos + item + "|"

        val arrValues = arrayOf(idSelectTipo,mm,pozo,hrInicioOt,presion, temperatura, golpesXmin,presionGas,strFotos)
        val TabOfFragmentB = (activity as OrdenTrabajo).getTabFragmentB()
        val fragmentB = activity
                .supportFragmentManager
                .findFragmentByTag(TabOfFragmentB) as Ot2;

        fragmentB.b_updateText(arrValues)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK) {
            if(requestCode == 1) {
                var f = File(Environment.getExternalStorageDirectory().toString())
                for (temp in f.listFiles()) {
                    if(temp.name.equals("temp.jpg")) {
                        f = temp
                        break
                    }
                }
                try {
                    val bitmap: Bitmap
                    val bitmapOptions = BitmapFactory.Options()

                    bitmap = BitmapFactory.decodeFile(f.absolutePath,
                            bitmapOptions)

                    val path  = Environment.getExternalStorageDirectory().toString()+"/"+ "imgPDM"
                    //  + "Cam" + File.separator + "default";
                    val ff = File(path)

                    if(!ff.isDirectory) {
                        val newFolder = "/imgPDM" //nombre de la Carpeta que vamos a crear
                        val extStorageDirectory = Environment.getExternalStorageDirectory().toString()
                        val myNewFolder = File(extStorageDirectory + newFolder)
                        myNewFolder.mkdir() //creamos la carpeta
                    }
                    f.delete()
                    var outFile: OutputStream?
                    val file = File(path, System.currentTimeMillis().toString() + ".jpg")

                    //name = path + "/" + file.getName();
                    val name = file.name
                    arrListFotos.add(name)
                    adaptador1!!.notifyDataSetChanged()

                    try {
                        outFile = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outFile)
                        outFile.flush()
                        outFile.close()
                        cantFotos++
                        if(cantFotos > 3) {
                            botonFotos!!.isEnabled = false
                        }
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }
}