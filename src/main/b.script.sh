#!/bin/bash

# Variables
MYSQL_IMAGE="mysql:8.0"
MYSQL_CONTAINER="mysql-container"
MYSQL_ROOT_PASSWORD="root"
MYSQL_PORT="3309"
DB_NAME="warehouse_db"

# Pull MySQL image
echo "Pulling MySQL Docker image..."
docker pull $MYSQL_IMAGE

# Run MySQL container
echo "Starting MySQL container..."
docker run --name $MYSQL_CONTAINER -e MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD -p $MYSQL_PORT:3306 -d $MYSQL_IMAGE

# Wait for the MySQL container to be ready (adjust sleep time if necessary)
echo "Waiting for MySQL to initialize..."
sleep 15  # Give MySQL some time to fully initialize

# Create the database in MySQL
echo "Creating the database $DB_NAME..."
docker exec -i $MYSQL_CONTAINER mysql -uroot -p$MYSQL_ROOT_PASSWORD -e "CREATE DATABASE IF NOT EXISTS $DB_NAME;"

# Output the success message
echo "Database '$DB_NAME' is successfully created and MySQL is running on port $MYSQL_PORT."

# Optional: To check if the database is created, you can run:
# docker exec -i $MYSQL_CONTAINER mysql -uroot -p$MYSQL_ROOT_PASSWORD -e "SHOW DATABASES;"

