package com.jikokujo.profile.presentation.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jikokujo.theme.Typography
import java.util.Locale.getDefault

@Composable
fun SectionHeading(
    modifier: Modifier,
    title: String,
    @DrawableRes icon: Int,
){
    Row(
        modifier = modifier
            .height(60.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 5.dp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier.width(4.dp))
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Text(
                text = title,
                style = Typography.headlineMedium.merge(
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier.width(4.dp))
            Icon(
                modifier = Modifier
                    .fillMaxHeight(1/2f)
                    .aspectRatio(1f),
                painter = painterResource(icon),
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = title.lowercase(getDefault()),
            )
        }
        Spacer(modifier.width(4.dp))
        HorizontalDivider(
            modifier = Modifier.weight(8f),
            thickness = 5.dp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}