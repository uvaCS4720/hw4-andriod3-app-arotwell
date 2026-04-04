package edu.nd.pmcburne.hello

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import edu.nd.pmcburne.hello.ui.theme.MyApplicationTheme

//claude opus 4.6 was used to assist with styling

private val UvaNavy      = Color(0xFF232D4B)
private val UvaNavyLight = Color(0xFF2E3D66)
private val UvaOrange    = Color(0xFFE57200)
private val WarmSurface  = Color(0xFFF5F5F0)
private val OnSurface    = Color(0xFF1A1A2E)

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainScreen(viewModel)
            }
        }
    }
}


@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allTags         by viewModel.allTags.collectAsState()
    val selectedTag     by viewModel.selectedTag.collectAsState()
    val filteredMarkers by viewModel.filteredPlacemarks.collectAsState()
    val isLoading       by viewModel.isLoading.collectAsState()
    val errorMessage    by viewModel.errorMessage.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmSurface)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(UvaNavy, UvaNavyLight)
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector        = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint               = UvaOrange,
                        modifier           = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text          = "UVA Campus Map",
                        color         = Color.White,
                        fontSize      = 20.sp,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    )
                }
                Text(
                    text          = "University of Virginia · Charlottesville",
                    color         = Color.White.copy(alpha = 0.55f),
                    fontSize      = 12.sp,
                    letterSpacing = 0.2.sp,
                    modifier      = Modifier.padding(start = 30.dp, top = 2.dp)
                )
            }
        }

        // for teh tag filter
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .shadow(
                    elevation    = 6.dp,
                    shape        = RoundedCornerShape(16.dp),
                    ambientColor = UvaNavy.copy(alpha = 0.12f),
                    spotColor    = UvaNavy.copy(alpha = 0.12f)
                ),
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text          = "FILTER BY TAG",
                    fontSize      = 10.sp,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color         = UvaNavy.copy(alpha = 0.45f),
                    modifier      = Modifier.padding(bottom = 6.dp)
                )
                TagDropdown(
                    tags          = allTags,
                    selectedTag   = selectedTag,
                    onTagSelected = viewModel::selectTag,
                    modifier      = Modifier.fillMaxWidth()
                )
            }
        }
         // for the map
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .shadow(
                    elevation    = 8.dp,
                    shape        = RoundedCornerShape(16.dp),
                    ambientColor = UvaNavy.copy(alpha = 0.18f),
                    spotColor    = UvaNavy.copy(alpha = 0.18f)
                )
                .clip(RoundedCornerShape(16.dp))
        ) {
            CampusMap(placemarks = filteredMarkers)

            // location count badge
            if (!isLoading && filteredMarkers.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .background(UvaNavy.copy(alpha = 0.88f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text       = "${filteredMarkers.size} location${if (filteredMarkers.size != 1) "s" else ""}",
                        color      = Color.White,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier         = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.75f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = UvaOrange, strokeWidth = 3.dp)
                        Text(
                            text       = "Loading campus data…",
                            color      = UvaNavy,
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagDropdown(
    tags: List<String>,
    selectedTag: String,
    onTagSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded         = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier         = modifier
    ) {
        OutlinedTextField(
            value         = selectedTag,
            onValueChange = {},
            readOnly      = true,
            singleLine    = true,
            trailingIcon  = {
                Icon(
                    imageVector        = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint               = if (expanded) UvaOrange else UvaNavy
                )
            },
            shape  = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = UvaOrange,
                unfocusedBorderColor    = UvaNavy.copy(alpha = 0.28f),
                focusedTextColor        = UvaNavy,
                unfocusedTextColor      = UvaNavy,
                focusedContainerColor   = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color      = UvaNavy
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false },
            modifier         = Modifier.background(Color.White)
        ) {
            tags.forEach { tag ->
                val isSelected = tag == selectedTag
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        color = if (isSelected) UvaOrange else Color.Transparent,
                                        shape = RoundedCornerShape(3.dp)
                                    )
                            )
                            Text(
                                text       = tag,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color      = if (isSelected) UvaNavy else OnSurface.copy(alpha = 0.72f),
                                fontSize   = 14.sp
                            )
                        }
                    },
                    onClick = {
                        onTagSelected(tag)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(textColor = if (isSelected) UvaNavy else OnSurface),
                    modifier = Modifier.background(
                        if (isSelected) UvaNavy.copy(alpha = 0.05f) else Color.Transparent
                    ),
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}


private val UVA_CENTER = LatLng(38.0336, -78.5080)

@Composable
fun CampusMap(
    placemarks: List<PlacemarkerWithTags>,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(UVA_CENTER, 15f)
    }

    GoogleMap(
        modifier            = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings          = MapUiSettings(
            zoomControlsEnabled     = false,  // cleaner; pinch-to-zoom still works
            myLocationButtonEnabled = false
        ),
        properties = MapProperties(isTrafficEnabled = false)
    ) {
        placemarks.forEach { pwt ->
            Marker(
                state   = MarkerState(LatLng(pwt.placemarker.latitude, pwt.placemarker.longitude)),
                title   = pwt.placemarker.name,
                snippet = pwt.placemarker.description
            )
        }
    }
}