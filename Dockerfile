FROM openjdk:8-jdk-alpine
RUN apk update && apk upgrade && apk add netcat-openbsd && apk add curl
RUN echo $JAVA_HOME
RUN cd /tmp/ && \
    wget 'http://download.oracle.com/otn-pub/java/jce/8/jce_policy-8.zip' --header "Cookie: oraclelicense=accept-securebackup-cookie" && \
    unzip jce_policy-8.zip && \
    rm jce_policy-8.zip && \
    yes |cp -v /tmp/UnlimitedJCEPolicyJDK8/*.jar /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dserver.port=8776","-jar","/app.jar"]
EXPOSE 8776
