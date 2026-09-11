package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject

enum class SampleCategory {
    VIRUS,
    PEST,
    BENEFICIAL
}

data class SampleItem(
    val id: String,
    val category: SampleCategory,
    val nameArabic: String,
    val nameScientific: String,
    var value: String = "0/3",
    var notes: String = "---"
) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("category", category.name)
            put("nameArabic", nameArabic)
            put("nameScientific", nameScientific)
            put("value", value)
            put("notes", notes)
        }
    }

    companion object {
        fun fromJson(obj: JSONObject): SampleItem {
            return SampleItem(
                id = obj.optString("id", ""),
                category = runCatching { SampleCategory.valueOf(obj.optString("category", SampleCategory.PEST.name)) }
                    .getOrDefault(SampleCategory.PEST),
                nameArabic = obj.optString("nameArabic", ""),
                nameScientific = obj.optString("nameScientific", ""),
                value = obj.optString("value", "0/3"),
                notes = obj.optString("notes", "---")
            )
        }

        fun listToJson(list: List<SampleItem>): String {
            val array = JSONArray()
            list.forEach { array.put(it.toJson()) }
            return array.toString()
        }

        fun listFromJson(jsonStr: String): List<SampleItem> {
            if (jsonStr.isBlank()) return emptyList()
            return try {
                val array = JSONArray(jsonStr)
                val list = mutableListOf<SampleItem>()
                for (i in 0 until array.length()) {
                    list.add(fromJson(array.getJSONObject(i)))
                }
                list
            } catch (e: Exception) {
                emptyList()
            }
        }

        fun getDefaultSamples(): List<SampleItem> {
            return listOf(
                // 1. Viruses (الفيروسات)
                SampleItem(
                    id = "v_p",
                    category = SampleCategory.VIRUS,
                    nameArabic = "فيروس P",
                    nameScientific = "Virus P",
                    value = "0",
                    notes = "---"
                ),

                // 2. Pests (الآفات)
                SampleItem(
                    id = "p_acariens",
                    category = SampleCategory.PEST,
                    nameArabic = "العناكب الحمراء",
                    nameScientific = "Acariens.R",
                    value = "0/3",
                    notes = "---"
                ),
                SampleItem(
                    id = "p_oidium",
                    category = SampleCategory.PEST,
                    nameArabic = "البياض الدقيقي",
                    nameScientific = "Oïdium",
                    value = "0/3",
                    notes = "---"
                ),
                SampleItem(
                    id = "p_mouche",
                    category = SampleCategory.PEST,
                    nameArabic = "الذبابة البيضاء",
                    nameScientific = "Mouche blanche",
                    value = "0/3",
                    notes = "---"
                ),
                SampleItem(
                    id = "p_puceron_vert",
                    category = SampleCategory.PEST,
                    nameArabic = "المن الأخضر",
                    nameScientific = "Puceron vert",
                    value = "0/3",
                    notes = "---"
                ),
                SampleItem(
                    id = "p_puceron_noir",
                    category = SampleCategory.PEST,
                    nameArabic = "المن الأسود",
                    nameScientific = "Puceron noir",
                    value = "0/3",
                    notes = "---"
                ),
                SampleItem(
                    id = "p_thrips",
                    category = SampleCategory.PEST,
                    nameArabic = "تربس الزهور",
                    nameScientific = "Thrips Flower",
                    value = "0/3",
                    notes = "---"
                ),
                SampleItem(
                    id = "p_thrips_chili",
                    category = SampleCategory.PEST,
                    nameArabic = "التربس شيلي",
                    nameScientific = "Thrips chili",
                    value = "0/3",
                    notes = "---"
                ),
                SampleItem(
                    id = "p_nematodes",
                    category = SampleCategory.PEST,
                    nameArabic = "النيماتودا",
                    nameScientific = "Nématodes",
                    value = "0/3",
                    notes = "---"
                ),
                SampleItem(
                    id = "p_tuta",
                    category = SampleCategory.PEST,
                    nameArabic = "توتا أبسلوتا",
                    nameScientific = "Tuta absoluta",
                    value = "0/3",
                    notes = "---"
                ),

                // 3. Beneficial Insects (الحشرات النافعة)
                SampleItem(
                    id = "b_orius",
                    category = SampleCategory.BENEFICIAL,
                    nameArabic = "أوريوس",
                    nameScientific = "Orius",
                    value = "0/3",
                    notes = "---"
                ),
                SampleItem(
                    id = "b_swirskii",
                    category = SampleCategory.BENEFICIAL,
                    nameArabic = "سويرسكي",
                    nameScientific = "Swirskii",
                    value = "0/3",
                    notes = "---"
                ),
                SampleItem(
                    id = "b_phytoseiulus",
                    category = SampleCategory.BENEFICIAL,
                    nameArabic = "الفيتوسيولوس",
                    nameScientific = "Phytoseiulus",
                    value = "0/3",
                    notes = "---"
                ),
                SampleItem(
                    id = "b_eretmocerus",
                    category = SampleCategory.BENEFICIAL,
                    nameArabic = "إيريتاموس",
                    nameScientific = "Eretmocerus",
                    value = "0/3",
                    notes = "---"
                ),
                SampleItem(
                    id = "b_aphidius",
                    category = SampleCategory.BENEFICIAL,
                    nameArabic = "كوليماني",
                    nameScientific = "Aphidius col...",
                    value = "0/3",
                    notes = "---"
                ),
                SampleItem(
                    id = "b_matricariae",
                    category = SampleCategory.BENEFICIAL,
                    nameArabic = "اف-متريكاري",
                    nameScientific = "A.matricariae",
                    value = "0/3",
                    notes = "---"
                ),
                SampleItem(
                    id = "b_chrysoperla",
                    category = SampleCategory.BENEFICIAL,
                    nameArabic = "كريسوب",
                    nameScientific = "Chrysoperla",
                    value = "0/3",
                    notes = "---"
                )
            )
        }
    }
}
