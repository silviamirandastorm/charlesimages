## Lista estados

Recupera a lista de estados. O método aceita os seguintes parâmetros:

* `Callback.State`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.states.all(object : Callback.State {
    override fun onStatesSuccess(statesList: List<States>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```
        
Ou

``` kotlin
val observable : Observable<List<States>> = JarvisClient.states.all()
```


## Lista Regiões

Recupera a lista de regiões. O método aceita os seguintes parâmetros:

* `Callback.Region`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.regions.all(object : Callback.Region {
    override fun onRegionsSuccess(regionsList: List<Regions>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```
        
Ou

``` kotlin
val observable : Observable<List<Regions>> = JarvisClient.regions.all()
```


## Lista Regiões Por Estado

Recupera a lista de regiões por estado. O método aceita os seguintes parâmetros:

* `Acronym`: Singla do estado.
* `Callback.Region`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.regions.all("RJ", object : Callback.Region {
    override fun onRegionsSuccess(regionsList: List<Regions>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<List<Regions>> = JarvisClient.regions.all("RJ")
```



## Lista Programas Afiliadas

Recupera a lista de programs das afiliadas. O método aceita os seguintes parâmetros:

* `Callback.AffiliatePrograms`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.affiliatePrograms.all(object : Callback.AffiliatePrograms {
    override fun onAffiliateProgramsSuccess(triple: Triple<Boolean, Int?, List<Title>>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```
        
Ou

``` kotlin
val observable : Observable<Triple<Boolean, Int?, List<Title>>> = JarvisClient.affiliatePrograms.all()
```


 