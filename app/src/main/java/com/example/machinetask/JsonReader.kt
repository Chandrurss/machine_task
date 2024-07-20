package com.example.machinetask

import android.content.Context
import org.json.JSONArray

class JsonReader(private val context: Context) {
    fun readJsonFile(fileName: String): JSONArray? {
        var json: JSONArray? = null
        try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val jsonString = String(buffer, Charsets.UTF_8)
            json = JSONArray(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return json
    }
}