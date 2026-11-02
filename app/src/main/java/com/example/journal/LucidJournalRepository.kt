package com.example.journal

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LucidDreamRecord(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val content: String,
    val isLucid: Boolean,
    val dateString: String
)

class LucidJournalRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("lucid_dreams_journal_pro", Context.MODE_PRIVATE)
    private val _records = MutableStateFlow<List<LucidDreamRecord>>(emptyList())
    val records: StateFlow<List<LucidDreamRecord>> = _records.asStateFlow()

    init {
        loadRecords()
    }

    private fun loadRecords() {
        val raw = prefs.getString("saved_dreams", null)
        if (raw.isNullOrBlank()) {
            val defaults = getDefaultSampleRecords()
            _records.value = defaults
            saveToDisk(defaults)
        } else {
            try {
                val array = JSONArray(raw)
                val list = mutableListOf<LucidDreamRecord>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        LucidDreamRecord(
                            id = obj.optLong("id", System.currentTimeMillis()),
                            title = obj.optString("title", "حلم بدون عنوان"),
                            content = obj.optString("content", ""),
                            isLucid = obj.optBoolean("isLucid", false),
                            dateString = obj.optString("dateString", "")
                        )
                    )
                }
                _records.value = list
            } catch (e: Exception) {
                _records.value = getDefaultSampleRecords()
            }
        }
    }

    fun saveDream(title: String, content: String, isLucid: Boolean) {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val newRecord = LucidDreamRecord(
            title = if (title.isBlank()) "حلم بدون عنوان" else title.trim(),
            content = content.trim(),
            isLucid = isLucid,
            dateString = dateFormat.format(Date())
        )
        val updated = listOf(newRecord) + _records.value
        _records.value = updated
        saveToDisk(updated)
    }

    fun deleteDream(id: Long) {
        val updated = _records.value.filter { it.id != id }
        _records.value = updated
        saveToDisk(updated)
    }

    private fun saveToDisk(list: List<LucidDreamRecord>) {
        try {
            val array = JSONArray()
            for (item in list) {
                val obj = JSONObject()
                obj.put("id", item.id)
                obj.put("title", item.title)
                obj.put("content", item.content)
                obj.put("isLucid", item.isLucid)
                obj.put("dateString", item.dateString)
                array.put(obj)
            }
            prefs.edit().putString("saved_dreams", array.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getDefaultSampleRecords(): List<LucidDreamRecord> {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        val yesterday = dateFormat.format(Date(System.currentTimeMillis() - 86400000L))

        return listOf(
            LucidDreamRecord(
                id = 1L,
                title = "طيران فوق السحاب بإشارة جاما",
                content = "سمعت النغمة الصاعدة الهادئة أثناء النوم، تذكرت على الفور سيناريو الطيران وفحصت يدي في الحلم فأدركت أنني أحلم! استطعت التحليق والتحكم بالاتجاه بسلاسة.",
                isLucid = true,
                dateString = today
            ),
            LucidDreamRecord(
                id = 2L,
                title = "استكشاف غابة الفانتزي والمطر",
                content = "ليلة هادئة جداً مع ذبذبات سيتا. استيقظت بذاكرة واضحة ومشاعر مريحة وتفاصيل دقيقة عن أشجار الغابة.",
                isLucid = false,
                dateString = yesterday
            )
        )
    }
}
