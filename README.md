# Boat Manager

This project is a secured Rest API to manage a fleet of Boats.
Once you're logged in, you can:

- Create, Read, Update and Delete boats.
- Upload and Download Image for each boat.
- Update the position of the boat.

## Architecture

![architecture](architecture.png)

The architecture is composed of 3 parts:

- The Boat Manager microservice containerized in Docker. It contains the Rest API so the user can access the Data, it is
  also connected to the database and the object storage.
- The PostgreSQL Database, also containerized in Docker. It will contain all the information about the boats except for
  the image.
- The Minio Object Storage, also containerized in Docker. It will contain all the images of boat.

An external API is used to retrieve information about the boat if the user defines a valid IMO code for a boat.

## Start locally

To start locally, first you need to have Java 17 and the Docker Engine. Then you need to create a local /data directory.

```shell
mkdir data
./gradlew bootRun
```

This command will create the local /data directory and start the application and 2 containers, one for Postgres and the
other one for Minio

## Deploy in production

When pull request is validated, a GitHub actions is triggered to start the build of the Docker for Boat
Manager microservice. Then the docker is pushed to Docker Hub and a trigger will launch the restart of the Docker on
the production server.

## Boat information API

This [API](https://zylalabs.com/api-marketplace/shipping/vessel+information+and+route+tracking+api/1835/) is used.
It allows to retrieve additional information about boat with a valid International Maritime
Organization (IMO) code. This code is mandatory for modern ships.
The current geographic position of the boat is also accessible and a cron is used to update it every 30 minutes. The
update can also be triggered by user using an API endpoint.

## Limits and Improvements.

- The users are stored in memory and should be moved either in the database or in an Identity management software.
- The API uses for the ships information can sometimes take a lot of time to answer (more thant 20s) so the fetch of
  the position of the ships can fail. That why the user can ask for an update of the position manually.
- Even though the boat have a lot of fields that could be searchable, it is limited right now to search by name and id.
- The unit test for the external api calls works locally, but they are not working during the docker build, so they
  are disabled.

## Sources

Here is the list of website I used to help build this backend (not exhaustive):

- [Spring](https://spring.io/)
- [Baeldung](https://www.baeldung.com)
- [Stackoverflow](https://stackoverflow.com)
- [Minio](https://min.io/)
- [Docker Hub](https://hub.docker.com)
- [Github](https://github.com)