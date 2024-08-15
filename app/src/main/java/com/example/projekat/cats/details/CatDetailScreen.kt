package com.example.projekat.cats.details

import android.content.Intent
import android.graphics.Paint.Style
import android.net.Uri
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import coil.compose.SubcomposeAsyncImage
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.FlowColumnScopeInstance.align
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.projekat.cats.api.model.CatApiModel
import com.example.projekat.cats.api.model.Weight
import com.example.projekat.cats.db.Cat
import com.example.projekat.cats.list.model.CatUiModel
import com.example.projekat.core.composables.AppIconButton
import com.example.projekat.photos.grid.model.PhotoUiModel

fun NavGraphBuilder.catDetails(
    route: String,

    onGalleryClick: (String) -> Unit,
    navController: NavController,
    onClose: () -> Unit,
) = composable(
    route = route,

    enterTransition = { slideInVertically { it } },
    popExitTransition = { slideOutVertically { it } },

) { navBackStackEntry ->

    val catDetailsViewModel: CatDetailsViewModel = hiltViewModel(navBackStackEntry)

    val state = catDetailsViewModel.state.collectAsState()




    CatDetailsScreen(
        state = state.value,
        onGalleryClick = onGalleryClick,
        onClose = onClose
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatDetailsScreen(
    state: CatDetailsContract.CatDetailsState,
    onGalleryClick: (String) -> Unit,
    onClose: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.cat?.name ?: "Loading",
                        style = TextStyle(
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                },
                navigationIcon = {
                    AppIconButton(
                        imageVector = Icons.Default.ArrowBack,
                        onClick = onClose,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (state.fetching) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (state.error) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {

                        Text(text = "Failed to load.")
                    }
                } else if (state.cat != null) {

//                    SubcomposeAsyncImage(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(280.dp)
//                            .align(Alignment.CenterHorizontally),
//                        model = state.image,
//                        contentScale = ContentScale.Crop,
//                        contentDescription = null,
//                    )

                    state.image?.let {
                        LoginDataColumn(
                            data = state.cat,
                            image = it,
                            onGalleryClick = onGalleryClick,
                            wiki = state.cat.wikipedia_url,
                        )
                    }


                }
//                else {
//                    NoDataContent(id = state.passwordId)
//                }
            }
        }
    )
}




@Composable
private fun LoginDataColumn(
    data: CatUiModel,
    image: PhotoUiModel,
    onGalleryClick: (String) -> Unit,
    wiki: String,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())

    ) {

        SubcomposeAsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .align(Alignment.CenterHorizontally),
            model = image.url,
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )


        Spacer(modifier = Modifier.height(16.dp))



        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
            text = data.description,
//            text = "descriptcia ovde",
        )

        Spacer(modifier = Modifier.height(8.dp))



        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = data.temperament,

        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = "Origin: " +  data.origin,

        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = "Life span: " + data.life_span,
//            text = "Life span: pvde",

            )

        Spacer(modifier = Modifier.height(8.dp))




        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,

            text ="Avarage weight: " + data.weight,

//            text ="Avarage weight: ovde" ,
            )

        Spacer(modifier = Modifier.height(8.dp))
        var retka = ""
        if(data.rare == 0){
            retka = "No"
        }else retka = "Yes"

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = "Rare: " + retka,
//            text = "Rare:  ovde" ,

            )

        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.Black )
        Spacer(modifier = Modifier.height(16.dp))


        Row (
            Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Widget(number = data.child_friendly, trait = "Child Friendly" )

            Widget(number = data.affection_level, trait = "Affection Level" )
        }

        Spacer(modifier = Modifier.height(8.dp))


        Row (
            Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Widget(number = data.adaptability, trait = "Adaptability" )
            Widget(number = data.intelligence, trait = "Inteligence" )
            Widget(number = data.social_needs, trait = "Social Needs" )
        }


        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.Black )
//        Spacer(modifier = Modifier.height(8.dp))


        val context = LocalContext.current
        val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(wiki)) }

        Row(
            Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = { context.startActivity(intent) },
//            onClick = { },
                modifier = Modifier
                    .width(200.dp)
//                    .fillMaxWidth()
                    .padding(16.dp)
//                    .align(Alignment.CenterHorizontally),
            ) {
                Text(text = "Wikipedia")
            }

//        Spacer(modifier = Modifier.height(8.dp))


            Button(
                onClick = {  onGalleryClick(data.id) },

                modifier = Modifier
                    .width(200.dp)
//                    .fillMaxWidth()
                    .padding(16.dp)
//                    .align(Alignment.CenterHorizontally),
            ) {
                Text(text = "Gallery")
            }
        }




//        FilledIconButton(
//            onClick = {
//                onGalleryClick(data.id)
//            },
//            modifier = Modifier
//                .height(55.dp)
//                .width(250.dp)
//                .align(Alignment.CenterHorizontally),
//            colors = IconButtonDefaults.iconButtonColors(
//                containerColor = Color.Black,
//                contentColor = Color.White,
//            )
//        ) {
//            Text(
//                modifier = Modifier.padding(horizontal = 16.dp),
//                style = TextStyle(
//                    fontSize = 22.sp,
//                    fontWeight = FontWeight.Bold,
//                    letterSpacing = 2.sp,
//                ),
//                text = "Gallery",
//            )
//        }



    }
}

@Composable
fun Widget(
    number: Int,
    trait: String
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .border(5.dp, Color.Black, CircleShape)
                .padding(2.dp)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = number.toString(),
                style = MaterialTheme.typography.headlineLarge
            )
        }
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 3.dp),
            text = trait,
            style = TextStyle(fontSize = 18.sp),
        )
    }
}


//@Preview
//@Composable
//fun PreviewDetailsScreen() {
//    Surface {
//        CatDetailsScreen(
//            state = CatDetailsState(
//                id = "124124",
////                cat = CatApiModel(
////                    id = "124124",
////                    name = "Raf Servis",
////                    description = "nredzic@raf.rs",
////                    wikipedia_url = "wiki",
////                    temperament = "temperament",
////                    origin = "poreklo",
////                    life_span = "zivotni vek",
////                    weight = Weight("1","12"),
////                    rare = 1 ,
////                    adaptability = 1,
////                    affection_level = 2,
////                    reference_image_id = "0XYvRd7oD",
////                    social_needs = 3,
////                    child_friendly = 5,
////                    intelligence = 5
//////                    password = "mojasifra"
////                ),
//            ),
//            eventPublisher = {},
//            onClose = {},
//        )
//    }
//}
