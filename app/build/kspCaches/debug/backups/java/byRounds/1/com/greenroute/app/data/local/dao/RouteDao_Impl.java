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
import com.greenroute.app.data.local.entities.Route;
import java.lang.Class;
import java.lang.Double;
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
public final class RouteDao_Impl implements RouteDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Route> __insertionAdapterOfRoute;

  private final EntityDeletionOrUpdateAdapter<Route> __deletionAdapterOfRoute;

  private final EntityDeletionOrUpdateAdapter<Route> __updateAdapterOfRoute;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSavedStatus;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public RouteDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRoute = new EntityInsertionAdapter<Route>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `routes` (`id`,`userId`,`startLocation`,`endLocation`,`transportType`,`co2Emission`,`dateTime`,`distance`,`duration`,`isSaved`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Route entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getUserId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getUserId());
        }
        if (entity.getStartLocation() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getStartLocation());
        }
        if (entity.getEndLocation() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getEndLocation());
        }
        if (entity.getTransportType() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getTransportType());
        }
        if (entity.getCo2Emission() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getCo2Emission());
        }
        if (entity.getDateTime() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getDateTime());
        }
        statement.bindDouble(8, entity.getDistance());
        statement.bindLong(9, entity.getDuration());
        final int _tmp = entity.isSaved() ? 1 : 0;
        statement.bindLong(10, _tmp);
      }
    };
    this.__deletionAdapterOfRoute = new EntityDeletionOrUpdateAdapter<Route>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `routes` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Route entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfRoute = new EntityDeletionOrUpdateAdapter<Route>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `routes` SET `id` = ?,`userId` = ?,`startLocation` = ?,`endLocation` = ?,`transportType` = ?,`co2Emission` = ?,`dateTime` = ?,`distance` = ?,`duration` = ?,`isSaved` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Route entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getUserId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getUserId());
        }
        if (entity.getStartLocation() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getStartLocation());
        }
        if (entity.getEndLocation() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getEndLocation());
        }
        if (entity.getTransportType() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getTransportType());
        }
        if (entity.getCo2Emission() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getCo2Emission());
        }
        if (entity.getDateTime() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getDateTime());
        }
        statement.bindDouble(8, entity.getDistance());
        statement.bindLong(9, entity.getDuration());
        final int _tmp = entity.isSaved() ? 1 : 0;
        statement.bindLong(10, _tmp);
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateSavedStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE routes SET isSaved = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM routes";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final Route route, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfRoute.insertAndReturnId(route);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Route route, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfRoute.handle(route);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Route route, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRoute.handle(route);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSavedStatus(final int routeId, final boolean isSaved,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSavedStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isSaved ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, routeId);
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
          __preparedStmtOfUpdateSavedStatus.release(_stmt);
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
  public Flow<List<Route>> getAllRoutes() {
    final String _sql = "SELECT * FROM routes ORDER BY dateTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"routes"}, new Callable<List<Route>>() {
      @Override
      @NonNull
      public List<Route> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfStartLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "startLocation");
          final int _cursorIndexOfEndLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "endLocation");
          final int _cursorIndexOfTransportType = CursorUtil.getColumnIndexOrThrow(_cursor, "transportType");
          final int _cursorIndexOfCo2Emission = CursorUtil.getColumnIndexOrThrow(_cursor, "co2Emission");
          final int _cursorIndexOfDateTime = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTime");
          final int _cursorIndexOfDistance = CursorUtil.getColumnIndexOrThrow(_cursor, "distance");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfIsSaved = CursorUtil.getColumnIndexOrThrow(_cursor, "isSaved");
          final List<Route> _result = new ArrayList<Route>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Route _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final Integer _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            }
            final String _tmpStartLocation;
            if (_cursor.isNull(_cursorIndexOfStartLocation)) {
              _tmpStartLocation = null;
            } else {
              _tmpStartLocation = _cursor.getString(_cursorIndexOfStartLocation);
            }
            final String _tmpEndLocation;
            if (_cursor.isNull(_cursorIndexOfEndLocation)) {
              _tmpEndLocation = null;
            } else {
              _tmpEndLocation = _cursor.getString(_cursorIndexOfEndLocation);
            }
            final String _tmpTransportType;
            if (_cursor.isNull(_cursorIndexOfTransportType)) {
              _tmpTransportType = null;
            } else {
              _tmpTransportType = _cursor.getString(_cursorIndexOfTransportType);
            }
            final Double _tmpCo2Emission;
            if (_cursor.isNull(_cursorIndexOfCo2Emission)) {
              _tmpCo2Emission = null;
            } else {
              _tmpCo2Emission = _cursor.getDouble(_cursorIndexOfCo2Emission);
            }
            final Long _tmpDateTime;
            if (_cursor.isNull(_cursorIndexOfDateTime)) {
              _tmpDateTime = null;
            } else {
              _tmpDateTime = _cursor.getLong(_cursorIndexOfDateTime);
            }
            final double _tmpDistance;
            _tmpDistance = _cursor.getDouble(_cursorIndexOfDistance);
            final int _tmpDuration;
            _tmpDuration = _cursor.getInt(_cursorIndexOfDuration);
            final boolean _tmpIsSaved;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSaved);
            _tmpIsSaved = _tmp != 0;
            _item = new Route(_tmpId,_tmpUserId,_tmpStartLocation,_tmpEndLocation,_tmpTransportType,_tmpCo2Emission,_tmpDateTime,_tmpDistance,_tmpDuration,_tmpIsSaved);
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
  public Flow<List<Route>> getRoutesByUserId(final int userId) {
    final String _sql = "SELECT * FROM routes WHERE userId = ? ORDER BY dateTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"routes"}, new Callable<List<Route>>() {
      @Override
      @NonNull
      public List<Route> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfStartLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "startLocation");
          final int _cursorIndexOfEndLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "endLocation");
          final int _cursorIndexOfTransportType = CursorUtil.getColumnIndexOrThrow(_cursor, "transportType");
          final int _cursorIndexOfCo2Emission = CursorUtil.getColumnIndexOrThrow(_cursor, "co2Emission");
          final int _cursorIndexOfDateTime = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTime");
          final int _cursorIndexOfDistance = CursorUtil.getColumnIndexOrThrow(_cursor, "distance");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfIsSaved = CursorUtil.getColumnIndexOrThrow(_cursor, "isSaved");
          final List<Route> _result = new ArrayList<Route>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Route _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final Integer _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            }
            final String _tmpStartLocation;
            if (_cursor.isNull(_cursorIndexOfStartLocation)) {
              _tmpStartLocation = null;
            } else {
              _tmpStartLocation = _cursor.getString(_cursorIndexOfStartLocation);
            }
            final String _tmpEndLocation;
            if (_cursor.isNull(_cursorIndexOfEndLocation)) {
              _tmpEndLocation = null;
            } else {
              _tmpEndLocation = _cursor.getString(_cursorIndexOfEndLocation);
            }
            final String _tmpTransportType;
            if (_cursor.isNull(_cursorIndexOfTransportType)) {
              _tmpTransportType = null;
            } else {
              _tmpTransportType = _cursor.getString(_cursorIndexOfTransportType);
            }
            final Double _tmpCo2Emission;
            if (_cursor.isNull(_cursorIndexOfCo2Emission)) {
              _tmpCo2Emission = null;
            } else {
              _tmpCo2Emission = _cursor.getDouble(_cursorIndexOfCo2Emission);
            }
            final Long _tmpDateTime;
            if (_cursor.isNull(_cursorIndexOfDateTime)) {
              _tmpDateTime = null;
            } else {
              _tmpDateTime = _cursor.getLong(_cursorIndexOfDateTime);
            }
            final double _tmpDistance;
            _tmpDistance = _cursor.getDouble(_cursorIndexOfDistance);
            final int _tmpDuration;
            _tmpDuration = _cursor.getInt(_cursorIndexOfDuration);
            final boolean _tmpIsSaved;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSaved);
            _tmpIsSaved = _tmp != 0;
            _item = new Route(_tmpId,_tmpUserId,_tmpStartLocation,_tmpEndLocation,_tmpTransportType,_tmpCo2Emission,_tmpDateTime,_tmpDistance,_tmpDuration,_tmpIsSaved);
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
  public Flow<List<Route>> getSavedRoutes() {
    final String _sql = "SELECT * FROM routes WHERE isSaved = 1 ORDER BY dateTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"routes"}, new Callable<List<Route>>() {
      @Override
      @NonNull
      public List<Route> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfStartLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "startLocation");
          final int _cursorIndexOfEndLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "endLocation");
          final int _cursorIndexOfTransportType = CursorUtil.getColumnIndexOrThrow(_cursor, "transportType");
          final int _cursorIndexOfCo2Emission = CursorUtil.getColumnIndexOrThrow(_cursor, "co2Emission");
          final int _cursorIndexOfDateTime = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTime");
          final int _cursorIndexOfDistance = CursorUtil.getColumnIndexOrThrow(_cursor, "distance");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfIsSaved = CursorUtil.getColumnIndexOrThrow(_cursor, "isSaved");
          final List<Route> _result = new ArrayList<Route>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Route _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final Integer _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            }
            final String _tmpStartLocation;
            if (_cursor.isNull(_cursorIndexOfStartLocation)) {
              _tmpStartLocation = null;
            } else {
              _tmpStartLocation = _cursor.getString(_cursorIndexOfStartLocation);
            }
            final String _tmpEndLocation;
            if (_cursor.isNull(_cursorIndexOfEndLocation)) {
              _tmpEndLocation = null;
            } else {
              _tmpEndLocation = _cursor.getString(_cursorIndexOfEndLocation);
            }
            final String _tmpTransportType;
            if (_cursor.isNull(_cursorIndexOfTransportType)) {
              _tmpTransportType = null;
            } else {
              _tmpTransportType = _cursor.getString(_cursorIndexOfTransportType);
            }
            final Double _tmpCo2Emission;
            if (_cursor.isNull(_cursorIndexOfCo2Emission)) {
              _tmpCo2Emission = null;
            } else {
              _tmpCo2Emission = _cursor.getDouble(_cursorIndexOfCo2Emission);
            }
            final Long _tmpDateTime;
            if (_cursor.isNull(_cursorIndexOfDateTime)) {
              _tmpDateTime = null;
            } else {
              _tmpDateTime = _cursor.getLong(_cursorIndexOfDateTime);
            }
            final double _tmpDistance;
            _tmpDistance = _cursor.getDouble(_cursorIndexOfDistance);
            final int _tmpDuration;
            _tmpDuration = _cursor.getInt(_cursorIndexOfDuration);
            final boolean _tmpIsSaved;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSaved);
            _tmpIsSaved = _tmp != 0;
            _item = new Route(_tmpId,_tmpUserId,_tmpStartLocation,_tmpEndLocation,_tmpTransportType,_tmpCo2Emission,_tmpDateTime,_tmpDistance,_tmpDuration,_tmpIsSaved);
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
  public Flow<List<Route>> getRecentRoutes(final int limit) {
    final String _sql = "SELECT * FROM routes ORDER BY dateTime DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"routes"}, new Callable<List<Route>>() {
      @Override
      @NonNull
      public List<Route> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfStartLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "startLocation");
          final int _cursorIndexOfEndLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "endLocation");
          final int _cursorIndexOfTransportType = CursorUtil.getColumnIndexOrThrow(_cursor, "transportType");
          final int _cursorIndexOfCo2Emission = CursorUtil.getColumnIndexOrThrow(_cursor, "co2Emission");
          final int _cursorIndexOfDateTime = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTime");
          final int _cursorIndexOfDistance = CursorUtil.getColumnIndexOrThrow(_cursor, "distance");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfIsSaved = CursorUtil.getColumnIndexOrThrow(_cursor, "isSaved");
          final List<Route> _result = new ArrayList<Route>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Route _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final Integer _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            }
            final String _tmpStartLocation;
            if (_cursor.isNull(_cursorIndexOfStartLocation)) {
              _tmpStartLocation = null;
            } else {
              _tmpStartLocation = _cursor.getString(_cursorIndexOfStartLocation);
            }
            final String _tmpEndLocation;
            if (_cursor.isNull(_cursorIndexOfEndLocation)) {
              _tmpEndLocation = null;
            } else {
              _tmpEndLocation = _cursor.getString(_cursorIndexOfEndLocation);
            }
            final String _tmpTransportType;
            if (_cursor.isNull(_cursorIndexOfTransportType)) {
              _tmpTransportType = null;
            } else {
              _tmpTransportType = _cursor.getString(_cursorIndexOfTransportType);
            }
            final Double _tmpCo2Emission;
            if (_cursor.isNull(_cursorIndexOfCo2Emission)) {
              _tmpCo2Emission = null;
            } else {
              _tmpCo2Emission = _cursor.getDouble(_cursorIndexOfCo2Emission);
            }
            final Long _tmpDateTime;
            if (_cursor.isNull(_cursorIndexOfDateTime)) {
              _tmpDateTime = null;
            } else {
              _tmpDateTime = _cursor.getLong(_cursorIndexOfDateTime);
            }
            final double _tmpDistance;
            _tmpDistance = _cursor.getDouble(_cursorIndexOfDistance);
            final int _tmpDuration;
            _tmpDuration = _cursor.getInt(_cursorIndexOfDuration);
            final boolean _tmpIsSaved;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSaved);
            _tmpIsSaved = _tmp != 0;
            _item = new Route(_tmpId,_tmpUserId,_tmpStartLocation,_tmpEndLocation,_tmpTransportType,_tmpCo2Emission,_tmpDateTime,_tmpDistance,_tmpDuration,_tmpIsSaved);
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
  public Object getRouteById(final int id, final Continuation<? super Route> $completion) {
    final String _sql = "SELECT * FROM routes WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Route>() {
      @Override
      @Nullable
      public Route call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfStartLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "startLocation");
          final int _cursorIndexOfEndLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "endLocation");
          final int _cursorIndexOfTransportType = CursorUtil.getColumnIndexOrThrow(_cursor, "transportType");
          final int _cursorIndexOfCo2Emission = CursorUtil.getColumnIndexOrThrow(_cursor, "co2Emission");
          final int _cursorIndexOfDateTime = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTime");
          final int _cursorIndexOfDistance = CursorUtil.getColumnIndexOrThrow(_cursor, "distance");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfIsSaved = CursorUtil.getColumnIndexOrThrow(_cursor, "isSaved");
          final Route _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final Integer _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            }
            final String _tmpStartLocation;
            if (_cursor.isNull(_cursorIndexOfStartLocation)) {
              _tmpStartLocation = null;
            } else {
              _tmpStartLocation = _cursor.getString(_cursorIndexOfStartLocation);
            }
            final String _tmpEndLocation;
            if (_cursor.isNull(_cursorIndexOfEndLocation)) {
              _tmpEndLocation = null;
            } else {
              _tmpEndLocation = _cursor.getString(_cursorIndexOfEndLocation);
            }
            final String _tmpTransportType;
            if (_cursor.isNull(_cursorIndexOfTransportType)) {
              _tmpTransportType = null;
            } else {
              _tmpTransportType = _cursor.getString(_cursorIndexOfTransportType);
            }
            final Double _tmpCo2Emission;
            if (_cursor.isNull(_cursorIndexOfCo2Emission)) {
              _tmpCo2Emission = null;
            } else {
              _tmpCo2Emission = _cursor.getDouble(_cursorIndexOfCo2Emission);
            }
            final Long _tmpDateTime;
            if (_cursor.isNull(_cursorIndexOfDateTime)) {
              _tmpDateTime = null;
            } else {
              _tmpDateTime = _cursor.getLong(_cursorIndexOfDateTime);
            }
            final double _tmpDistance;
            _tmpDistance = _cursor.getDouble(_cursorIndexOfDistance);
            final int _tmpDuration;
            _tmpDuration = _cursor.getInt(_cursorIndexOfDuration);
            final boolean _tmpIsSaved;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSaved);
            _tmpIsSaved = _tmp != 0;
            _result = new Route(_tmpId,_tmpUserId,_tmpStartLocation,_tmpEndLocation,_tmpTransportType,_tmpCo2Emission,_tmpDateTime,_tmpDistance,_tmpDuration,_tmpIsSaved);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
