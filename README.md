# Gym Workout Manager

A Java Swing desktop application for managing gym clients, trainers, workouts, and client progress. MySQL stores the application data.

## Requirements

- Windows
- JDK (both `java` and `javac` available on `PATH`)
- MySQL Server

The MySQL Connector/J driver is included in `lib/`.

## Database setup

1. Start MySQL Server.
2. Open `gym_db.sql` in MySQL Workbench and run it to create the database and sample data.

**Warning:** `gym_db.sql` begins by dropping and recreating the `gym_db` database. This permanently deletes any existing data in that database. Back up existing data before running the script.

## Run

Double-click `run-hidden.vbs` to start without a terminal window. A small dialog asks for the MySQL username and password; the password is masked while typing.

For troubleshooting, you can also double-click `run.bat` or open PowerShell in this folder and run:

```powershell
.\run.bat
```

The launcher compiles the application before it starts. Enter your MySQL account credentials, not the gym application's sample login credentials.

## Sample application logins

| Role | Username | Password |
| --- | --- | --- |
| Admin | `admin` | `admin123` |
| Trainer | `trainer1` | `trainer123` |
| Client | `client1` | `client123` |

These credentials are for the sample users inserted by `gym_db.sql`. Change them before using the application with real data.

## Project structure

- `src/controller/` — application actions and validation
- `src/dao/` — database queries
- `src/db/` — MySQL connection configuration
- `src/model/` — application data models
- `src/view/` — Swing windows
- `lib/` — MySQL JDBC driver
- `gym_db.sql` — database schema and sample data
- `run-hidden.vbs` — launches without a terminal window
- `run.bat` / `run.ps1` — Windows build and launch scripts
