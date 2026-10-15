package com.johnohhh.diybrick

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

class ActionStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveAction(uid: String, action: CardAction) {
        prefs.edit()
            .putString("$PREFIX$uid", JSONObject().apply {
                put("type", action.type)
                put("label", action.label)
            }.toString())
            .apply()
    }

    fun getAction(uid: String): CardAction? {
        val json = prefs.getString("$PREFIX$uid", null) ?: return null
        return try {
            val obj = JSONObject(json)
            CardAction(obj.getString("type"), obj.getString("label"))
        } catch (_: Exception) {
            null
        }
    }

    fun getAllActions(): Map<String, CardAction> {
        val result = mutableMapOf<String, CardAction>()
        prefs.all.forEach { (key, value) ->
            if (key.startsWith(PREFIX) && value is String) {
                try {
                    val obj = JSONObject(value)
                    val uid = key.removePrefix(PREFIX)
                    result[uid] = CardAction(obj.getString("type"), obj.getString("label"))
                } catch (_: Exception) { /* skip corrupt */ }
            }
        }
        return result
    }

    fun deleteAction(uid: String) {
        prefs.edit().remove("$PREFIX$uid").apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "diy_brick_actions"
        private const val PREFIX = "card_"
    }
}
