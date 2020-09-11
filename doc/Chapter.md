## Lista de Capítulos

Recupera lista de capítulos. O método aceita os seguintes parâmetros:

* `TitleId`: Identificador do título.
* `Page`: Número da página utilizado para paginação.
* `PerPage`: Número de items recuperado por página.
* `Callback.Chapter`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.


``` kotlin
JarvisClient.chapter.all(titleId, page, perPage, object : Callback.Chapter {
    override fun onChaptersSuccess(triple: Triple<List<Chapter>, Boolean, Int>) {}

    override fun onFailure(throwable: Throwable) {}
})
```

Ou

``` kotlin
val observable : Observable<Triple<List<Chapter>, Boolean, Int>> = JarvisClient.chapter.all(titleId, page, perPage)
```

## Lista de Capítulos  por intervalo de data

Recupera lista de capítulos por intervalo de data. O método aceita os seguintes parâmetros:

* `TitleId`: Identificador do título.
* `StartDate`: Data de inicial da busca dos capítulos.
* `EndDate`: Data de final da busca dos capítulos.
* `Page`: Número da página utilizado para paginação.
* `PerPage`: Número de items recuperado por página.
* `Callback.Chapter`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.


``` kotlin
JarvisClient.chapter.byDateRange(titleId, startDate, endDate, page, perPage, object : Callback.Chapter {
    override fun onChapterByDateRangeSuccess(triple: Triple<List<Chapter>, Boolean, Int>) {}

    override fun onFailure(throwable: Throwable) {}
})
```

Ou

``` kotlin
val observable : Observable<Triple<List<Chapter>, Boolean, Int>> = JarvisClient.chapter.byDateRange(titleId, startDate, endDate, page, perPage)
```