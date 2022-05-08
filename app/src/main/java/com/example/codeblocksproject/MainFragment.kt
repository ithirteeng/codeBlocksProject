package com.example.codeblocksproject

import android.annotation.SuppressLint
import android.content.ClipData
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.View.DragShadowBuilder
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.codeblocksproject.databinding.FragmentMainBinding
import com.example.codeblocksproject.model.*
import com.example.codeblocksproject.ui.UserInterfaceClass
import kotlin.math.abs


class MainFragment : Fragment(R.layout.fragment_main), MainFragmentInterface {
    companion object {
        const val PINK_COLOR = "pink"
        const val CHOCOLATE_COLOR = "chocolate"
        const val SPACE_COLOR = "space"
        const val MONOCHROME_COLOR = "monochrome"
        const val SHREK_COLOR = "shrek"
    }

    private val blockList: MutableList<CustomView> = mutableListOf()
    private val blockMap: MutableMap<Int, CustomView> = mutableMapOf()
    private val consoleFragment = ConsoleFragment()
    private val blocksFragment = BlocksFragment()
    private lateinit var binding: FragmentMainBinding
    private val workFieldRect = Rect()
    private var startBlockID = 0
    private var endBlockID = 0
    private var freeId = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ui = UserInterfaceClass(view.context, consoleFragment, blocksFragment)
        ui.setupAllUIFunctions(view)
        setupOtherFragmentsFunctions()
        binding.mainWorkfield.setOnDragListener(choiceDragListener())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.mainWorkfield.getHitRect(workFieldRect)

        val startBlock = StartProgramBlock(binding.start, requireContext())
        startBlockID = binding.start.id
        blockList.add(startBlock)
        blockMap[startBlockID] = startBlock
        startBlock.position = 0

        val endBlock = EndProgramBlock(binding.end, requireContext())
        endBlockID = binding.end.id
        blockList.add(endBlock)
        blockMap[endBlockID] = endBlock
        endBlock.position = 1

        startBlock.nextId = endBlockID
        endBlock.previousId = startBlockID

        return binding.root
    }

    private fun setupOtherFragmentsFunctions() {
        addingFragments()
        blocksButtonEvent()
        startButtonEvent()
    }

    private fun addingFragments() {
        val fragmentManager = childFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.container, consoleFragment)
        transaction.add(R.id.container, blocksFragment)
        transaction.hide(consoleFragment)
        transaction.hide(blocksFragment)
        transaction.commit()
    }

    private fun blocksButtonEvent() {
        binding.blocksButton.setOnClickListener {
            if (consoleFragment.isClosedStart) {
                if (blocksFragment.isClosedBlocks) {
                    blocksFragment.isClosedBlocks = false
                    val fragmentManager = childFragmentManager
                    val transaction = fragmentManager.beginTransaction()
                    transaction.setCustomAnimations(R.anim.bottom_panel_slide_out, 0)
                    blocksFragment.onCreateAnimation(0, true, 1)
                    transaction.show(blocksFragment)
                    transaction.commit()

                    Handler(Looper.getMainLooper()).postDelayed({
                        kotlin.run {
                            binding.blocksButton.visibility = View.GONE
                            binding.startButton.visibility = View.GONE
                        }
                    }, 350)
                }
            }
        }
    }

    private fun startButtonEvent() {
        binding.startButton.setOnClickListener {
            if (blocksFragment.isClosedBlocks) {
                if (consoleFragment.isClosedStart) {
                    consoleFragment.isClosedStart = false
                    val fragmentManager = childFragmentManager
                    val transaction = fragmentManager.beginTransaction()
                    transaction.setCustomAnimations(R.anim.bottom_panel_slide_out, 0)
                    consoleFragment.onCreateAnimation(0, true, 1)
                    transaction.show(consoleFragment)
                    transaction.commit()

                    Handler(Looper.getMainLooper()).postDelayed({
                        kotlin.run {
                            binding.startButton.visibility = View.GONE
                            binding.blocksButton.visibility = View.GONE
                        }
                    }, 350)
                    consoleFragment.checkCode(blocksToCode())
                }
            }
        }
    }

    override fun displayButtons() {
        binding.startButton.visibility = View.VISIBLE
        binding.blocksButton.visibility = View.VISIBLE
    }

    override fun addBlock() {
        createBlock()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createBlock() {
        val newBlock = InitializationBlock(requireContext())

        val lastBlock = blockMap[blockMap[endBlockID]!!.previousId]!!
        newBlock.setDefault(lastBlock.blockView.x)

        lastBlock.nextId = newBlock.blockView.id
        newBlock.previousId = lastBlock.blockView.id

        blockMap[endBlockID]!!.previousId = newBlock.blockView.id
        newBlock.nextId = endBlockID

        binding.mainWorkfield.addView(newBlock, lastBlock.position + 1)
        newBlock.position = lastBlock.position + 1
        blockMap[endBlockID]!!.position++


        newBlock.setOnTouchListener(choiceTouchListener())

        blockList.add(newBlock)
        blockMap[newBlock.blockView.id] = newBlock
    }

    private fun View.setDefault(x: Float) {
        this.x = x
        this.z = 1F
        this.id = freeId
        freeId++
        if (freeId == startBlockID || freeId == endBlockID) {
            freeId++
        }
    }

    private var x = 0F
    private var y = 0F

    private fun deleteView(block: CustomView) {
        binding.mainWorkfield.removeView(block.blockView)
        blockMap.remove(block.blockView.id)
        blockList.remove(block)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun choiceTouchListener() = OnTouchListener { view, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            x = event.x
            y = event.y
            val currentBlock = blockMap[view.id]
            if (currentBlock!!.previousId != -1) {
                blockMap[currentBlock.previousId]!!.nextId = currentBlock.nextId
            }
            if (currentBlock.nextId != -1) {
                blockMap[currentBlock.nextId]!!.previousId = currentBlock.previousId
            }

            var block = currentBlock
            while (block!!.blockView.id != endBlockID) {
                block = blockMap[block.nextId]!!
                block.position--
                binding.mainWorkfield.removeView(block.blockView)
            }

            block = currentBlock
            while (block!!.blockView.id != endBlockID) {
                block = blockMap[block.nextId]!!
                binding.mainWorkfield.addView(block.blockView, block.position)
            }
            view.visibility = View.INVISIBLE
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = DragShadowBuilder(view)
            view.startDragAndDrop(data, shadowBuilder, view, 0)
            draggingBlock = blockMap[view.id]!!
            true
        } else {
            false
        }
    }

    private lateinit var draggingBlock: CustomView
    private var location = MyPoint(0f, 0f)

    private fun choiceDragListener() = View.OnDragListener { view, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                binding.mainWorkfield.removeView(draggingBlock.blockView)
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                if (view == binding.mainWorkfield) {
                    location.x = event.x
                    location.y = event.y
                }
            }
            DragEvent.ACTION_DROP -> {
                if (view == binding.mainWorkfield) {
                    draggingBlock.blockView.z = 1F

                    val currentBlock = blockMap[draggingBlock.blockView.id]!!
                    var block = blockMap[startBlockID]

                    var isBlockNested = false

                    while (block!!.blockView.id != endBlockID) {
                        if (isCrossed(location, block)) {

                            val temp = block.nextId
                            block.nextId = currentBlock.blockView.id
                            currentBlock.previousId = block.blockView.id
                            currentBlock.nextId = temp
                            blockMap[temp]!!.previousId = currentBlock.blockView.id

                            isBlockNested = true
                            currentBlock.position = block.position + 1

                            binding.mainWorkfield.removeView(currentBlock.blockView)
                            binding.mainWorkfield.addView(
                                currentBlock.blockView,
                                currentBlock.position
                            )

                            var counter = 1
                            var tempBlock = currentBlock
                            while (tempBlock.blockView.id != endBlockID) {
                                tempBlock = blockMap[tempBlock.nextId]!!
                                tempBlock.position++
                                counter++
                            }

                            val v = blockMap[currentBlock.previousId]!!.blockView
                            val diff = abs(currentBlock.blockView.height - v.height)


                            counter--
                            draggingBlock.blockView.y =
                                v.y + currentBlock.blockView.height * counter + binding.end.height + diff
                            draggingBlock.blockView.x = v.x

                            break
                        }
                        block = blockMap[block.nextId]!!
                    }
                    if (!isBlockNested || draggingBlock.position > blockMap[endBlockID]!!.position) {
                        deleteView(currentBlock)
                    }

                } else {
                    deleteView(draggingBlock)
                }
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                draggingBlock.blockView.visibility = View.VISIBLE
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                Log.e("####", "exited")
            }
        }
        true
    }

    private fun isCrossed(point: MyPoint, parent: CustomView): Boolean {
        val x = parent.blockView.x
        val y = parent.blockView.y

        return if (point.x >= x && point.x <= x + parent.blockView.width) {
            point.y >= y && point.y <= y + parent.blockView.height
        } else {
            false
        }
    }

    private fun checked(view: View): Int {
        for (block in blockList) {
            if (block.blockView == view) {
                return block.blockView.id
            }
        }
        return -1
    }

    private fun blocksToCode(): String {
        var code = "{\n"
        var currentBlock = blockMap[startBlockID]

        while (currentBlock!!.blockView.id != endBlockID) {
            code += currentBlock.blockToCode() + "\n"
            currentBlock = blockMap[currentBlock.nextId]!!
        }
        code += "}"
        return code
    }



}