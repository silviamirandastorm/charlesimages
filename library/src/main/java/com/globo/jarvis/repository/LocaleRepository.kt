package com.globo.jarvis.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.rx
import com.globo.jarvis.Callback
import com.globo.jarvis.locale.LocaleQuery
import com.globo.jarvis.model.CountryCode
import com.globo.jarvis.model.Locale
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LocaleRepository constructor(
    private val apolloClient: ApolloClient
) {
    private val compositeDisposable by lazy { CompositeDisposable() }

    //Callback
    fun find(
        countryCodeDefault: CountryCode,
        tenantDefault: String,
        localesCallback: Callback.Locales
    ) {
        compositeDisposable.add(
            find(countryCodeDefault, tenantDefault)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        localesCallback.onLocaleSuccess(it)
                    },
                    { throwable ->
                        localesCallback.onFailure(throwable)
                    }
                ))
    }

    //RxJava
    fun find(countryCodeDefault: CountryCode, tenantDefault: String): Observable<Locale> =
        apolloClient
            .query(LocaleQuery.builder().build())
            .rx()
            .subscribeOn(Schedulers.io())
            .map {
                return@map transformLocaleQueryToLocaleVO(
                    it.data()?.locale(),
                    countryCodeDefault,
                    tenantDefault
                )
            }
            .onErrorResumeNext(
                Observable.defer {
                    Observable.just(
                        Locale(tenantDefault, countryCodeDefault)
                    )
                }
            )
            .onExceptionResumeNext(
                Observable.defer {
                    Observable.just(
                        Locale(tenantDefault, countryCodeDefault)
                    )
                }
            )

    internal fun transformLocaleQueryToLocaleVO(
        localeQuery: LocaleQuery.Locale?,
        countryCodeDefault: CountryCode,
        tenantDefault: String
    ) = Locale(
        localeQuery?.tenant().let { if (!it.isNullOrEmpty()) it else tenantDefault },
        CountryCode.safeValueOf(localeQuery?.countryCode() ?: countryCodeDefault.value)
    )
}
