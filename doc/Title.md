## Cover do Título

Recupera a imagem do cover do título com a url da imagem a ser baixada. O método aceita os seguintes parâmetros:

* `ProgramId`:  Id do programa utilizado para recuperar a imagem do cover de um programa.
* `Scale`: Tamanho da imagem do logo do canal. Para recuperar a scale, basta utilizar `context`.scale().
* `Callback.Cover`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

| Dimensão | Mobile | Tablet Portrait | Tablet Landscape |
|---|---|---|---
| 0.75X | X276 | X276 | X464 |
| 1x | X348 | X348 | X552 |
| 1.5X | X464 | X464 | X720 |
| 2x | X552 | X552 | X928 |
| 3x | X828 | X828 | X1392 |

``` kotlin
JarvisClient.title.cover(programId, scale, object : Callback.TitleCover {
    override fun onSuccess(coverVO: CoverVO) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<CoverVO> = JarvisClient.title.cover(mediaId, scale)
```

## Recupera title format

Recupera formato do título. O método aceita os seguintes parâmetros:

* `TitleId`: Identificador do título.
* `Callback.Title`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.


``` kotlin
JarvisClient.chapter.formatAndEpgActive(titleId, object : Callback.Title {
    override fun onFormatSuccess(format: Format) {}

    override fun onFailure(throwable: Throwable) {}
})
```

Ou

``` kotlin
val observable : Observable<Format> = JarvisClient.chapter.format(titleId)
```

## Recupera title epg active 

Recupera epgActive. O método aceita os seguintes parâmetros:

* `TitleId`: Identificador do título.
* `Callback.Title`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.


``` kotlin
JarvisClient.chapter.epgActive(titleId, object : Callback.Title {
    override fun onEpgActiveSuccess(isEpgActive: Boolean) {}

    override fun onFailure(throwable: Throwable) {}
})
```

Ou

``` kotlin
val observable : Observable<Boolean> = JarvisClient.chapter.epgActive(titleId)
```
