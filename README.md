# overviewer json generator

Reads minecraft player data from SOURCE_DIR and returns all players where player data has changed (logged in) in  and player name exists in player cache. 

| env var         | example value | data type |
|-----------------|---------------|-----------|
| RCON_SERVER     | localhost     | String    |
| RCON_PORT       | 25575         | Int       |
| RCON_PASSWORD   | minecraft     | String    |
| LOGIN_DATE      | 1522694775000 | Long      |
| SOURCE_DIR      | /minecraft    | String    |
| DESTINATION_DIR | /overviewer   | String    |
| SLEEP_MINUTES   | 5             | Int       |
