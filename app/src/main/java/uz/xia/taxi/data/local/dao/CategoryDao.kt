package uz.xia.taxi.data.local.dao

import androidx.room.*
import uz.xia.taxi.common.EN
import uz.xia.taxi.common.RU
import uz.xia.taxi.common.UZ_Kr
import uz.xia.taxi.common.UZ_Lt
import uz.xia.taxi.data.local.entity.CategoryData
import uz.xia.taxi.data.local.entity.CategoryDataName

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoryData: CategoryData)


    @Query("SELECT * FROM category_data")
    suspend fun getAll(): List<CategoryData>


    suspend fun getByLangCategories(lang: String): List<CategoryDataName> {
        return when (lang) {
            EN -> getAllEnNames()
            RU -> getAllRuNames()
            UZ_Lt -> getAllUzLtNames()
            UZ_Kr -> getAllUzKrNames()
            else -> getAllUzLtNames()
        }
    }

    @Transaction
    @Query("SELECT id,nameEn AS name,iconUrl AS icon FROM category_data")
    suspend fun getAllEnNames(): List<CategoryDataName>

    @Transaction
    @Query("SELECT id,nameRu AS name,iconUrl AS icon FROM category_data")
    suspend fun getAllRuNames(): List<CategoryDataName>

    @Transaction
    @Query("SELECT id,nameUzLat AS name,iconUrl AS icon FROM category_data")
    suspend fun getAllUzLtNames(): List<CategoryDataName>

    @Transaction
    @Query("SELECT id,nameUzKr AS name,iconUrl AS icon FROM category_data")
    suspend fun getAllUzKrNames(): List<CategoryDataName>


    @Query("SELECT * FROM category_data WHERE id=:Id")
    suspend fun getById(Id: Int): CategoryData

    @Delete
    suspend fun delete(categoryData: CategoryData)
}