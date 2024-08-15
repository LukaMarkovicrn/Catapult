package com.example.projekat.cats.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.projekat.cats.api.model.CatApiModel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.projekat.cats.api.model.Weight

import com.example.projekat.cats.list.CatListContract.CatListState
import com.example.projekat.cats.list.CatListContract.CatListUiEvent
import com.example.projekat.cats.list.model.CatUiModel
import com.example.projekat.core.composables.AppIconButton
import com.example.projekat.core.theme.AppTheme
import com.example.projekat.leaderBoard.list.LeaderBoardListContract
import kotlinx.coroutines.launch


fun NavGraphBuilder.cats(
    route: String,
    onCatClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onKvizClick: () -> Unit,
    onLeaderBoardClick: () -> Unit,
) = composable(
    route = route
) {
    val catListViewModel = hiltViewModel<CatListViewModel>()

    val state = catListViewModel.state.collectAsState()

    CatListScreen(
        state = state.value,
        eventPublisher = {
            catListViewModel.setEvent(it)
        },
        onCatClick = onCatClick,
        onProfileClick = onProfileClick,
        onKvizClick = onKvizClick,
        onLeaderBoardClick = onLeaderBoardClick

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatListScreen(
    state: CatListState,
    eventPublisher: (uiEvent: CatListUiEvent) -> Unit,
    onCatClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onKvizClick: () -> Unit,
    onLeaderBoardClick: () -> Unit
) {
    val uiScope = rememberCoroutineScope()
    val drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed)

//    val analytics = LocalAnalytics.current
//    SideEffect {
//        analytics.log("Neka poruka")
//    }

    BackHandler(enabled = drawerState.isOpen) {
        uiScope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        modifier = Modifier,
        drawerState = drawerState,
        drawerContent = {
            UserListDrawer(
                state = state,
                onProfileClick = {
                    uiScope.launch {
                        drawerState.close()
                    }
                    onProfileClick()
                },
                onKvizClick = {
                    uiScope.launch { drawerState.close() }
                    onKvizClick()

                },
                onLeaderBoardClick = {
                    uiScope.launch { drawerState.close() }
                    onLeaderBoardClick()

                },
            )
        },
        content = {
            CatListScaffold(
                state = state,
                onCatClick = onCatClick,
                onDrawerMenuClick = {
                    uiScope.launch {
                        drawerState.open()
                    }
                },
                eventPublisher = eventPublisher
            )

        }
    )

}


@Composable
private fun AppDrawerActionItem(
    icon: ImageVector,
    text: String,
    onClick: (() -> Unit)? = null,
) {
    ListItem(
        modifier = Modifier.clickable(
            enabled = onClick != null,
            onClick = { onClick?.invoke() }
        ),
        leadingContent = {
            Icon(imageVector = icon, contentDescription = null)
        },
        headlineContent = {
            Text(text = text)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserListDrawer(
    state: CatListState,
    onProfileClick: () -> Unit,
    onKvizClick: () -> Unit,
    onLeaderBoardClick: () -> Unit,
) {
    BoxWithConstraints {
        // We can use ModalDrawerSheet as a convenience or
        // built our own drawer as AppDrawer example
        ModalDrawerSheet(
            modifier = Modifier.width(maxWidth * 3 / 4),
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.BottomStart,
                ) {
                    Text(
                        modifier = Modifier.padding(all = 16.dp),
                        text = state.nickname,
                        style = AppTheme.typography.headlineSmall,
                    )
                }

                Divider(modifier = Modifier.fillMaxWidth())

                Column(modifier = Modifier.weight(2f)) {

                    AppDrawerActionItem(
                        icon = Icons.Default.Person,
                        text = "Profile",
                        onClick = onProfileClick,
                    )

                    AppDrawerActionItem(
                        icon = Icons.Default.Quiz,
                        text = "Kviz",
                        onClick = onKvizClick,
                    )

                    NavigationDrawerItem(
                        label = {
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = "Cats",
                            )
                        },
                        icon = {
                            Icon(imageVector = Icons.Default.CatchingPokemon, contentDescription = null)
                        },
                        badge = {
                            Badge {
                                Text(text = "20")
                            }
                        },
                        selected = true,
                        onClick = {

                        },
                    )

                    AppDrawerActionItem(
                        icon = Icons.Default.Leaderboard,
                        text = "LeaderBoard",
                        onClick = onLeaderBoardClick,
                    )

//
                }

                Divider(modifier = Modifier.fillMaxWidth())


                AppDrawerActionItem(
                    icon = Icons.Default.Settings,
                    text = "Settings",
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CatListScaffold(
    state: CatListState,
    onCatClick: (String) -> Unit,
    onDrawerMenuClick: () -> Unit,
    eventPublisher: (uiEvent: CatListUiEvent) -> Unit,
){

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    AppIconButton(
                        imageVector = Icons.Default.Menu,
                        onClick = onDrawerMenuClick,
                    )
                },
                title = {
                    Text(
                        text = "Cats",
                        style = TextStyle(
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
//                Modifier.padding(20.dp)
            )

            SearchBarAction(
                modifier = Modifier
                    .fillMaxWidth()
//                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(top = 60.dp)

                    .padding(horizontal = 20.dp),

//                    .clip(RoundedCornerShape(15.dp)),
                onQueryChange = { query ->
                    eventPublisher(
                        CatListUiEvent.SearchQueryChanged(
                            query = query
                        )
                    )
                },
                onCloseClicked = { eventPublisher(CatListUiEvent.CloseSearchMode) },
                query = state.query,
                activated = state.isSearchMode
            )



        },



        content = { paddingValues ->
            if (state.loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {



                    var help = state.cats
                    if(state.isSearchMode){
                        help = state.filteredCats
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = paddingValues,

                        ) {




                        items(

                            items = help,
                            key = { it.id },
                        ) { cat ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                border = BorderStroke(2.dp, Color.Black),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                                    .padding(bottom = 16.dp)
                                    .clickable { onCatClick(cat.id) },
                            ) {


                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(

                                        text = cat.name,
//                                    text = "@${cat.id}\n${cat.name}\n${cat.alt_names}\n${cat.description}",
                                        style = MaterialTheme.typography.headlineMedium,
                                    )

//                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(

                                        text = "-" + cat.alt_names,
//                                        text = "-",
//                                    text = "@${cat.id}\n${cat.name}\n${cat.alt_names}\n${cat.description}",
                                        style = MaterialTheme.typography.bodyMedium,
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(

                                        text = if(cat.description.length > 250) cat.description.take(250).plus("...") else cat.description,
//                                        text = "description ovde",
//                                    text = "@${cat.id}\n${cat.name}\n${cat.alt_names}\n${cat.description}",
                                        style = MaterialTheme.typography.bodySmall,
                                    )

                                    val temperaments = cat.temperament.split(",")
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(

                                        text = "Temperament: " + temperaments.shuffled().take(3).joinToString(", "),
//                                    text = "@${cat.id}\n${cat.name}\n${cat.alt_names}\n${cat.description}",
                                        style = MaterialTheme.typography.bodyMedium,
                                    )

//                                DeleteAppIconButton(
//                                    modifier = Modifier.padding(end = 8.dp),
//                                    text = stringResource(R.string.albums_delete_user_confirmation_text),
//                                    onDeleteConfirmed = {
//                                        eventPublisher(UserListUiEvent.RemoveUser(userId = cat.id))
//                                    },
//                                )
                                }
                            }
                        }
                    }
                }


            }
        }
    )
}

@Composable
private fun SearchBarAction(
    modifier: Modifier = Modifier,
    onQueryChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    query: String,
    activated: Boolean

) {

    var text by remember { mutableStateOf(query) }
    var active by remember { mutableStateOf(activated) }
    val focusManager = LocalFocusManager.current


    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,

        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            modifier = modifier,
//                .fillMaxWidth()
//                .background(MaterialTheme.colorScheme.primaryContainer)
////                .padding(top = 8.dp, bottom = 8.dp)
//                .clip(RoundedCornerShape(25.dp))
//                .padding(horizontal = 16.dp),

            value = text,
            onValueChange = { newText ->
                text = newText
                active = true
                onQueryChange(newText)
            },

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                }
            ),
            placeholder = { Text(text = "Search") },
            label = { Text(text = "Search") },
        )
    }
}






// https://developer.android.com/develop/ui/compose/tooling/previews

class UserListStateParameterProvider : PreviewParameterProvider<CatListState> {
    override val values: Sequence<CatListState> = sequenceOf(
        CatListState(
            loading = true,
        ),
        CatListState(
            loading = false,
        ),
        CatListState(
            loading = false,
            cats = listOf(
//                CatUiModel(id = "nig", name = "ger", temperament = "nigger"),
//                CatUiModel(id = "ni", name = "marko", wikipedia_url = "nesto", affection_level = 1, adaptability = 2, origin = "poreklo", alt_names = "", rare = 2, temperament = "temperament", social_needs = 3, child_friendly = 3, intelligence = 5, reference_image_id = "9012478", weight = Weight("12","11"), life_span = "13", description = "nanan"),
//                CatApiModel(id = "la", name = "janko", wikipedia_url = "nesto", description = "nanan"),
//                CatApiModel(id = "ga", name = "stefan", wikipedia_url = "nesto", description = "nanan"),
            ),
        ),
    )
}


//@Preview
//@Composable
//private fun PreviewUserList(
//    @PreviewParameter(UserListStateParameterProvider::class) userListState: CatListState,
//) {
//    CatListScreen(
//        state = userListState,
//        eventPublisher = {},
//        onCatClick = {},
//    )
//}
