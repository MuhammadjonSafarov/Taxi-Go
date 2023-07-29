package uz.xia.taxi.ui.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.xia.taxi.data.local.dao.UserAddressDao
import uz.xia.taxi.data.local.entity.UserAddress
import javax.inject.Inject

interface IAddressViewModel {
    fun getAddresses(): LiveData<List<UserAddress>>
}

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val userAddressDao: UserAddressDao
) : ViewModel(), IAddressViewModel {


    override fun getAddresses(): LiveData<List<UserAddress>> {
        return userAddressDao.getAll()
    }
}