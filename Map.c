#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <stdlib.h>
#include <ctype.h>

#include "MapJNI.h"

/*
    Contains the logic for all of the Map.java methods.
    This also includes the loadMap method.
    @author : tjv26
*/

/* Sets global variables for the map credentials */
char *mapName;
int mapWidth = 0;
int mapHeight = -1;
int winCondition = -1;
int LOOK_RADIUS = 5;
char **map;

/**
     * Checks if a given string starts with a certain substring
     *
     * @param *a : The first string to be compared
     * @param *b : The second string to be compared
     * @return : Whether or not a starts with b.
*/
bool startsWith(char *a, char *b){
   if(strncmp(a, b, strlen(b)) == 0){
        return true;
   }
   return false;
}

/*
     * Gets a substring of an input.
     *
     * @param *input : The input string to be substringed.
     * @param offset : The index of which to start the substring.
     * @param len : The length of the substring.
     * @param *dest : The destination to store the substring at.
     * @return : A pointer to the substring.
*/
char* subString (char* input, int offset, int len, char* dest){
  int inputLen = strlen (input);

  /*
    If the offset and length are greater than the original length
    then the substring is invalid
   */
  if (offset + len > inputLen){
     return NULL;
  }
  //If the length is 0 or less set the string to empty
  if(len <= 0){
    strcpy(dest, "");
    return dest;
  }
  //Copies the substring into the dest pointer variable
  strncpy (dest, input + offset, len);
  return dest;
}

/*
     * JNI method which sets the win condition for the map.
     *
     * @param *env : The java environment variable.
     * @param jObj : The jva object variable.
     * @param winString : The java string containing the win condition.
     * @return : Whether or not there was an error.
*/
JNIEXPORT jboolean JNICALL Java_MapJNI_setWin(JNIEnv *env, jobject jObj, jstring winString){
    //Resets the win condition
    winCondition = -1;
    //Converts the jstring into an iterable and readable c char array
    const char *win = (*env)->GetStringUTFChars(env, winString, 0);
    //The string is too short
    if(strlen(win) <= 4){
        return true;
    }
    int l = 0;
    char *winPtr = malloc(sizeof(char) * strlen(win));
    //Converts to a non constant char
    strcpy(winPtr, win);
    //Loops through the win condition until a digit is found
    while(*winPtr){
        if(isdigit(*winPtr)) {
    		long winConditionLong = strtol(winPtr, &winPtr, 10);
    		winCondition = winConditionLong;
    	}
    	else{
    	    winPtr++;
    	}
    }
    if(winCondition < 0){
        return true;
    }
    l++;
    return false;
}

/*
     * Sets the map name
     *
     * @param *nameLine : The line containing the name of the map.
     * @param offset : Whether or not the function was called from java.
     * @return : Whether or not there was an error.
*/
bool setName(char *nameLine, bool jstring){
    //The substring to be found
    char *nameSubstr = "name  \0";
    //Too short to contain a valid name, so return true
    if(strlen(nameLine) < 1){
        mapName = NULL;
        return true;
    }

    if(!startsWith(nameLine, nameSubstr) && strlen(nameLine) < 4){
        return true;
    }

    //Length of the map's name (after name substring)
    int mapNameLength = strlen(nameLine) - strlen(nameSubstr);
    //Add an extra character for null terminator if called from java
    if(jstring){
        mapNameLength++;
    }
    char *mapNameTemp = malloc(sizeof(char) * mapNameLength);
    mapNameTemp[mapNameLength] = '\0';

    //Removes the 'name ' part from the name line
    subString(nameLine, strlen(nameSubstr) - 1, mapNameLength, mapNameTemp);

    if(strlen(mapNameTemp) < 1){
        mapName = NULL;
        return true;
    }
    //Sets the map name
    mapName = mapNameTemp;
    return false;
}

/*
     * JNI method which sets the name of the map.
     *
     * @param *env : The java environment variable.
     * @param jObj : The jva object variable.
     * @param nameString : The java string containing the map name.
     * @return : Whether or not there was an error.
*/
JNIEXPORT jboolean JNICALL Java_MapJNI_setName(JNIEnv *env, jobject jObj, jstring nameString){
    const char *nameStringNative = (*env)->GetStringUTFChars(env, nameString, 0);
    int lineLength = strlen(nameStringNative) + 1;
    char *nameLinePass = malloc(sizeof(char) * lineLength);
    strcpy(nameLinePass, nameStringNative);
    return setName(nameLinePass, true);
}

/*
     * Populates the map.
     *
     * @param tempMap : The array in which the map is currently stored
     * @param **twoDMap : A pointer array which stores each row of the map (as an array element)
     * @param height : The height of the map.
     * @param width : The width of the map.
*/
void addMap(char tempMap[mapHeight][mapWidth], char **twoDMap, int height, int width){
	int i, j;
	for(j = 0; j < height; j++) {
		for(i = 0; i < width; i++) {
			twoDMap[j][i] = tempMap[j][i];
		}
	}
}

/*
     * JNI method which loads the map from a given file and sets it.
     *
     * @param *env : The java environment variable.
     * @param jObj : The java object variable.
     * @param fileNameStr : The java string containing the map file name.
*/
JNIEXPORT void JNICALL Java_MapJNI_loadMap(JNIEnv *env, jobject jObj, jstring fileNameStr){
    //Resets the variables
    mapHeight = -1;
    mapWidth = 0;
    free(map);
    free(mapName);
    //Converts the jstring fileNameStr into a readable C string
    const char *fileName = (*env)->GetStringUTFChars(env, fileNameStr, 0);
    FILE *file;
    char c;
    //Opens the file for read only
    file = fopen(fileName, "r");

    //If the file does not exist, print error message and exit
    if(file == NULL){
        char ioExceptionMsg[] = "Map is not valid";
        printf("Map is not valid");
        exit(-1);
    }
    else if(fileName[0] == '\0'){
        char fileExceptionMsg[] = "default file example_map.txt not found";
        exit(-1);
    }

    int nameLength = 0;
    int winLength = 0;
    int lineNumber = 0;
    bool widthFound = false;

    //Gets map dimensions
    while((c=fgetc(file))!=EOF){
        if(c != '\n'){
            //First line contains the map name
            if(lineNumber == 0){
                nameLength++;
            }
            //Second line contains the win condition
            else if(lineNumber == 1){
               winLength++;
            }
            //Only count map width on first line of the map
            else if(!widthFound){
                mapWidth++;
            }
        }
        //Otherwise new line
        else{
            if(lineNumber > 1){
                widthFound = true;
            }
            //Increase the map height
            lineNumber++;
            mapHeight++;
        }
    }

    file = fopen(fileName, "r");
    char *nameLine = malloc(sizeof(char) * nameLength);
    char *winLine = malloc(sizeof(char) * winLength);
    char mapRows[mapHeight][mapWidth];
    bool error;
    int j = 0;

    //Reads the line containing the map name into nameLine variable
    fgets(nameLine, nameLength + 2, file);
    setName(nameLine, false);

    //Reads the line containing the map name into nameLine variable
    fgets(winLine, winLength + 2, file);
    Java_MapJNI_setWin(env, jObj, (*env)->NewStringUTF(env, winLine));

    //Holds the map
    char mapContainer[mapHeight][mapWidth];
    //Creates an array of pointers pointing to each row of the map
    char **twoDMap = malloc(mapHeight * sizeof(char *));
    int i;
    //Determines how much space each row will take
    for(i = 0; i < mapHeight; i++) {
        twoDMap[i] = malloc(mapWidth * sizeof(char));
    }

    //Populates map container with each row of the map
    while(j < mapHeight && fgets(mapRows[j], sizeof(mapRows[0]) + 2, file)){
        strcpy(mapContainer[j], mapRows[j]);
        j++;
    }
    addMap(mapContainer, twoDMap, mapHeight, mapWidth);
    map = twoDMap;
    fclose(file);
}

/*
     * JNI method which returns the win condition.
     *
     * @param *env : The java environment variable.
     * @param jObj : The java object variable.
     * @return : How much gold is required to escape the dungeon.
*/
JNIEXPORT jint JNICALL Java_MapJNI_getGoldToWin(JNIEnv *env, jobject jObj){
    return winCondition;
}

/*
     * JNI method which returns the current map's width
     *
     * @param *env : The java environment variable.
     * @param jObj : The java object variable.
     * @return : The map's width.
*/
JNIEXPORT jint JNICALL Java_MapJNI_getMapWidth(JNIEnv *env, jobject jObj){
    return mapWidth;
}

/*
     * JNI method which returns the current map's height
     *
     * @param *env : The java environment variable.
     * @param jObj : The java object variable.
     * @return : The map's height.
*/
JNIEXPORT jint JNICALL Java_MapJNI_getMapHeight(JNIEnv *env, jobject jObj){
    return mapHeight;
}

/*
     * JNI method which returns the current map's name
     *
     * @param *env : The java environment variable.
     * @param jObj : The java object variable.
     * @return : The map's name as a jstring.
*/
JNIEXPORT jstring JNICALL Java_MapJNI_getMapName(JNIEnv *env, jobject jObj){
    return (*env)->NewStringUTF(env, mapName);
}

/*
     * JNI method which returns a given map tile
     *
     * @param *env : The java environment variable.
     * @param jObj : The java object variable.
     * @param x : The x coordinate of the tile.
     * @param y : The y coordinate of the tile.
     * @return : The corresponding map tile as a jchar.
*/
JNIEXPORT jchar JNICALL Java_MapJNI_getTile(JNIEnv *env, jobject jObj, jint x, jint y){
    jchar tile;
    if (y < 0 || x < 0 || y >= mapHeight || x >= mapWidth){
        tile = '#';
    	return tile;
    }
    tile = map[y][x];
    return tile;
}

/*
     * JNI method which replaces a current map tile
     *
     * @param *env : The java environment variable.
     * @param jObj : The java object variable.
     * @param x : The x coordinate of the tile.
     * @param y : The y coordinate of the tile.
     * @param with : The character to replace the map tile with.
*/
JNIEXPORT void JNICALL Java_MapJNI_replaceTile(JNIEnv *env, jobject jObj, jint x, jint y, jchar with){
        if (y < 0 || x < 0 || y >= mapHeight || x >= mapWidth){

        }
        else{
            char tileChar = (char)with;
            map[y][x] = tileChar;
    	}
}

/*
     * Helper function for the look JNI method which converts an array
        of jchars into a jcharArray type.
     *
     * @param *env : The java environment variable.
     * @param size : The size of the char array.
     * @param replyChar : The array of characters that represent a map row
     * @return : The jcharArray of characters representing a map row
*/
jobjectArray addLookRow(JNIEnv *env, int size, jchar replyChar[]){
    //Creating an array of type jcharArray
    jcharArray rowOfChars = (*env)->NewCharArray(env, size);

    //Setting the region for the array
    (*env)->SetCharArrayRegion(env, rowOfChars, 0, size, replyChar);

    return rowOfChars;
}

/*
     * JNI method which returns a look window with the x and y at the centre
     *
     * @param *env : The java environment variable.
     * @param jObj : The java object variable.
     * @param x : The x coordinate of the tile.
     * @param y : The y coordinate of the tile.
     * @return with : A jobjectArray representing a 2d char array in java
                      containing a seires of chars representing the look window
*/
JNIEXPORT jobjectArray JNICALL Java_MapJNI_look(JNIEnv *env, jobject jObj, jint x, jint y){
    jchar replyRow[LOOK_RADIUS];
    jcharArray replyCharRows[LOOK_RADIUS];
    int i, j, k;
    for(i = 0; i < LOOK_RADIUS; i++){
        for(j = 0; j < LOOK_RADIUS; j++){
            int posX = x + i - LOOK_RADIUS/2;
            int posY = y + j - LOOK_RADIUS/2;
            //If not outside the map, add to replyRow
    		if (posX >= 0 && posX < mapWidth &&
    		posY >= 0 && posY < mapHeight){
    			replyRow[j] = map[posY][posX];
    		}
    		else{
    			replyRow[j] = '#';
    		}
        }
        //Appends each row of the map
        replyCharRows[i] = addLookRow(env, LOOK_RADIUS, replyRow);
    }
    //Creating an array that contains all the rows for the look window
    jobjectArray replyRows = (*env)->NewObjectArray(env, LOOK_RADIUS, (*env)->GetObjectClass(env, replyCharRows[0]), 0);

    //Sets each element of replyRows to the row of chars made in above loop
    for(k = 0; k < LOOK_RADIUS; k++){
        (*env)->SetObjectArrayElement(env, replyRows, k, replyCharRows[k]);
    }
    return replyRows;
}