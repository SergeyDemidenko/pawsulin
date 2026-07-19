#!/bin/bash
# Pawsulin Environment Variables Configuration
# This script sets the environment variables required for the Pawsulin application.
# Use 'source ./set-env.sh' to keep these variables in your current shell session.

export DB_USERNAME='postgres'
export DB_PASSWORD='postgres'
export DB_URL='jdbc:postgresql://localhost:5432/pawsulin_db'
export JWT_SECRET='v9y$B&E)H@McQfTjWnZr4u7x!A%C*F-JaNdRgUkXp2s5v8y/B?E(G+KbPeShVmYq'
export CORS_ALLOWED_ORIGINS='http://localhost:3000,http://localhost:5173,http://localhost:8080,http://localhost,http://localhost:80,http://127.0.0.1'
export SPRING_PROFILES_ACTIVE='prod'

echo "Environment variables for Pawsulin have been set:"
echo "DB_USERNAME: $DB_USERNAME"
echo "DB_URL: $DB_URL"
echo "CORS_ALLOWED_ORIGINS: $CORS_ALLOWED_ORIGINS"
echo "SPRING_PROFILES_ACTIVE: $SPRING_PROFILES_ACTIVE"
