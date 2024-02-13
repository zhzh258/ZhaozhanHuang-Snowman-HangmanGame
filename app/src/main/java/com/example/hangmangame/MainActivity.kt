package com.example.hangmangame

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.viewModels
import com.example.hangmangame.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val hangmanViewModel: HangmanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpLetterButton()
        setUpNewGameButton()
        setUpHintButton()

        updatePuzzleTextView()
        updateImageView()
        updateHintTextView() // make sure that the hint persists when I rotate from portrait back to land
        updateHintButton()
    }

    private fun setUpLetterButton() {
        val letters = ('A'..'Z')
        letters.forEachIndexed { i, letter ->
            val button = Button(this).apply {
                text = letter.toString()
                layoutParams = GridLayout.LayoutParams().apply {
                    setMargins(2, 2, 2, 2)

                    width = GridLayout.LayoutParams.WRAP_CONTENT
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    textSize = 8f
                }
                setBackgroundColor(Color.GRAY)
                setOnClickListener { view : View ->
                    if (hangmanViewModel.turnState >= hangmanViewModel.maxTurns - 1) {
                        Toast.makeText(this@MainActivity, R.string.game_over, Toast.LENGTH_SHORT).show()
                    } else {
                        hangmanViewModel.selectLetter(i)
                        if (!hangmanViewModel.isCorrect(i)) {
                            hangmanViewModel.turnState ++
                            if (hangmanViewModel.turnState == hangmanViewModel.maxTurns) {
                                Toast.makeText(this@MainActivity, R.string.game_over, Toast.LENGTH_SHORT).show()
                            }
                        }
                        updateImageView()
                        updatePuzzleTextView()
                        updateLetterButton()

                        if (hangmanViewModel.hasWon()) { // the last letter has been found
                            // Disable the hint button
                            binding.hintButton?.isEnabled = false
                            binding.hintButton?.setBackgroundColor(Color.GRAY)
                            // Toast
                            Toast.makeText(this@MainActivity, R.string.gg, Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
            binding.letterGrid.addView(button)
        }
    }

    private fun updateLetterButton() {
        hangmanViewModel.selectionState.forEachIndexed { i, selected ->
            if (selected) {
                val button = binding.letterGrid.getChildAt(i)
                button.isEnabled = false
                button.setBackgroundColor(Color.BLACK)
            } else {
                val button = binding.letterGrid.getChildAt(i)
                button.isEnabled = true
                button.setBackgroundColor(Color.GRAY)
            }
        }
    }

    private fun setUpNewGameButton() {
        binding.newGameButton.setOnClickListener { view: View ->
            hangmanViewModel.init()
            updateLetterButton()
            updateImageView()
            updatePuzzleTextView()
            updateHintTextView()
            updateHintButton()
        }
    }

    private fun setUpHintButton() {
        binding.hintButton?.setOnClickListener { view: View ->
            if (hangmanViewModel.turnState == hangmanViewModel.maxTurns - 1) {
                Toast.makeText(this, R.string.no_more_hint, Toast.LENGTH_SHORT).show()
            } else if (hangmanViewModel.hintState == 0) { // First click
                hangmanViewModel.hintState ++
                binding.hintTextView?.text = getText(R.string.hint)
            } else if (hangmanViewModel.hintState == 1) { // Second Click
                hangmanViewModel.hintState ++
                hangmanViewModel.turnState ++
                val availableIndices: List<Int> = hangmanViewModel.fetchCurrentRemainingSelection()
                val n = availableIndices.size
                availableIndices.filter { value ->
                    !hangmanViewModel.puzzleWord.contains(hangmanViewModel.i2c(value))
                }.slice(1..n/2).forEach { value ->
                    hangmanViewModel.selectLetter(value)
                    updateImageView()
                    updatePuzzleTextView()
                    updateLetterButton()
                }
            } else if (hangmanViewModel.hintState == 2) { // Third Click
                hangmanViewModel.hintState ++
                hangmanViewModel.turnState ++

                // Select the vowels
                val availableLetters: List<Int> = hangmanViewModel.fetchCurrentRemainingAnswer()
                Log.d(TAG, availableLetters.toString())
                availableLetters.filter { value ->
                    "AEIOU".contains(hangmanViewModel.i2c(value))
                }.forEach { value ->
                    hangmanViewModel.selectLetter(value)
                    updateImageView()
                    updatePuzzleTextView()
                    updateLetterButton()
                }

                // Disable the hint button
                binding.hintButton?.isEnabled = false
                binding.hintButton?.setBackgroundColor(Color.GRAY)

            }
        }
    }

    private fun updateImageView() {
        val resourceId = when (hangmanViewModel.turnState) {
            0 -> R.drawable.image_part_001
            1 -> R.drawable.image_part_002
            2 -> R.drawable.image_part_003
            3 -> R.drawable.image_part_004
            4 -> R.drawable.image_part_005
            5 -> R.drawable.image_part_006
            6 -> R.drawable.image_part_007
            else -> R.drawable.image_part_008
        }
        binding.hangmanImageView.setImageResource(resourceId)
    }

    private fun updatePuzzleTextView() {
        binding.puzzleTextView.text = hangmanViewModel.fetchCurrentPuzzleText()
    }

    private fun updateHintTextView() {
        if (hangmanViewModel.hintState == 0) {
            binding.hintTextView?.text = ""
        } else {
            binding.hintTextView?.text = getText(R.string.hint)
        }
    }

    private fun updateHintButton() {
        if (hangmanViewModel.hintState == 0) {
            binding.hintButton?.isEnabled = true
            binding.hintButton?.setBackgroundColor(Color.GREEN)
        } else if (hangmanViewModel.hintState == hangmanViewModel.maxHintTimes){
            binding.hintTextView?.isEnabled = false
            binding.hintButton?.setBackgroundColor(Color.DKGRAY)
        }
    }
}