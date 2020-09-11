package com.globo.jarvis.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.rx
import com.globo.jarvis.Callback
import com.globo.jarvis.Device
import com.globo.jarvis.cover.CoverQuery
import com.globo.jarvis.fragment.*
import com.globo.jarvis.model.*
import com.globo.jarvis.recommendation.SuggestForTitleQuery
import com.globo.jarvis.title.*
import com.globo.jarvis.type.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class TitleRepository(
    private val apolloClient: ApolloClient,
    private val device: Device
) {
    companion object {
        const val SPLIT = ", "
    }

    private val compositeDisposable by lazy { CompositeDisposable() }

    // =============================================================================================
    // Functions with response by callback.
    // =============================================================================================

    @JvmOverloads
    fun all(
        titleId: String?,
        scale: String,
        userLogged: Boolean,
        titleCallback: Callback.Titles,
        programId: String? = null
    ) {
        compositeDisposable.add(
            all(titleId, scale, userLogged, programId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        titleCallback.onAllSuccess(it)
                    },

                    { throwable ->
                        titleCallback.onFailure(throwable)
                    }
                ))
    }

    fun details(
        titleId: String?,
        programId: String?,
        scale: String,
        titleCallback: Callback.Titles
    ) {
        compositeDisposable.add(
            details(titleId, programId, scale)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        titleCallback.onDetailSuccess(it)
                    },

                    { throwable ->
                        titleCallback.onFailure(throwable)
                    }
                ))
    }

    fun detailsWithUser(
        titleId: String?,
        userLogged: Boolean,
        titleCallback: Callback.Titles
    ) {
        compositeDisposable.add(
            detailsWithUser(titleId, userLogged)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        titleCallback.onDetailWithUserSuccess(it)
                    },

                    { throwable ->
                        titleCallback.onFailure(throwable)
                    }
                ))
    }

    fun cover(
        titleId: String?,
        scale: String,
        titlesCallback: Callback.Titles
    ) {
        compositeDisposable.add(
            cover(titleId, scale)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    {
                        titlesCallback.onCoverSuccess(it)
                    },
                    { throwable ->
                        titlesCallback.onFailure(throwable)
                    }
                ))
    }

    @JvmOverloads
    fun titleSuggestOfferId(
        titleId: String?,
        titleFormat: TitleFormat?,
        titleSuggestOfferIdCallback: Callback.TitleSuggestOfferId,
        group: SuggestGroups? = SuggestGroups.TITLE_SCREEN
    ) {
        compositeDisposable.add(
            titleSuggestOfferId(titleId, titleFormat, group)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    compositeDisposable.clear()
                }
                .subscribe(
                    { (suggestOfferId, abExperiment) ->
                        val pair = Pair(suggestOfferId, abExperiment)
                        titleSuggestOfferIdCallback.onSuccess(pair)
                    },
                    { throwable ->
                        titleSuggestOfferIdCallback.onFailure(throwable)
                    }
                )
        )
    }

    // =============================================================================================
    // Functions with response by RxJava.
    // =============================================================================================

    @JvmOverloads
    fun all(
        titleId: String?,
        scale: String,
        userLogged: Boolean,
        programId: String? = null
    ): Observable<Pair<Title, TitleUser>> =
        Observable.zip(
            details(titleId, programId, scale),
            detailsWithUser(titleId, userLogged),
            BiFunction<Title, TitleUser, Pair<Title, TitleUser>>
            { titleVO: Title, titleUserVO: TitleUser ->

                return@BiFunction Pair(titleVO, titleUserVO)
            })

    fun details(
        titleId: String?,
        programId: String?,
        scale: String
    ): Observable<Title> = apolloClient
        .query(builderDetailsTitleQuery(titleId, programId, scale))
        .rx()
        .subscribeOn(Schedulers.io())
        .map { jarvisResponse ->
            jarvisResponse.data()?.title()?.let { jarvisTitle ->
                val videoPlayback = jarvisTitle.structure()?.fragments()?.videoPlayback()
                val seasoned = jarvisTitle.structure()?.fragments()?.seasoned()
                val episode = jarvisTitle.structure()?.fragments()?.episode()
                val hasEpisodes = jarvisTitle.structure()?.fragments()?.hasEpisodes()
                val editorialOffers = jarvisTitle.extras()?.editorialOffers()

                val titleDetails = TitleDetails(
                    title = jarvisTitle.headline(),
                    originalHeadline = jarvisTitle.originalHeadline(),
                    formattedDuration = videoPlayback?.videoPlayback()?.formattedDuration(),
                    year = jarvisTitle.releaseYear()?.toString(),
                    country = transformListName(jarvisTitle.countries()),
                    directors = transformListName(jarvisTitle.directorsNames()),
                    cast = transformListName(jarvisTitle.castNames()),
                    genders = transformListName(jarvisTitle.genresNames()),
                    author = transformListName(jarvisTitle.authorsNames()),
                    screeWriter = transformListName(jarvisTitle.screenwritersNames()),
                    artDirectors = transformListName(jarvisTitle.artDirectorsNames()),
                    summary = jarvisTitle.description(),
                    contentRating = ContentRating(
                        jarvisTitle.contentRating(),
                        jarvisTitle.contentRatingCriteria()?.joinToString(SPLIT)?.capitalize()
                    )
                )

                return@map Title(
                    titleId = jarvisTitle.titleId(),
                    headline = jarvisTitle.headline(),
                    description = jarvisTitle.description(),
                    abExperiment = AbExperiment(pathUrl = jarvisTitle.url()),
                    titleDetails = titleDetails,
                    video = builderDefaultVideo(videoPlayback, seasoned, episode),
                    type = Type.normalize(jarvisTitle.type()),
                    format = Format.normalize(jarvisTitle.format()),
                    isEpgActive = jarvisTitle.epgActive(),
                    accessibleOffline = videoPlayback?.videoPlayback()?.accessibleOffline()
                        ?: false,
                    enableEpisodesTab = shouldEnableEpisodeTab(seasoned?.seasons()?.resources()),
                    enableScenesTab = shouldEnableScenesTab(seasoned?.seasonsWithExcerpts()),
                    enableChapterTab = shouldEnableChaptersProgramsOrEditionsTab(
                        hasEpisodes?.episodes()?.resources()
                    ),
                    enableProgramsTab = shouldEnableChaptersProgramsOrEditionsTab(
                        hasEpisodes?.episodes()?.resources()
                    ),
                    enableEditionsTab = shouldEnableChaptersProgramsOrEditionsTab(
                        hasEpisodes?.episodes()?.resources()
                    ),
                    enableExcerptsTab = shouldEnableExcerptsTab(
                        hasEpisodes?.excerpts()?.resources()
                    ),
                    enableEditorialTab = shouldEnableEditorialTab(editorialOffers),
                    cover = when (device) {
                        Device.TV -> jarvisTitle.cover()?.tv()
                        Device.TABLET -> jarvisTitle.cover()?.tablet()
                        else -> jarvisTitle.cover()?.mobile()
                    },
                    editorialOfferIds = transformListOfTitleEditorialOfferToListOfEditorialOfferId(
                        editorialOffers
                    )
                )

            } ?: throw Exception("Response do título inválido!")
        }

    fun detailsWithUser(
        titleId: String?,
        userLogged: Boolean
    ): Observable<TitleUser> =
        if (userLogged) apolloClient
            .query(
                TitleUserQuery
                    .builder()
                    .titleId(titleId ?: "")
                    .build()
            )
            .rx()
            .subscribeOn(Schedulers.io())
            .map {
                if (it.hasErrors()) {
                    Observable.defer { Observable.just(TitleUser()) }
                }

                val title = it.data()?.title()

                val filmContinueWatching =
                    title?.structure()?.fragments()?.filmStructureTitleUser()?.continueWatching()

                val seasonContinueWatching =
                    title?.structure()?.fragments()?.seasonStructureTitleUser()?.continueWatching()

                val episodeContinueWatching =
                    title?.structure()?.fragments()?.episodeStructureTitleUser()?.continueWatching()

                val continueWatching =
                    transformStructureToContinueWatching(
                        filmContinueWatching,
                        seasonContinueWatching,
                        episodeContinueWatching
                    )

                return@map TitleUser(continueWatching, title?.favorited() == true)
            }
        else Observable.defer { Observable.just(TitleUser()) }


    fun cover(titleId: String?, scaleCover: String): Observable<Cover> = apolloClient
        .query(builderQueryCover(titleId, scaleCover))
        .rx()
        .map { responseGetCoverQueryData: Response<CoverQuery.Data> ->
            val cover = responseGetCoverQueryData.data()?.title()?.cover()
            return@map Cover(cover?.mobile(), cover?.tabletPortrait(), cover?.tabletLandscape())
        }

    fun titleSuggestOfferId(
        titleId: String?,
        titleFormat: TitleFormat?,
        group: SuggestGroups? = SuggestGroups.TITLE_SCREEN
    ): Observable<Pair<String?, AbExperiment?>> = apolloClient
        .query(builderSuggestForTitleQuery(group, titleFormat, titleId))
        .rx()
        .subscribeOn(Schedulers.io())
        .map { response ->
            val bestFit = response.data()?.user()?.suggest()?.bestFit()
            val suggestOfferId =
                bestFit?.resource()?.fragments()?.suggestTitleRecommendedOfferFragment()?.id()
                    ?: bestFit?.resource()?.fragments()?.suggestTitleOfferFragment()?.id()

            val jarvisABExperiment = bestFit?.abExperiment()?.let {
                AbExperiment(
                    alternative = it.alternative(),
                    trackId = it.trackId(),
                    experiment = it.experiment()
                )
            }
            return@map Pair(suggestOfferId, jarvisABExperiment)
        }

    // Callback
    fun format(
        titleId: String?,
        titleCallback: Callback.Titles
    ) = compositeDisposable.add(
        format(titleId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                compositeDisposable.clear()
            }
            .subscribe(
                {format ->
                    titleCallback.onFormatSuccess(format)
                },
                { throwable ->
                    titleCallback.onFailure(throwable)
                }
            )
    )

    // RX
    fun format(titleId: String?) =
        apolloClient
            .query(builderTitleFormatQuery(titleId))
            .rx()
            .subscribeOn(Schedulers.io())
            .map { response ->
                Format.normalize(response.data()?.title()?.format())
            }

    // Callback
    fun epgActive(
        titleId: String?,
        chaptersCallback: Callback.Titles
    ) = compositeDisposable.add(
        epgActive(titleId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                compositeDisposable.clear()
            }
            .subscribe(
                {isEpgActive ->
                    chaptersCallback.onEpgActiveSuccess(isEpgActive)
                },
                { throwable ->
                    chaptersCallback.onFailure(throwable)
                }
            )
    )

    // RX
    fun epgActive(titleId: String?) =
        apolloClient
            .query(builderTitleEpgActiveQuery(titleId))
            .rx()
            .subscribeOn(Schedulers.io())
            .map { response ->
                response.data()?.title()?.epgActive() ?: true
            }

    // =============================================================================================
    // Internal Functions.
    // =============================================================================================

    internal fun shouldEnableScenesTab(seasonsWithExcerpt: List<Seasoned.SeasonsWithExcerpt>?) =
        !seasonsWithExcerpt.isNullOrEmpty()

    internal fun shouldEnableEpisodeTab(seasonStructure: List<Seasoned.Resource>?) =
        !seasonStructure?.filter { (it.totalEpisodes() ?: 0) >= 1 }.isNullOrEmpty()

    internal fun shouldEnableChaptersProgramsOrEditionsTab(chaptersStructure: List<HasEpisodes.Resource1>?) =
        !chaptersStructure.isNullOrEmpty()

    internal fun shouldEnableExcerptsTab(excerptsStructure: List<HasEpisodes.Resource>?) =
        !excerptsStructure.isNullOrEmpty()

    internal fun shouldEnableEditorialTab(editorialStructure: List<TitleQuery.EditorialOffer>?) =
        !editorialStructure.isNullOrEmpty()

    internal fun transformListName(nameResponseList: List<String>?) =
        nameResponseList?.joinToString(SPLIT)

    // =============================================================================================
    // Transform Functions.
    // =============================================================================================

    internal fun transformStructureToContinueWatching(
        filmContinueWatching: FilmStructureTitleUser.ContinueWatching?,
        seasonContinueWatching: SeasonStructureTitleUser.ContinueWatching?,
        episodeContinueWatching: EpisodeStructureTitleUser.ContinueWatching?
    ): ContinueWatching? =
        when {
            filmContinueWatching != null -> with(filmContinueWatching) {
                ContinueWatching(
                    id = id(),
                    headline = headline(),
                    description = description(),
                    duration = duration() ?: 0,
                    watchedProgress = watchedProgress() ?: 0,
                    rating = contentRating(),
                    kind = Kind.normalize(kind()),
                    thumb = thumb(),
                    formattedRemainingTime = formattedRemainingTime(),
                    formattedDuration = formattedRemainingTime()
                )
            }

            seasonContinueWatching != null -> with(seasonContinueWatching.video()) {
                ContinueWatching(
                    id = id(),
                    headline = headline(),
                    description = description(),
                    duration = duration() ?: 0,
                    watchedProgress = watchedProgress() ?: 0,
                    rating = contentRating(),
                    thumb = thumb(),
                    kind = Kind.normalize(kind()),
                    formattedRemainingTime = formattedRemainingTime(),
                    formattedDuration = formattedRemainingTime()
                )
            }

            episodeContinueWatching != null -> with(episodeContinueWatching.video()) {
                ContinueWatching(
                    id = id(),
                    headline = headline(),
                    description = description(),
                    duration = duration() ?: 0,
                    watchedProgress = watchedProgress() ?: 0,
                    rating = contentRating(),
                    thumb = thumb(),
                    kind = Kind.normalize(kind()),
                    formattedRemainingTime = formattedRemainingTime(),
                    formattedDuration = formattedRemainingTime()
                )
            }

            else -> null
        }

    internal fun transformListOfTitleEditorialOfferToListOfEditorialOfferId(
        listOfEditorialOffer: List<TitleQuery.EditorialOffer>?
    ) = listOfEditorialOffer?.map {
        it.fragments().titleEditorialOffer()?.offerId() ?: ""
    } ?: arrayListOf()

    // =============================================================================================
    // Builder Apollo Query Functions.
    // =============================================================================================

    internal fun builderDefaultVideo(
        videoPlayback: VideoPlayback?,
        seasoned: Seasoned?,
        episode: com.globo.jarvis.fragment.Episode?
    ) =
        when {
            videoPlayback != null ->
                Video(
                    id = videoPlayback.videoPlayback()?.id(),
                    availableFor = AvailableFor.normalize(
                        videoPlayback.videoPlayback()?.availableFor()
                    )
                )

            seasoned?.defaultEpisode() != null ->
                Video(
                    id = seasoned.defaultEpisode()?.video()?.id(),
                    availableFor = AvailableFor.normalize(
                        seasoned.defaultEpisode()?.video()?.availableFor()
                    )
                )

            episode?.defaultEpisode() != null ->
                Video(
                    id = episode.defaultEpisode()?.video()?.id(),
                    availableFor = AvailableFor.normalize(
                        episode.defaultEpisode()?.video()?.availableFor()
                    )
                )

            episode?.defaultExcerpt() != null ->
                Video(
                    id = episode.defaultExcerpt()?.id(),
                    availableFor = AvailableFor.normalize(episode.defaultExcerpt()?.availableFor())
                )

            else -> null
        }

    internal fun builderQueryCover(titleId: String?, coverLandscapeScales: String) =
        CoverQuery.builder()
            .titleId(titleId ?: "")
            .coverMobile(
                CoverLandscapeScales.safeValueOf(coverLandscapeScales)
                    .takeIf { scale -> scale != CoverLandscapeScales.`$UNKNOWN` }
            )
            .coverTabletPortrait(
                CoverLandscapeScales.safeValueOf(coverLandscapeScales)
                    .takeIf { scale -> scale != CoverLandscapeScales.`$UNKNOWN` }
            )
            .coverTabletLandscape(
                CoverLandscapeScales.safeValueOf(coverLandscapeScales)
                    .takeIf { scale -> scale != CoverLandscapeScales.`$UNKNOWN` }
            )
            .build()

    internal fun builderSuggestForTitleQuery(
        group: SuggestGroups?,
        titleFormat: TitleFormat?,
        titleId: String?
    ) =
        SuggestForTitleQuery.builder()
            .group(group)
            .format(TitleFormat.safeValueOf(titleFormat?.rawValue()))
            .titleId(titleId ?: "")
            .build()

    internal fun builderDetailsTitleQuery(
        titleId: String?,
        programId: String?,
        scale: String
    ) = builderImageDetailsTitleQuery(TitleQuery.builder(), scale)
        .titleId(titleId ?: "")
        .originProgramId(programId ?: "")
        .build()

    internal fun builderImageDetailsTitleQuery(
        titleQueryBuilder: TitleQuery.Builder,
        scale: String
    ) = when (device) {
        Device.TV -> titleQueryBuilder.coverTv(CoverWideScales.safeValueOf(scale))
        Device.TABLET -> titleQueryBuilder.coverTablet(CoverLandscapeScales.safeValueOf(scale))
        else -> titleQueryBuilder.coverMobile(CoverPortraitScales.safeValueOf(scale))
    }

    internal fun builderTitleFormatQuery(titleId: String?) =
        TitleFormatQuery
            .builder()
            .titleId(titleId ?: "")
            .build()

    internal fun builderTitleEpgActiveQuery(titleId: String?) =
        TitleEpgActiveQuery
            .builder()
            .titleId(titleId ?: "")
            .build()
}