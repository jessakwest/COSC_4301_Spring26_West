# Use lightweight Temurin JDK 17
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy source files
COPY *.java ./

# Compile Java files
RUN javac *.java

# Run Main class on container start
CMD ["java", "Main"]
