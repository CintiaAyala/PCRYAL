package com.example.cintia.pcryal.test

import com.example.cintia.pcryal.TareaAlmacen
import com.example.cintia.pcryal.Utility
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class TareaAlamacenTest {

    @Before
    fun setup() {

        val utility = Mockito.mock(Utility::class.java)
        Mockito.`when`(utility.getStringFromJson(getFileAsString(),"id", "1", "descripcion")).thenReturn("DESCARGA ACEITE USADO")
    }

    @Test
    fun newRecesoLaboral() {
        val tarea = TareaAlmacen("Almacen_2200092_20200419215949.json", "2200092","2", "10:00", "13:00", "-")
        Assert.assertNotNull(tarea)
        Assert.assertEquals(tarea.descripcion, "DESCARGA ACEITE USADO")
        Assert.assertEquals(tarea.horayequipo,"2200092")
        Assert.assertEquals(tarea.hrInicio,"10:00")
    }

    fun getFileAsString():String{
        return "{ \"codApp\": \"20200419215949\", \"horayequipo\": \"2200092\", \"tarea\": \"2\", \"hrInicioTarea\": \"10:00\", \"hrFinTarea\": \"13:00\", \"materiales\": \"-\", \"observaciones\": \"-\"}"
    }
}

