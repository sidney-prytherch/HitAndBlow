package com.sid.app.hitandblow.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.fragment.app.Fragment
import com.sid.app.hitandblow.MainActivity
import com.sid.app.hitandblow.R
import com.sid.app.hitandblow.helper.PreferenceHelper
import kotlinx.android.synthetic.main.fragment_options.view.*

class OptionsFragment : Fragment() {

    private lateinit var mContext: MainActivity
    private lateinit var buttonArray: Array<Button>
    private lateinit var colorArray: IntArray
    private lateinit var colorPreferencesArray: Array<String>
    private lateinit var colorIsEnabledArray: BooleanArray

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mContext = context as MainActivity
        } catch (e: ClassCastException) {
            throw e
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_options, container, false)

        colorIsEnabledArray = mContext.getColorsEnabledArray()
        val guessLimitOption = mContext.getGuessLimitOption()

        val guessSpinner = view.guessSpinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.guess_limit_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            guessSpinner.adapter = adapter
        }
        guessSpinner.setSelection(guessLimitOption)
        guessSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mContext.setGuessLimitOption(position)
            }
        }

        colorPreferencesArray = arrayOf(
            PreferenceHelper.RED_ENABLED,
            PreferenceHelper.ORANGE_ENABLED,
            PreferenceHelper.YELLOW_ENABLED,
            PreferenceHelper.GREEN_ENABLED,
            PreferenceHelper.BLUEGREEN_ENABLED,
            PreferenceHelper.LIGHTBLUE_ENABLED,
            PreferenceHelper.DARKBLUE_ENABLED,
            PreferenceHelper.PURPLE_ENABLED,
            PreferenceHelper.PINK_ENABLED
        )

        buttonArray = arrayOf(
            view.redButton,
            view.orangeButton,
            view.yellowButton,
            view.greenButton,
            view.bluegreenButton,
            view.lightblueButton,
            view.darkblueButton,
            view.purpleButton,
            view.pinkButton
        )

        colorArray = intArrayOf(
            R.color.red,
            R.color.orange,
            R.color.yellow,
            R.color.green,
            R.color.bluegreen,
            R.color.lightblue,
            R.color.darkblue,
            R.color.purple,
            R.color.pink
        )

        for (i in 0 until 9) {
            setUpButton(i)
        }

        colorIsEnabledArray.forEachIndexed { i, isEnabled ->
            if (isEnabled) {
                buttonArray[i].setBackgroundResource(R.color.transparent)
            } else {
                buttonArray[i].setBackgroundResource(colorArray[i])
            }
        }

        return view
    }

    private fun setUpButton(index: Int) {
        buttonArray[index].setOnClickListener {
            if (colorIsEnabledArray[index]){
                it.setBackgroundResource(colorArray[index])
                setButtonBooleans(index, false)
            } else {
                it.setBackgroundResource(R.color.transparent)
                setButtonBooleans(index, true)
            }
        }
    }

    private fun setButtonBooleans(index: Int, isEnabled: Boolean) {
        colorIsEnabledArray[index] = isEnabled
        mContext.setColorBoolean(colorPreferencesArray[index], isEnabled)
    }
}
