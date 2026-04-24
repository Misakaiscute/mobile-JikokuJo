package com.jikokujo.schedule.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
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
    onAction: (ScheduleAction) -> Unit
){
    val itemHeight = 40
    val maxItems = 5
    LazyColumn (
        modifier = modifier.heightIn(max = (((itemHeight + 2) * maxItems) - 2).dp)
    ) {
        items(state.queryables.count()) { index ->
            if (index > 0){
                HorizontalDivider(
                    modifier = modifier,
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            QueryableDropDownItem(
                modifier = modifier
                    .background(if (index % 2 == 0) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface)
                    .clickable(
                        enabled = !state.loading.contains(Loadable.Queryables()),
                        onClick = {
                            when (state.queryables[index]){
                                is Queryable.Route -> onAction(ScheduleAction.SelectRoute(state.queryables[index] as Queryable.Route))
                                is Queryable.Stop -> onAction(ScheduleAction.SelectStop(state.queryables[index] as Queryable.Stop))
                            }
                        }
                    ),
                item = state.queryables[index],
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
    Row(
        modifier = modifier
            .background(if (item is Queryable.Route) item.getColor("50") else Color.Transparent)
            .fillMaxWidth()
            .height(itemHeight.dp)
            .padding(horizontal = 5.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (item){
            is Queryable.Stop -> {
                Icon(
                    modifier = Modifier
                        .height((itemHeight - 5).dp)
                        .width((itemHeight - 5).dp),
                    painter = painterResource(R.drawable.busstop),
                    contentDescription = "stop",
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier.width(12.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.name,
                    style = Typography.bodyMedium.merge(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
            is Queryable.Route -> {
                Icon(
                    modifier = Modifier
                        .height((itemHeight - 5).dp)
                        .width((itemHeight - 5).dp),
                    painter = painterResource(item.getIcon()),
                    contentDescription = "transport icon",
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier.width(12.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.shortName,
                    style = Typography.bodyMedium.merge(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
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
        item = Queryable.Stop(listOf("001"), "Kálvin tér"),
        itemHeight = 40
    )
}