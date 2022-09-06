package com.example.carrier_pigeon.data.enums

import android.content.SharedPreferences

class SharedPrefsWrapper(private val sharedPref: SharedPreferences) {

    companion object {
        const val PHONE_NUMBER = "phone_number"
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
        const val HOME_ADDRESS = "home_address"
    }

    fun savePhoneNumber(phoneNumber: String) {
        sharedPref.edit().putString(PHONE_NUMBER, phoneNumber).apply()
    }

    fun getPhoneNumber() = sharedPref.getString(PHONE_NUMBER, "") ?: ""

    fun saveFirstName(firstName: String) {
        sharedPref.edit().putString(FIRST_NAME, firstName).apply()
    }

    fun getFirstName() = sharedPref.getString(FIRST_NAME, "") ?: ""

    fun saveLastName(lastName: String) {
        sharedPref.edit().putString(LAST_NAME, lastName).apply()
    }

    fun getLastName() = sharedPref.getString(LAST_NAME, "") ?: ""

    fun saveHomeAddress(homeAddress: String) {
        sharedPref.edit().putString(HOME_ADDRESS, homeAddress).apply()
    }

    fun getHomeAddress() = sharedPref.getString(HOME_ADDRESS, "") ?: ""
}
