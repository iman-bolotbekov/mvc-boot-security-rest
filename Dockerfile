FROM openjdk:21

ADD /target/spring-boot_security-demo-0.0.1-SNAPSHOT.jar backend.jar

ENTRYPOINT ["java", "-jar", "backend.jar"]