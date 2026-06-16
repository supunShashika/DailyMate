package com.s23010186.dailymate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // Database Information
    private static final String DATABASE_NAME = "DailyMate.db";
    private static final int DATABASE_VERSION = 3;

    // Tasks Table and Columns
    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESC = "description";
    private static final String COLUMN_DEADLINE = "deadline";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_LAT = "latitude";
    private static final String COLUMN_LNG = "longitude";

    // Users Table and Columns
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";
    private static final String COL_NAME = "name";
    private static final String COL_USERNAME = "username";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "DatabaseHelper initialized");
        this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys = ON");
            
            // 1. Create Users Table first (for foreign key reference)
            String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                    COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_EMAIL + " TEXT UNIQUE, " +
                    COL_PASSWORD + " TEXT, " +
                    COL_NAME + " TEXT, " +
                    COL_USERNAME + " TEXT)";
            db.execSQL(createUsersTable);
            Log.d(TAG, "Users table created");

            // 2. Create Tasks Table
            String createTasksTable = "CREATE TABLE " + TABLE_TASKS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_DESC + " TEXT, " +
                    COLUMN_DEADLINE + " TEXT, " +
                    COLUMN_LAT + " REAL DEFAULT 0.0, " +
                    COLUMN_LNG + " REAL DEFAULT 0.0, " +
                    COLUMN_STATUS + " INTEGER DEFAULT 0, " +
                    COLUMN_USER_ID + " INTEGER NOT NULL, " +
                    "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "))";
            db.execSQL(createTasksTable);
            Log.d(TAG, "Tasks table created");
        } catch (Exception e) {
            Log.e(TAG, "Error creating tables: " + e.getMessage(), e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
            Log.d(TAG, "Database upgraded");
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database: " + e.getMessage(), e);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Enable foreign key constraints on every database open
        db.execSQL("PRAGMA foreign_keys = ON");
        Log.d(TAG, "Foreign keys enabled");
    }

    // ==========================================
    //              TASK METHODS
    // ==========================================

    public boolean insertTask(String title, String description, String deadline, double lat, double lng,int userId) {
        Cursor userCheck = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            
            // Verify user exists
            userCheck = db.query(TABLE_USERS, new String[]{COL_USER_ID}, COL_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}, null, null, null);
            boolean userExists = userCheck.getCount() > 0;
            
            if (!userExists) {
                Log.e(TAG, "User with ID " + userId + " does not exist");
                return false;
            }

            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, title);
            values.put(COLUMN_DESC, description);
            values.put(COLUMN_DEADLINE, deadline);
            values.put(COLUMN_LAT, lat);
            values.put(COLUMN_LNG, lng);
            values.put(COLUMN_USER_ID, userId);
            values.put(COLUMN_STATUS, 0);

            long result = db.insert(TABLE_TASKS, null, values);
            
            if (result == -1) {
                Log.e(TAG, "Failed to insert task (result = -1)");
            } else {
                Log.d(TAG, "Task inserted successfully with id: " + result);
            }
            
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting task: " + e.getMessage(), e);
            return false;
        } finally {
            if (userCheck != null) {
                userCheck.close();
            }
        }
    }

    public Cursor getPendingTasks(int userId) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_TASKS, null, COLUMN_STATUS + " = 0 AND " + COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}, null, null, COLUMN_DEADLINE + " ASC");
            Log.d(TAG, "Query returned " + cursor.getCount() + " rows");
            return cursor;
        } catch (Exception e) {
            Log.e(TAG, "Error getting pending tasks: " + e.getMessage(), e);
            return null;
        }
    }

    public int getUserCount() {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS, null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            Log.d(TAG, "Total users in database: " + count);
            return count;
        } catch (Exception e) {
            Log.e(TAG, "Error getting user count: " + e.getMessage(), e);
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public int getTaskCount() {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TASKS, null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            Log.d(TAG, "Total tasks in database: " + count);
            return count;
        } catch (Exception e) {
            Log.e(TAG, "Error getting task count: " + e.getMessage(), e);
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public boolean insertUser(String email, String password, String name, String username) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_EMAIL, email);
            values.put(COL_PASSWORD, password);
            values.put(COL_NAME, name);
            values.put(COL_USERNAME, username);

            long result = db.insert(TABLE_USERS, null, values);
            Log.d(TAG, "User inserted with id: " + result);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting user: " + e.getMessage(), e);
            return false;
        }
    }

    public boolean checkUser(String email, String password) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.query(TABLE_USERS, null, COL_EMAIL + " = ? AND " + COL_PASSWORD + " = ?",
                    new String[]{email, password}, null, null, null);
            boolean exists = cursor.getCount() > 0;
            Log.d(TAG, "User check for " + email + " returned: " + exists);
            return exists;
        } catch (Exception e) {
            Log.e(TAG, "Error checking user: " + e.getMessage(), e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // ==========================================
    //           NEW TASK METHODS
    // ==========================================

    // Mark task as completed (status = 1) or pending (status = 0)
    public boolean updateTaskStatus(int taskId, int status) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_STATUS, status);
            int result = db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating task status: " + e.getMessage(), e);
            return false;
        }
    }

    // Delete a task
    public boolean deleteTask(int taskId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            int result = db.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting task: " + e.getMessage(), e);
            return false;
        }
    }

    // ==========================================
    //           NEW USER METHODS
    // ==========================================

    // Get User Details for the Change Account Screen
    public Cursor getUserDetails(int userId) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.query(TABLE_USERS, null, COL_USER_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Error getting user details: " + e.getMessage(), e);
            return null;
        }
    }

    // Update User Details
    public boolean updateUserDetails(int userId, String name, String username) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_NAME, name);
            values.put(COL_USERNAME, username);
            int result = db.update(TABLE_USERS, values, COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating user details: " + e.getMessage(), e);
            return false;
        }
    }

    public Cursor getCompletedTasks(int userId) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.query(TABLE_TASKS, null, COLUMN_STATUS + " = 1 AND " + COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}, null, null, COLUMN_DEADLINE + " DESC");
        } catch (Exception e) {
            Log.e(TAG, "Error getting completed tasks: " + e.getMessage(), e);
            return null;
        }
    }

    // Get a single task by its ID
    public Cursor getTaskById(int taskId) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.rawQuery("SELECT * FROM " + TABLE_TASKS + " WHERE " + COLUMN_ID + " = ?",
                    new String[]{String.valueOf(taskId)});
        } catch (Exception e) {
            Log.e(TAG, "Error getting task by ID: " + e.getMessage(), e);
            return null;
        }
    }

    // Search tasks by title
    public Cursor searchTasks(int userId, String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Use LIKE for partial matches
        String query = "SELECT * FROM " + TABLE_TASKS + " WHERE " + COLUMN_USER_ID + " = ? AND " + COLUMN_TITLE + " LIKE ?";
        return db.rawQuery(query, new String[]{String.valueOf(userId), "%" + keyword + "%"});
    }

    // ==========================================
    //              NEW USER METHOD
    // ==========================================

    public boolean updatePassword(String email, String currentPassword, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        // First, check if a user exists with this email and current password
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL
                + " = ? AND " + COL_PASSWORD + " = ?", new String[]{email, currentPassword});

        try {
            if (cursor.getCount() > 0) {
                // The user verified their identity, now update the password
                ContentValues values = new ContentValues();
                values.put(COL_PASSWORD, newPassword);

                // Update the row where the email matches
                int result = db.update(TABLE_USERS, values, COL_EMAIL + " = ?", new String[]{email});
                return result > 0;
            }
            // Return false if the email or current password was wrong
            return false;
        } finally {
            cursor.close();
        }
    }


}