
package com.example.words.game

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.words.R
import com.example.words.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class GameFragment : Fragment() {

    private val viewModel: GameViewModel by viewModels()
    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        Log.d("GameFragment", "GameFragment created/re-created!")
        Log.d("GameFragment", "Word: ${viewModel.currentScrambledWord} " +
                "Score: ${viewModel.score} WordCount: ${viewModel.currentWordCount}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gameViewModel = viewModel

        binding.maxNoOfWords = MAX_NO_OF_WORDS

        binding.lifecycleOwner = viewLifecycleOwner

        binding.submit.setOnClickListener {
            onSubmitWord()
        }

        binding.skip.setOnClickListener {
            onSkipWord()
        }

        //при клике вне editText снимется фокус с editText и сворачивается клавиатура
        binding.scrollView.setOnTouchListener { _, _ ->
            if (activity?.currentFocus is TextInputEditText) {
                hideKeyboard()
                activity?.currentFocus?.clearFocus()
            }
            return@setOnTouchListener false
        }

    }

    private fun hideKeyboard() {
        val inputMethodManager= activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            activity?.currentFocus?.windowToken, 0
        )
    }

    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext()) //создаем диалоговое окно
            .setTitle(getString(R.string.congratulations)) //устанавливаем заголовок
            .setMessage(getString(R.string.you_scored, viewModel.score.value)) //устанавливаем сообщение
            .setCancelable(false) //окно невозможно отменить при клике по "Назад"
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                exitGame()
            }
            .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                restartGame()
            }
            .show()
    }

    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()

        if (viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if (!viewModel.nextWord()) {
                showFinalScoreDialog()
            }
        }
        else {
            setErrorTextField(true)
        }
    }

    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextField(false)
        }
        else {
            showFinalScoreDialog()
        }
    }

    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
    }

    private fun exitGame() {
        activity?.finish()
    }

    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }

}
