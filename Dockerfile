# Java 21을 사용한 Gradle 기반 빌드 환경 설정
FROM openjdk:21 AS build

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 파일 복사
COPY . .

# Gradle 실행 권한 부여 (Permission 문제 해결)
RUN chmod +x gradlew

# Gradle 빌드 실행
RUN ./gradlew clean build -x check -x test

# 실행 환경 설정 (JDK 21 사용)
FROM openjdk:21

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]
