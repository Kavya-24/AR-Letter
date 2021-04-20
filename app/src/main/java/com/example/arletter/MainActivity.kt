package com.example.arletter

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment

    private val TAG = MainActivity::class.java.simpleName
    private var objectPresent = false


    //Main Activity once

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment



        //Create a plane listener
        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->

            //HitResult = HitResult
            //Plane = Plane
            //Motion Event
            Log.e(TAG, "Tapped plane")
            if (!objectPresent) {
                val anchor: Anchor = hitResult.createAnchor()
                ModelRenderable.builder()
                    .setSource(this, Uri.parse("Notebook_01.sfb"))
                    .build()
                    .thenAccept { mRenderable -> addModelToScene(anchor, mRenderable) }
                    .exceptionally { throwable -> throwError(throwable) }


            }
        }

    }

    private fun createTextNode(
        s: Editable?,
        model: TransformableNode,
        renderable: ModelRenderable
    ) {
        val tigerTitleNode = Node();
        tigerTitleNode.setParent(model);
        tigerTitleNode.setEnabled(false);
        tigerTitleNode.setLocalPosition(Vector3(0.0f, 1.0f, 0.0f));
        ViewRenderable.builder()
            .setView(this, R.layout.activity_main)
            .build()
            .thenAccept { renderable ->
                {
                    tigerTitleNode.renderable = renderable;
                    tigerTitleNode.isEnabled = true;

                }
            }

    }


    private fun throwError(throwable: Throwable?): Void? {
        //Create an error
        val b = AlertDialog.Builder(this)
        b.setMessage(throwable?.message)
            .show()
        return null
    }

    private fun addModelToScene(anchor: Anchor, mRenderable: ModelRenderable?) {
        //Create anchor node

        //This is a static node
        Log.e(TAG, "In addModelToScene")
        val anchorNode = AnchorNode(anchor)

        //To make this node movable
        val tNode = TransformableNode(arFragment.transformationSystem)
        tNode.setParent(anchorNode)

        tNode.renderable = mRenderable

        //For rendering
        arFragment.arSceneView.scene.addChild(anchorNode)
        tNode.select()
        objectPresent = true


    }

    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, modelObject: ModelRenderable) {

        val anchorNode = AnchorNode(anchor)

        TransformableNode(fragment.transformationSystem).apply {
            renderable = modelObject
            setParent(anchorNode)
            select()
        }

        fragment.arSceneView.scene.addChild(anchorNode)
    }

    private fun makeTextureSphere(hitResult: HitResult, res: Int) {

        Texture.builder().setSource(BitmapFactory.decodeResource(resources, res))
            .build()
            .thenAccept {
                MaterialFactory.makeOpaqueWithTexture(this, it)
                    .thenAccept { material ->
                        addNodeToScene(
                            arFragment, hitResult.createAnchor(),


                            ShapeFactory.makeSphere(
                                0.1f,
                                Vector3(0.0f, 0.15f, 0.0f),
                                material
                            )
                        )

                    }
            }


    }


}
