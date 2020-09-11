
## Lista de EPG

Recupera lista de epg com todos os programas do dia. O método aceita os seguintes parâmetros:

* **\*opcional\*** `Latitude` \ `Longitude`:  Utilizado para recuperar os canais referentes a sua localidade, por default os valores são null.
* `Scale`: Tamanho da imagem do logo do canal. Para recuperar a scale, basta utilizar `context`.scale().
* `Callback.Channels`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.
* **\*opcional\***  `Date`: Data utilizada para recuperar os programa desse dia, por default os valores são null.

| Dimensão | Mobile | Tablet Portrait | Tablet Landscape |
|---|---|---|---
| 0.75X | X360 | X360 | X720 |
| 1x | X360 | X360 | X720 |
| 1.5X | X360 | X720 | X1080 |
| 2x | X720 | X720 | X1080 |
| 3x | X720 | X1080 | X1080 |

``` kotlin
JarvisClient.epg.all(latitude, longitude, scale, date, object : Callback.Epgs {
    override fun onSuccess(epgVOList: List<EpgVO>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<List<EpgVO>> = JarvisClient.epg.all(latitude, longitude, scale, date)
```


## Detalhes do EPG

Recupera o detalhes baseado numa `data`,  com todos os programas deste dia. O método aceita os seguintes parâmetros:

* `MediaId`: Id da mídia do canal.
* `Scale`: Tamanho da imagem do logo do canal. Para recuperar a scale, basta utilizar `context`.scale().
* **\*opcional\*** `Latitude` \ `Longitude`:  Utilizado para recuperar os canais referentes a sua localidade, por default os valores são null.
* **\*opcional\*** `Date`: Data utilizada para recuperar o epg deste dia, por default o valor é null.
* `Callback.Channels`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

| Dimensão | Mobile | Tablet Portrait | Tablet Landscape |
|---|---|---|---
| 0.75X | X360 | X360 | X720 |
| 1x | X360 | X360 | X720 |
| 1.5X | X360 | X720 | X1080 |
| 2x | X720 | X720 | X1080 |
| 3x | X720 | X1080 | X1080 |

``` kotlin
JarvisClient.epg.details(mediaId, scale, latitude, longitude, date, object : Callback.Epgs {
    override fun onSuccess(epgVO: EpgVO) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<EpgVO> = JarvisClient.epg.details(mediaId, scale, latitude, longitude, date)
```