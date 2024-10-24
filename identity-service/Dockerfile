# Stage 1: build
# Start with a Maven image that includes JDK 21s
    # https://hub.docker.com/layers/library/maven/3.9.8-amazoncorretto-21/images/sha256-f17dfb3c9b659967e89871b1f37b39454706b06474162899ec925d505d8fecd1?context=explore
FROM maven:3.9.8-amazoncorretto-21 as build

# Coppy source code and pom.xml file to /app folder
WORKDIR /app
    # copy file pom.xml vào thư mục hiện tại "."
COPY pom.xml .
    # coppy thư mục src vào thư mục con thư mục hiện tại có tên "src"
COPY src ./src

# Build source code with Maven
    # Build ứng dụng bằng maven nhưng bỏ qua Unit Test
RUN mvn package -DskipTests

# Stage 2: create Image
# Start with Amazon Correteo JDK 21
    # amazoncorretto:21.0.4 là 1 image hoàn toàn khác với image bên trên.
    # https://hub.docker.com/layers/library/amazoncorretto/21.0.4/images/sha256-5301563fd2271bfdfbf7842d46953d4d3ee26700b737201debc8ef09d95b99cb?context=explore
FROM amazoncorretto:21.0.4

# Set working folder to App and copy complied file from above step
    # Tạo thư mục /app
WORKDIR /app
    # Copy file .jar vừa được build bên trên về thư muc app của mình và đặt tên nó là app.jar
COPY --from=build  /app/target/*.jar app.jar

# Command to run the application
    # Chỉ dẫn cho docker biết: java -jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]