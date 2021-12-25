package com.gorisse.thomas.sceneview.sample.arcursorplacement

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.ar.core.Anchor
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.CursorNode
import io.github.sceneview.utils.doOnApplyWindowInsets

class MainFragment : Fragment(R.layout.fragment_main) {

    lateinit var sceneView: ArSceneView
    lateinit var loadingView: View
    lateinit var actionButton: ExtendedFloatingActionButton

    lateinit var cursorNode: CursorNode
    var modelNode: ArNode? = null

    var isLoading = false
        set(value) {
            field = value
            loadingView.isGone = !value
            actionButton.isGone = value
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sceneView = view.findViewById(R.id.sceneView)
        sceneView.onTouchAr = { hitResult, _ ->
            anchorOrMove(hitResult.createAnchor())
        }
        loadingView = view.findViewById(R.id.loadingView)
        actionButton = view.findViewById<ExtendedFloatingActionButton>(R.id.actionButton).apply {
            val bottomMargin = (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
            doOnApplyWindowInsets { systemBarsInsets ->
                (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
                    systemBarsInsets.bottom + bottomMargin
            }
            setOnClickListener { cursorNode.createAnchor()?.let { anchorOrMove(it) } }
        }

        cursorNode = CursorNode(context = requireContext(), coroutineScope = lifecycleScope)
        cursorNode.onTrackingChanged = { _, isTracking ->
            if (!isLoading) {
                actionButton.isGone = !isTracking
            }
        }
        sceneView.addChild(cursorNode)
    }

    fun anchorOrMove(anchor: Anchor) {
        if (modelNode == null) {
            isLoading = true
            modelNode = ArNode(
                context = requireContext(),
                coroutineScope = lifecycleScope,
                anchor = anchor,
                modelGlbFileLocation = "models/spiderbot.glb",
                onModelLoaded = {
                    actionButton.text = getString(R.string.move_object)
                    actionButton.icon = resources.getDrawable(R.drawable.ic_target)
                    isLoading = false
                })
            sceneView.addChild(modelNode!!)
        } else {
            modelNode!!.anchor = anchor
        }
    }
}