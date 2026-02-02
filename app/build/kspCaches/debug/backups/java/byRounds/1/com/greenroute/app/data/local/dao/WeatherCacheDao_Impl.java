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
import com.greenroute.app.data.local.entities.WeatherCache;
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
public final class WeatherCacheDao_Impl implements WeatherCacheDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WeatherCache> __insertionAdapterOfWeatherCache;

  private final EntityDeletionOrUpdateAdapter<WeatherCache> __deletionAdapterOfWeatherCache;

  private final EntityDeletionOrUpdateAdapter<WeatherCache> __updateAdapterOfWeatherCache;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldCache;

  public WeatherCacheDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWeatherCache = new EntityInsertionAdapter<WeatherCache>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `weather_cache` (`id`,`city`,`temperature`,`conditions`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WeatherCache entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getCity() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getCity());
        }
        statement.bindDouble(3, entity.getTemperature());
        statement.bindString(4, entity.getConditions());
        if (entity.getTimestamp() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getTimestamp());
        }
      }
    };
    this.__deletionAdapterOfWeatherCache = new EntityDeletionOrUpdateAdapter<WeatherCache>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `weather_cache` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WeatherCache entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfWeatherCache = new EntityDeletionOrUpdateAdapter<WeatherCache>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `weather_cache` SET `id` = ?,`city` = ?,`temperature` = ?,`conditions` = ?,`timestamp` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WeatherCache entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getCity() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getCity());
        }
        statement.bindDouble(3, entity.getTemperature());
        statement.bindString(4, entity.getConditions());
        if (entity.getTimestamp() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getTimestamp());
        }
        statement.bindLong(6, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteOldCache = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM weather_cache WHERE timestamp < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final WeatherCache weather, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfWeatherCache.insertAndReturnId(weather);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final WeatherCache weather, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfWeatherCache.handle(weather);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final WeatherCache weather, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfWeatherCache.handle(weather);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldCache(final long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldCache.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
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
          __preparedStmtOfDeleteOldCache.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getWeatherByCity(final String city,
      final Continuation<? super WeatherCache> $completion) {
    final String _sql = "SELECT * FROM weather_cache WHERE city = ? ORDER BY timestamp DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, city);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<WeatherCache>() {
      @Override
      @Nullable
      public WeatherCache call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final int _cursorIndexOfTemperature = CursorUtil.getColumnIndexOrThrow(_cursor, "temperature");
          final int _cursorIndexOfConditions = CursorUtil.getColumnIndexOrThrow(_cursor, "conditions");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final WeatherCache _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpCity;
            if (_cursor.isNull(_cursorIndexOfCity)) {
              _tmpCity = null;
            } else {
              _tmpCity = _cursor.getString(_cursorIndexOfCity);
            }
            final double _tmpTemperature;
            _tmpTemperature = _cursor.getDouble(_cursorIndexOfTemperature);
            final String _tmpConditions;
            _tmpConditions = _cursor.getString(_cursorIndexOfConditions);
            final Long _tmpTimestamp;
            if (_cursor.isNull(_cursorIndexOfTimestamp)) {
              _tmpTimestamp = null;
            } else {
              _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            }
            _result = new WeatherCache(_tmpId,_tmpCity,_tmpTemperature,_tmpConditions,_tmpTimestamp);
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
  public Flow<List<WeatherCache>> getAllCachedWeather() {
    final String _sql = "SELECT * FROM weather_cache ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"weather_cache"}, new Callable<List<WeatherCache>>() {
      @Override
      @NonNull
      public List<WeatherCache> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final int _cursorIndexOfTemperature = CursorUtil.getColumnIndexOrThrow(_cursor, "temperature");
          final int _cursorIndexOfConditions = CursorUtil.getColumnIndexOrThrow(_cursor, "conditions");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<WeatherCache> _result = new ArrayList<WeatherCache>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WeatherCache _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpCity;
            if (_cursor.isNull(_cursorIndexOfCity)) {
              _tmpCity = null;
            } else {
              _tmpCity = _cursor.getString(_cursorIndexOfCity);
            }
            final double _tmpTemperature;
            _tmpTemperature = _cursor.getDouble(_cursorIndexOfTemperature);
            final String _tmpConditions;
            _tmpConditions = _cursor.getString(_cursorIndexOfConditions);
            final Long _tmpTimestamp;
            if (_cursor.isNull(_cursorIndexOfTimestamp)) {
              _tmpTimestamp = null;
            } else {
              _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            }
            _item = new WeatherCache(_tmpId,_tmpCity,_tmpTemperature,_tmpConditions,_tmpTimestamp);
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
