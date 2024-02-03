package uz.xia.taxigo.ui.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import uz.xia.taxigo.data.local.dao.UserAddressDao
import uz.xia.taxigo.data.local.entity.UserAddress
import uz.xia.taxigo.data.remote.model.EmptyUserAddress
import uz.xia.taxigo.data.remote.model.GroupUserAddress
import java.util.Calendar
import javax.inject.Inject

private const val TAG = "AddressViewModel"
interface IAddressViewModel {
    fun getAddresses(): LiveData<List<UserAddress>?>
}

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val userAddressDao: UserAddressDao
) : ViewModel(), IAddressViewModel {


    override fun getAddresses(): LiveData<List<UserAddress>?> {
        return userAddressDao.getAll().map { parseUiModel(it) }
    }

    private fun parseUiModel(it: List<UserAddress>): List<UserAddress> {
        val mList = mutableListOf<UserAddress>()
        var i = 0
        var j = 0
        var model = if (it.isEmpty()){
            EmptyUserAddress()
        }else{
            UserAddress()
        }
        while (j < it.size && i < it.size) {
            if (i == j){
                model = GroupUserAddress(it[i].updateAt)
                mList.add(model)
            }
            if (isSomeDay(it[i].updateAt,it[j].updateAt)) {
                mList.add(it[j])
                Timber.d("$TAG ${model.createAt}")
                j += 1
            } else {
                i = j
            }
        }
        if (mList.isEmpty()) mList.add(model)
        return mList
    }
    private fun isSomeDay(date1: Long, date2: Long): Boolean {
        val target = Calendar.getInstance()
        target.timeInMillis = date1
        val y1 = target.get(Calendar.YEAR)
        val d1 = target.get(Calendar.DAY_OF_YEAR)
        target.timeInMillis = date2
        return y1 == target.get(Calendar.YEAR) && d1 == target.get(Calendar.DAY_OF_YEAR)
    }
}
