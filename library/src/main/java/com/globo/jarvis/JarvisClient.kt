package com.globo.jarvis

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.os.Build
import com.apollographql.apollo.ApolloClient
import com.globo.jarvis.BuildConfig.VERSION_NAME
import com.globo.jarvis.adapter.DateTypeAdapter
import com.globo.jarvis.repository.*
import com.globo.jarvis.type.CustomType
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

object JarvisClient {
    private const val HEADER_KEY_AUTHORIZATION = "authorization"
    private const val HEADER_KEY_GLBUID = "glbuid"
    private const val HEADER_KEY_USER_ID = "x-user-id"
    private const val HEADER_KEY_CLIENT_VERSION = "x-client-version"
    private const val HEADER_KEY_LIBRARY_VERSION = "x-library-version"
    private const val HEADER_KEY_TENANT = "x-tenant-id"
    private const val HEADER_KEY_DEVICE = "x-device-id"
    private const val HEADER_KEY_PLATFORM = "x-platform-id"
    private const val HEADER_VALUE_PLATAFORM = "Android"
    private const val HEADER_VALUE_DEVICE_MOBILE = "mobile"
    private const val HEADER_VALUE_DEVICE_TABLET = "tablet"
    private const val HEADER_VALUE_DEVICE_TV = "tv"
    private const val CACHE_SIZE: Long = 50 * 1024 * 1024
    private const val CACHE_APOLLO = "jarvis_client_cache"

    private lateinit var apolloClient: ApolloClient
    private lateinit var settings: Settings
    val title by lazy { TitleRepository(apolloClient, settings.device()) }
    val suggest by lazy { SuggestRepository(apolloClient, settings.device()) }
    val epg by lazy { EpgRepository(apolloClient, settings.device()) }
    val categories by lazy { CategoriesRepository(apolloClient, settings.device()) }

    val affiliates by lazy { AffiliatesRepository(apolloClient, settings.device()) }
    val calendar by lazy { CalendarRepository(apolloClient) }
    val video by lazy { VideoRepository(apolloClient, settings.device()) }
    val season by lazy { SeasonRepository(apolloClient, settings.device()) }
    val locale by lazy { LocaleRepository(apolloClient) }
    val home by lazy { HomeRepository(apolloClient, settings.device()) }
    val scenes by lazy { ScenesRepository(apolloClient, season) }
    val episode by lazy { EpisodeRepository(apolloClient, season, settings.device()) }
    val chapter by lazy { ChapterRepository(apolloClient) }
    val sales by lazy { SalesRepository(apolloClient, settings.device()) }
    val broadcast by lazy { BroadcastRepository(apolloClient, settings.device()) }
    val channels by lazy { ChannelsRepository(apolloClient) }
    val user by lazy { UserRepository(apolloClient, settings.device()) }
    val search by lazy { SearchRepository(apolloClient, settings.device()) }
    val remoteConfig by lazy { RemoteConfigRepository(apolloClient) }

    @JvmOverloads
    fun initialize(settings: Settings, apolloClient: ApolloClient? = null) {
        this.settings = settings
        this.apolloClient = apolloClient ?: buildApolloClient()
    }

    private fun buildApolloClient() = ApolloClient
        .builder()
        .serverUrl(settings.environment())
        .useHttpGetMethodForQueries(true)
        .useHttpGetMethodForPersistedQueries(true)
        .enableAutoPersistedQueries(true)
        .addCustomTypeAdapter(CustomType.DATE, DateTypeAdapter())
        .okHttpClient(
            OkHttpClient
                .Builder()
                .addInterceptor(providerInterceptorLogging(settings.enableLog()))
                .addInterceptor(providerHeaderInterceptor())
                .addInterceptor(providerInterceptorOffline(settings.application()))
                .addNetworkInterceptor(providerInterceptorCache())
                .cache(Cache(File(settings.application().cacheDir, CACHE_APOLLO), CACHE_SIZE))
                .connectTimeout(settings.timeout(), TimeUnit.SECONDS)
                .readTimeout(settings.timeout(), TimeUnit.SECONDS)
                .writeTimeout(settings.timeout(), TimeUnit.SECONDS)
                .build()
        )
        .build()

    private fun providerInterceptorLogging(enableLog: Boolean) = HttpLoggingInterceptor().setLevel(
        if (enableLog) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    )

    private fun providerInterceptorOffline(application: Application) = Interceptor {
        val builder: Request.Builder = it.request().newBuilder()
        if (!isOnline(application)) {
            builder.cacheControl(CacheControl.FORCE_CACHE)
        }
        it.proceed(builder.build())
    }

    private fun providerInterceptorCache() = Interceptor { chain ->
        val response = chain.proceed(chain.request())
        val cacheControl = response.cacheControl()

        return@Interceptor if (cacheControl.isPublic) response
        else response
            .newBuilder()
            .headers(response.headers())
            .build()
    }

    private fun providerHeaderInterceptor() = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder().apply {
            addHeader(HEADER_KEY_TENANT, settings.tenant())
            header(HEADER_KEY_CLIENT_VERSION, settings.version())
            header(HEADER_KEY_LIBRARY_VERSION, VERSION_NAME)
            header(HEADER_KEY_PLATFORM, HEADER_VALUE_PLATAFORM)
            header(
                HEADER_KEY_DEVICE, when (settings.device()) {
                    Device.TABLET -> HEADER_VALUE_DEVICE_TABLET
                    Device.MOBILE -> HEADER_VALUE_DEVICE_MOBILE
                    else -> HEADER_VALUE_DEVICE_TV
                }
            )

            val glbId = settings.glbId()
            if (!glbId.isNullOrEmpty()) {
                header(HEADER_KEY_AUTHORIZATION, glbId)
            }

            val userId = settings.userId()
            if (!userId.isNullOrEmpty()) {
                header(HEADER_KEY_USER_ID, userId)
            }

            val anonymousUserId = settings.anonymousUserId()
            if (!anonymousUserId.isNullOrEmpty()) {
                header(HEADER_KEY_GLBUID, anonymousUserId)
            }
        }

        return@Interceptor chain.proceed(requestBuilder.build())
    }

    private fun isOnline(context: Context?): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        return connectivityManager?.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getNetworkCapabilities(activeNetwork)?.let { networkCapabilities ->
                    return@let (networkCapabilities.hasTransport(TRANSPORT_WIFI)
                            || networkCapabilities.hasTransport(TRANSPORT_CELLULAR)
                            || networkCapabilities.hasTransport(TRANSPORT_ETHERNET)
                            || networkCapabilities.hasTransport(TRANSPORT_VPN)
                            || networkCapabilities.hasTransport(TRANSPORT_BLUETOOTH))
                } ?: false
            } else {
                connectivityManager.activeNetworkInfo?.let { network ->
                    return@let network.isConnectedOrConnecting
                } ?: false
            }
        } ?: false
    }

    interface Settings {
        fun glbId(): String? = null

        fun userId(): String? = null

        fun anonymousUserId(): String? = null

        fun timeout(): Long = 3

        fun tenant(): String

        fun device(): Device

        fun version(): String

        fun application(): Application

        fun enableLog(): Boolean

        fun environment(): String = Environment.PRODUCTION.value
    }
}