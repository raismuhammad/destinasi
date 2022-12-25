package com.raisproject.destinasi.model

data class Destination(
    var id: String? = null,
    var destinationImage: String? = null,
    var destinationName: String? = null,
    var cityId: String? = null,
    var address: String? = null,
    var latitude: String? = null,
    var longitude: String? = null,
    var description: String? = null,
    var phoneNumber: String? = null
)
