package com.globo.jarvis

import com.globo.jarvis.categoriesdetails.CategoryDetailsStructureQuery
import com.globo.jarvis.home.HomeQuery
import com.globo.jarvis.model.*
import com.globo.jarvis.model.Locale
import com.globo.jarvis.sales.SalesQuery
import java.util.*

interface Callback {
    interface BroadcastCurrentSlots : Error {
        fun onBroadcastCurrentSlotsSuccess(slots: List<BroadcastSlot>) {}
    }

    interface BroadcastMediaIds : Error {
        fun onBroadcastMediaIdsSuccess(mediaIds: List<String>) {}
    }

    interface Broadcasts : Error {
        fun onBroadcastsSuccess(broadcastVOList: List<Broadcast>) {}

        fun onBroadcastSuccess(broadcastVO: Broadcast) {}
    }

    interface Channels : Error {
        fun onChannelsSuccess(triple: Triple<Int?, Boolean, List<Channel>>) {}
    }

    interface AffiliatePrograms : Error {
        fun onAffiliateProgramsSuccess(triple: Triple<Boolean, Int?, List<Title>>) {}
    }

    interface State : Error {
        fun onStatesSuccess(statesList: List<States>) {}
    }

    interface Region : Error {
        fun onRegionsSuccess(regionsList: List<Regions>) {}
    }

    interface Locales : Error {
        fun onLocaleSuccess(locale: Locale) {}
    }

    interface RemoteConfig : Error {
        fun onRemoteConfigSuccess(configurationList: Map<String, Configuration?>) {}
    }

    interface Videos : Error {
        fun onDetailsSuccess(video: Video) {}

        fun onNextVideoSuccess(pair: Pair<Boolean, NextVideo>) {}
    }

    interface Dates : Error {
        fun onDatesSuccess(pair: Pair<List<Calendar>, List<String>>) {}

        fun onEpisodeAndRelatedExcerptsByDateSuccess(pair: Pair<Video?, List<Thumb>>) {}
    }

    interface Episodes : Error {
        fun onDetailSuccess(episode: Episode) {}

        fun onDetailsWithSeasonSuccess(triple: Triple<String, List<Season>, EpisodeDetails>) {}
    }

    interface Chapters : Error {
        fun onChaptersSuccess(triple: Triple<List<Chapter>, Boolean, Int>) {}
        fun onChapterByDateRangeSuccess(triple: Triple<List<Chapter>, Boolean, Int>) {}
    }

    interface Excerpts : Error {
        fun onScenesSuccess(scene: Scene) {}
    }

    interface Recommendations : Error {
        fun onRecommendedChannels(recommendationList: List<Recommendation>) {}

        fun onRecommendedTitle(pair: Pair<AbExperiment?, Recommendation>) {}

        fun onRecommendedTitle(recommendation: Recommendation) {}
    }

    interface Sales : Error {
        fun onLandingPageSuccess(pair: Triple<Offer, List<Offer>, String?>) {}

        fun onDetailsOfferSuccess(offerList: List<Offer>) {}

        fun onDetailsGenericOfferSuccess(offer: Offer) {}

        fun onDetailsOfferHighlightsSuccess(offer: Offer) {}

        fun onStructureSuccess(triple: Triple<Pair<SalesQuery.PremiumHighlight?,
                List<SalesQuery.OfferItem>?>, String?, SalesQuery.Title?>) {
        }

        fun onRecommendationSuccess(salesRecommendation: SalesRecommendation) {}
    }

    interface Categories : Error {
        fun onCategoriesSuccess(triple: Triple<Int, Boolean, List<Category>>) {}

        fun onCategoryDetailsGridSuccess(categoryDetails: CategoryDetails) {}

        fun onCategoryDetailsPageSuccess(pair: Pair<String?, List<CategoryOffer>>) {}

        fun onDetailsOfferTitleSuccess(categoryOffer: CategoryOffer) {}

        fun onPaginationGridSuccess(categoryOffer: CategoryOffer) {}

        fun onStructureSuccess(triple: Triple<String?, CategoryDetailsStructureQuery.PremiumHighlight?, List<CategoryDetailsStructureQuery.OfferItem>>) {}

        fun onOfferDetailsSuccess(categoryOfferList: List<CategoryOffer>) {}

        fun onDetailsCategoryBySlugSuccess(categoryDetails: CategoryDetails) {}

        fun onDetailsCategoryByIdSuccess(categoryDetails: CategoryDetails) {}

        fun onGroupCategoriesSuccess(categoryDetailsList: List<CategoryDetails>) {}
    }

    interface Home : Error {
        fun onStructureSuccess(triple: Triple<String, HomeQuery.PremiumHighlight?, List<HomeQuery.OfferItem>>) {}

        fun onOfferCategoriesSuccess(triple: Triple<Int?, Boolean, List<Category>>) {}
    }

    interface Search : Error {
        fun onSearchSuccess(triple: Triple<SearchTitles, SearchChannel, SearchVideos>) {}

        fun onSearchTitlesSuccess(searchTitles: SearchTitles) {}

        fun onSearchVideosSuccess(searchVideos: SearchVideos) {}

        fun onSearchChannelsSuccess(searchChannel: SearchChannel) {}

        fun onTopHitsSuccess(searchTopHitsList: List<SearchTopHits>) {}

        fun onGlobalSearchSuccess(pair: Pair<List<Title>, List<Thumb>>) {}
    }

    interface User : Error {
        fun onMyLastedWatchedVideosSuccess(triple: Triple<Int?, Boolean, List<ContinueWatching>>) {}

        fun onMyListSuccess(myList: MyList) {}

        fun onAddMyListSuccess(addedMyList: Boolean) {}

        fun onDeleteMyListSuccess(deletedMyList: Boolean) {}
    }

    interface Epgs : Error {
        fun onEpgListSuccess(epgList: List<Epg>) {}

        fun onEpgSuccess(epg: Epg) {}
    }

    interface Seasons : Error {
        fun onSeasonByChapterSuccess(dateRangeList: List<DateRange>) {}

        fun onSeasonByEpisodeSuccess(pair: Pair<String, List<Season>>) {}

        fun onSeasonByScenesSuccess(pair: Pair<Season, List<Season>>) {}
    }

    interface Scenes : Error {
        fun onScenesWithSeasonSuccess(pair: Pair<List<Season>, Triple<Boolean, Int, List<Scene>>>) {}

        fun onScenesWithoutSeasonDetailsSuccess(
            pair: Pair<Pair<Season?, List<Season>>,
                    Triple<Boolean, Int, List<ScenesPreview>>>
        ) {
        }

        fun onScenesWithoutDetailsSuccess(triple: Triple<Boolean, Int, List<ScenesPreview>>) {}

        fun onScenesSuccess(triple: Triple<Boolean, Int, List<Scene>>) {}

        fun onStructureSuccess(triple: Triple<Boolean, Int, List<ScenesPreview>>) {}

        fun onScenesDetailsSuccess(sceneList: List<Scene>) {}
    }

    interface Titles : Error {
        fun onCoverSuccess(cover: Cover) {}

        fun onAllSuccess(pair: Pair<Title, TitleUser>) {}

        fun onDetailSuccess(title: Title) {}

        fun onDetailWithUserSuccess(titleUser: TitleUser) {}

        fun onOriginProgramIdSuccess(originProgramId: String) {}

        fun onFormatSuccess(format: Format) {}

        fun onEpgActiveSuccess(isEpgActive: Boolean) {}

    }

    interface TitlesSuggestion : Error {
        fun onAllSuccess(listOfRecommendedTitles: Pair<List<RecommendedTitleOffer>, AbExperiment?>) {}
    }

    interface TitleSuggestOfferId : Error {
        fun onSuccess(pair: Pair<String?, AbExperiment?>)
    }

    interface Error {
        fun onFailure(throwable: Throwable) {}
    }
}
