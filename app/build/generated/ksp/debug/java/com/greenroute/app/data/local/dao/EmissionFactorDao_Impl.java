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
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.greenroute.app.data.local.entities.EmissionFactor;
import java.lang.Class;
import java.lang.Double;
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
public final class EmissionFactorDao_Impl implements EmissionFactorDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EmissionFactor> __insertionAdapterOfEmissionFactor;

  private final EntityDeletionOrUpdateAdapter<EmissionFactor> __deletionAdapterOfEmissionFactor;

  private final EntityDeletionOrUpdateAdapter<EmissionFactor> __updateAdapterOfEmissionFactor;

  public EmissionFactorDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEmissionFactor = new EntityInsertionAdapter<EmissionFactor>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `emission_factors` (`transportType`,`factor`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EmissionFactor entity) {
        statement.bindString(1, entity.getTransportType());
        if (entity.getFactor() == null) {
          statement.bindNull(2);
        } else {
          statement.bindDouble(2, entity.getFactor());
        }
      }
    };
    this.__deletionAdapterOfEmissionFactor = new EntityDeletionOrUpdateAdapter<EmissionFactor>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `emission_factors` WHERE `transportType` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EmissionFactor entity) {
        statement.bindString(1, entity.getTransportType());
      }
    };
    this.__updateAdapterOfEmissionFactor = new EntityDeletionOrUpdateAdapter<EmissionFactor>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `emission_factors` SET `transportType` = ?,`factor` = ? WHERE `transportType` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EmissionFactor entity) {
        statement.bindString(1, entity.getTransportType());
        if (entity.getFactor() == null) {
          statement.bindNull(2);
        } else {
          statement.bindDouble(2, entity.getFactor());
        }
        statement.bindString(3, entity.getTransportType());
      }
    };
  }

  @Override
  public Object insert(final EmissionFactor factor, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfEmissionFactor.insertAndReturnId(factor);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<EmissionFactor> factors,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfEmissionFactor.insert(factors);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final EmissionFactor factor, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfEmissionFactor.handle(factor);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final EmissionFactor factor, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfEmissionFactor.handle(factor);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<EmissionFactor>> getAllFactors() {
    final String _sql = "SELECT * FROM emission_factors";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"emission_factors"}, new Callable<List<EmissionFactor>>() {
      @Override
      @NonNull
      public List<EmissionFactor> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTransportType = CursorUtil.getColumnIndexOrThrow(_cursor, "transportType");
          final int _cursorIndexOfFactor = CursorUtil.getColumnIndexOrThrow(_cursor, "factor");
          final List<EmissionFactor> _result = new ArrayList<EmissionFactor>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EmissionFactor _item;
            final String _tmpTransportType;
            _tmpTransportType = _cursor.getString(_cursorIndexOfTransportType);
            final Double _tmpFactor;
            if (_cursor.isNull(_cursorIndexOfFactor)) {
              _tmpFactor = null;
            } else {
              _tmpFactor = _cursor.getDouble(_cursorIndexOfFactor);
            }
            _item = new EmissionFactor(_tmpTransportType,_tmpFactor);
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
  public Object getFactorByType(final String transportType,
      final Continuation<? super EmissionFactor> $completion) {
    final String _sql = "SELECT * FROM emission_factors WHERE transportType = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, transportType);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<EmissionFactor>() {
      @Override
      @Nullable
      public EmissionFactor call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTransportType = CursorUtil.getColumnIndexOrThrow(_cursor, "transportType");
          final int _cursorIndexOfFactor = CursorUtil.getColumnIndexOrThrow(_cursor, "factor");
          final EmissionFactor _result;
          if (_cursor.moveToFirst()) {
            final String _tmpTransportType;
            _tmpTransportType = _cursor.getString(_cursorIndexOfTransportType);
            final Double _tmpFactor;
            if (_cursor.isNull(_cursorIndexOfFactor)) {
              _tmpFactor = null;
            } else {
              _tmpFactor = _cursor.getDouble(_cursorIndexOfFactor);
            }
            _result = new EmissionFactor(_tmpTransportType,_tmpFactor);
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
