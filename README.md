# Movie Repository

A Java backend project built as part of the SP-1 assignment. The goal was to fetch movie data from an external API, map it to entities, persist everything to a PostgreSQL database, and expose a service layer that lets you query the data in different ways.

---

## What does it do?

The project talks to [The Movie Database API (TMDB)](https://www.themoviedb.org/) and pulls in Danish movies released between 2020 and 2025. For each movie it fetches:

- The movie's basic info (title, rating, popularity, release date)
- Which genres it belongs to
- Who directed it
- Which actors are in it and what character they played

All of that gets persisted into a PostgreSQL database using Hibernate/JPA. Once the data is in the database you can query it through the service layer — for example get the top 10 rated movies, the lowest rated, the most popular, search by genre or title, or get the overall average rating.

---

## Tech stack

| What               | Why |
|--------------------|---|
| Java 17            | Language |
| Hibernate 7 / JPA  | ORM — maps Java objects to database tables |
| PostgreSQL         | Database |
| Jackson            | Deserializing JSON from the TMDB API |
| JUnit 6 + Hamcrest | Testing |


---

## Project structure

```
src/
├── main/
│   ├── java/app/
│   │   ├── Main.java                  # Entry point
│   │   ├── dto/                       # Data Transfer Objects — mirrors the TMDB JSON response
│   │   ├── entities/                  # JPA entities (Movie, Actor, Director, Genre, MovieActor)
│   │   ├── exceptions/                # Custom exceptions for DB and API errors
│   │   ├── persistence/
│   │   │   ├── config/                # Hibernate setup and entity registration
│   │   │   └── daos/                  # Generic DAO + MovieDAO with all the queries
│   │   ├── services/                  # MovieService — orchestrates API fetching and DB persistence
│   │   └── utils/                     # APIReader (handles HTTP + Jackson deserialization)
│   └── resources/
│       └── config.properties          # DB name, username, password
└── test/
    └── java/app/
        ├── config/HibernateTestConfig # Separate Hibernate config pointing at Testcontainers
        └── persistence/
            ├── daos/MovieDAOTest       # Integration tests for all DAO methods
            └── testutils/TestPopulator # Seeds known data before each test
```

---

## Database model

The core idea is that a **Movie** sits in the middle of everything:

- A movie has **one Director** (many movies can share one director → `@ManyToOne`)
- A movie has **many Genres** and a genre can belong to many movies → `@ManyToMany` with a join table `movie_genre`
- A movie has **many Actors** but the relationship also carries extra data (the character name), so there's a join entity `MovieActor` that sits between `Movie` and `Actor` → two `@ManyToOne` relationships

```
Director ──< Movie >── movie_genre ──< Genre
                │
            MovieActor
                │
             Actor
```

### Why not just use cascade?

When we persist a movie we can't blindly cascade into genres, directors and actors because those entities are shared across movies. If we did, we'd end up with duplicate rows every time — one "Action" genre per movie instead of one shared row.

Instead the DAO uses a `findOrPersistOnApiId` helper: before inserting a genre/director/actor it first checks whether one with that TMDB API id already exists in the database. If it does, it reuses it. If not, it inserts it. This keeps the data normalised without needing any unique-constraint tricks or merge cascades.

---

## How to run it

### Prerequisites

- Java 17+
- PostgreSQL running locally
- A TMDB API key (free at [themoviedb.org](https://www.themoviedb.org/settings/api))

### 1. Configure the database

Edit `src/main/resources/config.properties`:

```properties
DB_NAME=moviedb
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

Make sure the database exists before starting:

```sql
CREATE DATABASE moviedb;
```

### 2. Set your API key

The API key is read from an environment variable:

```bash
export API_KEY=your_tmdb_api_key_here
```

### 3. Fetch and save movies

In `Main.java`, uncomment this line:

```java
ms.fetchAndSaveToDB();
```

Then run `Main.java`. It will page through the TMDB discover endpoint and persist every Danish movie it finds.

### 4. Query the data

After populating the DB the rest of `main()` demonstrates all the available queries — top rated, lowest rated, most popular, by genre, by title, and average rating.

---

## Key design decisions

**Generic `DAO<T>`** — the base DAO covers `create`, `get`, `getAll`, `update` and `delete` for any entity. `MovieDAO` extends it and adds the movie-specific queries on top.

**Service layer** — `MovieServiceImpl` is the only place that knows about both the API and the DAO. It maps DTOs → entities and calls the DAO. `Main` only talks to the service, never to the DAO directly.

**`findOrPersistOnApiId`** — the core pattern that keeps shared entities (genres, directors, actors) de-duplicated. Lookup by the TMDB `apiId` field, persist only if not found.


