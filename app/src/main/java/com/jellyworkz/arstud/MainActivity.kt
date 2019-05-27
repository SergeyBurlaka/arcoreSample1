package com.jellyworkz.arstud

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.animation.ModelAnimator
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {

    private lateinit var fragment: ArFragment
    private lateinit var modelLoader: ModelLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)
        fragment = (supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment)
        modelLoader = ModelLoader(WeakReference(this))
        initGallery()
    }

    private fun startAnimation(node: TransformableNode, renderable: ModelRenderable?): ModelAnimator? {
        if (renderable == null || renderable.animationDataCount == 0) {
            return null
        }
        for (i in 0 until renderable.animationDataCount) {
            val animationData = renderable.getAnimationData(i)
        }
        val animator = ModelAnimator(renderable.getAnimationData(0), renderable)
        animator.start()
        return animator
    }

    fun togglePauseAndResume(animator: ModelAnimator) {
        if (animator.isPaused) {
            animator.resume()
        } else if (animator.isStarted) {
            animator.pause()
        } else {
            animator.start()
        }
    }

    private fun getScreenCenter(): android.graphics.Point {
        val vw = findViewById<View>(android.R.id.content)
        return android.graphics.Point(vw.width / 2, vw.height / 2)
    }

    private fun initGallery() {
        findViewById<LinearLayout>(R.id.gallery_layout).let { gallery ->
            ImageView(this@MainActivity).apply {
                setImageResource(R.drawable.droid_thumb)
                setOnClickListener {
                    addObject(Uri.parse("andy_dance.sfb"))
                }
                gallery.addView(this)
            }

            val cabin = ImageView(this)
            cabin.setImageResource(R.drawable.cabin_thumb)
            cabin.contentDescription = "cabin"
            cabin.setOnClickListener { view -> addObject(Uri.parse("Cabin.sfb")) }
            gallery.addView(cabin)

            val house = ImageView(this)
            house.setImageResource(R.drawable.house_thumb)
            house.contentDescription = "house"
            house.setOnClickListener { view -> addObject(Uri.parse("House.sfb")) }
            gallery.addView(house)

            val igloo = ImageView(this)
            igloo.setImageResource(R.drawable.igloo_thumb)
            igloo.contentDescription = "igloo"
            igloo.setOnClickListener { view -> addObject(Uri.parse("igloo.sfb")) }
            gallery.addView(igloo)

        }
    }

    private fun addObject(model: Uri) {
        val frame = fragment.arSceneView.arFrame
        val pt = getScreenCenter()
        val hits: List<HitResult>
        if (frame != null) {
            hits = frame.hitTest(pt.x.toFloat(), pt.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                    modelLoader.loadModel(hit.createAnchor(), model)
                    break
                }
            }
        }
    }

    fun addNodeToScene(anchor: Anchor, renderable: ModelRenderable) {
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(fragment.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        node.select()
        startAnimation(node = node, renderable = renderable).apply {
            this.let {
                node.setOnTapListener { _, _ ->
                    togglePauseAndResume(animator = this!!)
                }
            }
        }

    }

    fun onException(throwable: Throwable) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(throwable.message).setTitle("Codelab error!")
        val dialog = builder.create()
        dialog.show()
        return
    }
}
