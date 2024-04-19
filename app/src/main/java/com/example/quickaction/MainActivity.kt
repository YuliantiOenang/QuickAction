package com.example.quickaction

import android.os.Bundle
import android.util.TypedValue
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.example.quickaction.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        var initialTouchX: Float = 0f
        var initialTouchY: Float = 0f
        var dragDistanceY: Float = 0f

        binding.fab.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.btnCancel.visibility = View.VISIBLE
                    val tenDpInPixels = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        10f,
                        resources.displayMetrics
                    )
                    dragDistanceY = tenDpInPixels
                    updateUI(tenDpInPixels)
                    binding.dragRectangle.visibility = View.VISIBLE
                    // User started to drag
                    binding.overlay.visibility = View.VISIBLE
                    initialTouchX = event.x
                    initialTouchY = event.y
                }

                MotionEvent.ACTION_MOVE -> {
                    // User is dragging
                    val dragDistanceX = event.x - initialTouchX
                    dragDistanceY = event.y - initialTouchY
                    // Update the UI based on the drag distance
                    println(dragDistanceY)
                    updateUI(dragDistanceY)
                }

                MotionEvent.ACTION_UP -> {
                    val location = IntArray(2)
                    binding.btnCancel.getLocationOnScreen(location)
                    val x = event.rawX - location[0]
                    val y = event.rawY - location[1]
                    if (x >= 0 && x <= binding.btnCancel.width &&
                        y >= 0 && y <= binding.btnCancel.height
                    ) {
                        binding.btnCancel.performClick()
                    } else {
                        Toast.makeText(this, "temperature updated", Toast.LENGTH_SHORT).show()
                        hideAll()
                    }
                }
            }
            true
        }

        binding.btnCancel.setOnClickListener {
            hideAll()
        }

    }

    private fun hideAll() {
        binding.dragRectangle.visibility = View.GONE
        binding.overlay.visibility = View.GONE
        binding.btnCancel.visibility = View.GONE
    }

    private fun updateUI(dragDistanceY: Float) {
        // Update the height of the dragRectangle view based on the drag distance
        val params = binding.dragRectangle.layoutParams
        var absHeight = Math.abs(dragDistanceY.toInt())
        if (absHeight == 0) {
            absHeight = 1
        }
        params.height = Math.min(absHeight, 500)
        binding.dragRectangle.layoutParams = params
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}