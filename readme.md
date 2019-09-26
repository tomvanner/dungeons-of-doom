##Dungeons of Doom Version

##INSTALLATION INSTRUCTIONS
- Navigate to this directory in the command line
- Enter ‘javac *.java’ in order to compile all of the game files

##RUN INSTRUCTIONS
- Type ‘java DODServer <port number>’ into the command line to start the server
- Type ‘java HumanClient <hostname> <port number>’ into the command line if you want to connect as a human
- Type ‘java BotClient <hostname> <port number>’ into the command line if you want to connect a bot

##HOW TO PLAY
- The aim of the game is to collect the required gold, and reach an exit tile
- Type in a command which you wish to execute
- The list of valid commands are as thus:
	HELLO - tells you the amount of gold that is required to exit the dungeon
	MOVE <direction> - moves the player in the specified direction, i.e. N,E,S,W
	PICKUP - pickup any gold on the tile the player is currently occupying
	LOOK - returns a 5x5 window of all the tiles around the player
	LIST - return a list of all the players currently connected to the server
	SHOUT <message> - broadcasts a message to every player that is connected

	WHISPER <player name> message - sends a private message to the specified player, e.g. WHISPER <Bob> hello

	QUIT - quits the game
	
