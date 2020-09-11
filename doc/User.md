## Minha Lista

Recupera a lista de título favoritados pelo usuário. O método aceita os seguintes parâmetros:

* `Scale`: Tamanho do poster do título. Para recuperar a scale, basta utilizar `context`.scale().
* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 6 items.
* `Callback.User`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

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
JarvisClient.userRepository.myList(scale, page, perPage, object : Callback.User {
    override fun onMyListSuccess(myListVO: MyListVO) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<MyListVO> = JarvisClient.userRepository.myList(scale, page, perPage)
```

## Adicionar Minha Lista 

Adiciona o item na minha lista. O método aceita os seguintes parâmetros:

* `TitleId`: Identificador do título que está sendo adicionado a lista do usuário.
* `Callback.User`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.userRepository.addToMyList(titleId, object : Callback.User {
    override fun onAddMyListSuccess(addedMyList: Boolean) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```
Ou

``` kotlin
val observable : Observable<Boolean> = JarvisClient.userRepository.addToMyList(titleId)
```


## Remover Minha Lista 

Remove o item da minha lista. O método aceita os seguintes parâmetros:

* `TitleId`: Identificador do título que está sendo removido da lista do usuário.
* `Callback.User`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.userRepository.deleteMyList(titleId, object : Callback.User {
    override fun onDeleteMyListSuccess(deletedMyList: Boolean) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<Boolean> = JarvisClient.userRepository.deleteMyList(titleId)
```


## Últimos Vídeos Assistidos

Recupera a lista de vídeos assistidos pelo usuário. O método aceita os seguintes parâmetros:

* `GlbId`: Token de sessão do usuário.
* `Scale`: Tamanho do poster do título. Para recuperar a scale, basta utilizar `context`.scale().
* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 6 items.
* `Callback.User`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

| Dimensão  | Mobile | Tablet | TV |
|---|---|---|---|
| 0.75X    | 197x111 | 228x129 | --
| 1x       | 262x147 |  304x171 |  --
| 1.5X     | 393x221 | 456x257 |  --
| 2x       | 524x294 | 608x342  |  --
| 3x       | 786x441 | 912x513 |  --
| HD       | -- | -- |  292x165
| FULL_HD  | -- | -- |  248x438
| 4K       | -- | -- |  876x496

``` kotlin
JarvisClient.userRepository.lastVideos(glbId, scale, page, perPage, object : Callback.User {
    override fun onMyLastedWatchedVideosSuccess(continueWatchingVOList: List<ContinueWatchingVO>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<List<ContinueWatchingVO>> = JarvisClient.userRepository.lastVideos(glbId, scale, page, perPage)
```
