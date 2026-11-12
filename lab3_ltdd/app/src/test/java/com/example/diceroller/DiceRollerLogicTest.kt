package com.example.diceroller

import org.junit.Assert.assertTrue
import org.junit.Test

class DiceRollerLogicTest {

    @Test
    fun diceRoll_generatesNumberWithinRange() {
        repeat(500) {
            val result = (1..6).random()
            assertTrue(result in 1..6)
        }
    }
}

