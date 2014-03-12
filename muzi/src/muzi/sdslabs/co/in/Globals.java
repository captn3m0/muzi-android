package muzi.sdslabs.co.in;

import java.util.ArrayList;
import java.util.HashMap;

/** Global Variables to be used in all the activities */

public class Globals {

	public static ArrayList<HashMap<String, String>> nowPlayingSongList;
	public static String TAG_NAME = "name";
	public static String TAG_PATH = "path";
	public static String TAG_IMAGEPATH = "id";
	public static int currentSongIndex, tempSongIndex;
	public static boolean shouldShuffle = false;
}