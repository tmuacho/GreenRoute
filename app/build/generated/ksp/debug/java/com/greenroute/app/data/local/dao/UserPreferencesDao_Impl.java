package com.greenroute.app.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.greenroute.app.data.local.entities.UserPreferences;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserPreferencesDao_Impl implements UserPreferencesDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserPreferences> __insertionAdapterOfUserPreferences;

  private final EntityDeletionOrUpdateAdapter<UserPreferences> __deletionAdapterOfUserPreferences;

  private final EntityDeletionOrUpdateAdapter<UserPreferences> __updateAdapterOfUserPreferences;

  public UserPreferencesDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserPreferences = new EntityInsertionAdapter<UserPreferences>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_preferences` (`id`,`userId`,`preferredTransport`,`ecoModeEnabled`,`notificationsEnabled`,`language`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserPreferences entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getUserId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getUserId());
        }
        statement.bindString(3, entity.getPreferredTransport());
        final int _tmp = entity.getEcoModeEnabled() ? 1 : 0;
        statement.bindLong(4, _tmp);
        final int _tmp_1 = entity.getNotificationsEnabled() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        statement.bindString(6, entity.getLanguage());
      }
    };
    this.__deletionAdapterOfUserPreferences = new EntityDeletionOrUpdateAdapter<UserPreferences>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `user_preferences` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserPreferences entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfUserPreferences = new EntityDeletionOrUpdateAdapter<UserPreferences>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `user_preferences` SET `id` = ?,`userId` = ?,`preferredTransport` = ?,`ecoModeEnabled` = ?,`notificationsEnabled` = ?,`language` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserPreferences entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getUserId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getUserId());
        }
        statement.bindString(3, entity.getPreferredTransport());
        final int _tmp = entity.getEcoModeEnabled() ? 1 : 0;
        statement.bindLong(4, _tmp);
        final int _tmp_1 = entity.getNotificationsEnabled() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        statement.bindString(6, entity.getLanguage());
        statement.bindLong(7, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final UserPreferences preferences,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfUserPreferences.insertAndReturnId(preferences);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final UserPreferences preferences,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfUserPreferences.handle(preferences);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final UserPreferences preferences,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfUserPreferences.handle(preferences);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<UserPreferences> getPreferencesByUserId(final int userId) {
    final String _sql = "SELECT * FROM user_preferences WHERE userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_preferences"}, new Callable<UserPreferences>() {
      @Override
      @Nullable
      public UserPreferences call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfPreferredTransport = CursorUtil.getColumnIndexOrThrow(_cursor, "preferredTransport");
          final int _cursorIndexOfEcoModeEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "ecoModeEnabled");
          final int _cursorIndexOfNotificationsEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationsEnabled");
          final int _cursorIndexOfLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "language");
          final UserPreferences _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final Integer _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            }
            final String _tmpPreferredTransport;
            _tmpPreferredTransport = _cursor.getString(_cursorIndexOfPreferredTransport);
            final boolean _tmpEcoModeEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEcoModeEnabled);
            _tmpEcoModeEnabled = _tmp != 0;
            final boolean _tmpNotificationsEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfNotificationsEnabled);
            _tmpNotificationsEnabled = _tmp_1 != 0;
            final String _tmpLanguage;
            _tmpLanguage = _cursor.getString(_cursorIndexOfLanguage);
            _result = new UserPreferences(_tmpId,_tmpUserId,_tmpPreferredTransport,_tmpEcoModeEnabled,_tmpNotificationsEnabled,_tmpLanguage);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<UserPreferences> getCurrentPreferences() {
    final String _sql = "SELECT * FROM user_preferences LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_preferences"}, new Callable<UserPreferences>() {
      @Override
      @Nullable
      public UserPreferences call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfPreferredTransport = CursorUtil.getColumnIndexOrThrow(_cursor, "preferredTransport");
          final int _cursorIndexOfEcoModeEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "ecoModeEnabled");
          final int _cursorIndexOfNotificationsEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationsEnabled");
          final int _cursorIndexOfLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "language");
          final UserPreferences _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final Integer _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            }
            final String _tmpPreferredTransport;
            _tmpPreferredTransport = _cursor.getString(_cursorIndexOfPreferredTransport);
            final boolean _tmpEcoModeEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEcoModeEnabled);
            _tmpEcoModeEnabled = _tmp != 0;
            final boolean _tmpNotificationsEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfNotificationsEnabled);
            _tmpNotificationsEnabled = _tmp_1 != 0;
            final String _tmpLanguage;
            _tmpLanguage = _cursor.getString(_cursorIndexOfLanguage);
            _result = new UserPreferences(_tmpId,_tmpUserId,_tmpPreferredTransport,_tmpEcoModeEnabled,_tmpNotificationsEnabled,_tmpLanguage);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
