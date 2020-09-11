## Lista Configurações Remotas

Recupera a lista de configurações remotas. O método aceita os seguintes parâmetros:

* `RemoteConfigGroups`: Plataforma que requisitando os dados.
* `Scopes`: Lista das áreas que contem configurações remotas.
* `Callback.RemoteConfig`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.remoteConfig.all(remoteConfigGroups, scopes, object : Callback.RemoteConfig {
    override fun fun onRemoteConfigSuccess(remoteConfigurationList: List<RemoteConfiguration>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<List<RemoteConfiguration>> = JarvisClient.remoteConfig.all(remoteConfigGroups, scopes)
```
