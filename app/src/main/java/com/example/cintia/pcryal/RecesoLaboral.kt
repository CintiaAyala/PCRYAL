package com.example.cintia.pcryal

class RecesoLaboral {
    var  utility = Utility()
    var nameFile: String? = null
    var horayequipo: String? = null
    var idReceso: String? = null
    var descripcion: String? = null
    var hrInicio: String? = null
    var hrFin: String? = null

    constructor(nameFile : String, horayequipo: String, idReceso: String, hrInicio: String, hrFin: String) {
        this.nameFile = nameFile //name & extension ex. "Receso_154522_20200121083707.json"
        this.horayequipo = horayequipo
        this.idReceso = idReceso
        this.descripcion = utility.getStringFromJson(utility.leerArchivos("recesoslaborales.json"), "id", idReceso, "descripcion")!!
        this.hrInicio = hrInicio
        this.hrFin = hrFin
    }
}