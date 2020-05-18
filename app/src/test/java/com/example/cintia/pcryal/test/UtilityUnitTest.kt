package com.example.cintia.pcryal

import org.junit.Test

import org.junit.Assert.*

class UtilityUnitTest {
    var utility = Utility()
    @Test
    fun validDates() {
        assertTrue(utility.validDates("07:00", "17:20"))
    }
    @Test
    fun invalidDates() {
        assertFalse(utility.validDates("09:00", "08:20"))
    }
    @Test
    fun parNro(){
        assertTrue(utility.par(4))
    }
}