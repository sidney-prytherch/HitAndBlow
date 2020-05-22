package com.sid.app.hitandblow.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.sid.app.hitandblow.R
import com.sid.app.hitandblow.helper.AnswerParcel
import kotlinx.android.synthetic.main.results_fragment.view.*

class ResultsFragment: DialogFragment() {
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.results_fragment, null)
            val answerParcel = requireArguments().get("answerParcel") as AnswerParcel
            view.colorOne.setBackgroundResource(PlayFragment.unselectedColorBackground[answerParcel.answer[0]])
            view.colorTwo.setBackgroundResource(PlayFragment.unselectedColorBackground[answerParcel.answer[1]])
            view.colorThree.setBackgroundResource(PlayFragment.unselectedColorBackground[answerParcel.answer[2]])
            view.colorFour.setBackgroundResource(PlayFragment.unselectedColorBackground[answerParcel.answer[3]])
            view.results.text = if (answerParcel.won) getString(R.string.you_win) else getString(R.string.you_lose)
            builder.setTitle(R.string.results)
                .setView(view)
                .setNegativeButton(R.string.view_guesses) { _, _ ->
                    dialog?.cancel()
                }
                .setPositiveButton(R.string.play_again) { _, _ ->
                    (parentFragmentManager.primaryNavigationFragment!! as PlayFragment).restartGame()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}