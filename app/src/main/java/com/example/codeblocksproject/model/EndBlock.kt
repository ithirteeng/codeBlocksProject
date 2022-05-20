package com.example.codeblocksproject.model

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.codeblocksproject.R

class EndBlock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CustomView, ConstraintLayout(context, attrs) {
    override val isNestingPossible = false
    override var previousId = -1
    override var nextId = -1
    override val blockType = BlockTypes.END_BLOCK_TYPE
    override val blockView: View =
        LayoutInflater.from(context).inflate(R.layout.block_end_cycle, this)
    override val pattern = "}"
    override var position = 0

    override fun blockToCode() = pattern

    override fun makeEditTextsDisabled() {}

    override fun ifTextViewEmpty() = false

    override fun content() = arrayListOf<String>()

    override fun loadBlock(data: BlockData) {
        this.id = data.id
        this.nextId = data.nextId
        this.previousId = data.prevId
        this.position = data.position
    }
}