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
import com.greenroute.app.data.local.entities.EcoStats;
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
public final class EcoStatsDao_Impl implements EcoStatsDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EcoStats> __insertionAdapterOfEcoStats;

  private final EntityDeletionOrUpdateAdapter<EcoStats> __deletionAdapterOfEcoStats;

  private final EntityDeletionOrUpdateAdapter<EcoStats> __updateAdapterOfEcoStats;

  public EcoStatsDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEcoStats = new EntityInsertionAdapter<EcoStats>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `eco_stats` (`id`,`userId`,`totalDistance`,`totalEmissionSaved`,`routesCount`,`lastUpdated`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EcoStats entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getUserId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getUserId());
        }
        statement.bindDouble(3, entity.getTotalDistance());
        statement.bindDouble(4, entity.getTotalEmissionSaved());
        statement.bindLong(5, entity.getRoutesCount());
        statement.bindLong(6, entity.getLastUpdated());
      }
    };
    this.__deletionAdapterOfEcoStats = new EntityDeletionOrUpdateAdapter<EcoStats>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `eco_stats` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EcoStats entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfEcoStats = new EntityDeletionOrUpdateAdapter<EcoStats>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `eco_stats` SET `id` = ?,`userId` = ?,`totalDistance` = ?,`totalEmissionSaved` = ?,`routesCount` = ?,`lastUpdated` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EcoStats entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getUserId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getUserId());
        }
        statement.bindDouble(3, entity.getTotalDistance());
        statement.bindDouble(4, entity.getTotalEmissionSaved());
        statement.bindLong(5, entity.getRoutesCount());
        statement.bindLong(6, entity.getLastUpdated());
        statement.bindLong(7, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final EcoStats stats, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfEcoStats.insertAndReturnId(stats);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final EcoStats stats, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfEcoStats.handle(stats);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final EcoStats stats, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfEcoStats.handle(stats);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<EcoStats> getStatsByUserId(final int userId) {
    final String _sql = "SELECT * FROM eco_stats WHERE userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"eco_stats"}, new Callable<EcoStats>() {
      @Override
      @Nullable
      public EcoStats call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfTotalDistance = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDistance");
          final int _cursorIndexOfTotalEmissionSaved = CursorUtil.getColumnIndexOrThrow(_cursor, "totalEmissionSaved");
          final int _cursorIndexOfRoutesCount = CursorUtil.getColumnIndexOrThrow(_cursor, "routesCount");
          final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
          final EcoStats _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final Integer _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            }
            final double _tmpTotalDistance;
            _tmpTotalDistance = _cursor.getDouble(_cursorIndexOfTotalDistance);
            final double _tmpTotalEmissionSaved;
            _tmpTotalEmissionSaved = _cursor.getDouble(_cursorIndexOfTotalEmissionSaved);
            final int _tmpRoutesCount;
            _tmpRoutesCount = _cursor.getInt(_cursorIndexOfRoutesCount);
            final long _tmpLastUpdated;
            _tmpLastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
            _result = new EcoStats(_tmpId,_tmpUserId,_tmpTotalDistance,_tmpTotalEmissionSaved,_tmpRoutesCount,_tmpLastUpdated);
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
  public Flow<EcoStats> getCurrentStats() {
    final String _sql = "SELECT * FROM eco_stats LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"eco_stats"}, new Callable<EcoStats>() {
      @Override
      @Nullable
      public EcoStats call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfTotalDistance = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDistance");
          final int _cursorIndexOfTotalEmissionSaved = CursorUtil.getColumnIndexOrThrow(_cursor, "totalEmissionSaved");
          final int _cursorIndexOfRoutesCount = CursorUtil.getColumnIndexOrThrow(_cursor, "routesCount");
          final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
          final EcoStats _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final Integer _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            }
            final double _tmpTotalDistance;
            _tmpTotalDistance = _cursor.getDouble(_cursorIndexOfTotalDistance);
            final double _tmpTotalEmissionSaved;
            _tmpTotalEmissionSaved = _cursor.getDouble(_cursorIndexOfTotalEmissionSaved);
            final int _tmpRoutesCount;
            _tmpRoutesCount = _cursor.getInt(_cursorIndexOfRoutesCount);
            final long _tmpLastUpdated;
            _tmpLastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
            _result = new EcoStats(_tmpId,_tmpUserId,_tmpTotalDistance,_tmpTotalEmissionSaved,_tmpRoutesCount,_tmpLastUpdated);
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
