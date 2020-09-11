## Lista de Datas

Recupera a lista de datas com conteúdos disponíveis. O método aceita os seguintes parâmetros:

* `TitleId`: Identificador do título.
* `Callback.Dates`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.


``` kotlin
JarvisClient.calendar.all(titleId, object : Callback.Dates {
    override fun onDatesSuccess(pair: Pair<List<Calendar>, List<String>>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<Pair<List<Calendar>, List<String>>> = JarvisClient.calendar.all(titleId)
```
