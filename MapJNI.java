import java.io.*;

/**
 * Contains all of the native methods required for the JNI implementation of map
 *
 * @author : tjv26
 */
public class MapJNI {
    static {
		try{
			System.loadLibrary("dod");
		}
		catch(UnsatisfiedLinkError e){
			e.printStackTrace();
			System.out.println("Native code failed to load");
			System.exit(-1);
		}
        
    }

    public native int getGoldToWin();

    protected native char[][] look(int x, int y);

    public native String getMapName();

    public native int getMapWidth();

    public native int getMapHeight();

    public native char getTile(int x, int y);

    public native void replaceTile(int x, int y, char with);

    public native boolean setWin(String in);

    public native boolean setName(String in);

    public native void loadMap(String fileName);
}
