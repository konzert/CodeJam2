package ca.mcgill.ecse321.cityways_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import ca.hh.codejam_android.MapsActivity
import ca.hh.codejam_android.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton = findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener{
            startActivity(Intent(this@MainActivity, MapsActivity::class.java))
        }
    }
}
