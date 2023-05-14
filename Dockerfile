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
WORKDIR /app-bin

# Copy the compiled JAR file from the build container
COPY --from=build /app/target/*-jar-with-dependencies.jar /app-bin/orderbook-app.jar

# Copy the run.sh script into the container
COPY run.sh /app-bin/run.sh

# Make the run.sh script executable
RUN chmod +x /app-bin/run.sh

# Set the default command for the container
# CMD ["/app-bin/run.sh"]

# Set the default command for the container
CMD ["java", "-jar", "/app-bin/orderbook-app.jar"]