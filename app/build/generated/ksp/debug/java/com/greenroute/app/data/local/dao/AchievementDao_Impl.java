package com.greenroute.app.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.greenroute.app.data.local.entities.Achievement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AchievementDao_Impl implements AchievementDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Achievement> __insertionAdapterOfAchievement;

  private final EntityDeletionOrUpdateAdapter<Achievement> __deletionAdapterOfAchievement;

  private final EntityDeletionOrUpdateAdapter<Achievement> __updateAdapterOfAchievement;

  public AchievementDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAchievement = new EntityInsertionAdapter<Achievement>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `achievements` (`id`,`userId`,`title`,`description`,`earned`,`dateEarned`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Achievement entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getUserId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getUserId());
        }
        statement.bindString(3, entity.getTitle());
        statement.bindString(4, entity.getDescription());
        final int _tmp = entity.getEarned() ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.getDateEarned() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getDateEarned());
        }
      }
    };
    this.__deletionAdapterOfAchievement = new EntityDeletionOrUpdateAdapter<Achievement>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `achievements` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Achievement entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfAchievement = new EntityDeletionOrUpdateAdapter<Achievement>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `achievements` SET `id` = ?,`userId` = ?,`title` = ?,`description` = ?,`earned` = ?,`dateEarned` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Achievement entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getUserId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getUserId());
        }
        statement.bindString(3, entity.getTitle());
        statement.bindString(4, entity.getDescription());
        final int _tmp = entity.getEarned() ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.getDateEarned() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getDateEarned());
        }
        statement.bindLong(7, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final Achievement achievement,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAchievement.insertAndReturnId(achievement);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Achievement achievement,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfAchievement.handle(achievement);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Achievement achievement,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfAchievement.handle(achievement);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Achievement>> getAllAchievements() {
    final String _sql = "SELECT * FROM achievements";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"achievements"}, new Callable<List<Achievement>>() {
      @Override
      @NonNull
      public List<Achievement> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfEarned = CursorUtil.getColumnIndexOrThrow(_cursor, "earned");
          final int _cursorIndexOfDateEarned = CursorUtil.getColumnIndexOrThrow(_cursor, "dateEarned");
          final List<Achievement> _result = new ArrayList<Achievement>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Achievement _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final Integer _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            }
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final boolean _tmpEarned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEarned);
            _tmpEarned = _tmp != 0;
            final Long _tmpDateEarned;
            if (_cursor.isNull(_cursorIndexOfDateEarned)) {
              _tmpDateEarned = null;
            } else {
              _tmpDateEarned = _cursor.getLong(_cursorIndexOfDateEarned);
            }
            _item = new Achievement(_tmpId,_tmpUserId,_tmpTitle,_tmpDescription,_tmpEarned,_tmpDateEarned);
            _result.add(_item);
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
  public Flow<List<Achievement>> getAchievementsByUserId(final int userId) {
    final String _sql = "SELECT * FROM achievements WHERE userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"achievements"}, new Callable<List<Achievement>>() {
      @Override
      @NonNull
      public List<Achievement> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfEarned = CursorUtil.getColumnIndexOrThrow(_cursor, "earned");
          final int _cursorIndexOfDateEarned = CursorUtil.getColumnIndexOrThrow(_cursor, "dateEarned");
          final List<Achievement> _result = new ArrayList<Achievement>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Achievement _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final Integer _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            }
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final boolean _tmpEarned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEarned);
            _tmpEarned = _tmp != 0;
            final Long _tmpDateEarned;
            if (_cursor.isNull(_cursorIndexOfDateEarned)) {
              _tmpDateEarned = null;
            } else {
              _tmpDateEarned = _cursor.getLong(_cursorIndexOfDateEarned);
            }
            _item = new Achievement(_tmpId,_tmpUserId,_tmpTitle,_tmpDescription,_tmpEarned,_tmpDateEarned);
            _result.add(_item);
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
  public Flow<List<Achievement>> getEarnedAchievements() {
    final String _sql = "SELECT * FROM achievements WHERE earned = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"achievements"}, new Callable<List<Achievement>>() {
      @Override
      @NonNull
      public List<Achievement> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfEarned = CursorUtil.getColumnIndexOrThrow(_cursor, "earned");
          final int _cursorIndexOfDateEarned = CursorUtil.getColumnIndexOrThrow(_cursor, "dateEarned");
          final List<Achievement> _result = new ArrayList<Achievement>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Achievement _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final Integer _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            }
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final boolean _tmpEarned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEarned);
            _tmpEarned = _tmp != 0;
            final Long _tmpDateEarned;
            if (_cursor.isNull(_cursorIndexOfDateEarned)) {
              _tmpDateEarned = null;
            } else {
              _tmpDateEarned = _cursor.getLong(_cursorIndexOfDateEarned);
            }
            _item = new Achievement(_tmpId,_tmpUserId,_tmpTitle,_tmpDescription,_tmpEarned,_tmpDateEarned);
            _result.add(_item);
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
