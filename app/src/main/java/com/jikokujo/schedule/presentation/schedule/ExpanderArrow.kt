package com.jikokujo.schedule.presentation.schedule

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jikokujo.R

@Composable
fun ExpanderArrow(
    modifier: Modifier,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(
            topStart = 0f,
            topEnd = 0f,
            bottomEnd = 20f,
            bottomStart = 20f
        ),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = PaddingValues(all = 0.dp),
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier.rotate(if (isExpanded) 180f else 0f),
            painter = painterResource(R.drawable.expand_button),
            contentDescription = "expand button",
            tint = Color.Black
        )
    }
}