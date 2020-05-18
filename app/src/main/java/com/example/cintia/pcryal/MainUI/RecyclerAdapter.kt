package com.example.cintia.pcryal

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.*

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    var funciones: MutableList<ListaFuncionalidades>  = ArrayList()
    lateinit var context:Context


    fun RecyclerAdapter(funciones : MutableList<ListaFuncionalidades>, context: Context){
        this.funciones = funciones
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = funciones.get(position)
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.layout_funcionalidades, parent, false))
    }

    override fun getItemCount(): Int {
        return funciones.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreFuncion = view.findViewById(R.id.tvNombreFuncion) as TextView
        val descripcion = view.findViewById(R.id.tvDescripcion) as TextView
        val acceso = view.findViewById(R.id.tvAcceso) as TextView
        val imgFuncion = view.findViewById(R.id.ivFuncion) as ImageView

        fun bind(funciones:ListaFuncionalidades, context: Context){
            var jornada = Jornada()
            nombreFuncion.text = funciones.nombreFuncion
            descripcion.text = funciones.descripcion
            acceso.text = funciones.acceso
            val openActiv = funciones.nombreActividad
            imgFuncion.loadUrl(funciones.path)
            itemView.setOnClickListener({
                if(funciones.acceso == "Acceso Concedido"){
                    when(openActiv){
                        "Jornada" -> context.startActivity(Intent(context, Jornada::class.java))
                        "DescargarNovedades" -> context.startActivity(Intent(context, DescargarNovedades::class.java))
                        "EnviarNovedades" -> context.startActivity(Intent(context, EnviarNovedades::class.java))
                        "MenuOt" -> context.startActivity(Intent(context, MenuOt::class.java))
                        "RecesosLaborales" -> context.startActivity(Intent(context, RecesosLaborales::class.java))
                        "TareasAlmacen" -> context.startActivity(Intent(context, TareasAlmacen::class.java))
                        "FinalizarJornada" -> jornada.alertFinJornada(context)
                    }
                }
                Toast.makeText(context, funciones.acceso, Toast.LENGTH_SHORT).show()
            })
        }

        fun ImageView.loadUrl(url: String) {
            var d = url;
            val resId = getResources().getIdentifier(d,"drawable", context.packageName);
            this.setBackgroundResource(resId);
        }
    }
}