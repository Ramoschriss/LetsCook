package com.devspace.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.devspace.myapplication.ui.theme.LetsCook
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LetsCook {
                var randomRecipes by remember { mutableStateOf<List<RecipesDto>>(emptyList()) }

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
                            }
                        } else {
                            Log.d("MainActivity", "Request Error ::${response.errorBody()}")
                        }
                    }

                    override fun onFailure(call: Call<RecipesResponse>, t: Throwable) {
                        Log.d("MainActivity", "Network Error ::${t.message}")
                    }

                })

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    RecipesSession(
                        label = "Random Recipes",
                        recipesList = randomRecipes,
                        onClick = { recipesClicked ->
                        }
                    )

                }
            }
        }
    }
}

@Composable
fun RecipesSession(
    label: String,
    recipesList: List<RecipesDto>,
    onClick: (RecipesDto) -> Unit
) {
    @Composable
    fun CenteredTitle() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Let's cook",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(8.dp),

        ) {
        CenteredTitle()

        Text(
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            text = label,
        )
        Spacer(modifier = Modifier.size(8.dp))
        RecipesList(recipesList = recipesList, onClick = onClick)
    }
}

@Composable
fun RecipesList(
    recipesList: List<RecipesDto>,
    onClick: (RecipesDto) -> Unit
) {
    LazyColumn {
        items(recipesList) {
            RecipesItem(
                recipesDto = it,
                onClick = onClick
            )
        }
    }
}


@Composable
fun RecipesItem(
    recipesDto: RecipesDto,
    onClick: (RecipesDto) -> Unit
) {

    Column(
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .clickable {
            onClick.invoke(recipesDto)
        }
    ) {
        AsyncImage(
            modifier = Modifier
                .padding(end = 6.dp)
                .width(420.dp)
                .height(150.dp),
            contentScale = ContentScale.Crop,
            model = recipesDto.image,
            contentDescription = "${recipesDto.title} Poster image"
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = recipesDto.title,
            maxLines = 1,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = recipesDto.summary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray
        )
    }
}

