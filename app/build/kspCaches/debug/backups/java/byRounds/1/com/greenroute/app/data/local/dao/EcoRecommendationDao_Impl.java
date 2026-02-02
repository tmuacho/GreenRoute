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
import com.greenroute.app.data.local.entities.EcoRecommendation;
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
public final class EcoRecommendationDao_Impl implements EcoRecommendationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EcoRecommendation> __insertionAdapterOfEcoRecommendation;

  private final EntityDeletionOrUpdateAdapter<EcoRecommendation> __deletionAdapterOfEcoRecommendation;

  private final EntityDeletionOrUpdateAdapter<EcoRecommendation> __updateAdapterOfEcoRecommendation;

  public EcoRecommendationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEcoRecommendation = new EntityInsertionAdapter<EcoRecommendation>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `eco_recommendations` (`id`,`routeId`,`message`,`score`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EcoRecommendation entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getRouteId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getRouteId());
        }
        if (entity.getMessage() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getMessage());
        }
        statement.bindDouble(4, entity.getScore());
      }
    };
    this.__deletionAdapterOfEcoRecommendation = new EntityDeletionOrUpdateAdapter<EcoRecommendation>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `eco_recommendations` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EcoRecommendation entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfEcoRecommendation = new EntityDeletionOrUpdateAdapter<EcoRecommendation>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `eco_recommendations` SET `id` = ?,`routeId` = ?,`message` = ?,`score` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EcoRecommendation entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getRouteId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getRouteId());
        }
        if (entity.getMessage() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getMessage());
        }
        statement.bindDouble(4, entity.getScore());
        statement.bindLong(5, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final EcoRecommendation recommendation,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfEcoRecommendation.insertAndReturnId(recommendation);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final EcoRecommendation recommendation,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfEcoRecommendation.handle(recommendation);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final EcoRecommendation recommendation,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfEcoRecommendation.handle(recommendation);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<EcoRecommendation>> getAllRecommendations() {
    final String _sql = "SELECT * FROM eco_recommendations";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"eco_recommendations"}, new Callable<List<EcoRecommendation>>() {
      @Override
      @NonNull
      public List<EcoRecommendation> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRouteId = CursorUtil.getColumnIndexOrThrow(_cursor, "routeId");
          final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final List<EcoRecommendation> _result = new ArrayList<EcoRecommendation>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EcoRecommendation _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final Integer _tmpRouteId;
            if (_cursor.isNull(_cursorIndexOfRouteId)) {
              _tmpRouteId = null;
            } else {
              _tmpRouteId = _cursor.getInt(_cursorIndexOfRouteId);
            }
            final String _tmpMessage;
            if (_cursor.isNull(_cursorIndexOfMessage)) {
              _tmpMessage = null;
            } else {
              _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
            }
            final double _tmpScore;
            _tmpScore = _cursor.getDouble(_cursorIndexOfScore);
            _item = new EcoRecommendation(_tmpId,_tmpRouteId,_tmpMessage,_tmpScore);
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
  public Flow<List<EcoRecommendation>> getRecommendationsByRouteId(final int routeId) {
    final String _sql = "SELECT * FROM eco_recommendations WHERE routeId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, routeId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"eco_recommendations"}, new Callable<List<EcoRecommendation>>() {
      @Override
      @NonNull
      public List<EcoRecommendation> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRouteId = CursorUtil.getColumnIndexOrThrow(_cursor, "routeId");
          final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final List<EcoRecommendation> _result = new ArrayList<EcoRecommendation>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EcoRecommendation _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final Integer _tmpRouteId;
            if (_cursor.isNull(_cursorIndexOfRouteId)) {
              _tmpRouteId = null;
            } else {
              _tmpRouteId = _cursor.getInt(_cursorIndexOfRouteId);
            }
            final String _tmpMessage;
            if (_cursor.isNull(_cursorIndexOfMessage)) {
              _tmpMessage = null;
            } else {
              _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
            }
            final double _tmpScore;
            _tmpScore = _cursor.getDouble(_cursorIndexOfScore);
            _item = new EcoRecommendation(_tmpId,_tmpRouteId,_tmpMessage,_tmpScore);
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
