package com.greenroute.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.greenroute.app.data.local.entities.User;
import java.lang.Class;
import java.lang.Exception;
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
public final class UserDao_Impl implements UserDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<User> __insertionAdapterOfUser;

  private final EntityDeletionOrUpdateAdapter<User> __deletionAdapterOfUser;

  private final EntityDeletionOrUpdateAdapter<User> __updateAdapterOfUser;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public UserDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUser = new EntityInsertionAdapter<User>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user` (`id`,`name`,`email`,`profileImageUri`,`totalRoutes`,`totalEmissionSaved`,`joinedAt`,`lastActive`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final User entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        statement.bindString(3, entity.getEmail());
        if (entity.getProfileImageUri() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getProfileImageUri());
        }
        statement.bindLong(5, entity.getTotalRoutes());
        statement.bindDouble(6, entity.getTotalEmissionSaved());
        if (entity.getJoinedAt() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getJoinedAt());
        }
        if (entity.getLastActive() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getLastActive());
        }
      }
    };
    this.__deletionAdapterOfUser = new EntityDeletionOrUpdateAdapter<User>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `user` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final User entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfUser = new EntityDeletionOrUpdateAdapter<User>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `user` SET `id` = ?,`name` = ?,`email` = ?,`profileImageUri` = ?,`totalRoutes` = ?,`totalEmissionSaved` = ?,`joinedAt` = ?,`lastActive` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final User entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        statement.bindString(3, entity.getEmail());
        if (entity.getProfileImageUri() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getProfileImageUri());
        }
        statement.bindLong(5, entity.getTotalRoutes());
        statement.bindDouble(6, entity.getTotalEmissionSaved());
        if (entity.getJoinedAt() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getJoinedAt());
        }
        if (entity.getLastActive() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getLastActive());
        }
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM user";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final User user, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfUser.insertAndReturnId(user);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final User user, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfUser.handle(user);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final User user, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfUser.handle(user);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<User>> getAllUsers() {
    final String _sql = "SELECT * FROM user";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user"}, new Callable<List<User>>() {
      @Override
      @NonNull
      public List<User> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfProfileImageUri = CursorUtil.getColumnIndexOrThrow(_cursor, "profileImageUri");
          final int _cursorIndexOfTotalRoutes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalRoutes");
          final int _cursorIndexOfTotalEmissionSaved = CursorUtil.getColumnIndexOrThrow(_cursor, "totalEmissionSaved");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfLastActive = CursorUtil.getColumnIndexOrThrow(_cursor, "lastActive");
          final List<User> _result = new ArrayList<User>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final User _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpProfileImageUri;
            if (_cursor.isNull(_cursorIndexOfProfileImageUri)) {
              _tmpProfileImageUri = null;
            } else {
              _tmpProfileImageUri = _cursor.getString(_cursorIndexOfProfileImageUri);
            }
            final int _tmpTotalRoutes;
            _tmpTotalRoutes = _cursor.getInt(_cursorIndexOfTotalRoutes);
            final double _tmpTotalEmissionSaved;
            _tmpTotalEmissionSaved = _cursor.getDouble(_cursorIndexOfTotalEmissionSaved);
            final Long _tmpJoinedAt;
            if (_cursor.isNull(_cursorIndexOfJoinedAt)) {
              _tmpJoinedAt = null;
            } else {
              _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt);
            }
            final Long _tmpLastActive;
            if (_cursor.isNull(_cursorIndexOfLastActive)) {
              _tmpLastActive = null;
            } else {
              _tmpLastActive = _cursor.getLong(_cursorIndexOfLastActive);
            }
            _item = new User(_tmpId,_tmpName,_tmpEmail,_tmpProfileImageUri,_tmpTotalRoutes,_tmpTotalEmissionSaved,_tmpJoinedAt,_tmpLastActive);
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
  public Object getUserById(final int id, final Continuation<? super User> $completion) {
    final String _sql = "SELECT * FROM user WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<User>() {
      @Override
      @Nullable
      public User call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfProfileImageUri = CursorUtil.getColumnIndexOrThrow(_cursor, "profileImageUri");
          final int _cursorIndexOfTotalRoutes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalRoutes");
          final int _cursorIndexOfTotalEmissionSaved = CursorUtil.getColumnIndexOrThrow(_cursor, "totalEmissionSaved");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfLastActive = CursorUtil.getColumnIndexOrThrow(_cursor, "lastActive");
          final User _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpProfileImageUri;
            if (_cursor.isNull(_cursorIndexOfProfileImageUri)) {
              _tmpProfileImageUri = null;
            } else {
              _tmpProfileImageUri = _cursor.getString(_cursorIndexOfProfileImageUri);
            }
            final int _tmpTotalRoutes;
            _tmpTotalRoutes = _cursor.getInt(_cursorIndexOfTotalRoutes);
            final double _tmpTotalEmissionSaved;
            _tmpTotalEmissionSaved = _cursor.getDouble(_cursorIndexOfTotalEmissionSaved);
            final Long _tmpJoinedAt;
            if (_cursor.isNull(_cursorIndexOfJoinedAt)) {
              _tmpJoinedAt = null;
            } else {
              _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt);
            }
            final Long _tmpLastActive;
            if (_cursor.isNull(_cursorIndexOfLastActive)) {
              _tmpLastActive = null;
            } else {
              _tmpLastActive = _cursor.getLong(_cursorIndexOfLastActive);
            }
            _result = new User(_tmpId,_tmpName,_tmpEmail,_tmpProfileImageUri,_tmpTotalRoutes,_tmpTotalEmissionSaved,_tmpJoinedAt,_tmpLastActive);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<User> getCurrentUser() {
    final String _sql = "SELECT * FROM user LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user"}, new Callable<User>() {
      @Override
      @Nullable
      public User call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfProfileImageUri = CursorUtil.getColumnIndexOrThrow(_cursor, "profileImageUri");
          final int _cursorIndexOfTotalRoutes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalRoutes");
          final int _cursorIndexOfTotalEmissionSaved = CursorUtil.getColumnIndexOrThrow(_cursor, "totalEmissionSaved");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfLastActive = CursorUtil.getColumnIndexOrThrow(_cursor, "lastActive");
          final User _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpProfileImageUri;
            if (_cursor.isNull(_cursorIndexOfProfileImageUri)) {
              _tmpProfileImageUri = null;
            } else {
              _tmpProfileImageUri = _cursor.getString(_cursorIndexOfProfileImageUri);
            }
            final int _tmpTotalRoutes;
            _tmpTotalRoutes = _cursor.getInt(_cursorIndexOfTotalRoutes);
            final double _tmpTotalEmissionSaved;
            _tmpTotalEmissionSaved = _cursor.getDouble(_cursorIndexOfTotalEmissionSaved);
            final Long _tmpJoinedAt;
            if (_cursor.isNull(_cursorIndexOfJoinedAt)) {
              _tmpJoinedAt = null;
            } else {
              _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt);
            }
            final Long _tmpLastActive;
            if (_cursor.isNull(_cursorIndexOfLastActive)) {
              _tmpLastActive = null;
            } else {
              _tmpLastActive = _cursor.getLong(_cursorIndexOfLastActive);
            }
            _result = new User(_tmpId,_tmpName,_tmpEmail,_tmpProfileImageUri,_tmpTotalRoutes,_tmpTotalEmissionSaved,_tmpJoinedAt,_tmpLastActive);
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
