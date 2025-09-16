# Use official base image of Java Runtime
FROM openjdk:11

#RUN addgroup -S nonroot \
#    && adduser -S nonroot -G nonroot

USER nonroot

# Set volume point to /tmp
VOLUME /tmp

# Make port 8080 available to the world outside container
#EXPOSE 8081
#EXPOSE 8080
#EXPOSE 8085

ENV TZ=Asia/Kolkata

#RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 9092

# Set application's JAR file
#ARG JAR_FILE=target/apwarrantydemo-0.0.1-SNAPSHOT.jar
ARG JAR_FILE=./build/libs/*.jar

# Add the application's JAR file to the container
ADD ${JAR_FILE} app.jar

# Run the JAR file
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]

