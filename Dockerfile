FROM ly405653510/java8-oracle-utf-8-shanghai

MAINTAINER liuyuan <405653510@qq.com>

ADD ./target/  /opt/App/

ADD ./target/lib/linux-x86-64/lib64/ /lib/

WORKDIR /opt/App/

ENTRYPOINT ["java", "-jar"]

CMD ["backend.jar"]