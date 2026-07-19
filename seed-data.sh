#!/bin/bash
set -e

# Pawsulin Data Seed Script
# This script runs the backend application with the 'seed' profile to populate the database with initial data.

# Step 0: Load environment variables
if [ -f "./set-env.sh" ]; then
    echo "Loading environment variables from set-env.sh..."
    # We use a subshell to avoid exporting variables to the current shell if this script is sourced
    source ./set-env.sh
else
    echo "Warning: set-env.sh not found. Ensure environment variables (DB_URL, etc.) are set."
fi

echo "---------------------------------------------------"
echo "Starting Data Seeder..."
echo "---------------------------------------------------"

# Run the backend with 'seed' profile and disable web server to exit after seeding
./gradlew :backend:bootRun --args='--spring.profiles.active=dev,seed --spring.main.web-application-type=none'

echo "---------------------------------------------------"
echo "Data seeding process finished."
echo "Check the logs above for details."
echo "---------------------------------------------------"
