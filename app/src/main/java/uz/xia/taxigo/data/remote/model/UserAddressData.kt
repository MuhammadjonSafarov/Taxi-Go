package uz.xia.taxigo.data.remote.model

import uz.xia.taxigo.data.local.entity.UserAddress

class EmptyUserAddress:UserAddress()
class GroupUserAddress(val time:Long):UserAddress()
