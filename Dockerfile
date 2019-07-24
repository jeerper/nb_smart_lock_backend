###############################################################################
#                             第一阶段构建:Docker容器基础操作系统镜像
###############################################################################
FROM gizmotronic/oracle-java8 as operating_system

#RUN echo "http://mirrors.ustc.edu.cn/alpine/v3.4/main" > /etc/apk/repositories \
# && echo "http://mirrors.ustc.edu.cn/alpine/v3.4/community" >> /etc/apk/repositories \
# && apk update \
# && apk add tzdata \
# && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
# && echo "Asia/Shanghai" >  /etc/timezone \
# && apk del tzdata \
# && rm -rf /var/cache/apk/*

###############################################################################
#                             第二阶段构建:将应用打包进容器
###############################################################################
FROM operating_system

MAINTAINER liuyuan <405653510@qq.com>

ADD ./target/  /opt/App/

ADD ./target/lib/linux-x86-64/lib64/libFEC.so  /lib/libFEC.so
ADD ./target/lib/linux-x86-64/lib64/libSEC.so  /lib/libSEC.so

WORKDIR /opt/App/

ENTRYPOINT ["java", "-jar"]

CMD ["backend.jar"]