package uz.xia.taxigo.di

import uz.xia.taxigo.utils.LocaleHelper
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
/*
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
*/
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
import uz.xia.taxigo.common.BASE_AVTOTICKET_URL
import uz.xia.taxigo.common.BASE_URL
import uz.xia.taxigo.common.DB_ASSETS_NAME
import uz.xia.taxigo.common.NOMINATION_URL
import uz.xia.taxigo.common.STORAGE_DB_NAME
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.data.PreferenceManagerImpl
import uz.xia.taxigo.data.local.AppDataBase
import uz.xia.taxigo.data.local.DatabaseMigrations.MIGRATION_1_2
import uz.xia.taxigo.data.local.DatabaseMigrations.MIGRATION_2_3
import uz.xia.taxigo.data.local.DatabaseMigrations.MIGRATION_3_4
import uz.xia.taxigo.data.local.dao.UserAddressDao
import uz.xia.taxigo.network.ApiService
import uz.xia.taxigo.network.AutoTicketApiService
import uz.xia.taxigo.network.NominationService
import uz.xia.taxigo.network.intercepter.HeaderInterceptor
import uz.xia.taxigo.utils.device_model.DeviceHeaderProvider
import uz.xia.taxigo.utils.device_model.DeviceInfoInterector
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
    fun provideAvtoticketService(client: OkHttpClient): AutoTicketApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_AVTOTICKET_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return retrofit.create(AutoTicketApiService::class.java)
    }

    @Provides
    fun provideOkHttpClient(chuckedInterceptor: ChuckerInterceptor,
                            headerInterceptor: HeaderInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(chuckedInterceptor)
            .addInterceptor(headerInterceptor)
            .connectTimeout(45L, TimeUnit.SECONDS)
            .readTimeout(45L, TimeUnit.SECONDS)
            .writeTimeout(45L, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideHeaderInterceptor(@ApplicationContext context: Context,preference: IPreference): HeaderInterceptor {
        val deviceHeaderProvider:DeviceHeaderProvider = DeviceInfoInterector(context)
        return HeaderInterceptor(deviceHeaderProvider,preference)
    }

    @Provides
    @Singleton
    fun provideChuckerInterceptor(@ApplicationContext context: Context):ChuckerInterceptor{
        val chuckedInterceptor = ChuckerInterceptor.Builder(context)
            .collector(ChuckerCollector(context))
            .maxContentLength(250_000L)
            .redactHeaders(emptySet())
            .alwaysReadResponseBody(false)
            .build()
        return chuckedInterceptor
    }



    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): AppDataBase {
        return Room.databaseBuilder(
            context, AppDataBase::class.java,STORAGE_DB_NAME
        ).createFromAsset(DB_ASSETS_NAME)
            .addMigrations(MIGRATION_1_2)
            .addMigrations(MIGRATION_2_3)
            .addMigrations(MIGRATION_3_4)
            .build()
    }
    @Provides
    @Singleton
    fun provideLocaleHelper(preference: IPreference) = LocaleHelper(preference)

    @Provides
    fun provideAddressDao(dataBase: AppDataBase): UserAddressDao {
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
