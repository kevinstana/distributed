# The image built from this Dockerfile
# has been pushed to github packages
# with the name devops-backend.
# This file is no longer required, it can be deleted.
# But keep it for now.

FROM openjdk:21-rc-oracle as builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY ./src ./src
RUN ./mvnw  package -Dmaven.test.skip

FROM openjdk:19-jdk-alpine3.16
RUN apk update && apk add curl
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 9090
COPY --from=builder /app/target/*.jar /app/*.jar
ENTRYPOINT ["java", "-jar", "/app/*.jar" ]