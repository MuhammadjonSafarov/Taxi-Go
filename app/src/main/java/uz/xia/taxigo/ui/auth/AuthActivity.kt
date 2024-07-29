package uz.xia.taxigo.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import uz.xia.taxigo.R
import uz.xia.taxigo.databinding.ActivityAuthBinding
import uz.xia.taxigo.utils.setStatusBarColor
@AndroidEntryPoint
class AuthActivity :AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(R.color.colorWhite, R.color.colorBlack, false)
        binding= ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
