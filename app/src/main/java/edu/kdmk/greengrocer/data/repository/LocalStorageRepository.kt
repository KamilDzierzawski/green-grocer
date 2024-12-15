package edu.kdmk.greengrocer.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import edu.kdmk.greengrocer.data.model.AuthUser

class LocalStorageRepository(
    private val context: Context
) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val gson = Gson()

    fun saveUserData(authUser: AuthUser) {
        val editor = sharedPreferences.edit()
        val userJson = gson.toJson(authUser)
        editor.putString("user_data", userJson)
        editor.apply()
    }

    fun getUserData(): AuthUser? {
        val userJson = sharedPreferences.getString("user_data", null)
        return if (userJson != null) {
            gson.fromJson(userJson, AuthUser::class.java)
        } else {
            null
        }
    }

    fun clearUserData() {
        val editor = sharedPreferences.edit()
        editor.remove("user_data")
        editor.apply()
    }
}