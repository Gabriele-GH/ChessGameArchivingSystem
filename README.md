# Chess Game Archiving System

## License
This project is licensed under the **Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)** license.

### What you can do:
- Use, modify, and share the code **only for non-commercial purposes**.
- You must provide **proper attribution** to the author: Gabriele Pezzini, with a link to the repository.

### What you cannot do:
- Use the code for commercial purposes (selling, integrating into paid products, paid services) without permission.

### Commercial Use
To obtain a commercial license, please contact the author: **Gabriele Pezzini**.

Full license text: [CC BY-NC 4.0](https://creativecommons.org/licenses/by-nc/4.0/legalcode)

### Description
This application is written in Java and stores games in a relational database.
It is based on JDK 25 and the compatible databases are:
MariaDB 11.x
Postgres 17
The application stores games either by entering them move by move or by importing them from .pgn files.
It is possible to export games in .pgn format.
Games are not stored as a sequence of moves but as a sequence of positions.
A position is present only once in the system and may have been played in multiple games.
The application recognises the current position and is able to suggest both the notes entered by the user relating to the position and the possible continuations suggested based on the stored games. 
The user is shown the number of games won, lost or drawn for each known continuation relating to the current position.
Is possibile to search games by player, opening, game result, ecc

