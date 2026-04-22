package com.jikokujo.profile.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jikokujo.core.data.model.User
import com.jikokujo.core.utils.loadingShimmer
import com.jikokujo.profile.presentation.ProfileState
import com.jikokujo.theme.AppTheme
import com.jikokujo.theme.Typography

@Composable
fun UserAccountContent(
    modifier: Modifier,
    profileState: ProfileState
){
    if (profileState.user != null) {
        val initials = "${profileState.user.firstName.first()}${profileState.user.lastName.first()}"

        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = Typography.bodyMedium.merge(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${profileState.user.firstName} ${profileState.user.lastName}",
                    style = Typography.bodyLarge.merge(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = profileState.user.email,
                    style = Typography.bodySmall.merge(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = Color.Transparent,
                        shape = CircleShape
                    )
                    .loadingShimmer(
                        durationMillis = 1000,
                        background = MaterialTheme.colorScheme.surface
                    )
            )
            Spacer(Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(1/2f)
                        .weight(1f)
                        .loadingShimmer(
                            durationMillis = 1000,
                            background = MaterialTheme.colorScheme.surface
                        )
                )
                Spacer(Modifier.height(5.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .loadingShimmer(
                            durationMillis = 1000,
                            background = MaterialTheme.colorScheme.surface
                        )
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun UserAccountContentPreview(){
    AppTheme {
        UserAccountContent(
            modifier = Modifier,
            profileState = ProfileState(
                user = User(
                    id = 1,
                    firstName = "John",
                    lastName = "Doe",
                    email = "john.doe@gmail.com"
                )
            )
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun UserAccountContentLoadingPreview(){
    AppTheme {
        UserAccountContent(
            modifier = Modifier,
            profileState = ProfileState()
        )
    }
}