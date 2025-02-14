# [Hub of All Things](https://hubofallthings.com)

This repository contains an implementation of the [Hub-of-All-Things](http://hubofallthings.com) HAT Microserver
project.

# Citizen App Local Dev Instructions

This is for either Linux or macOS.

## Requirements:
* Docker 
    * docker.com
* Node Version Manager
    * https://github.com/nvm-sh/nvm
* Clone HAT2.0
    * https://github.com/dataswift/HAT2.0
* Clone Local DB
    * https://github.com/dataswifty/localdockerdb
* Clone Rumpel/Citizen App
    * https://github.com/dataswifty/rumpel-react


## Changes you need to make:
* Add this entry to your host file
127.0.0.1   bobtheplumber.example.com
* For rumpel-react, you need to use node version 12, which you can set using NVM
    * “> nvm use 12”

Local Development
1. Local Postgres
    * Open a terminal in the localdockerdb repo root
    * Build the image using “docker build .”
        * This will return a hash as the image ID, use that in the next step.
    * Run using “docker run --rm -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=password --name pg-docker -p 5432:5432 HASH_FROM_PREVIOUS_STEP”
2. Run the local HAT
    * Open a terminal in the HAT repo root
    * git submodule init
    * git submodule update
    * cp sample.awsconfig ~/.aws/config _*Warning!*_  you may have an AWS config file already, this will overwrite it.
    * make run-dev
    * Visit http://bobtheplumber.example.com:9000
        * This will cause the JIT compilation and the evolutions to run, so allow a bit of time before the page renders
￼
3. You can now make a change to rumpel-react
    * Open a terminal and set the environment variable PDA_FE_DIR to <HAT_REPO_DIR>/
        * For example, I checked out the HAT repo to:
            * ~/g/dataswift/OSS/HAT2.0
        * And I set the environment variable:
            * export PDA_FE_DIR=~/g/dataswift/OSS/HAT2.0/hat/app/org/hatdex/hat/phata/assets
    * Open the file ProfileDropDown.tsx and find line 59, which consists of “Welcome {userHatName}”
        * Change “Welcome” to “Hello”
    * make yarn-install
    * make yarn-build
    * make copy-files 
    * Reload bobtheplumber.example.com:9000 and now it says Hello!
￼

# 

## Releases

The current project version is [here](https://github.com/dataswift/HAT2.0/releases).

## About the project

The Hub-of-All-Things is a HAT Microserver for individuals to own, control and share their data.

A Personal Microserver (“the HAT”) is a personal single tenant (“the individual self”) technology system that is fully
individual self-service, to enable an individual to define a full set of “meta-data” defined as a specific set of
personal data, personal preferences and personal behaviour events.

The HAT enables individuals to share the correct information (quality and quantity), with the correct people, in the
correct situations for the correct purposes and to gain the benefits.

## Technology stack

This HAT Microserver implementation is written in Scala (2.12.11) uses the following technology stack:

- [PostgreSQL](https://www.postgresql.org) relational database (version 9.5)
- [Play Framework](https://www.playframework.com) (version 2.6)
- [Akka](https://akka.io) (version 2.5)
- [Slick](https://scala-slick.org/) as the database access layer (version 3.2)

## Running the project - Either via docker-compose (recommended) or building locally

### 1. Get the Source and the submodules for both of the methods

    > git clone https://github.com/Hub-of-all-Things/HAT2.0.git
    > cd HAT2.0
    > git submodule init 
    > git submodule update

### 2. Configure your /etc/hosts

    127.0.0.1   bobtheplumber.hat.org
    127.0.0.1   bobtheplumber.example.com

### 3a. Using docker-compose

    > cd <DIRECTORY_YOU_CHECKED_OUT_INTO>/deployment/docker
    > docker-compose up

When the build finishes, open [`https://bobtheplumber.example.com:9001`](https://bobtheplumber.example.com:9001) in a
browser. Standard account login password is `testing`.

### 3b. Building locally

### HAT Setup

HAT runs as a combination of a backing PostgreSQL database (with a
[public schema](https://github.com/Hub-of-all-Things/hat-database-schema)
for flattened data storage) and a software stack that provides logic to work with the schema using HTTP APIs.

To run it from source in a development environment two sets of tools are required:

- PostgreSQL database and utilities
- [Scala Build Tool](https://www.scala-sbt.org) (SBT)

To launch the HAT, follow these steps:

1. Create the database, which we assume is available as `localhost`:
    ```bash
    > createdb testhatdb1
    > createuser testhatdb1
    > psql postgres -c "GRANT CREATE ON DATABASE testhatdb1 TO testhatdb1"
    ```
2. Compile the project:
    ```bash
    > make dev
    ```
3. Add custom local domain mapping to your `/etc/hosts` file. This will make sure when you go to the defined address
   from your machine you will be pointed back to your own machine. E.g.:
    ```
    127.0.0.1   bobtheplumber.hat.org
    127.0.0.1   bobtheplumber.example.com
    ```
4. Run the project:
    ```bash
    > make run-dev
    ```
5. Go to [http://bobtheplumber.example.com:9000](http://bobtheplumber.example.com:9000)

**You're all set!**

### Customising your development environment

Your best source of information on how the development environment could be customised is the `hat/conf/dev.conf`
configuration file. Make sure you run the project locally with the configuration enabled (using the steps above)
or it will just show you the message that the HAT could not be found.

Among other things, the configuration includes:

- host names alongside port numbers of the test HATs ([http://yourname.hat.org:9000](http://yourname.hat.org:9000))
- access credentials used to log in as the owner or restricted platform user into the HAT (the default password is a
  very unsafe *testing*)
- database connection details (important if you want to change your database setup above)
- private and public keys used for token signing and verification

Specifically, it has 4 major sections:

- Enables the development environment self-initialisation module:
    ```
    play.modules {
      enabled += "org.hatdex.hat.modules.DevHatInitializationModule"
    }
    ```
- Sets the list of database evolutions to be executed on initialisation:
    ```
    devhatMigrations = [
      "evolutions/hat-database-schema/11_hat.sql",
      "evolutions/hat-database-schema/12_hatEvolutions.sql",
      "evolutions/hat-database-schema/13_liveEvolutions.sql",
      "evolutions/hat-database-schema/14_newHat.sql"]
    ```  
- `devhats` list sets out the list of HATs that are served by the current server, for each including owner details to be
  initialised with and database access credentials. Each specified database must exist before launching the server but
  are initialised with the right schema at start time
- `hat` section lists all corresponding HAT configurations to serve, here you could change the HAT domain name, owner's
  email address or public/private keypair used by the HAT for its token operations

## Using docker-compose

We have put together a [docker-compose](https://docs.docker.com/compose/) file that will allow you to run a PostgreSQL node and a HAT node easily.

### Get the Source and the submodules

    > git clone https://github.com/Hub-of-all-Things/HAT2.0.git
    > cd HAT2.0
    > git submodule init 
    > git submodule update
    > cd deployment/docker
    > docker-compose up
    > open [https://bobtheplumber.example:9001](https://bobtheplumber.example:9001)

## Using Helm 3

The HAT solution is easy deployable on top of Kubernetes via [Helm 3 chart](charts).

## Additional information

- API documentation can be found at the [developers' portal](https://developers.hubofallthings.com)
- [HAT Database Schema](https://github.com/Hub-of-all-Things/hat-database-schema) has been split up into a separate
  project for easier reuse across different environments.

## License

HAT including HAT Schema and API is licensed
under [AGPL - GNU AFFERO GENERAL PUBLIC LICENSE](https://github.com/Hub-of-all-Things/HAT/blob/master/LICENSE/AGPL)
