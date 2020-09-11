## Lista Canais

Recupera a lista de canais. O método aceita os seguintes parâmetros:

* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `broadcastChannelTrimmedLogoScales`: Tamanho da trimmed logo do canal.
* `broadcastChannelFilters`: Filtro que pode ser aplicado para retornar canais apenas com página de detalhes
* `Callback.Channels`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

| Dimensão  | Mobile | Tablet | TV |
|---|---|---|---|
| 0.75X | X56 | X56 | -
| 1x | X84 |  X84 | -
| 1.5X | X112 | X112 | X112
| 2x | X168 | X168  | X168
| 3x | X224 | X224 | X224

``` kotlin
JarvisClient.channel.all(page, perPage, perPage, broadcastChannelTrimmedLogoScales, broadcastChannelFilters, object : Callback.Channels {
    override fun onSuccess(channelVOList: List<ChannelVO>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```
        
Ou

``` kotlin
val observable : Observable<List<ChannelVO>> = JarvisClient.channel.all(page, perPage, perPage, broadcastChannelTrimmedLogoScales, broadcastChannelFilters)
```


 