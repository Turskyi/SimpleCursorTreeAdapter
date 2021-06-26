package io.github.turskyi.simplecursortreeadapter

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper

class DB(private val mCtx: Context) {
    companion object {
        private const val DB_NAME = "myDb"
        private const val DB_VERSION = 1

        // Company table name, fields and create query
        private const val COMPANY_TABLE = "company"
        const val COMPANY_COLUMN_ID = "_id"
        const val COMPANY_COLUMN_NAME = "name"
        private const val COMPANY_TABLE_CREATE = ("create table "
                + COMPANY_TABLE + "(" + COMPANY_COLUMN_ID
                + " integer primary key, " + COMPANY_COLUMN_NAME + " text" + ");")

        // Phone table name, fields and create query
        private const val PHONE_TABLE = "phone"
        private const val PHONE_COLUMN_ID = "_id"
        const val PHONE_COLUMN_NAME = "name"
        const val PHONE_COLUMN_COMPANY = "company"
        private const val PHONE_TABLE_CREATE = ("create table "
                + PHONE_TABLE + "(" + PHONE_COLUMN_ID
                + " integer primary key autoincrement, " + PHONE_COLUMN_NAME
                + " text, " + PHONE_COLUMN_COMPANY + " integer" + ");")
    }

    private var mDBHelper: DBHelper? = null
    private var mDB: SQLiteDatabase? = null

    // open connection
    fun open() {
        mDBHelper = DBHelper(mCtx, DB_NAME, null, DB_VERSION)
        mDB = mDBHelper!!.writableDatabase
    }

    // close connection
    fun close() {
        if (mDBHelper != null) mDBHelper!!.close()
    }

    // company data
    val companyData: Cursor
        get() = mDB!!.query(COMPANY_TABLE, null, null, null, null, null, null)

    // data on the phones of a specific group
    fun getPhoneData(companyID: Long): Cursor {
        return mDB!!.query(
            PHONE_TABLE, null, PHONE_COLUMN_COMPANY + " = "
                    + companyID, null, null, null, null
        )
    }

    private inner class DBHelper(
        context: Context?, name: String?, factory: CursorFactory?,
        version: Int
    ) : SQLiteOpenHelper(context, name, factory, version) {
        override fun onCreate(db: SQLiteDatabase) {
            val cv = ContentValues()

            // data on the phones of a specific group
            val companies = arrayOf("HTC", "Samsung", "LG")

            // create and fill in the companies table
            db.execSQL(COMPANY_TABLE_CREATE)
            for (i in companies.indices) {
                cv.put(COMPANY_COLUMN_ID, i + 1)
                cv.put(COMPANY_COLUMN_NAME, companies[i])
                db.insert(COMPANY_TABLE, null, cv)
            }

            // phone (item) names
            val phonesHTC = arrayOf(
                "Sensation", "Desire",
                "Wildfire", "Hero"
            )
            val phonesSamsungs = arrayOf(
                "Galaxy S II", "Galaxy Nexus",
                "Wave"
            )
            val phonesLG = arrayOf(
                "Optimus", "Optimus Link",
                "Optimus Black", "Optimus One"
            )

            // create and fill in the table of phones
            db.execSQL(PHONE_TABLE_CREATE)
            cv.clear()
            for (i in phonesHTC.indices) {
                cv.put(PHONE_COLUMN_COMPANY, 1)
                cv.put(PHONE_COLUMN_NAME, phonesHTC[i])
                db.insert(PHONE_TABLE, null, cv)
            }
            for (i in phonesSamsungs.indices) {
                cv.put(PHONE_COLUMN_COMPANY, 2)
                cv.put(PHONE_COLUMN_NAME, phonesSamsungs[i])
                db.insert(PHONE_TABLE, null, cv)
            }
            for (i in phonesLG.indices) {
                cv.put(PHONE_COLUMN_COMPANY, 3)
                cv.put(PHONE_COLUMN_NAME, phonesLG[i])
                db.insert(PHONE_TABLE, null, cv)
            }
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
    }
}