package com.example.cintia.pcryal

class TareaAlmacen {
    var  utility = Utility()
    var nameFile: String? = null
    var horayequipo: String? = null
    var idTarea: String? = null
    var descripcion: String? = null
    var hrInicio: String? = null
    var hrFin: String? = null
    var materiales: String? = null

    constructor(nameFile : String, horayequipo: String, idTarea: String, hrInicio: String, hrFin: String, materiales : String) {
        this.nameFile = nameFile //name & extension ex. "Almacen_154522_20200121083707.json"
        this.horayequipo = horayequipo
        this.idTarea = idTarea
        this.descripcion = utility.getStringFromJson(utility.leerArchivos("tareasalmacen.json"), "id", idTarea, "descripcion")!!
        this.hrInicio = hrInicio
        this.hrFin = hrFin
        this.materiales = materiales
    }
}