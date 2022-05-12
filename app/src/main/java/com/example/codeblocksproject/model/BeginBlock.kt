package com.example.codeblocksproject.model

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.codeblocksproject.R

class BeginBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CustomView, ConstraintLayout(context, attrs) {
    override val isNestingPossible = false
    override var previousId = -1
    override var nextId = -1
    override val blockType = BlockTypes.BEGIN_BLOCK_TYPE
    override val blockView: View = LayoutInflater.from(context).inflate(R.layout.begin_cycle_block, this)
    override val pattern = "{"
    override var position = 0

    override fun blockToCode(): String {
        return pattern
    }

    override fun makeEditTextsDisabled() {
    }

}