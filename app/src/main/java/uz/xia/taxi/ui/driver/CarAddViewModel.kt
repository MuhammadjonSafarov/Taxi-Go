package uz.xia.taxi.ui.driver

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.xia.taxi.data.local.AppDataBase
import javax.inject.Inject

@HiltViewModel
class CarAddViewModel @Inject constructor(
    private val appDataBase: AppDataBase
):ViewModel() {
}
