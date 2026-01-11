package com.jikokujo.schedule.presentation.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.unit.dp
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.getIconForType
@Composable
fun RouteSelectionDropDown(
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
            .heightIn(0.dp, (((itemHeight + 1) * maxItems) - 1).dp)
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
            RouteSelectionDropDownItem(
                modifier = modifier.background(if (i % 2 == 0) Color.LightGray else Color.White),
                item = state.queryables[i] as Queryable.Route,
                onClick = { onAction(Action.SelectRoute(state.queryables[i] as Queryable.Route)) },
                itemHeight = itemHeight
            )
        }
    }
}
@Composable
private fun RouteSelectionDropDownItem(
    modifier: Modifier,
    item: Queryable.Route,
    onClick: () -> Unit,
    itemHeight: Int
){
    val overscrollEffect = rememberOverscrollEffect()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight.dp)
            .clickable(
                onClick = onClick
            )
            .overscroll(overscrollEffect),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.fillMaxWidth(1/10f),
            painter = painterResource(item.getIconForType()),
            contentDescription = "transport icon",
            tint = Color(item.color.hexToLong())
        )
        Text(
            modifier = Modifier.weight(1f),
            text = item.name,
            color = Color(item.color.hexToLong())
        )
    }
}