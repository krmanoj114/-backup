# Use official base image of Java Runtime
FROM openjdk:11
#yes

#RUN addgroup -S nonroot \
#    && adduser -S nonroot -G nonroot

USER nonroot

ADD certificate.crt /usr/local/openjdk-11/lib/security/certificate.crt

#RUN keytool -keystore /usr/local/openjdk-11/lib/security/cacerts -storepass changeit -noprompt -trustcacerts -importcert -alias https://tpex-dev.tdem.toyota-asia.com -file /usr/local/openjdk-11/lib/security/certificate.crt

# Set volume point to /tmp
VOLUME /tmp

# Make port 8080 available to the world outside container
#EXPOSE 8081
#EXPOSE 8080
#EXPOSE 8085


ENV TZ=Asia/Bangkok

#RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 9591

# Set application's JAR file
#ARG JAR_FILE=target/apwarrantydemo-0.0.1-SNAPSHOT.jar
ARG JAR_FILE=./build/libs/*.jar

# Add the application's JAR file to the container
ADD ${JAR_FILE} app.jar

# Run the JAR file
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
