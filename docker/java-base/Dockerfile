# ----------------------------------------------
# Stage 1:  Minimal java image with sdkman + Ubuntu LTS
# ----------------------------------------------
FROM mcr.microsoft.com/openjdk/jdk:11-ubuntu as base-builder

WORKDIR /srv

# Defining default Java, can be any java provided by sdkman
ARG JAVA_VERSION="11.0.17-amzn"

ENV JAVA_OUTPUT_DIR="/java"
ENV DEBIAN_FRONTEND=noninteractive
ENV PATH="$PATH:/java/bin"

# Installing basic packages 
RUN apt update && \
    apt upgrade -y && \
    apt install -y --no-install-recommends zip unzip wget libtcnative-1 tzdata tini ca-certificates openssl libapr1 libpq-dev


# Install curl
RUN wget -O - https://repo.dotcms.com/artifactory/ext-release-local/com/dotcms/curl-static/curl-`uname -m` | install /dev/stdin /usr/bin/curl && \
    chmod a+x /usr/bin/curl 

# Downloading sdkman
#RUN curl -sL "https://get.sdkman.io" | bash

# Installing Java via sdkman 
    # Modules included
    # java.naming - javax/naming/NamingException
    # java.desktop - java/beans/PropertyEditorSupport
    # java.management - javax/management/MBeanServer
    # java.security.jgss - org/ietf/jgss/GSSException
    # java.instrument - java/lang/instrument/IllegalClassFormatException
    # jdk.unsupported - sun/misc/Unsafe, needed by caffine
    # java.scripting - Nashhorn, needed by log4j
    # java.rmi needed by Quartz
    # jdk.compiler needed to build
    # jdk.zipfs needed for gradle

RUN jlink \
    --verbose \
    --add-modules \
        java.base,jdk.crypto.ec,jdk.jdwp.agent,jdk.management,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument,jdk.unsupported,java.scripting,java.rmi,jdk.compiler,jdk.zipfs,jdk.naming.dns,jdk.localedata,java.xml,jdk.xml.dom \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --output "$JAVA_OUTPUT_DIR" 
    
# Cleanup
RUN rm -rf /root/.sdkman && \
    apt purge -y zip unzip wget msopenjdk-11 packages-microsoft-prod fontconfig-config && \
    apt autoremove -y && \
    apt clean && \
    rm -rf /var/lib/apt/lists/*

# ----------------------------------------------
# Stage 2:  Flatten everything to 1 layer
# ----------------------------------------------
FROM scratch
COPY --from=base-builder / /
