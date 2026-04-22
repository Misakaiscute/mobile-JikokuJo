package com.jikokujo.map.presentation.tripinfosheet

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jikokujo.map.presentation.TripAction
import com.jikokujo.map.presentation.TripInfoState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripInfoSheet(
    modifier: Modifier,
    state: TripInfoState,
    onAction: (TripAction) -> Unit
){
    val sheetState = rememberModalBottomSheetState()

    if (state.tripInfoShown) {
        ModalBottomSheet(
            modifier = modifier,
            onDismissRequest = {
                onAction(TripAction.HideTripInfo)
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            SheetHeading(
                modifier = Modifier,
                state = state,
                onAction = { action ->
                    onAction(action)
                }
            )
            Spacer(Modifier.height(4.dp))
            SheetContent(
                modifier = Modifier,
                state = state
            )
        }
    }
}