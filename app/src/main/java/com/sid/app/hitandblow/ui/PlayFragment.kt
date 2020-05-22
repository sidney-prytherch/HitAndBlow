package com.sid.app.hitandblow.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.sid.app.hitandblow.MainActivity
import com.sid.app.hitandblow.R
import com.sid.app.hitandblow.helper.AnswerParcel
import kotlinx.android.synthetic.main.fragment_play.view.*
import kotlinx.android.synthetic.main.guess_row.view.*
import kotlin.math.min

class PlayFragment : Fragment() {

    private lateinit var mainView: View
    private var solution = intArrayOf()
    private lateinit var mContext: MainActivity
    private lateinit var colorIsEnabledArray: BooleanArray
    private lateinit var colorViewArray: Array<Button>
    private lateinit var guessesLayouts: Array<ConstraintLayout>
    private lateinit var buttonGroups: Array<ButtonGroup>
    private var currentButtonIndex = 0
    private var currentButtonGroupIndex = 0
    private var isInResultsScreen = false
    private var won = false


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mContext = context as MainActivity
        } catch (e: ClassCastException) {
            throw e
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mainView = inflater.inflate(R.layout.fragment_play, container, false)
        setHasOptionsMenu(true)

        // get options
        colorIsEnabledArray = mContext.getColorsEnabledArray()
        val guessLimitOption = when (mContext.getGuessLimitOption()) {
            0 -> 4
            1 -> 5
            2 -> 6
            3 -> 7
            4 -> 8
            5 -> 9
            else -> 10
        }

        // get relevant views
        val allRows = arrayOf(
            mainView.row1 as ConstraintLayout,
            mainView.row2 as ConstraintLayout,
            mainView.row3 as ConstraintLayout,
            mainView.row4 as ConstraintLayout,
            mainView.row5 as ConstraintLayout,
            mainView.row6 as ConstraintLayout,
            mainView.row7 as ConstraintLayout,
            mainView.row8 as ConstraintLayout,
            mainView.row9 as ConstraintLayout,
            mainView.row10 as ConstraintLayout
        )

        // If there are not enough colors, display the color error text and return
        if (colorIsEnabledArray.filter { it }.size < 4) {
            for (element in allRows) {
                element.visibility = View.GONE
            }
            mainView.buttonLayout.visibility = View.GONE
            mainView.colorOptions.visibility = View.GONE
            mainView.colorError.visibility = View.VISIBLE
            return mainView
        }

        // set up the guess layout rows and create the array of ButtonGroups
        guessesLayouts = Array(guessLimitOption){ ConstraintLayout(context) }
        mainView.guessesRows.weightSum = guessLimitOption.toFloat()
        buttonGroups = Array(guessLimitOption) {i ->
            guessesLayouts[i] = allRows[i]
            val numberString = "${(i + 1)}."
            guessesLayouts[i].guessNumber.text = numberString

            // create the buttonGroups for the current row
            val buttonGroup = ButtonGroup(
                guessesLayouts[i].guessOneButton,
                guessesLayouts[i].guessTwoButton,
                guessesLayouts[i].guessThreeButton,
                guessesLayouts[i].guessFourButton,
                guessesLayouts[i].hit,
                guessesLayouts[i].blow,
                i
            )

            // create the buttons for row's buttonGroup
            buttonGroup.buttons.forEach { buttonNode ->
                buttonNode.button.setOnClickListener {
                    if (!isInResultsScreen && buttonGroup.groupIndex == currentButtonGroupIndex) {
                        buttonGroup.buttons[currentButtonIndex].setIsFocused(false)
                        currentButtonIndex = buttonNode.buttonIndex
                        buttonNode.setIsFocused(true)
                    }
                }
            }

            buttonGroup
        }

        // remove/hide the unused guess rows
        for (i in guessLimitOption until allRows.size) {
            allRows[i].visibility = View.GONE
        }

        // set up the color buttons
        colorViewArray = arrayOf(
            mainView.red,
            mainView.orange,
            mainView.yellow,
            mainView.green,
            mainView.bluegreen,
            mainView.lightblue,
            mainView.darkblue,
            mainView.purple,
            mainView.pink
        )

        // remove unused color buttons, and take the remaining colors, shuffle them, and take the first 4 as the solution
        solution = colorIsEnabledArray.mapIndexed { i, colorIsEnabled ->
            if (!colorIsEnabled) {
                colorViewArray[i].visibility = View.GONE
                -1
            } else {
                colorViewArray[i].setOnClickListener {
                    if (!isInResultsScreen) {
                        currentButtonIndex = buttonGroups[currentButtonGroupIndex].setColor(currentButtonIndex, i)
                    }
                }
                i
            }
        }.filter { it > -1 }.shuffled().take(4).toIntArray()

        mainView.viewResults.setOnClickListener {
            val answerParcel = AnswerParcel(solution, won)
            val bundle = bundleOf("answerParcel" to answerParcel)
            Navigation.findNavController(mainView).navigate(R.id.action_play_to_results, bundle)
        }

        mainView.restart.setOnClickListener {
            restartGame()
        }

        // set up the guess button
        mainView.makeGuess.setOnClickListener {
            val guess = buttonGroups[currentButtonGroupIndex].getCode()
            // only move on if the guess is complete - all 4 places must have a color
            if (guess != null) {
                // calculate the hit and blow count and display it
                val hit = buttonGroups[currentButtonGroupIndex].setHitAndBlow(guess, solution)
                // remove the focus for the button of the buttonGroup
                buttonGroups[currentButtonGroupIndex].buttons[currentButtonIndex].setIsFocused(false)

                // if there are 4 hits, HOORAY!
                if (hit == 4) {
                    won = true
                    goToResults()
                    displayResults()
                    return@setOnClickListener
                }
                // move to the next group. If there is no next group, they lose
                currentButtonGroupIndex++
                currentButtonIndex = 0
                if (currentButtonGroupIndex >= buttonGroups.size) {
                    goToResults()
                    displayResults()
                } else {
                    buttonGroups[currentButtonGroupIndex].buttons[currentButtonIndex].setIsFocused(true)
                }
            }
        }

        buttonGroups[currentButtonGroupIndex].buttons[currentButtonIndex].setIsFocused(true)

        return mainView
    }

    private fun displayResults() {
        val answerParcel = AnswerParcel(solution, won)
        val bundle = bundleOf("answerParcel" to answerParcel)
        Navigation.findNavController(mainView).navigate(R.id.action_play_to_results, bundle)
    }

    private fun goToResults() {
        isInResultsScreen = true
        mainView.restart.visibility = View.VISIBLE
        mainView.viewResults.visibility = View.VISIBLE
        mainView.makeGuess.visibility = View.GONE
    }

    fun restartGame() {
        won = false
        isInResultsScreen = false
        mainView.restart.visibility = View.GONE
        mainView.viewResults.visibility = View.GONE
        mainView.makeGuess.visibility = View.VISIBLE
        currentButtonIndex = 0
        currentButtonGroupIndex = 0
        buttonGroups.forEach { buttonGroup ->
            buttonGroup.reset()
        }
        buttonGroups[currentButtonGroupIndex].buttons[currentButtonIndex].setIsFocused(true)
        solution = colorIsEnabledArray.mapIndexed { i, colorIsEnabled ->
            if (!colorIsEnabled) {
                colorViewArray[i].visibility = View.GONE
                -1
            } else {
                colorViewArray[i].setOnClickListener {
                    currentButtonIndex = buttonGroups[currentButtonGroupIndex].setColor(currentButtonIndex, i)
                }
                i
            }
        }.filter { it > -1 }.shuffled().take(4).toIntArray()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.play_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.howToPlay) {
            Navigation.findNavController(mainView).navigate(R.id.action_play_to_how_to_play)
            true
        } else super.onOptionsItemSelected(item)

    }

    class ButtonGroup(view1: View, view2: View, view3: View, view4: View, private val hitView: TextView, private val blowView: TextView, val groupIndex: Int) {
        val buttons = arrayOf(
            ButtonNode(view1, 0),
            ButtonNode(view2, 1),
            ButtonNode(view3, 2),
            ButtonNode(view4, 3)
        )
        fun setColor(currentIndex: Int, colorIndex: Int): Int {
            var nextIndex = currentIndex
            for (index in 0 until 4) {
                if (buttons[index].getColor() == colorIndex) {
                    buttons[index].resetColor()
                }
                if (index > currentIndex && nextIndex == currentIndex && !buttons[index].isColored()) {
                    nextIndex = index
                }
            }
            nextIndex = if (nextIndex != currentIndex) nextIndex else min(currentIndex + 1, 3)
            if (currentIndex != nextIndex) {
                buttons[currentIndex].setColorAndUnsetFocused(colorIndex)
                buttons[nextIndex].setIsFocused(true)
            } else {
                buttons[currentIndex].setColor(colorIndex)
            }
            return nextIndex
        }
        private fun resetColors() {
            for (button in buttons) {
                button.resetColor()
            }
        }
        fun getCode(): IntArray? {
            return buttons.map {
                val color = it.getColor()
                if (color == 9) {
                    return null
                }
                color
            }.toIntArray()
        }
        private fun resetHitAndBlow() {
            hitView.text = ""
            blowView.text = ""
        }
        fun setHitAndBlow(guess: IntArray, solution: IntArray): Int {
            var blow = 0
            var hit = 0
            guess.forEachIndexed { i, guessedColor ->
                if (guessedColor == solution[i]) {
                    hit++
                } else if (solution.contains(guessedColor)) {
                    blow++
                }
            }
            val hitText = """Hit: $hit"""
            val blowText = """Blow: $blow"""
            hitView.text = hitText
            blowView.text = blowText
            return hit
        }
        fun reset() {
            resetColors()
            resetHitAndBlow()
        }
        fun setRowColors(colors: IntArray) {
            buttons.forEachIndexed { i, buttonNode ->
                buttonNode.setColor(colors[i])
            }
        }
    }

    class ButtonNode(val button: View, val buttonIndex: Int) {
        private var isFocused = false
        private var colorIndex = 9
        private val unselectedColorIndex = 9

        fun isColored():Boolean {
            return colorIndex != 9
        }

        fun setColorAndUnsetFocused(index: Int) {
            isFocused = false
            setColor(index)
        }

        fun setColor(index: Int) {
            colorIndex = index
            setColor()
        }

        fun getColor():Int {
            return colorIndex
        }

        fun resetColor() {
            colorIndex = unselectedColorIndex
            setColor()
        }

        private fun setColor() {
            button.setBackgroundResource(
                if (isFocused) selectedColorBackground[colorIndex] else unselectedColorBackground[colorIndex]
            )
        }

        fun setIsFocused(focused: Boolean) {
            isFocused = focused
            setColor()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (solution.isNotEmpty()){
            outState.putIntArray("solution", solution)
            outState.putInt("currentButtonGroupIndex", currentButtonGroupIndex)
            outState.putInt("currentButtonIndex", currentButtonIndex)
            outState.putBoolean("isInResultsScreen", isInResultsScreen)
            buttonGroups.forEachIndexed { i, buttonGroup ->
                outState.putIntArray("""buttonGroup$i""", buttonGroup.getCode() ?: intArrayOf(9,9,9,9))
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            buttonGroups[currentButtonGroupIndex].buttons[currentButtonIndex].setIsFocused(false)
            currentButtonGroupIndex = savedInstanceState.getInt("currentButtonGroupIndex")
            currentButtonIndex = savedInstanceState.getInt("currentButtonIndex")
            if (currentButtonIndex < 4 && currentButtonGroupIndex < buttonGroups.size) {
                buttonGroups[currentButtonGroupIndex].buttons[currentButtonIndex].setIsFocused(true)
            }
            solution = savedInstanceState.getIntArray("solution") ?: solution
            isInResultsScreen = savedInstanceState.getBoolean("isInResultsScreen")
            buttonGroups.forEachIndexed { i, buttonGroup ->
                val colors = savedInstanceState.getIntArray("""buttonGroup$i""") ?: intArrayOf(9,9,9,9)
                buttonGroup.setRowColors(colors)
                if (i < currentButtonGroupIndex || (isInResultsScreen && i == currentButtonGroupIndex)) {
                    buttonGroup.setHitAndBlow(colors, solution)
                }
            }
            if (isInResultsScreen) {
                goToResults()
            }
        }
    }

    companion object {

        val unselectedColorBackground = arrayOf(
            R.drawable.color_red,
            R.drawable.color_orange,
            R.drawable.color_yellow,
            R.drawable.color_green,
            R.drawable.color_bluegreen,
            R.drawable.color_lightblue,
            R.drawable.color_darkblue,
            R.drawable.color_purple,
            R.drawable.color_pink,
            R.drawable.color_blank
        )

        val selectedColorBackground = arrayOf(
            R.drawable.selected_color_red,
            R.drawable.selected_color_orange,
            R.drawable.selected_color_yellow,
            R.drawable.selected_color_green,
            R.drawable.selected_color_bluegreen,
            R.drawable.selected_color_lightblue,
            R.drawable.selected_color_darkblue,
            R.drawable.selected_color_purple,
            R.drawable.selected_color_pink,
            R.drawable.selected_color_blank
        )
    }
}
