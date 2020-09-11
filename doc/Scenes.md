## Cena com lista de thumbs

Cria cena e recupela lista de thumbs. O método aceita os seguintes parâmetros:

* `VideoId`: Identificador do vídeo.
* `Number`: Número da temporada.
* `Headline`: Título do trilho de cenas.
* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `ThumbSmall`: Tamanho do thumb
* `ThumbLarge`: Tamanho do thumb
* `Callback.Scenes`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.scenes.withThumbs(videoId, number, headline, page, perPage, thumbSmall, thumbLarge, object: Callback.Scenes {
    override fun onScenesSuccess(triple: Triple<Boolean, Int, List<Scene>>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<Scene> = JarvisClient.scenes.withThumbs(videoId, number, headline, page, perPage, thumbSmall, thumbLarge)
```


## Cena com informaçãoes de temporada

Recupera a lista de cenas com informações de temporada. O método aceita os seguintes parâmetros:

* `VideoId`: Identificador do vídeo.
* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `ThumbSmall`: Tamanho do thumb
* `ThumbLarge`: Tamanho do thumb
* `Callback.Scenes`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.scenes.withSeason(videoId, page, perPage, thumbSmall, thumbLarge, object: Callback.Scenes {
    override fun onScenesWithSeasonSuccess(triple: Triple<Boolean, Int, List<Scene>>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<Pair<List<Season>, Triple<Boolean, Int, List<Scene>>>> = JarvisClient.scenes.withSeason(videoId, page, perPage, thumbSmall, thumbLarge)
```

## Cena com informaçãoes de temporada s/ detalhes de cena

Recupera a lista de cenas com informações de temporada sem os detalhes de cena. O método aceita os seguintes parâmetros:

* `VideoId`: Identificador do vídeo.
* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `Callback.Scenes`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.scenes.withoutSeasonDetails(videoId, page, perPage, object: Callback.Scenes {
    override fun onScenesWithSeasonSuccess(triple: Triple<Boolean, Int, List<Scene>>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<Pair<Pair<Season?, List<Season>>,
                             Triple<Boolean, Int, List<ScenesPreview>>>> = JarvisClient.scenes.withoutSeasonDetails(videoId, page, perPage)
```


## Lista de cenas s/ detalhes

Recupera a lista de cenas sem os detalhes. O método aceita os seguintes parâmetros:

* `titleId`: Identificador do título.
* `seasonId`: Identificador da temporada.
* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `ThumbSmall`: Tamanho do thumb
* `ThumbLarge`: Tamanho do thumb
* `Callback.Scenes`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.scenes.withoutDetails(titleId, seasonId, page, perPage, thumbSmall, thumbLarge object: Callback.Scenes {
    override fun onScenesWithoutDetailsSuccess(triple: Triple<Boolean, Int, List<ScenesPreview>>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<Triple<Boolean, Int, List<ScenesPreview>>> = JarvisClient.scenes.withoutDetails(titleId, seasonId, page, perPage, thumbSmall, thumbLarge)
```

## Detalhes das cenas

Recupera detalhes de uma lista de cenas. O método aceita os seguintes parâmetros:

* `listScenesPreview`: Lista de ScenesPreview.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `ThumbSmall`: Tamanho do thumb
* `ThumbLarge`: Tamanho do thumb
* `Callback.Scenes`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.scenes.detailsScenes(listScenesPreview, page, perPage, thumbSmall, thumbLarge, object: Callback.Scenes {
    override fun onScenesDetailsSuccess(sceneList: List<Scene>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<List<Scene>>= JarvisClient.scenes.detailsScenes(listScenesPreview, page, perPage, thumbSmall, thumbLarge)
```


