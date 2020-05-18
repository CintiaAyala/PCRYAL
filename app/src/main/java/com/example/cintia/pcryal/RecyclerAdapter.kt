package com.example.cintia.pcryal

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

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
            nombreFuncion.text = funciones.nombreFuncion
            descripcion.text = funciones.descripcion
            acceso.text = funciones.acceso
            imgFuncion.loadUrl(funciones.path)
            itemView.setOnClickListener(View.OnClickListener {
                if(funciones.acceso == "Acceso Permitido"){
                    val intentActividad = Intent(context, Jornada::class.java)
                    context.startActivity(intentActividad)
                }


                Toast.makeText(context, funciones.nombreFuncion, Toast.LENGTH_SHORT).show()
            })
        }
        fun ImageView.loadUrl(url: String) {
            var d = url;
            val resId = getResources().getIdentifier(d,"drawable", context.packageName);
            this.setBackgroundResource(resId);
        }
    }
}