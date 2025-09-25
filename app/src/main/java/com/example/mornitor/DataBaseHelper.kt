package com.example.mornitor

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DataBaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MornitorDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "user"
        private const val COLUMN_ID = "id"
        private const val COLUMN_PASSWORD = "password"
        // 由于是单用户应用，我们使用固定ID
        private const val DEFAULT_USER_ID = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // 创建用户表
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """
        db?.execSQL(createTableQuery)
        Log.d("DataBaseHelper", "User table created")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 升级数据库时的操作
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // 检查密码是否存在
    fun doesPasswordExist(): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor? = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID),
            "$COLUMN_ID = ?",
            arrayOf(DEFAULT_USER_ID.toString()),
            null, null, null
        )
        val exists = cursor?.count ?: 0 > 0
        cursor?.close()
        db.close()
        return exists
    }

    // 初始化密码
    fun initPassword(password: String): Boolean {
        if (doesPasswordExist()) {
            return false // 密码已存在，不能初始化
        }

        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_ID, DEFAULT_USER_ID)
        contentValues.put(COLUMN_PASSWORD, password)

        val result = db.insert(TABLE_NAME, null, contentValues).toInt() != -1
        db.close()
        return result
    }

    // 验证密码
    fun verifyPassword(password: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor? = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID),
            "$COLUMN_ID = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(DEFAULT_USER_ID.toString(), password),
            null, null, null
        )
        val isValid = cursor?.count ?: 0 > 0
        cursor?.close()
        db.close()
        return isValid
    }

    // 更新密码
    fun updatePassword(newPassword: String): Boolean {
        if (!doesPasswordExist()) {
            return false // 密码不存在，无法更新
        }

        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_PASSWORD, newPassword)

        val result = db.update(
            TABLE_NAME,
            contentValues,
            "$COLUMN_ID = ?",
            arrayOf(DEFAULT_USER_ID.toString())
        ) > 0
        db.close()
        return result
    }
}