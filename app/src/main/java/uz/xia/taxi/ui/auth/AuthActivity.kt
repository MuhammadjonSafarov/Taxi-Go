package uz.xia.taxi.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uz.xia.taxi.R
import uz.xia.taxi.databinding.ActivityAuthBinding
import uz.xia.taxi.utils.setStatusBarColor

class AuthActivity :AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(R.color.colorWhite, R.color.colorBlack, false)
        binding= ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}