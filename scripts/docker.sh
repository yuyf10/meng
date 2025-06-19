./mvnw clean package -DskipTests
docker build -t meng .
docker run --network host --env-file env --name meng -d --restart unless-stopped meng