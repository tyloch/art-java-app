# Use the official Maven image as the base image
FROM maven:3.8.3-openjdk-11 as build

# Set the working directory
WORKDIR /app

# Copy the Maven pom.xml file
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy the source code files into the container
COPY src ./src 

# Package the Java application
RUN mvn package && ls -la /app/target

# Set up the runtime container
FROM openjdk:11

# Set the working directory
WORKDIR /app

# Copy the compiled JAR file from the build container
#COPY --from=build /app/target/*.jar /app/orderbook-app.jar

# Copy the compiled JAR file from the build container
COPY --from=build /app/target/*-jar-with-dependencies.jar /app/orderbook-app.jar


# Set the entry point for the container
ENTRYPOINT ["java", "-jar", "/app/orderbook-app.jar"]
