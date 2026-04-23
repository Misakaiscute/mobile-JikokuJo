package com.jikokujo.map.presentation.tripinfosheet

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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jikokujo.R
import com.jikokujo.map.presentation.TripAction
import com.jikokujo.map.presentation.TripInfoState
import com.jikokujo.schedule.data.model.getColor
import com.jikokujo.schedule.data.model.getIcon
import com.jikokujo.theme.AppTheme

@Composable
fun SheetHeading(
    modifier: Modifier,
    state: TripInfoState,
    onAction: (TripAction) -> Unit
) {
    Row(
        modifier = modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Title(
            modifier = Modifier.weight(1f),
            state = state
        )
        if (state.favourites != null){
            ToggleFavouriteButton(
                modifier = Modifier,
                state = state,
                onAction = { action ->
                    onAction(action)
                }
            )
        }
    }
}
@Composable
private fun Title(
    modifier: Modifier,
    state: TripInfoState,
){
    Row(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(state.routeAssociated!!.getIcon()),
            contentDescription = "transportation icon",
            modifier = Modifier
                .fillMaxHeight(2/3f)
                .aspectRatio(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = state.trip!!.headSign,
                style = MaterialTheme.typography.titleLarge.merge(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                ),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = state.routeAssociated.shortName,
                style = MaterialTheme.typography.titleSmall.merge(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}
@Composable
private fun ToggleFavouriteButton(
    modifier: Modifier,
    state: TripInfoState,
    onAction: (TripAction) -> Unit
){
    val isTripFavourite: Boolean = state.favourites?.find {
        return@find it.route.id == state.trip?.routeId && it.atMins == state.trip.stops[0].arrivalTime
    } != null
    IconButton(
        modifier = modifier
            .fillMaxHeight()
            .aspectRatio(1f),
        onClick = {
            onAction(TripAction.ToggleFavourite(
                routeId = state.trip!!.routeId,
                atMins = state.trip.stops[0].arrivalTime
            ))
        },
        shape = RoundedCornerShape(0.dp)
    ){
        if (isTripFavourite) {
            Icon(
                modifier = Modifier
                    .fillMaxHeight(2/3f)
                    .aspectRatio(1f),
                painter = painterResource(R.drawable.star_solid),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = "remove favourite"
            )
        } else {
            Icon(
                modifier = Modifier
                    .fillMaxHeight(2/3f)
                    .aspectRatio(1f),
                painter = painterResource(R.drawable.star_outline),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = "add favourite"
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun SheetHeadingPreview(){
    AppTheme {
        SheetHeading(
            modifier = Modifier,
            state = TripInfoState(),
            onAction = { _ -> }
        )
    }
}