package com.jikokujo.profile.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jikokujo.R
import com.jikokujo.core.data.model.Favourite
import com.jikokujo.core.utils.loadingShimmer
import com.jikokujo.core.utils.timeFormatter
import com.jikokujo.profile.presentation.Loadable
import com.jikokujo.profile.presentation.ProfileAction
import com.jikokujo.profile.presentation.ProfileState
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.getColor
import com.jikokujo.schedule.data.model.getIcon
import com.jikokujo.theme.AppTheme
import com.jikokujo.theme.Typography

@Composable
fun FavouritesContent(
    modifier: Modifier,
    profileState: ProfileState,
    onAction: (ProfileAction) -> Unit
){
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 5.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (profileState.loading.contains(Loadable.Favourites()) || profileState.favourites == null) {
            for(i in 0..8){
                FavouriteItem(
                    modifier = Modifier.loadingShimmer(
                        durationMillis = 1000,
                        background = MaterialTheme.colorScheme.surface
                    ),
                    favourite = null,
                    onAction = { action ->
                        onAction(action)
                    }
                )
            }
        } else if (profileState.error.contains(Loadable.Favourites())) {
            Row(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Valami hiba történt.",
                    style = Typography.bodyLarge.merge(
                        color = MaterialTheme.colorScheme.error
                    )
                )
            }
        } else {
            if (profileState.favourites.count() < 1){
                Row(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Nincsen még egy járat sem a kedvencek között.",
                        style = Typography.bodyLarge.merge(
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            } else {
                profileState.favourites.sortedBy {
                    it.atMins
                }.forEach { fav ->
                    FavouriteItem(
                        modifier = Modifier,
                        favourite = fav,
                        onAction = { action ->
                            onAction(action)
                        }
                    )
                }
            }
        }
    }
}
@Composable
private fun FavouriteItem(
    modifier: Modifier,
    favourite: Favourite?,
    onAction: (ProfileAction) -> Unit
){
    if (favourite != null){
        Row(
            modifier = modifier
                .height(40.dp)
                .fillMaxWidth()
                .padding(all = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
                painter = painterResource(favourite.route.getIcon()),
                tint = favourite.route.getColor(),
                contentDescription = "vehicle icon"
            )
            Spacer(Modifier.width(4.dp))
            Text(
                modifier = Modifier.width(80.dp),
                text = favourite.route.shortName,
                style = Typography.bodyMedium.merge(
                    color = favourite.route.getColor(),
                    textAlign = TextAlign.Center
                )
            )
            Spacer(Modifier.width(4.dp))
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                Text(
                    text = timeFormatter(favourite.atMins),
                    style = Typography.bodyLarge.merge(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
            Spacer(Modifier.width(4.dp))
            IconButton(
                onClick = {
                    onAction(ProfileAction.ToggleFavourite(favourite.route.id, favourite.atMins))
                },
                shape = RoundedCornerShape(0.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.trashcan),
                    contentDescription = "back button",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    } else {
        Row(
            modifier = modifier
                .height(40.dp)
                .fillMaxWidth()
                .padding(all = 4.dp)
                .loadingShimmer(
                    durationMillis = 1000,
                    background = MaterialTheme.colorScheme.surface
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {}
    }
}
@Preview(showBackground = true)
@Composable
private fun FavouriteItemPreview(){
    AppTheme {
        FavouriteItem(
            modifier = Modifier.background(Color.White),
            favourite = Favourite(
                route = Queryable.Route(
                    shortName = "123",
                    type = 2,
                    color = "3464b3",
                    id = "111"
                ),
                atMins = 678
            ),
            onAction = { _ -> }
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun FavouriteItemLoadingPreview(){
    AppTheme {
        FavouriteItem(
            modifier = Modifier.background(Color.White),
            favourite = null,
            onAction = { _ -> }
        )
    }
}