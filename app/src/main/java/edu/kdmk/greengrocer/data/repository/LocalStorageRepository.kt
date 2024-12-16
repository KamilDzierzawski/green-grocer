package edu.kdmk.greengrocer.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import edu.kdmk.greengrocer.data.model.AuthUser
import java.io.File

class LocalStorageRepository(
    private val context: Context
) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val gson = Gson()

    private val imageFilePath: String = "${context.filesDir}/profile_images/profile_image.jpg"

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

    fun saveUserProfileImage(imageFile: File) {
        val imageDir = File(context.filesDir, "profile_images")
        if (!imageDir.exists()) {
            imageDir.mkdir()
        }

        val destFile = File(imageDir, "profile_image.jpg")
        imageFile.copyTo(destFile, overwrite = true)
    }

    fun getUserProfileImage(): File? {
        val imageFile = File(imageFilePath)
        return if (imageFile.exists()) {
            imageFile
        } else {
            null
        }
    }

    fun deleteUserProfileImage() {
        val imageFile = File(imageFilePath)
        if (imageFile.exists()) {
            imageFile.delete()
        }
    }

    fun clearUserData() {
        val editor = sharedPreferences.edit()
        editor.remove("user_data")
        editor.apply()

        val imageFile = File(imageFilePath)
        if (imageFile.exists()) {
            imageFile.delete()
        }
    }
}