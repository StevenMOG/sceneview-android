package io.github.sceneview.ar.interaction

import dev.romainguy.kotlin.math.clamp
import io.github.sceneview.ar.node.ArNode

class ScaleGesture(arNode: ArNode) : GestureStrategy(arNode) {
    override fun scroll(delta: Float) {
        arNode.scale = clamp(
            arNode.scale + (-delta * 0.05f),
            0.5f,
            1.5f
        ) // TODO: Make min max scale configurable
    }
}