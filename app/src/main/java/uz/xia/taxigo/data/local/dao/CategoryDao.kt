package uz.xia.taxigo.data.local.dao

import androidx.room.*
import uz.xia.taxigo.common.EN
import uz.xia.taxigo.common.RU
import uz.xia.taxigo.common.UZ_KR
import uz.xia.taxigo.common.UZ_LT
import uz.xia.taxigo.data.local.entity.CategoryData
import uz.xia.taxigo.data.local.entity.CategoryDataName

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoryData: CategoryData)


    @Query("SELECT * FROM category_data")
    suspend fun getAll(): List<CategoryData>


   /* suspend fun getByLangCategories(lang: String): List<CategoryDataName> {
        return when (lang) {
            EN -> getAllEnNames()
            RU -> getAllRuNames()
            UZ_LT -> getAllUzLtNames()
            UZ_KR -> getAllUzKrNames()
            else -> getAllUzLtNames()
        }
    }*/

    @Transaction
    @Query("SELECT id,name_en AS name,iconUrl AS icon FROM category_data")
    suspend fun getAllEnNames(): List<CategoryDataName>

    @Transaction
    @Query("SELECT id,name_ru AS name,iconUrl AS icon FROM category_data")
    suspend fun getAllRuNames(): List<CategoryDataName>

    @Transaction
    @Query("SELECT id,name_uz_lt AS name,iconUrl AS icon FROM category_data")
    suspend fun getAllUzLtNames(): List<CategoryDataName>

    @Transaction
    @Query("SELECT id,name_uz_kr AS name,iconUrl AS icon FROM category_data")
    suspend fun getAllUzKrNames(): List<CategoryDataName>


    @Query("SELECT * FROM category_data WHERE id=:Id")
    suspend fun getById(Id: Int): CategoryData

    @Delete
    suspend fun delete(categoryData: CategoryData)
}
