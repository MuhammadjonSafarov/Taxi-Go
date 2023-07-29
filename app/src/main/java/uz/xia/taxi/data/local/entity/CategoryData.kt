package uz.xia.taxi.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_data")
data class CategoryData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nameRu: String = "",
    val nameEn: String = "",
    val nameUzLat: String = "",
    val nameUzKr: String = "",
    val iconUrl:String = ""
)
data class CategoryDataName(
    val id: Long = 0,
    val name: String = "",
    val icon:String=""
)