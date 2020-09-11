## Busca de Vídeos + Títulos

Busca vídeos e títulos baseado numa query. O método aceita os seguintes parâmetros:

* `Query`: Valor pesquisado pelo usuário.
* `Scale`: Tamanho do poster do título. Para recuperar a scale, basta utilizar `context`.scale().
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `Callback.Search`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

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
JarvisClient.search.all(query, scale, perPage, object : Callback.Search {
    override fun onSearchSuccess(pair: Pair<SearchTitlesVO, SearchVideosVO>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<Pair<SearchTitlesVO, SearchVideosVO>> = JarvisClient.search.all(query, scale, perPage)
```

## Busca Global de Vídeos + Títulos
:bangbang: :bomb: O REQUEST É  SÍNCRONO :bomb: :bangbang:

Busca global de vídeos e títulos baseado numa query. O método aceita os seguintes parâmetros:

* `Query`: Valor pesquisado pelo usuário.
* `ScaleCover`: Tamanho do poster do título. Para recuperar a scale, basta utilizar `context`.scale().
* `Callback.Search`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

| Dimensão  | Mobile | Tablet Portrait | Tablet Landscape | TV |
|---|---|---|---|---|
| 0.75X    | CoverPortraitScales.X288 | CoverLandscapeScales.X276 | CoverLandscapeScales.X464 | --
| 1x       | CoverPortraitScales.X384 | CoverLandscapeScales.X348 | CoverLandscapeScales.X552 |  --
| 1.5X     | CoverPortraitScales.X576 | CoverLandscapeScales.X464 |  CoverLandscapeScales.X720 | --
| 2x       | CoverPortraitScales.X768 | CoverLandscapeScales.X552  |  CoverLandscapeScales.X928 | --
| 3x       | CoverPortraitScales.X1152 | CoverLandscapeScales.X828 |  CoverLandscapeScales.X1392 | --
| HD       | -- | -- |  -- | CoverWideScales.X696
| FULL_HD  | -- | -- |  -- | CoverWideScales.X928
| 4K       | -- | -- |  -- | CoverWideScales.X1392

``` kotlin
JarvisClient.search.global(query, scaleCover, object : Callback.Search {
    override fun onGlobalSearchSuccess(pair: Pair<List<TitleVO>, List<ThumbVO>>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<Pair<List<TitleVO>, List<ThumbVO>>> = JarvisClient.search.global(query, scaleCover)
```


## Busca de Vídeos

Busca vídeos baseado numa query. O método aceita os seguintes parâmetros:

* `Query`: Valor pesquisado pelo usuário.
* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `Callback.Search`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.search.video(query, page, perPage, object : Callback.User {
    override fun onSearchVideoSuccess(searchVideosVO: SearchVideosVO) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<SearchVideosVO> = JarvisClient.search.video(query, page, perPage)
```


## Busca de Canais

Busca canais baseado numa query. O método aceita os seguintes parâmetros:

* `Query`: Valor pesquisado pelo usuário.
* `BroadcastChannelTrimmedLogoScales`: Tamanho da trimmed logo do canal.
* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `Callback.Search`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.search.channels(query, page, perPage, object : Callback.Search {
    override fun onSearchChannelsSuccess(searchChannel: SearchChannel) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<SearchChannel> = JarvisClient.search.channels(query, page, perPage)
```


## Busca  Títulos

Busca títulos baseado numa query. O método aceita os seguintes parâmetros:

* `Query`: Valor pesquisado pelo usuário.
* `Scale`: Tamanho do poster do título. Para recuperar a scale, basta utilizar `context`.scale().
* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `Callback.Search`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

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
JarvisClient.search.title(query, scale, page, perPage, object : Callback.Search {
    override fun onSearchTitleSuccess(searchTitlesVO: SearchTitlesVO) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<SearchTitlesVO> = JarvisClient.search.title(query, scale, page, perPage)
```


## Busca Títulos Mais Vistos

Busca títulos mais vistos. O método aceita os seguintes parâmetros:

* `PerPage`: Número de items recuperado por página, tedo o valor default como 3 items.
* `Callback.Search`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.search.topHits(perPage, object : Callback.Search {
    override fun onTopHitsSuccess(topHitsVOList: List<TopHitsVO>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<List<TopHitsVO>> = JarvisClient.search.topHits(perPage)
```


