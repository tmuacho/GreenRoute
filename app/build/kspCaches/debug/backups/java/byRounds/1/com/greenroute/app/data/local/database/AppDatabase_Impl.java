package com.greenroute.app.data.local.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.greenroute.app.data.local.dao.AchievementDao;
import com.greenroute.app.data.local.dao.AchievementDao_Impl;
import com.greenroute.app.data.local.dao.EcoRecommendationDao;
import com.greenroute.app.data.local.dao.EcoRecommendationDao_Impl;
import com.greenroute.app.data.local.dao.EcoStatsDao;
import com.greenroute.app.data.local.dao.EcoStatsDao_Impl;
import com.greenroute.app.data.local.dao.EmissionFactorDao;
import com.greenroute.app.data.local.dao.EmissionFactorDao_Impl;
import com.greenroute.app.data.local.dao.RouteDao;
import com.greenroute.app.data.local.dao.RouteDao_Impl;
import com.greenroute.app.data.local.dao.SavedPlaceDao;
import com.greenroute.app.data.local.dao.SavedPlaceDao_Impl;
import com.greenroute.app.data.local.dao.UserDao;
import com.greenroute.app.data.local.dao.UserDao_Impl;
import com.greenroute.app.data.local.dao.UserPreferencesDao;
import com.greenroute.app.data.local.dao.UserPreferencesDao_Impl;
import com.greenroute.app.data.local.dao.WeatherCacheDao;
import com.greenroute.app.data.local.dao.WeatherCacheDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile UserDao _userDao;

  private volatile UserPreferencesDao _userPreferencesDao;

  private volatile RouteDao _routeDao;

  private volatile SavedPlaceDao _savedPlaceDao;

  private volatile EcoStatsDao _ecoStatsDao;

  private volatile EcoRecommendationDao _ecoRecommendationDao;

  private volatile AchievementDao _achievementDao;

  private volatile EmissionFactorDao _emissionFactorDao;

  private volatile WeatherCacheDao _weatherCacheDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `user` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `email` TEXT NOT NULL, `profileImageUri` TEXT, `totalRoutes` INTEGER NOT NULL, `totalEmissionSaved` REAL NOT NULL, `joinedAt` INTEGER, `lastActive` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_preferences` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER, `preferredTransport` TEXT NOT NULL, `ecoModeEnabled` INTEGER NOT NULL, `notificationsEnabled` INTEGER NOT NULL, `language` TEXT NOT NULL, FOREIGN KEY(`userId`) REFERENCES `user`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_user_preferences_userId` ON `user_preferences` (`userId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `routes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER, `startLocation` TEXT, `endLocation` TEXT, `transportType` TEXT, `co2Emission` REAL, `dateTime` INTEGER, `distance` REAL NOT NULL, `duration` INTEGER NOT NULL, `isSaved` INTEGER NOT NULL, FOREIGN KEY(`userId`) REFERENCES `user`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_routes_userId` ON `routes` (`userId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `saved_places` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER, `name` TEXT NOT NULL, `latitude` REAL, `longitude` REAL, FOREIGN KEY(`userId`) REFERENCES `user`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_saved_places_userId` ON `saved_places` (`userId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `eco_stats` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER, `totalDistance` REAL NOT NULL, `totalEmissionSaved` REAL NOT NULL, `routesCount` INTEGER NOT NULL, `lastUpdated` INTEGER NOT NULL, FOREIGN KEY(`userId`) REFERENCES `user`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_eco_stats_userId` ON `eco_stats` (`userId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `eco_recommendations` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `routeId` INTEGER, `message` TEXT, `score` REAL NOT NULL, FOREIGN KEY(`routeId`) REFERENCES `routes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_eco_recommendations_routeId` ON `eco_recommendations` (`routeId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `achievements` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `earned` INTEGER NOT NULL, `dateEarned` INTEGER, FOREIGN KEY(`userId`) REFERENCES `user`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_achievements_userId` ON `achievements` (`userId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `emission_factors` (`transportType` TEXT NOT NULL, `factor` REAL, PRIMARY KEY(`transportType`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `weather_cache` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `city` TEXT, `temperature` REAL NOT NULL, `conditions` TEXT NOT NULL, `timestamp` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '1a282cbe4d261f8dd4472b494e93fd3f')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `user`");
        db.execSQL("DROP TABLE IF EXISTS `user_preferences`");
        db.execSQL("DROP TABLE IF EXISTS `routes`");
        db.execSQL("DROP TABLE IF EXISTS `saved_places`");
        db.execSQL("DROP TABLE IF EXISTS `eco_stats`");
        db.execSQL("DROP TABLE IF EXISTS `eco_recommendations`");
        db.execSQL("DROP TABLE IF EXISTS `achievements`");
        db.execSQL("DROP TABLE IF EXISTS `emission_factors`");
        db.execSQL("DROP TABLE IF EXISTS `weather_cache`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsUser = new HashMap<String, TableInfo.Column>(8);
        _columnsUser.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUser.put("name", new TableInfo.Column("name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUser.put("email", new TableInfo.Column("email", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUser.put("profileImageUri", new TableInfo.Column("profileImageUri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUser.put("totalRoutes", new TableInfo.Column("totalRoutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUser.put("totalEmissionSaved", new TableInfo.Column("totalEmissionSaved", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUser.put("joinedAt", new TableInfo.Column("joinedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUser.put("lastActive", new TableInfo.Column("lastActive", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUser = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUser = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUser = new TableInfo("user", _columnsUser, _foreignKeysUser, _indicesUser);
        final TableInfo _existingUser = TableInfo.read(db, "user");
        if (!_infoUser.equals(_existingUser)) {
          return new RoomOpenHelper.ValidationResult(false, "user(com.greenroute.app.data.local.entities.User).\n"
                  + " Expected:\n" + _infoUser + "\n"
                  + " Found:\n" + _existingUser);
        }
        final HashMap<String, TableInfo.Column> _columnsUserPreferences = new HashMap<String, TableInfo.Column>(6);
        _columnsUserPreferences.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserPreferences.put("userId", new TableInfo.Column("userId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserPreferences.put("preferredTransport", new TableInfo.Column("preferredTransport", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserPreferences.put("ecoModeEnabled", new TableInfo.Column("ecoModeEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserPreferences.put("notificationsEnabled", new TableInfo.Column("notificationsEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserPreferences.put("language", new TableInfo.Column("language", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserPreferences = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysUserPreferences.add(new TableInfo.ForeignKey("user", "CASCADE", "NO ACTION", Arrays.asList("userId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesUserPreferences = new HashSet<TableInfo.Index>(1);
        _indicesUserPreferences.add(new TableInfo.Index("index_user_preferences_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        final TableInfo _infoUserPreferences = new TableInfo("user_preferences", _columnsUserPreferences, _foreignKeysUserPreferences, _indicesUserPreferences);
        final TableInfo _existingUserPreferences = TableInfo.read(db, "user_preferences");
        if (!_infoUserPreferences.equals(_existingUserPreferences)) {
          return new RoomOpenHelper.ValidationResult(false, "user_preferences(com.greenroute.app.data.local.entities.UserPreferences).\n"
                  + " Expected:\n" + _infoUserPreferences + "\n"
                  + " Found:\n" + _existingUserPreferences);
        }
        final HashMap<String, TableInfo.Column> _columnsRoutes = new HashMap<String, TableInfo.Column>(10);
        _columnsRoutes.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("userId", new TableInfo.Column("userId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("startLocation", new TableInfo.Column("startLocation", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("endLocation", new TableInfo.Column("endLocation", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("transportType", new TableInfo.Column("transportType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("co2Emission", new TableInfo.Column("co2Emission", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("dateTime", new TableInfo.Column("dateTime", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("distance", new TableInfo.Column("distance", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("duration", new TableInfo.Column("duration", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("isSaved", new TableInfo.Column("isSaved", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRoutes = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysRoutes.add(new TableInfo.ForeignKey("user", "CASCADE", "NO ACTION", Arrays.asList("userId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesRoutes = new HashSet<TableInfo.Index>(1);
        _indicesRoutes.add(new TableInfo.Index("index_routes_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        final TableInfo _infoRoutes = new TableInfo("routes", _columnsRoutes, _foreignKeysRoutes, _indicesRoutes);
        final TableInfo _existingRoutes = TableInfo.read(db, "routes");
        if (!_infoRoutes.equals(_existingRoutes)) {
          return new RoomOpenHelper.ValidationResult(false, "routes(com.greenroute.app.data.local.entities.Route).\n"
                  + " Expected:\n" + _infoRoutes + "\n"
                  + " Found:\n" + _existingRoutes);
        }
        final HashMap<String, TableInfo.Column> _columnsSavedPlaces = new HashMap<String, TableInfo.Column>(5);
        _columnsSavedPlaces.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedPlaces.put("userId", new TableInfo.Column("userId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedPlaces.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedPlaces.put("latitude", new TableInfo.Column("latitude", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedPlaces.put("longitude", new TableInfo.Column("longitude", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSavedPlaces = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysSavedPlaces.add(new TableInfo.ForeignKey("user", "CASCADE", "NO ACTION", Arrays.asList("userId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSavedPlaces = new HashSet<TableInfo.Index>(1);
        _indicesSavedPlaces.add(new TableInfo.Index("index_saved_places_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        final TableInfo _infoSavedPlaces = new TableInfo("saved_places", _columnsSavedPlaces, _foreignKeysSavedPlaces, _indicesSavedPlaces);
        final TableInfo _existingSavedPlaces = TableInfo.read(db, "saved_places");
        if (!_infoSavedPlaces.equals(_existingSavedPlaces)) {
          return new RoomOpenHelper.ValidationResult(false, "saved_places(com.greenroute.app.data.local.entities.SavedPlace).\n"
                  + " Expected:\n" + _infoSavedPlaces + "\n"
                  + " Found:\n" + _existingSavedPlaces);
        }
        final HashMap<String, TableInfo.Column> _columnsEcoStats = new HashMap<String, TableInfo.Column>(6);
        _columnsEcoStats.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEcoStats.put("userId", new TableInfo.Column("userId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEcoStats.put("totalDistance", new TableInfo.Column("totalDistance", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEcoStats.put("totalEmissionSaved", new TableInfo.Column("totalEmissionSaved", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEcoStats.put("routesCount", new TableInfo.Column("routesCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEcoStats.put("lastUpdated", new TableInfo.Column("lastUpdated", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEcoStats = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysEcoStats.add(new TableInfo.ForeignKey("user", "CASCADE", "NO ACTION", Arrays.asList("userId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesEcoStats = new HashSet<TableInfo.Index>(1);
        _indicesEcoStats.add(new TableInfo.Index("index_eco_stats_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        final TableInfo _infoEcoStats = new TableInfo("eco_stats", _columnsEcoStats, _foreignKeysEcoStats, _indicesEcoStats);
        final TableInfo _existingEcoStats = TableInfo.read(db, "eco_stats");
        if (!_infoEcoStats.equals(_existingEcoStats)) {
          return new RoomOpenHelper.ValidationResult(false, "eco_stats(com.greenroute.app.data.local.entities.EcoStats).\n"
                  + " Expected:\n" + _infoEcoStats + "\n"
                  + " Found:\n" + _existingEcoStats);
        }
        final HashMap<String, TableInfo.Column> _columnsEcoRecommendations = new HashMap<String, TableInfo.Column>(4);
        _columnsEcoRecommendations.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEcoRecommendations.put("routeId", new TableInfo.Column("routeId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEcoRecommendations.put("message", new TableInfo.Column("message", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEcoRecommendations.put("score", new TableInfo.Column("score", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEcoRecommendations = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysEcoRecommendations.add(new TableInfo.ForeignKey("routes", "CASCADE", "NO ACTION", Arrays.asList("routeId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesEcoRecommendations = new HashSet<TableInfo.Index>(1);
        _indicesEcoRecommendations.add(new TableInfo.Index("index_eco_recommendations_routeId", false, Arrays.asList("routeId"), Arrays.asList("ASC")));
        final TableInfo _infoEcoRecommendations = new TableInfo("eco_recommendations", _columnsEcoRecommendations, _foreignKeysEcoRecommendations, _indicesEcoRecommendations);
        final TableInfo _existingEcoRecommendations = TableInfo.read(db, "eco_recommendations");
        if (!_infoEcoRecommendations.equals(_existingEcoRecommendations)) {
          return new RoomOpenHelper.ValidationResult(false, "eco_recommendations(com.greenroute.app.data.local.entities.EcoRecommendation).\n"
                  + " Expected:\n" + _infoEcoRecommendations + "\n"
                  + " Found:\n" + _existingEcoRecommendations);
        }
        final HashMap<String, TableInfo.Column> _columnsAchievements = new HashMap<String, TableInfo.Column>(6);
        _columnsAchievements.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("userId", new TableInfo.Column("userId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("earned", new TableInfo.Column("earned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("dateEarned", new TableInfo.Column("dateEarned", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAchievements = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysAchievements.add(new TableInfo.ForeignKey("user", "CASCADE", "NO ACTION", Arrays.asList("userId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesAchievements = new HashSet<TableInfo.Index>(1);
        _indicesAchievements.add(new TableInfo.Index("index_achievements_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        final TableInfo _infoAchievements = new TableInfo("achievements", _columnsAchievements, _foreignKeysAchievements, _indicesAchievements);
        final TableInfo _existingAchievements = TableInfo.read(db, "achievements");
        if (!_infoAchievements.equals(_existingAchievements)) {
          return new RoomOpenHelper.ValidationResult(false, "achievements(com.greenroute.app.data.local.entities.Achievement).\n"
                  + " Expected:\n" + _infoAchievements + "\n"
                  + " Found:\n" + _existingAchievements);
        }
        final HashMap<String, TableInfo.Column> _columnsEmissionFactors = new HashMap<String, TableInfo.Column>(2);
        _columnsEmissionFactors.put("transportType", new TableInfo.Column("transportType", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmissionFactors.put("factor", new TableInfo.Column("factor", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEmissionFactors = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEmissionFactors = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEmissionFactors = new TableInfo("emission_factors", _columnsEmissionFactors, _foreignKeysEmissionFactors, _indicesEmissionFactors);
        final TableInfo _existingEmissionFactors = TableInfo.read(db, "emission_factors");
        if (!_infoEmissionFactors.equals(_existingEmissionFactors)) {
          return new RoomOpenHelper.ValidationResult(false, "emission_factors(com.greenroute.app.data.local.entities.EmissionFactor).\n"
                  + " Expected:\n" + _infoEmissionFactors + "\n"
                  + " Found:\n" + _existingEmissionFactors);
        }
        final HashMap<String, TableInfo.Column> _columnsWeatherCache = new HashMap<String, TableInfo.Column>(5);
        _columnsWeatherCache.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeatherCache.put("city", new TableInfo.Column("city", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeatherCache.put("temperature", new TableInfo.Column("temperature", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeatherCache.put("conditions", new TableInfo.Column("conditions", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeatherCache.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWeatherCache = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWeatherCache = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWeatherCache = new TableInfo("weather_cache", _columnsWeatherCache, _foreignKeysWeatherCache, _indicesWeatherCache);
        final TableInfo _existingWeatherCache = TableInfo.read(db, "weather_cache");
        if (!_infoWeatherCache.equals(_existingWeatherCache)) {
          return new RoomOpenHelper.ValidationResult(false, "weather_cache(com.greenroute.app.data.local.entities.WeatherCache).\n"
                  + " Expected:\n" + _infoWeatherCache + "\n"
                  + " Found:\n" + _existingWeatherCache);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "1a282cbe4d261f8dd4472b494e93fd3f", "24d633ae32eb45ac29692a698e2c6482");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "user","user_preferences","routes","saved_places","eco_stats","eco_recommendations","achievements","emission_factors","weather_cache");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `user`");
      _db.execSQL("DELETE FROM `user_preferences`");
      _db.execSQL("DELETE FROM `routes`");
      _db.execSQL("DELETE FROM `saved_places`");
      _db.execSQL("DELETE FROM `eco_stats`");
      _db.execSQL("DELETE FROM `eco_recommendations`");
      _db.execSQL("DELETE FROM `achievements`");
      _db.execSQL("DELETE FROM `emission_factors`");
      _db.execSQL("DELETE FROM `weather_cache`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UserDao.class, UserDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(UserPreferencesDao.class, UserPreferencesDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RouteDao.class, RouteDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SavedPlaceDao.class, SavedPlaceDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EcoStatsDao.class, EcoStatsDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EcoRecommendationDao.class, EcoRecommendationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AchievementDao.class, AchievementDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EmissionFactorDao.class, EmissionFactorDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WeatherCacheDao.class, WeatherCacheDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public UserDao userDao() {
    if (_userDao != null) {
      return _userDao;
    } else {
      synchronized(this) {
        if(_userDao == null) {
          _userDao = new UserDao_Impl(this);
        }
        return _userDao;
      }
    }
  }

  @Override
  public UserPreferencesDao userPreferencesDao() {
    if (_userPreferencesDao != null) {
      return _userPreferencesDao;
    } else {
      synchronized(this) {
        if(_userPreferencesDao == null) {
          _userPreferencesDao = new UserPreferencesDao_Impl(this);
        }
        return _userPreferencesDao;
      }
    }
  }

  @Override
  public RouteDao routeDao() {
    if (_routeDao != null) {
      return _routeDao;
    } else {
      synchronized(this) {
        if(_routeDao == null) {
          _routeDao = new RouteDao_Impl(this);
        }
        return _routeDao;
      }
    }
  }

  @Override
  public SavedPlaceDao savedPlaceDao() {
    if (_savedPlaceDao != null) {
      return _savedPlaceDao;
    } else {
      synchronized(this) {
        if(_savedPlaceDao == null) {
          _savedPlaceDao = new SavedPlaceDao_Impl(this);
        }
        return _savedPlaceDao;
      }
    }
  }

  @Override
  public EcoStatsDao ecoStatsDao() {
    if (_ecoStatsDao != null) {
      return _ecoStatsDao;
    } else {
      synchronized(this) {
        if(_ecoStatsDao == null) {
          _ecoStatsDao = new EcoStatsDao_Impl(this);
        }
        return _ecoStatsDao;
      }
    }
  }

  @Override
  public EcoRecommendationDao ecoRecommendationDao() {
    if (_ecoRecommendationDao != null) {
      return _ecoRecommendationDao;
    } else {
      synchronized(this) {
        if(_ecoRecommendationDao == null) {
          _ecoRecommendationDao = new EcoRecommendationDao_Impl(this);
        }
        return _ecoRecommendationDao;
      }
    }
  }

  @Override
  public AchievementDao achievementDao() {
    if (_achievementDao != null) {
      return _achievementDao;
    } else {
      synchronized(this) {
        if(_achievementDao == null) {
          _achievementDao = new AchievementDao_Impl(this);
        }
        return _achievementDao;
      }
    }
  }

  @Override
  public EmissionFactorDao emissionFactorDao() {
    if (_emissionFactorDao != null) {
      return _emissionFactorDao;
    } else {
      synchronized(this) {
        if(_emissionFactorDao == null) {
          _emissionFactorDao = new EmissionFactorDao_Impl(this);
        }
        return _emissionFactorDao;
      }
    }
  }

  @Override
  public WeatherCacheDao weatherCacheDao() {
    if (_weatherCacheDao != null) {
      return _weatherCacheDao;
    } else {
      synchronized(this) {
        if(_weatherCacheDao == null) {
          _weatherCacheDao = new WeatherCacheDao_Impl(this);
        }
        return _weatherCacheDao;
      }
    }
  }
}
