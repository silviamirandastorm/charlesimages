## Detalhes do Vídeo

Recupera o detalhes do vídeo baseado no `videoId`. O método aceita os seguintes parâmetros:

* `videoId`: Código do país default.
* `Scale`: Tamanho da imagem do logo do canal. Para recuperar a scale, basta utilizar `context`.scale().
* `Callback.Videos`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

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
JarvisClient.video.details(videoId, scale, object : Callback.Videos {
    override fun onDetailsSuccess(video: Video) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<Video> = JarvisClient.video.details(videoId, scale)
```

## Próximo Vídeo

Recupera o detalhes do próximo vídeo baseado no `videoId`. O método aceita os seguintes parâmetros:

* `videoId`: Código do país default.
* `Callback.Videos`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

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
JarvisClient.video.nextVideo(videoId, object : Callback.Videos {
    override fun onNextVideoSuccess(pair: Pair<Boolean, Video>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<Pair<Boolean, Video>> = JarvisClient.video.nextVideo(videoId)
```

