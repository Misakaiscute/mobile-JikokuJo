package com.jikokujo.schedule.presentation.schedule

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jikokujo.R
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.getColor
import com.jikokujo.schedule.data.model.getIcon
import com.jikokujo.theme.Typography

@Composable
fun QueryableDropDown(
    modifier: Modifier,
    state: ScheduleSearchState,
    onAction: (Action) -> Unit
){
    val itemHeight = 40
    val maxItems = 5
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(bottomStart = 20f, bottomEnd = 0f, topStart = 0f, topEnd = 0f))
            .height((((itemHeight + 1) * maxItems) - 1).dp)
            .scrollable(
                state = scrollState,
                orientation = Orientation.Vertical
            )
    ) {
        for (i in 0 ..< state.queryables.count()){
            if (i > 0){
                HorizontalDivider(
                    modifier = modifier,
                    thickness = 1.dp,
                    color = Color.Black
                )
            }
            QueryableDropDownItem(
                modifier = modifier
                    .background(if (i % 2 == 0) Color.LightGray else Color.White)
                    .clickable(
                        enabled = !state.isLoading,
                        onClick = {
                            when (state.queryables[i]){
                                is Queryable.Route -> onAction(Action.SelectRoute(state.queryables[i] as Queryable.Route))
                                is Queryable.Stop -> onAction(Action.SelectStop(state.queryables[i] as Queryable.Stop))
                            }
                        }
                    ),
                item = state.queryables[i],
                itemHeight = itemHeight
            )
        }
    }
}
@Composable
private fun QueryableDropDownItem(
    modifier: Modifier,
    item: Queryable,
    itemHeight: Int
){
    val overscrollEffect = rememberOverscrollEffect()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight.dp)
            .overscroll(overscrollEffect),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (item){
            is Queryable.Stop -> {
                Icon(
                    modifier = Modifier
                        .fillMaxWidth(1/10f)
                        .aspectRatio(1f),
                    painter = painterResource(R.drawable.busstop),
                    contentDescription = "stop",
                    tint = Color.Black
                )
                Spacer(modifier.width(12.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.name,
                    style = Typography.bodyLarge,
                    color = Color.Black
                )
            }
            is Queryable.Route -> {
                Icon(
                    modifier = Modifier
                        .fillMaxWidth(1/10f)
                        .aspectRatio(1f),
                    painter = painterResource(item.getIcon()),
                    contentDescription = "transport icon",
                    tint = item.getColor()
                )
                Spacer(modifier.width(12.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.shortName,
                    style = Typography.bodyLarge,
                    color = item.getColor()
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun DropdownItemWithRoutePreview(){
    QueryableDropDownItem(
        modifier = Modifier,
        item = Queryable.Route("001", "M3-mas metró", "0b6324", 3),
        itemHeight = 40
    )
}
@Preview(showBackground = true)
@Composable
private fun DropdownItemWithStopPreview(){
    QueryableDropDownItem(
        modifier = Modifier,
        item = Queryable.Stop("001", "Kálvin tér"),
        itemHeight = 40
    )
}