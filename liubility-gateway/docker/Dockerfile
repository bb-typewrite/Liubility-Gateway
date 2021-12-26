#FROM wdragondragon/jsp-gzhmt

#FROM classmethod/openjdk-alpine-git
FROM wdragondragon/java8-font-shanghai
MAINTAINER jdragon
ENV LANG C.UTF-8
#ENV LANGUAGE en_US:en
ENV TZ=Asia/Shanghai
ENV LC_ALL C.UTF-8
COPY liubility-gateway-1.0-SNAPSHOT.jar liubility-gateway-1.0-SNAPSHOT.jar

CMD java -jar /liubility-gateway-1.0-SNAPSHOT.jar