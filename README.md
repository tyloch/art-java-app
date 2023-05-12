# Order Book App

This is a Java application that processes order book data. The application can be run using a Docker container.

## Prerequisites

Before running the application, make sure you have the following installed on your system:

1. Docker - [Install Docker](https://docs.docker.com/get-docker/)
2. jq - [Install jq](https://stedolan.github.io/jq/download/)

## Building the Docker image

To build the Docker image for the application, navigate to the project directory and run the following command:

```bash
docker build -t art-orderbook 
```
This command will create a Docker image named art-orderbook.

## Running the application

To run the application using the art-orderbook Docker image, execute the following command:

```bash
docker run -i art-orderbook < tests/test2.in | jq -S . | diff - <(jq -S . tests/test2.out)
```
or
```bash
docker run -i -v $(pwd):/app art-orderbook /app/run.sh < tests/test2.in | jq -S . | diff - <(jq -S . tests/test2.out)
```

This command will run the Java application with the input file tests/test2.in, sort the resulting JSON using jq, and compare it to the expected output in tests/test2.out.

If there is no difference between the output and the expected output, the command will not produce any output. If there are differences, the command will display the differences between the two files.

Replace tests/test2.in and tests/test2.out with the appropriate input and expected output files for other test cases.




