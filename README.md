# bunuti

### Intro
This is a simple temporary poc

### Technical stack:
- java (21)
- JPA / hibernate
- maven wrapper
- H2

### TODO Next

- IF the application grows it could be nice to use 3 maven modules:
  application
  infra
  domain
- add a postgres database dockerised instead of an in memory like H2
  To separate responsabilities
- add DTOs (Entity should not be seen in the app layer)
- add tests for each layers
- add given/when/then pattern
- add authentication (JWT for e.g.)
- add authorization
- Add profiles (dev, test, staging, pp, prod...)
- add swagger
- add a database migration tools
- add sonar build task
- add open api (nice to have but not mandatory)
- add gitlab-ci conf
- add conf for prettier, git

