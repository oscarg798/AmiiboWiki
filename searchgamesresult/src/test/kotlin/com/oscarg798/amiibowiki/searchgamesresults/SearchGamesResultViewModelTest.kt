package com.oscarg798.amiibowiki.searchgamesresults

import androidx.lifecycle.SavedStateHandle
import com.oscarg798.amiibowiki.core.models.GameSearchResult
import com.oscarg798.amiibowiki.core.usecases.IsFeatureEnableUseCase
import com.oscarg798.amiibowiki.searchgamesresults.logger.SearchGamesResultLogger
import com.oscarg798.amiibowiki.searchgamesresults.models.GameSearchParam
import com.oscarg798.amiibowiki.searchgamesresults.models.ViewGameSearchResult
import com.oscarg798.amiibowiki.searchgamesresults.mvi.ViewState
import com.oscarg798.amiibowiki.searchgamesresults.mvi.SearchResultWish
import com.oscarg798.amiibowiki.searchgamesresults.mvi.UIEffect
import com.oscarg798.amiibowiki.searchgamesresults.usecase.SearchGameResult
import com.oscarg798.amiibowiki.searchgamesresults.usecase.SearchGamesByAmiiboUseCase
import com.oscarg798.amiibowiki.searchgamesresults.usecase.SearchGamesByQueryUseCase
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import com.oscarg798.amiibowiki.testutils.testrules.ViewModelTestRule
import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchGamesResultViewModelTest :
    ViewModelTestRule.ViewModelCreator<ViewState, SearchGamesResultViewModel> {

    @get:Rule
    val viewModelRule: ViewModelTestRule<ViewState, UIEffect, SearchGamesResultViewModel> =
        ViewModelTestRule(this)

    private val handle = relaxedMockk<SavedStateHandle>()
    private val searchGamesByAmiiboUseCase: SearchGamesByAmiiboUseCase = relaxedMockk()
    private val isFeatureEnableUseCase: IsFeatureEnableUseCase = relaxedMockk()
    private val searchGamesByQueryUseCase: SearchGamesByQueryUseCase = relaxedMockk()
    private val searchGamesLogger: SearchGamesResultLogger = relaxedMockk()
    private val showAsRelatedSectionGames = true

    private val viewModel: SearchGamesResultViewModel
        get() = viewModelRule.viewModel

    @Before
    fun setup() {
        every { handle.get<ViewState>(any()) } answers { null }
    }

    override fun create(): SearchGamesResultViewModel = SearchGamesResultViewModel(
        handle,
        showAsRelatedSectionGames,
        searchGamesByAmiiboUseCase,
        isFeatureEnableUseCase,
        searchGamesByQueryUseCase,
        searchGamesLogger,
        viewModelRule.coroutineContextProvider
    )

    @Test
    fun `given an amiibo id to search when process wish invoke then it should  search the amiibos`() {
        every { searchGamesByAmiiboUseCase.execute(AMIIBO_ID) } answers { flowOf(listOf(RESULT)) }

        viewModel.onWish(
            SearchResultWish.SearchGames(
                GameSearchParam.AmiiboGameSearchParam(
                    AMIIBO_ID
                )
            )
        )

        viewModelRule.stateCollector.wereValuesEmitted(
            listOf(
                STATE.copy(
                    isLoading = true,
                    idling = false
                ),
                STATE.copy(
                    isLoading = false,
                    idling = false,
                    gamesResult = listOf(ViewGameSearchResult(RESULT))
                )
            )
        )

        verify { searchGamesByAmiiboUseCase.execute(AMIIBO_ID) }
    }

    @Test
    fun `given an amiibo id search to result saved when process wish invoke then it should  search the amiibos`() {
        every { handle.get<ViewState>(any()) } answers {
            STATE.copy(
                gamesResult = listOf(
                    ViewGameSearchResult(RESULT)
                )
            )
        }

        viewModel.onWish(
            SearchResultWish.SearchGames(
                GameSearchParam.AmiiboGameSearchParam(
                    AMIIBO_ID
                )
            )
        )

        viewModelRule.stateCollector.wereValuesEmitted(
            listOf(
                STATE.copy(
                    isLoading = false,
                    idling = false,
                    gamesResult = listOf(ViewGameSearchResult(RESULT))
                )
            )
        )

        verify {
            searchGamesByAmiiboUseCase wasNot Called
            handle.get<ViewState>(any())
        }
    }

    @Test
    fun `given a query to search when process wish invoke then return relevant results`() {
        every { searchGamesByQueryUseCase.execute(QUERY) } answers {
            SearchGameResult.Allowed(
                flowOf(
                    listOf(RESULT)
                )
            )
        }

        viewModel.onWish(
            SearchResultWish.SearchGames(
                GameSearchParam.StringQueryGameSearchParam(
                    QUERY
                )
            )
        )

        viewModelRule.stateCollector.wereValuesEmitted(
            listOf(
                STATE.copy(
                    isLoading = true,
                    idling = false
                ),
                STATE.copy(
                    isLoading = true,
                    idling = false
                ),
                STATE.copy(
                    isLoading = false,
                    idling = false,
                    gamesResult = listOf(ViewGameSearchResult(RESULT))
                )
            )
        )

        verify { searchGamesByQueryUseCase.execute(QUERY) }
    }

    @Test
    fun `when a not allowed search result is return by the usecase then it should return an idel state`() {
        every { searchGamesByQueryUseCase.execute(QUERY) } answers { SearchGameResult.NotAllowed }

        viewModel.onWish(
            SearchResultWish.SearchGames(
                GameSearchParam.StringQueryGameSearchParam(
                    QUERY
                )
            )
        )

        viewModelRule.stateCollector.wereValuesEmitted(
            listOf(
                STATE.copy(
                    isLoading = true,
                    idling = false
                ),
                STATE
            )
        )

        verify { searchGamesByQueryUseCase.execute(QUERY) }
    }
}

private const val QUERY = "1"
private val STATE = ViewState()
private const val AMIIBO_ID = "1"
private val RESULT = GameSearchResult(1, "Mario", "Mario", 3)
