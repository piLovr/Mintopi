## Application.properties
```properties
//collects Beans of Type "Command" automatically
mintopi.commands.levenshteinThreshold=0 //disabled
mintopi.commands.listenerRegistration=true

//is enabled by default if no custom Been found
mintopi.roomStorage.storageType: memory (hybrid/discard) //wether to store room metadata in memory, in memory and database (if creds provided) or just don't

mintopi.accountStorage.storageType: memory (hybrid/discard)
```