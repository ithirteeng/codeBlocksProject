package com.example.codeblocksproject.model

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.example.codeblocksproject.R

class AssignmentBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CustomView, LinearLayout(context, attrs) {
    private val view =
        LayoutInflater.from(context).inflate(R.layout.block_assignment, this).apply {
            val valueEdit: EditText = findViewById(R.id.varValue)
            val nameEdit: EditText = findViewById(R.id.varName)
            val valueText: TextView = findViewById(R.id.valueText)
            val nameText: TextView = findViewById(R.id.nameText)

            toEditText(valueText, valueEdit, context)
            toEditText(nameText, nameEdit, context)
            toTextView(valueEdit, valueText)
            toTextView(nameEdit, nameText)

        }

    override val isNestingPossible = false
    override var previousId: Int = -1
    override var nextId: Int = -1
    override val blockView: View = view
    override val blockType = BlockTypes.INPUT_BLOCK_TYPE
    override val pattern = "<name> = <value>;"
    override var position = 0

    override fun blockToCode(): String {
        val varName = blockView.findViewById<EditText>(R.id.varName).text.toString()
        val varValue = blockView.findViewById<EditText>(R.id.varValue).text.toString()

        return pattern.replace("<name>", varName).replace("<value>", varValue)
    }

    override fun makeEditTextsDisabled() {
        val valueEdit: EditText = view.findViewById(R.id.varValue)
        val nameEdit: EditText = view.findViewById(R.id.varName)
        val valueText: TextView = view.findViewById(R.id.valueText)
        val nameText: TextView = view.findViewById(R.id.nameText)
        convertEditTextToTextView(valueText, valueEdit)
        convertEditTextToTextView(nameText, nameEdit)
    }

    override fun ifTextViewEmpty(): Boolean {
        val valueText: TextView = view.findViewById(R.id.valueText)
        val nameText: TextView = view.findViewById(R.id.nameText)

        return valueText.text.isEmpty() || nameText.text.isEmpty()
    }

    override fun content(): ArrayList<String> {
        val valueText: TextView = findViewById(R.id.valueText)
        val nameText: TextView = findViewById(R.id.nameText)
        return arrayListOf(nameText.text.toString(), valueText.text.toString())
    }

    init {
        blockView.setBackgroundResource(R.drawable.assignment_block_background)
        blockView.setPadding(
            context.resources.getDimensionPixelOffset(R.dimen.startAndEndBlockPadding),
            context.resources.getDimensionPixelOffset(R.dimen.topAndBottomBlockPadding),
            context.resources.getDimensionPixelOffset(R.dimen.startAndEndBlockPadding),
            context.resources.getDimensionPixelOffset(R.dimen.topAndBottomBlockPadding)
        )
    }
}