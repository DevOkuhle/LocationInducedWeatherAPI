package com.example.locationinducedweatherapp.data.model

data class RememberSaveAblePassObject(
    var saveAbleStateFlow: Boolean = false,
    var setSaveAbleStateFlowToFalse: () -> Unit = {},
    var setSaveAbleStateFlowToTrue: () -> Unit = {}
)