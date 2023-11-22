package uz.xia.taxi.data.remote.model

import uz.xia.taxi.data.local.entity.UserAddress

class EmptyUserAddress:UserAddress()
class GroupUserAddress(val time:Long):UserAddress()
