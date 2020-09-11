## Lista Broadcast

Recupera a lista de broadcast limitado a dois programas. O método aceita os seguintes parâmetros:

* **\*opcional\*** `Latitude` \ `Longitude`:  Utilizado para recuperar os broadcast referentes a sua localidade, por default os valores são null.
* `BroadcastChannelLogoScales`: Tamanho da imagem da logo do canal com safe area.
* `BroadcastChannelTrimmedLogoScales`: Tamanho da imagem da logo do canal sem safe area.
* `BroadcastImageOnAirScales`: Tamanho da imagem do image on air.
* `Limit`: Quantidade de slot's (programas) recuperados.
* `Callback.Channels`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.broadcasts.all(latitude, longitude, broadcastChannelLogoScales, broadcastChannelTrimmedLogoScales, broadcastImageOnAirScales, limit, object : Callback.Channels {
    override fun onSuccess(broadcastVOList: List<BroadcastVO>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<List<BroadcastVO>> = JarvisClient.broadcasts.all(latitude, longitude, broadcastChannelLogoScales, broadcastChannelTrimmedLogoScales, broadcastImageOnAirScales, limit)
```


## Detalhes do Broadcast

Recupera o detalhes do broadcast. O método aceita os seguintes parâmetros:

* `MediaId`: Id da mídia do broadcast.
* **\*opcional\*** `Latitude` \ `Longitude`:  Utilizado para recuperar os broadcast referentes a sua localidade, por default os valores são null.
* `BroadcastChannelLogoScales`: Tamanho da imagem da logo do canal com safe area.
* `BroadcastChannelTrimmedLogoScales`: Tamanho da imagem da logo do canal sem safe area.
* `BroadcastImageOnAirScales`: Tamanho da imagem do image on air.
* `Limit`: Quantidade de slot's (programas) recuperados.
* `Callback.Channels`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

``` kotlin
JarvisClient.broadcasts.all(mediaId, latitude, longitude, broadcastChannelLogoScales, broadcastChannelTrimmedLogoScales, broadcastImageOnAirScales, limit, object : Callback.Channels {
    override fun onSuccess(broadcastVO: BroadcastVO) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<ChannelVO> = JarvisClient.broadcast.details(mediaId, latitude, longitude, broadcastChannelLogoScales, broadcastChannelTrimmedLogoScales, broadcastImageOnAirScales, limit)
```

## Lista de MediaIds

Recupera a lista de mediaIds dos broadcasts.

```kotlin
JarvisClient.broadcast.mediaIds(callback = object : Callback.BroadcastMediaIds {
    override fun onBroadcastMediaIdsSuccess(mediaIds: List<String>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

```kotlin
val observable : Observable<List<String>> = JarvisClient.broadcast.mediaIds()
```

## Lista de EpgCurrentSlots

Recupera a lista dos slots de canais associados aos broadcasts, com os seguintes parâmetros:


* `MediaId`: Id da mídia do broadcast.
* `TransmissionId`: Id para um programa na grade.
* `Limit`: Quantidade de slot retornado.

```kotlin
JarvisClient.broadcast.currentSlots(mediaId, transmissionId, limit, callback = object : Callback.BroadcastCurrentSlots {
    override fun onBroadcastCurrentSlotsSuccess(slots: List<BroadcastSlot>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

```kotlin
val observable : Observable<List<BroadcastSlot>> = JarvisClient.broadcast.currentSlots(mediaId, transmissionId, )
```