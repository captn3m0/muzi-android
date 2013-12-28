package muzi.sdslabs.co.in;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class SongDB {

	public static final String KEY_ID_DB = "_id";

	public static final String KEY_ID_TRACK = "_id_track";
	public static final String KEY_TITLE = "title";
	public static final String KEY_TRACK = "track";
	public static final String KEY_ALBUM_ID = "albumId";
	public static final String KEY_ALBUM_NAME = "albumName";
	public static final String KEY_FILE = "file";
	public static final String KEY_YEAR = "year";
	public static final String KEY_ARTIST = "artist";
	public static final String KEY_BAND_ID = "bandId";
	public static final String KEY_BAND_NAME = "bandName";
	public static final String KEY_GENRE_ID = "genreId";
	public static final String KEY_GENRE_NAME = "genreName";
	public static final String KEY_NO_OF_PLAYS = "numberOfPlays";
	public static final String KEY_LENGTH = "length";
	public static final String KEY_LYRICS = "lyrics";

	private static String DATABASE_NAME = Environment
			.getExternalStorageDirectory() + "/Muzi.db";

	private static final String DATABASE_TABLE = "songs";
	private static final int DATABASE_VERSION = 1;

	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;

	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("CREATE VIRTUAL TABLE " + DATABASE_TABLE
					+ " USING fts3 (" + KEY_ID_DB
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_ID_TRACK
					+ " INTEGER, " + KEY_TITLE + " TEXT NOT NULL, " + KEY_TRACK
					+ " INTEGER, " + KEY_ALBUM_ID + " INTEGER, "
					+ KEY_ALBUM_NAME + " TEXT NOT NULL, " + KEY_FILE
					+ " TEXT NOT NULL, " + KEY_YEAR + " INTEGER, " + KEY_ARTIST
					+ " TEXT NOT NULL, " + KEY_BAND_ID + " INTEGER, "
					+ KEY_BAND_NAME + " TEXT NOT NULL, " + KEY_GENRE_ID
					+ " INTEGER, " + KEY_GENRE_NAME + " TEXT NOT NULL, "
					+ KEY_NO_OF_PLAYS + " INTEGER, " + KEY_LENGTH
					+ " INTEGER, " + KEY_LYRICS + " TEXT NOT NULL);");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.w("Database", "Upgrading database from version " + oldVersion
					+ " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
	}

	public SongDB(Context c) {
		ourContext = c;
	}

	public SongDB open() throws SQLException {
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}

	public SongDB opentoread() throws SQLException {
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getReadableDatabase();
		return this;
	}

	public boolean isOpen() {
		Log.i("is open called in journal db", ourDatabase.isOpen() + "");
		return ourDatabase.isOpen();
	}

	public void close() {
		if (ourDatabase.isOpen())
			ourHelper.close();
	}
}
