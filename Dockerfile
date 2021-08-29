FROM clojure:lein

WORKDIR /build/
COPY . /build/
RUN lein uberjar

FROM openjdk:11
WORKDIR /root/
COPY --from=0 /build/target/ .
ENTRYPOINT ["java", "-jar", "decoder-ring-0.1.0-SNAPSHOT-standalone.jar"]