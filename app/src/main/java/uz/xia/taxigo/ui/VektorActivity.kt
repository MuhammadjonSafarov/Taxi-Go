package uz.xia.taxigo.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import uz.xia.taxigo.utils.path_click_listener.RichPath
import uz.xia.taxigo.utils.path_click_listener.RichPathView
import uz.xia.taxigo.R

class VektorActivity : AppCompatActivity(), RichPath.OnPathClickListener {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vektor)
        val a = findViewById<RichPathView>(R.id.path_view)
        a.onPathClickListener = this
    }

    override fun onClick(richPath: RichPath) {
        if (richPath.name == "first") {
            Toast.makeText(this, "First clicked", Toast.LENGTH_SHORT).show()
        }
        if (richPath.name == "second") {
            Toast.makeText(this, "Second clicked", Toast.LENGTH_SHORT).show()
        }
    }
}