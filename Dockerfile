FROM alpine:edge
LABEL author=remote.zip.com
RUN apk add --no-cache openjdk11
COPY files/UnlimitedJCEPolicyJDK8/* \
  /usr/lib/jvm/java-11-openjdk/jre/lib/security/