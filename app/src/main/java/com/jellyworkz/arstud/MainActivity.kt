package com.jellyworkz.arstud

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {

    private lateinit var fragment: ArFragment
    private val pointer = PointerDrawable()
    private var isTracking: Boolean = false
    private var isHitting: Boolean = false
    private lateinit var modelLoader: ModelLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragment = (supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment).apply {
            arSceneView.scene.addOnUpdateListener {
                onUpdate(it)
                this@MainActivity.onUpdate()
            }
        }
        modelLoader = ModelLoader(WeakReference(this))
        initGallery()
    }

    private fun onUpdate() {
        val trackingChanged = updateTracking()
        val contentView = this@MainActivity.findViewById<View>(android.R.id.content)
        if (trackingChanged) {
            Log.d(L.TAG, " Tracking changed!. is track = $isTracking")
            if (isTracking) {
                Log.d(L.TAG, "add on tracking")
                contentView.overlay.add(pointer)
            } else {
                contentView.overlay.remove(pointer)
            }
            contentView.invalidate()
        }

        if (isTracking) {
            val hitTestChanged = updateHitTest()
            if (hitTestChanged) {
                pointer.enabled = (isHitting)
                contentView.invalidate()
            }
        }
    }

    private fun updateTracking(): Boolean {
        val frame = fragment.arSceneView.arFrame
        val wasTracking = isTracking
        isTracking = frame != null && frame.camera.trackingState == TrackingState.TRACKING
        return isTracking != wasTracking
    }

    private fun updateHitTest(): Boolean {
        val frame = fragment.arSceneView.arFrame
        val pt = getScreenCenter()
        val hits: List<HitResult>
        val wasHitting = isHitting
        isHitting = false
        if (frame != null) {
            hits = frame.hitTest(pt.x.toFloat(), pt.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                    isHitting = true
                    break
                }
            }
        }
        return wasHitting != isHitting
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
    }

    fun onException(throwable: Throwable) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(throwable.message).setTitle("Codelab error!")
        val dialog = builder.create()
        dialog.show()
        return
    }
}
