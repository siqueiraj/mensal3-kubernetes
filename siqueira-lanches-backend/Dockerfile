# --- Estágio 1: build ---
# Usa uma imagem com Maven + JDK só para compilar. Essa camada não vai para a imagem final.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copia primeiro só o pom.xml e baixa as dependências.
# Isso aproveita o cache de camadas do Docker: se o pom.xml não mudar,
# o Docker reusa essa camada e não baixa tudo de novo a cada build.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Agora copia o código-fonte e compila, gerando o .jar
COPY src ./src
RUN mvn package -DskipTests -B

# --- Estágio 2: runtime ---
# Imagem final só com o JRE (não precisa do Maven nem do JDK completo para rodar).
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia apenas o .jar gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Limita o heap da JVM (necessário na t3.micro, que tem só 1GB de RAM)
ENV JAVA_OPTS="-Xmx400m"

# Ativa o profile de produção por padrão
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
