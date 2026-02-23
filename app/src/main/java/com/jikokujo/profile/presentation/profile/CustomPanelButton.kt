package com.jikokujo.profile.presentation.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jikokujo.R
import com.jikokujo.theme.AppTheme
import com.jikokujo.theme.Typography
import java.util.Locale.getDefault

@Composable
fun CustomPanelButton(
    modifier: Modifier,
    @DrawableRes icon: Int,
    color: Color,
    text: String,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
){
    Button(
        modifier = modifier.padding(all = 2.dp),
        shape = RoundedCornerShape(size = 10.dp),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 2.dp,
            color = color
        ),
        enabled = isEnabled,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Icon(
                modifier = Modifier.scale(1.5f),
                painter = painterResource(icon),
                tint = color,
                contentDescription = text.lowercase(getDefault()),
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = text,
                style = Typography.bodyMedium.merge(
                    color = color,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun CustomPanelPreview(){
    AppTheme(dynamicColor = false) {
        CustomPanelButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            icon = R.drawable.favourites_star,
            color = MaterialTheme.colorScheme.primary,
            text = "Kedvencek",
            onClick = {}
        )
    }
}