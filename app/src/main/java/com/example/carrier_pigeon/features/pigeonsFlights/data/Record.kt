package com.example.carrier_pigeon.features.pigeonsFlights.data

data class Record(
    var nr: String,
    var country: String,
    var dateOfBirth: String,
    var series: String,
    var gender: String,
    var color: String,
    var firstVaccine: Int,
    val secondVaccine: Int,
    val thirdVaccine: Int
)
