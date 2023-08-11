package com.dummy.snakegame

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import androidx.appcompat.app.AppCompatActivity
import com.dummy.snakegame.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs


class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    var dx = 0f
    var dy = 0f
    var x1 = 0f
    var x2 = 0f
    var y1 = 0f
    var y2 = 0f
    var direction: String = ""
    var parentTop = 0f
    var parentBottom = 0f
    var parentLeft = 0f
    var parentRight = 0f

    private var mVelocityTracker: VelocityTracker? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.flJoyStick.post {
            parentLeft = viewBinding.flJoyStick.x
            parentTop = viewBinding.flJoyStick.y
            parentRight = viewBinding.flJoyStick.x + viewBinding.flJoyStick.width
            parentBottom = viewBinding.flJoyStick.y + viewBinding.flJoyStick.height

            println("parent: $parentTop")
            println("parent: $parentBottom")
            println("parent: $parentLeft")
            println("parent: $parentRight")
        }

        viewBinding.fabJoyStick.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {/*dx = motionEvent.rawX - (view.width / 2)
                    dy = motionEvent.rawY - (view.height)
                    view.animate().x(dx).y(dy).setDuration(0).start()*/
//                    x1 = motionEvent.x
//                    y1 = motionEvent.y

                    // Reset the velocity tracker back to its initial state.
                    mVelocityTracker?.clear()
                    // If necessary retrieve a new VelocityTracker object to watch the
                    // velocity of a motion.
                    mVelocityTracker = mVelocityTracker ?: VelocityTracker.obtain()
                    // Add a user's movement to the tracker.
                    mVelocityTracker?.addMovement(motionEvent)
                }

                MotionEvent.ACTION_MOVE -> {
                    dx = motionEvent.rawX - (view.width / 2)
                    dy = motionEvent.rawY - (view.height)
//                    view.animate().x(dx).y(dy).setDuration(0).start()

                    val viewLeft = dx
                    val viewTop = dy
                    val viewRight = dx + (view.width)
                    val viewBottom = dy + (view.height)

                    if ((viewLeft + 20 > parentLeft && viewRight < parentRight) && (viewTop > parentTop && viewBottom < parentBottom)) {
                        view.animate().x(dx).y(dy).setDuration(0).start()

                        if ((view.x) in (view.x..(parentLeft))) {
                            println("ACTION_MOVE: left")
                        }/*if(viewRight < parentRight){
                            println("ACTION_MOVE: right")
                        }
                        if(viewTop > parentTop){
                            println("ACTION_MOVE: top")
                        }
                        if(viewBottom < parentBottom){
                            println("ACTION_MOVE: bottom")
                        }*/
                    }

                    /*x2 = motionEvent.x
                    y2 = motionEvent.y
                    dx = x2 - x1
                    dy = y2 - y1

                    // Use dx and dy to determine the direction of the move
                    CoroutineScope(Dispatchers.Main).launch {
                        CoroutineScope(Dispatchers.IO).async {
                            direction = if (abs(dx) > abs(dy)) {
                                if (dx > 0) "right"
                                else "left"
                            } else {
                                if (dy > 0) "down"
                                else "up"
                            }
                        }.await()
                        delay(100)
                        moveObject(direction)
                    }*/

                    mVelocityTracker?.run {
                        val pointerId: Int = motionEvent.getPointerId(motionEvent.actionIndex)
                        addMovement(motionEvent)
                        // When you want to determine the velocity, call
                        // computeCurrentVelocity(). Then call getXVelocity()
                        // and getYVelocity() to retrieve the velocity for each pointer ID.
                        computeCurrentVelocity(1000)

                        val xVelocity = getXVelocity(pointerId)
                        val yVelocity = getYVelocity(pointerId)

                        CoroutineScope(Dispatchers.Main).launch {
                            CoroutineScope(Dispatchers.IO).async {
                                if (abs(xVelocity) > abs(yVelocity)) {
                                    if (xVelocity > 0) {
                                        // right
                                        direction = "right"
                                    } else {
                                        // left
                                        direction = "left"
                                    }
                                } else {
                                    if (yVelocity > 0) {
                                        // down
                                        direction = "down"
                                    } else {
                                        // up
                                        direction = "up"
                                    }
                                }
                            }.await()
                            delay(200)
                            moveObject(direction)
                        }
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Return a VelocityTracker object back to be re-used by others.
                    mVelocityTracker?.recycle()
                    mVelocityTracker = null
                }
            }
            return@setOnTouchListener true
        }
    }

    private fun moveObject(direction: String) {
        println("moveObject: $direction")
        val objectX = viewBinding.ivObject.x
        val objectY = viewBinding.ivObject.y
        when (direction) {
            "right" -> {
                viewBinding.ivObject.animate().x(objectX + 1f).setDuration(0).start()
            }

            "left" -> {
                viewBinding.ivObject.animate().x(objectX - 1f).setDuration(0).start()
            }

            "down" -> {
                viewBinding.ivObject.animate().y(objectY + 1f).setDuration(0).start()
            }

            "up" -> {
                viewBinding.ivObject.animate().y(objectY - 1f).setDuration(0).start()
            }
        }
    }
}