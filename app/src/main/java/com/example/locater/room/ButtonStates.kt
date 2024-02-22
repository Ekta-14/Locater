package com.example.locater.room

import androidx.room.ColumnInfo

data class ButtonStates(
    @ColumnInfo(name = "btn_check_in_state") val checkInState: Boolean,
    @ColumnInfo(name = "btn_check_out_state") val checkOutState: Boolean
)
