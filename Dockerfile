FROM adoptopenjdk:11.0.4_11-jre-hotspot
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /app/
WORKDIR /app
ENTRYPOINT ["java","/src/main/java/com/tesco/demo/MinimumPriceApplication.java"]