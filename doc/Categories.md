## Detalhes Categorias By Slug

Recupera o detalhes de uma categoria baseado no `slug`. O método aceita os seguintes parâmetros:

* `Slug`: Nome da categoria buscada.
* `Scale`: Tamanho da imagem do logo do canal. Para recuperar a scale, basta utilizar `context`.scale().
* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `Callback.Categories`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

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
JarvisClient.categories.detailsCategoryBySlug(slug, scale, page, perPage, object : Callback.Categories {
    override fun onDetailsCategoryBySlugSuccess(categoryDetails: CategoryDetails) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<CategoryDetails> = JarvisClient.categories.detailsCategoryBySlug(slug, scale, page, perPage)
```




## Detalhes Categorias By Id

Recupera o detalhes de uma categoria baseado no `id`. O método aceita os seguintes parâmetros:

* `Id`: Identificador da categoria.
* `Scale`: Tamanho da imagem do logo do canal. Para recuperar a scale, basta utilizar `context`.scale().
* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `Callback.Categories`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

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
JarvisClient.categories.detailsCategoryById(id, scale, page, perPage, object : Callback.Categories {
    override fun onDetailsCategoryByIdSuccess(categoryDetails: CategoryDetails) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<CategoryDetails> = JarvisClient.categories.detailsCategoryById(slug, scale, page, perPage)
```



## Detalhes Grupo Categorias

Recupera o detalhes de um grupo de categoria. O método aceita os seguintes parâmetros:

* `Scale`: Tamanho da imagem do logo do canal. Para recuperar a scale, basta utilizar `context`.scale().
* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `CategoriesList`: Lista com o nome das categorias a serem recuperados os detalhes.
* `Callback.Categories`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

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
JarvisClient.categories.detailsGroup(scale, page, perPage, categoriesList, object : Callback.Categories {
    override fun onGroupCategoriesSuccess(categoryDetailsList: List<CategoryDetails>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<List<CategoryDetails>> = JarvisClient.categories.detailsGroup(scale, page, perPage, categoriesList)
```


## Lista de Categorias

Recupera a lista de categorias. O método aceita os seguintes parâmetros:

* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `Callback.Categories`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.


``` kotlin
JarvisClient.categories.list(page, perPage, object : Callback.Categories {
    override fun onCategoriesSuccess(triple: Triple<Int, Boolean, List<Category>>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<Triple<Int, Boolean, List<Category>>> = JarvisClient.categories.list(page, perPage)
```


## Detalhes Categorias Grid

Recupera o detalhes de uma categoria exibida em grid. O método aceita os seguintes parâmetros:

* `Slug`: Nome da categoria buscada.
* `Id`: Identificador da categoria.
* `Scale`: Tamanho da imagem do logo do canal. Para recuperar a scale, basta utilizar `context`.scale().
* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `Callback.Categories`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

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
JarvisClient.categories.detailsGrid(slug, id, scale, page, perPage, object : Callback.Categories {
    override fun onCategoryDetailsGridSuccess(categoryDetails: CategoryDetails) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<CategoryDetails> = JarvisClient.categories.detailsGrid(slug, id, scale, page, perPage)
```


## Detalhes Categorias Página

Recupera o detalhes de uma categoria exibida em forma de página (dinânmica). O método aceita os seguintes parâmetros:

* `Id`: Identificador da categoria.
* `Scale`: Tamanho da imagem do logo do canal. Para recuperar a scale, basta utilizar `context`.scale().
* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `Callback.Categories`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

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
JarvisClient.categories.detailsPage(id, scale, page, perPage, object : Callback.Categories {
    override fun onCategoryDetailsPageSuccess(pair: Pair<String?, List<CategoryOffer>>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<Pair<String?, List<CategoryOffer>>> = JarvisClient.categories.detailsPage(id, scale, page, perPage)
```


## Detalhes Oferta de Título

Recupera o detalhes de uma oferta do tipo título. O método aceita os seguintes parâmetros:

* `Id`: Identificador da oferta.
* `OfferTitle`: Título da oferta.
* `PageComponentType`: Tipo da oferta.
* `Navigation`: Destino que a oferta leva quando clicada.
* `Scale`: Tamanho da imagem do logo do canal. Para recuperar a scale, basta utilizar `context`.scale().
* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `Callback.Categories`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

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
JarvisClient.categories.detailsOfferTitle(id, offerTitle, pageComponentType, pageOfferFragmentNavigation, scale, page, perPage, object : Callback.Categories {
    override fun onDetailsOfferTitleSuccess(categoryOffer: CategoryOffer) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<CategoryOffer> = JarvisClient.categories.detailsOfferTitle(id, offerTitle, pageComponentType, pageOfferFragmentNavigation, scale, page, perPage)
```


## Próxima Página Grid

Recupera o detalhes da próxima página de uma categoria exibida em grid. O método aceita os seguintes parâmetros:

* `Id`: Identificador da categoria.
* `Scale`: Tamanho da imagem do logo do canal. Para recuperar a scale, basta utilizar `context`.scale().
* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `Callback.Categories`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

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
JarvisClient.categories.paginationGrid(id, scale, page, perPage, object : Callback.Categories {
    override fun onPaginationGridSuccess(categoryOffer: CategoryOffer) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<CategoryOffer> = JarvisClient.categories.paginationGrid(id, scale, page, perPage)
```


## Lista de Ofertas

Recupera a lista de ofertas. O método aceita os seguintes parâmetros:

* `Id`: Identificador da categoria.
* `Scale`: Tamanho da imagem do logo do canal. Para recuperar a scale, basta utilizar `context`.scale().
* `Callback.Categories`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

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
JarvisClient.categories.structure(id, scale,  object : Callback.Categories {
    override fun onStructureSuccess(triple: Triple<String?, CategoryDetailsStructureQuery.PremiumHighlight?, List<CategoryDetailsStructureQuery.OfferItem>>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<CategoryOffer> = JarvisClient.categories.structure(id, scale)
```


## Detalhes da Oferta

Recupera o detalhes de uma oferta. O método aceita os seguintes parâmetros:

* `OfferItemList`: Lista de ofertas da categoria.
* `Scale`: Tamanho da imagem do logo do canal. Para recuperar a scale, basta utilizar `context`.scale().
* `Page`: Número da página utilizado para paginação, tendo o valor default como 1.
* `PerPage`: Número de items recuperado por página, tedo o valor default como 12 items.
* `Callback.Categories`: Interface que deve ser instânciada para obter o resultado de erro ou sucesso.

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
JarvisClient.categories.detailsOffer(offerItemList, scale, page, perPage, object : Callback.Categories {
    override fun onOfferDetailsSuccess(categoryOfferList: List<CategoryOffer>) {
    }

    override fun onFailure(throwable: Throwable) {
    }
})
```

Ou

``` kotlin
val observable : Observable<List<CategoryOffer>> = JarvisClient.categories.detailsOffer(offerItemList, scale, page, perPage)
```
