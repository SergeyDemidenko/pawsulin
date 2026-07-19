@echo off
REM Pawsulin Environment Variables Configuration
REM This script sets the environment variables required for the Pawsulin application.
REM Use 'call set-env.bat' to keep these variables in your current command prompt session.

set "DB_USERNAME=postgres"
set "DB_PASSWORD=postgres"
set "DB_URL=jdbc:postgresql://localhost:5432/pawsulin_db"
set "JWT_SECRET=v9y$B&E)H@McQfTjWnZr4u7x!A%C*F-JaNdRgUkXp2s5v8y/B?E(G+KbPeShVmYq"
set "CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173,http://localhost:8080,http://localhost,http://localhost:80,http://127.0.0.1"
set "SPRING_PROFILES_ACTIVE=prod"

echo Environment variables for Pawsulin have been set:
echo DB_USERNAME: %DB_USERNAME%
echo DB_URL: %DB_URL%
echo CORS_ALLOWED_ORIGINS: %CORS_ALLOWED_ORIGINS%
echo SPRING_PROFILES_ACTIVE: %SPRING_PROFILES_ACTIVE%
