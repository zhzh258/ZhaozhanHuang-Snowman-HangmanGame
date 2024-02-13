package com.example.hangmangame

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val TAG = "ViewModel"
const val CURRENT_TURN_STATE_KEY = "CURRENT_TURN_STATE_KEY"
const val CURRENT_SELECTION_STATE_KEY = "CURRENT_SELECTION_STATE_KEY"
const val CURRENT_HINT_STATE_KEY = "CURRENT_HINT_STATE_KEY"
const val PUZZLE_WORD_KEY = "PUZZLE_WORD_KEY"


class HangmanViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    var turnState : Int
        get() = savedStateHandle.get(CURRENT_TURN_STATE_KEY) ?: 0
        set(value) = savedStateHandle.set(CURRENT_TURN_STATE_KEY, value)

    var selectionState : MutableList<Boolean>
        get() = savedStateHandle.get(CURRENT_SELECTION_STATE_KEY) ?: MutableList<Boolean>(26) {false}
        set(value) = savedStateHandle.set(CURRENT_SELECTION_STATE_KEY, value)

    var hintState: Int
        get() = savedStateHandle.get(CURRENT_HINT_STATE_KEY) ?: 0
        set(value) = savedStateHandle.set(CURRENT_HINT_STATE_KEY, value)

    val puzzleWord: String
        get() = savedStateHandle.get(PUZZLE_WORD_KEY) ?: "WATERMELON"

    val maxHintTimes : Int = 3
    val maxTurns = 8

    fun init() {
        selectionState = MutableList<Boolean>(26) {false}
        hintState = 0
        turnState = 0
    }

    fun selectLetter(i: Int) {
        Log.d(TAG, "select letter ${i}")
        val updatedSelectionState = selectionState.toMutableList() // Create a copy of the current list
        updatedSelectionState[i] = true // Update the copy
        selectionState = updatedSelectionState
//        selectionState[i] = true
    }

    fun fetchCurrentPuzzleText(): String {
        return puzzleWord.map { c: Char ->
            if (selectionState[c.code - 'A'.code]) c else '_'
        }.joinToString(" ")
    }

    fun isCorrect(i: Int): Boolean {
        val c = (i + 'A'.code).toChar()
        return puzzleWord.contains(c)
    }

    fun fetchCurrentRemainingAnswer(): List<Int> {
        val res: MutableList<Int> = mutableListOf()
        for (i in 0..26-1) {
            if (puzzleWord.contains(i2c(i)) && !selectionState[i]){
                res.add(i)
            }
        }
        return res
    }

    fun fetchCurrentRemainingSelection(): List<Int> {
        val res: MutableList<Int> = mutableListOf()
        for (i in 0..26-1) {
            if (!selectionState[i]){
                res.add(i)
            }
        }
        return res
    }

    fun i2c(i: Int): Char {
        return (i + 'A'.code).toChar()
    }

    fun c2i(c: Char): Int {
        return c.code - 'A'.code
    }

    fun hasWon(): Boolean {
        return puzzleWord.all { c ->
            selectionState[c2i(c)]
        }
    }
}