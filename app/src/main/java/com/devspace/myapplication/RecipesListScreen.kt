package com.devspace.myapplication

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RecipesListScreen(navController: NavHostController) {

    var randomRecipes by remember { mutableStateOf<List<RecipesDto>>(emptyList()) }


    LaunchedEffect(key1 = Unit) {
        val apiService = RetrofitClient.retrofitInstance.create(ApiService::class.java)
        val callRandom = apiService.getRandom()

        callRandom.enqueue(object : Callback<RecipesResponse> {
            override fun onResponse(
                call: Call<RecipesResponse>,
                response: Response<RecipesResponse>
            ) {
                if (response.isSuccessful) {
                    val random = response.body()?.recipes
                    if (random != null) {
                        randomRecipes = random
                    } else {
                        Log.d(
                            "RecipesListScreen",
                            "Response successful but body or recipes is null"
                        )
                    }
                } else {
                    Log.d(
                        "RecipesListScreen",
                        "Request Error :: ${response.code()} - ${response.message()} - ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }

            override fun onFailure(call: Call<RecipesResponse>, t: Throwable) {
                Log.e("RecipesListScreen", "Network Error :: ${t.message}", t)
            }
        })
    }


    RecipesListContent(
        randomRecipes = randomRecipes,
        onClick = { itemClicked ->
            navController.navigate(route = "recipesDetail")
        }
    )
}

@Composable
private fun RecipesListContent(
    randomRecipes: List<RecipesDto>,
    onClick: (RecipesDto) -> Unit
) {
    RecipesSession(
        label = "Random Recipes",
        recipesList = randomRecipes,
        onClick = onClick
    )
}

@Composable
private fun RecipesSession(
    label: String,
    recipesList: List<RecipesDto>,
    onClick: (RecipesDto) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        Text(
            text = "Let's Cook",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            textAlign = TextAlign.Center
        )


        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF333333),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        RecipesList(recipesList = recipesList, onClick = onClick)
    }
}

@Composable
private fun RecipesList(
    recipesList: List<RecipesDto>,
    onClick: (RecipesDto) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(recipesList) {
            RecipesItem(
                recipesDto = it,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun RecipesItem(
    recipesDto: RecipesDto,
    onClick: (RecipesDto) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(recipesDto) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = recipesDto.image,
                contentDescription = "${recipesDto.title} image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = recipesDto.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = recipesDto.summary,
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

