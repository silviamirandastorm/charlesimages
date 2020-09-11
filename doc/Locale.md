## Locale

Recupera localização baseado no `Ip Address` que contém o `countryCode` e o `tenant`. O método aceita os seguintes parâmetros:

* `CountryCodeDefault`: Código default para país.
* `TenantDefault`: Id default da aplicação.
* `Callback.Locales`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.locale.find(countryCodeDefault, tenantDefault, object : Callback.Locales {
    override fun onLocaleSuccess(locale: Locale) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<Locale> = JarvisClient.locale.find(countryCodeDefault, tenantDefault)
```

