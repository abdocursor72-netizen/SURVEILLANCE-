package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Report
import com.example.data.model.SampleItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import androidx.room.migration.Migration
import java.util.Locale

@Database(entities = [Report::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reportDao(): ReportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE reports ADD COLUMN imageUri TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE reports ADD COLUMN diseaseNote TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sf_surveillance.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(DatabaseCallback(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = getDatabase(context).reportDao()
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                    val today = sdf.format(Date())
                    val defaultSamples = SampleItem.getDefaultSamples()
                    val jsonSamples = SampleItem.listToJson(defaultSamples)

                    // Seed two sample reports matching the user's screenshots
                    val report1 = Report(
                        observerName = "ملاحظ رئيسي",
                        sector = "a",
                        fieldNumber = "5",
                        observationDate = today,
                        savedDate = today,
                        specialNotes = "تم فحص الحقل بالكامل، ظهور طفيف للذبابة البيضاء.",
                        samplesJson = jsonSamples,
                        isPinned = false,
                        isDeleted = false,
                        timestamp = System.currentTimeMillis() - 60000
                    )

                    val report2 = Report(
                        observerName = "ملاحظ رئيسي",
                        sector = "A",
                        fieldNumber = "5",
                        observationDate = today,
                        savedDate = today,
                        specialNotes = "الحالة ممتازة مع انتشار جيد للحشرات النافعة.",
                        samplesJson = jsonSamples,
                        isPinned = false,
                        isDeleted = false,
                        timestamp = System.currentTimeMillis() - 120000
                    )

                    dao.insertReport(report1)
                    dao.insertReport(report2)
                }
            }
        }
    }
}
