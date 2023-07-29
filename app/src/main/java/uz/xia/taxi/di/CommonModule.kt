package uz.xia.taxi.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import uz.xia.taxi.network.ApiService
import uz.xia.taxi.network.NominationService
import uz.xia.taxi.common.BASE_URL
import uz.xia.taxi.common.DATA_BASE_NAME
import uz.xia.taxi.common.NOMINATION_URL
import uz.xia.taxi.data.IPreference
import uz.xia.taxi.data.PreferenceManagerImpl
import uz.xia.taxi.data.local.AppDataBase
import uz.xia.taxi.data.local.DatabaseMigrations.MIGRATION_1_2
import uz.xia.taxi.data.local.dao.UserAddressDao
import uz.xia.taxi.data.local.entity.UserAddress
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {

    @Provides
    fun provideApiService(client: OkHttpClient): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    fun provideNominationService(client: OkHttpClient): NominationService {
        val retrofit = Retrofit.Builder()
            .baseUrl(NOMINATION_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return retrofit.create(NominationService::class.java)
    }

    @Provides
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val chuckedInterceptor = ChuckerInterceptor.Builder(context)
            .collector(ChuckerCollector(context))
            .maxContentLength(250_000L)
            .redactHeaders(emptySet())
            .alwaysReadResponseBody(false)
            .build()
        return OkHttpClient.Builder()
            .addInterceptor(chuckedInterceptor)
            .connectTimeout(45L, TimeUnit.SECONDS)
            .readTimeout(45L, TimeUnit.SECONDS)
            .writeTimeout(45L, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): AppDataBase {
        return Room.databaseBuilder(context,
            AppDataBase::class.java, DATA_BASE_NAME)
//            .createFromAsset(DATA_BASE_NAME)
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideAddressDao(dataBase: AppDataBase):UserAddressDao{
        return dataBase.userAddressDao()
    }

    @Provides
    @Singleton
    fun providePreferenceManager(preferences: SharedPreferences): IPreference =
        PreferenceManagerImpl(preferences)

    @Provides
    fun providePreference(@ApplicationContext context: Context): SharedPreferences =
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
}
