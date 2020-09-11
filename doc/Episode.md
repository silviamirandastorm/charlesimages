## Detalhe do Episódio

Recupera o detalhe de um episódio baseado no `videoId`. O método aceita os seguintes parâmetros:

* `VideoId`: Identificador do vídeo.
* `Scale`: Tamanho da imagem do logo do canal. Para recuperar a scale, basta utilizar `context`.scale().
* `Callback.Episodes`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

| Dimensão  | Mobile | Tablet | TV |
|---|---|---|---|
| 0.75X    | 75x114 | 108x159 | --
| 1x       | 100x152 |  144x212 |  --
| 1.5X     | 150x228 | 216x318 |  --
| 2x       | 200x304 | 288x424  |  --
| 3x       | 300x456 | 432x636 |  --
| HD       | -- | -- |  146x216
| FULL_HD  | -- | -- |  324x219
| 4K       | -- | -- |  437x648


``` kotlin
JarvisClient.episode.detail(videoId, scale, object : Callback.Episodes {
    override fun onDetailSuccess(episode: Episode) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<Episode> = JarvisClient.episode.detail(videoId, scale,)
```


## Detalhes dos Episódio e Temporadas

Recupera o detalhes dos episódios baseado no `titleId` mais a lista de temporadas. O método aceita os seguintes parâmetros:

* `TitleId`: Identificador do título.
* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `Callback.Episodes`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.episode.detailsWithSeason(titleId, page, perPage, object : Callback.Episodes {
    override fun onDetailsWithSeasonSuccess(triple: Triple<String, List<Season>, EpisodeDetails>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<Triple<String, List<Season>, EpisodeDetails>> = JarvisClient.episode.detailsWithSeason(titleId, page, perPage)
```



## Detalhes dos Episódio e Vídeos Relacionados Por Data

Recupera o detalhes dos episódios baseado no `titleId` mais a lista de vídeos relacionados por data. O método aceita os seguintes parâmetros:

* `TitleId`: Identificador do título.
* `StartDate`: Data de inicial da busca dos episódios.
* `EndDate`: Data de final da busca dos episódios.
* `Callback.Episodes`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.episode.detailsWithRelatedExcerptsByDate(titleId, startDate, endDate, object : Callback.Episodes {
    override fun onEpisodeAndRelatedExcerptsByDateSuccess(pair: Pair<Video, List<Thumb>>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<Pair<Video, List<Thumb>>> = JarvisClient.episode.detailsWithRelatedExcerptsByDate(titleId, startDate, endDate)
```

