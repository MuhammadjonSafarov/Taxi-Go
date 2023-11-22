package uz.xia.taxi.data.local.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.xia.taxi.common.EMPTY_STRING

@Entity(tableName = "category_data")
data class CategoryData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "name_uz_kr") val nameUzKr: String = EMPTY_STRING,
    @ColumnInfo(name = "name_uz_lt") val nameUzLt: String = EMPTY_STRING,
    @ColumnInfo(name = "name_en") val nameRu: String = EMPTY_STRING,
    @ColumnInfo(name = "name_ru") val nameEn: String = EMPTY_STRING,
    val iconUrl:String = ""
)
data class CategoryDataName(
    val id: Long = 0,
    val name: String = "",
    val icon:String=""
)
