@echo off
setlocal

:: Pawsulin Data Seed Script (Windows)
:: This script runs the backend application with the 'seed' profile to populate the database with initial data.

:: Step 0: Load environment variables
if exist set-env.bat call set-env.bat
if not exist set-env.bat echo Warning: set-env.bat not found. Ensure environment variables (DB_URL, etc.) are set.

echo ---------------------------------------------------
echo Starting Data Seeder...
echo ---------------------------------------------------

:: Run the backend with 'seed' profile and disable web server to exit after seeding
call gradlew.bat :backend:bootRun --args="--spring.profiles.active=dev,seed --spring.main.web-application-type=none"

echo ---------------------------------------------------
echo Data seeding process finished.
echo Check the logs above for details.
echo ---------------------------------------------------

endlocal
