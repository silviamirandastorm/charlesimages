package com.globo.jarvis.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.rx
import com.globo.jarvis.Callback
import com.globo.jarvis.model.Configuration
import com.globo.jarvis.remoteconfig.RemoteConfigQuery
import com.globo.jarvis.type.RemoteConfigGroups
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class RemoteConfigRepository(private val apolloClient: ApolloClient) {
    private val compositeDisposable by lazy { CompositeDisposable() }

    //Callback
    fun all(
        group: String,
        remoteConfigCallback: Callback.RemoteConfig,
        vararg scopes: String
    ) {
        compositeDisposable.add(
            all(group, *scopes)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        remoteConfigCallback.onRemoteConfigSuccess(it)
                    },
                    { throwable ->
                        remoteConfigCallback.onFailure(throwable)
                    }
                ))
    }

    //RxJava
    fun all(
        group: String,
        vararg scopes: String
    ): Observable<Map<String, Configuration?>> = Observable
        .merge(scopes.map { detailsRemoteConfig(group, it) })
        .observeOn(AndroidSchedulers.mainThread())
        .map { transformConfigsToConfiguration(it.data()?.remoteConfigs()?.resources()) }
        .flatMapIterable { it }
        .toMap({ it.first }, { it.second })
        .toObservable()

    internal fun detailsRemoteConfig(
        group: String,
        scope: String
    ) =
        apolloClient
            .query(buildRemoteConfigQuery(group, scope))
            .rx()
            .subscribeOn(Schedulers.io())


    internal fun buildRemoteConfigQuery(
        group: String,
        scope: String
    ) = RemoteConfigQuery
        .builder()
        .group(RemoteConfigGroups.safeValueOf(group))
        .scope(scope)
        .build()


    fun transformConfigsToConfiguration(remoteConfigQueryResourceList: List<RemoteConfigQuery.Resource>?): List<Pair<String, Configuration?>> =
        remoteConfigQueryResourceList
            ?.map { transformRemoteConfigQueryResourceToConfiguration(it) }
            ?.filter { it.first.isNotEmpty() }
            .orEmpty()


    internal fun transformRemoteConfigQueryResourceToConfiguration(remoteConfigQueryResource: RemoteConfigQuery.Resource):
            Pair<String, Configuration?> {
        val remoteConfigQueryResourceFragments = remoteConfigQueryResource.fragments()
        val colorConfiguration = remoteConfigQueryResourceFragments.colorConfiguration()
        val dateConfiguration = remoteConfigQueryResourceFragments.dateConfiguration()
        val imageConfiguration = remoteConfigQueryResourceFragments.imageConfiguration()
        val dateTimeConfiguration = remoteConfigQueryResourceFragments.dateTimeConfiguration()
        val jsonConfiguration = remoteConfigQueryResourceFragments.jsonConfiguration()
        val booleanConfiguration = remoteConfigQueryResourceFragments.booleanConfiguration()
        val numberConfiguration = remoteConfigQueryResourceFragments.numberConfiguration()
        val stringConfiguration = remoteConfigQueryResourceFragments.stringConfiguration()

        return when {
            colorConfiguration != null -> colorConfiguration.key() to Configuration(
                valueString = colorConfiguration.valueColor()
            )

            dateConfiguration != null -> dateConfiguration.key() to Configuration(
                valueString = dateConfiguration.valueDate()
            )

            imageConfiguration != null -> imageConfiguration.key() to Configuration(
                valueString = imageConfiguration.valueImage()
            )

            dateTimeConfiguration != null -> dateTimeConfiguration.key() to Configuration(
                valueString = dateTimeConfiguration.valueDateTime()
            )

            jsonConfiguration != null -> jsonConfiguration.key() to Configuration(
                valueString = jsonConfiguration.valueJson()
            )

            booleanConfiguration != null -> booleanConfiguration.key() to Configuration(
                valueBoolean = booleanConfiguration.valueBoolean()
            )

            numberConfiguration != null -> numberConfiguration.key() to Configuration(
                valueNumber = numberConfiguration.valueNumer()
            )

            stringConfiguration != null -> stringConfiguration.key() to Configuration(
                valueString = stringConfiguration.valueString()
            )

            else -> "" to null
        }
    }
}