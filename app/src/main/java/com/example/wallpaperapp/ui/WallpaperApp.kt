package com.example.wallpaperapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.wallpaperapp.model.Photo
import com.example.wallpaperapp.repository.WallpaperRepository
import com.example.wallpaperapp.util.WallpaperSetter
import com.example.wallpaperapp.util.WallpaperTarget
import kotlinx.coroutines.launch

enum class Category(val label: String) {
    NATURE("طبيعة"),
    ANIME("أنمي"),
    REGULAR("عادية")
}

@Composable
fun WallpaperApp() {
    var category by remember { mutableStateOf(Category.NATURE) }
    var photos by remember { mutableStateOf<List<Photo>>(emptyList()) }
    var selectedPhoto by remember { mutableStateOf<Photo?>(null) }
    var loading by remember { mutableStateOf(false) }
    var applying by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    suspend fun load() {
        loading = true
        errorMsg = null
        photos = try {
            when (category) {
                Category.NATURE -> WallpaperRepository.getNature()
                Category.ANIME -> WallpaperRepository.getAnime()
                Category.REGULAR -> WallpaperRepository.getRegular()
            }
        } catch (e: Exception) {
            errorMsg = "فشل تحميل الصور، تأكد من الإنترنت ومفتاح API"
            emptyList()
        }
        loading = false
    }

    LaunchedEffect(category) { load() }

    // شاشة المعاينة وتعيين الخلفية
    if (selectedPhoto != null) {
        val photo = selectedPhoto!!
        Column(Modifier.fillMaxSize()) {
            AsyncImage(
                model = photo.fullUrl,
                contentDescription = null,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Row(
                Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    "الرئيسية" to WallpaperTarget.HOME,
                    "القفل" to WallpaperTarget.LOCK,
                    "الاثنين" to WallpaperTarget.BOTH
                ).forEach { (label, target) ->
                    Button(
                        enabled = !applying,
                        onClick = {
                            scope.launch {
                                applying = true
                                errorMsg = try {
                                    WallpaperSetter.apply(context, photo.fullUrl, target)
                                    null
                                } catch (e: Exception) {
                                    "فشل تعيين الخلفية"
                                }
                                applying = false
                            }
                        }
                    ) { Text(label) }
                }
            }
            if (applying) LinearProgressIndicator(Modifier.fillMaxWidth())
            errorMsg?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
            }
            TextButton(
                onClick = { selectedPhoto = null },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) { Text("رجوع") }
        }
        return
    }

    // شاشة التصفح
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Category.entries.forEach { cat ->
                FilterChip(
                    selected = category == cat,
                    onClick = { category = cat },
                    label = { Text(cat.label) }
                )
            }
        }

        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            errorMsg != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(errorMsg!!, color = MaterialTheme.colorScheme.error)
            }
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(photos) { photo ->
                    AsyncImage(
                        model = photo.thumbUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .aspectRatio(0.65f)
                            .clickable { selectedPhoto = photo }
                    )
                }
            }
        }
    }
}
