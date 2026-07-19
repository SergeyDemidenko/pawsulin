#!/bin/bash
set -e

# Pawsulin Clean Build and Deploy Script

# Step 0: Load environment variables
# This ensures that required variables like DB_PASSWORD and JWT_SECRET are available
if [ -f "./set-env.sh" ]; then
    echo "Loading environment variables from set-env.sh..."
    source ./set-env.sh
else
    echo "Warning: set-env.sh not found. Ensure environment variables are set in your shell."
fi

# Step 1: Check if app is running and stop it if it is
echo "Stopping the application if it is running..."
docker compose down

# Step 2: Clean docker images
echo "Cleaning locally built docker images..."
# --rmi local removes images used by services that don't have a custom tag
# --volumes removes named volumes declared in the 'volumes' section of the compose file
# --remove-orphans removes containers for services not defined in the compose file
docker compose down --rmi local --volumes --remove-orphans

# Step 3: Clean build with gradle
echo "Performing a clean build of the backend with Gradle..."
chmod +x gradlew
./gradlew clean :backend:bootJar

# Step 4: Run docker image (Build images)
# The user requested to 'run docker image' - in compose context this typically means building them
echo "Building docker images..."
docker compose build

# Step 5: Run the app
echo "Starting the application in detached mode..."
docker compose up -d

echo "---------------------------------------------------"
echo "Deployment complete!"
echo "Backend: http://localhost:8080/actuator/health"
echo "Frontend: http://localhost"
echo "---------------------------------------------------"
