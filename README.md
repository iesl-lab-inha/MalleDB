<!-- TABLE OF CONTENTS -->
## Table of Contents

* [About the Project](#about-the-project)

* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Usage](#usage)
* [License](#license)
* [Contact](#contact)



<!-- ABOUT THE PROJECT -->
## About The Project

MalleDB is not solo database, but a combination of other best databases.
It is fully configurable based on the system requirements

Here is list of available databases:
* MySQL
* LevelDB
* Cassandra

<!-- GETTING STARTED -->
## Getting Started

In order to use the MalleDB library, you should set up the systems first

### Prerequisites

The current version of MalleDB requires below prerequisites:
* MySQL server
* Cassandra Server
* CQL version 3.4.4
* Maven
* Java Virtual Machine (JDK8)


### Installation

1. Add .jar file into project directory
2. Enjoy using MalleDB library!

<!-- USAGE EXAMPLES -->
## Usage

###Provided API:

* init() -> Initialize the database with default configuration
* init(Options options) -> Initialize the database with custom configuration
* create() -> Creates required db/keyspace and tables
* close() -> Closes the connection


* insert(String key, String value) -> inserts key-value pairs
* read(String key) -> reads the value for the given key
* update(String key, String value) -> updates the old value with the new one
* delete(String key) -> deletes the item for given key

###Configuration

The Options class is used to configure the database

#####Constructors:
* Options() -> default configuration
* Options(DB_TYPE blockdb) -> using only one sub database
* Options(DB_TYPE mdb, DB_TYPE bdb, DB_TYPE tdb) -> using different sub databases

#####Set Parameters:
* setMySQLCONF(String server, String user, String passw, String db, String[] tables)
* setCassandraConf(String server, int port, String rep_strategy, int rep_factor, String keyspace, String[] tables)
* setLevelDBConf(String db)

###Example

Here is an example of MalleDB library usage:

```sh
MalleDB malleDB = new MalleDB();
Options options = new Options(Options.DB_TYPE.MYSQL, Options.DB_TYPE.CASSANDRA, Options.DB_TYPE.LEVELDB);
malleDB.init(options);
malleDB.create();


String key = generateRandomString(20);
String value = generateRandomString(500);

malleDB.insert(key, value);

malleDB.read(key);

malleDB.delete(key);

malleDB.close();
```

<!-- LICENSE -->
## License

Distributed under the IESL License. See `LICENSE` for more information.



<!-- CONTACT -->
## Contact

Tillo - [@ti11o](https://github.com/ti11o) - mr.khikmatillo@gmail.com
