#!/bin/sh -l
set -e

sed -i 's,${projectName},'${projectName}',g' ./.env

sed -i 's,${WebServer_Port},'${WebServer_Port}',g' ./.env

sed -i 's,${WebServer_Port},'${WebServer_Port}',g' ${WORKSPACE}/src/main/resources/application-pro.yml

sed -i 's,${Registry_Center_Port},'${Registry_Center_Port}',g' ${WORKSPACE}/src/main/resources/application-pro.yml

sed -i 's,${Registry_Center_URL},'${Registry_Center_URL}',g' ./.env

sed -i 's,${Registry_Center_IP},'${Registry_Center_IP}',g' ./.env



echo "编译"${projectName}

cd ${WORKSPACE}/

mvn clean

mvn install

if [ $? = 0 ]
then
 echo "maven构建成功"
else
 exit 1
fi



cd ${WORKSPACE}/deploy

echo "部署"${projectName}"(Docker版本)"


docker-compose -p ${projectName} down  --rmi all

docker-compose -p ${projectName} up  -d

if [ ! -d ${java_service_wrapper_template_path} ]
then
    exit 0
fi


echo "打包"${projectName}"(Windows版本)"

if [ ! -d ${fileStoragePath}/${projectName} ]
then
  echo "装载java后台启动程序包装模板"
  cp -rf ${java_service_wrapper_template_path} ${fileStoragePath}/${projectName}
  sed -i 's,${AppName},'${projectName}',g' ${fileStoragePath}/${projectName}/conf/wrapper.conf
fi

cd ${WORKSPACE}/target

rm -rf ${fileStoragePath}/${projectName}/bin/backend.jar
rm -rf ${fileStoragePath}/${projectName}/bin/lib
rm -rf ${fileStoragePath}/${projectName}/bin/config

cp -rf backend.jar ${fileStoragePath}/${projectName}/bin
cp -rf lib ${fileStoragePath}/${projectName}/bin/
cp -rf config ${fileStoragePath}/${projectName}/bin/

echo "推送组件信息到组件货架上"
curl -X POST "http://${Cbb_Store_Server_Url}/api/component/pushComponentInfo" -H "accept: */*" -H "Content-Type: application/json;charset=UTF-8" -d "{ \"description\": \"${component_description}\", \"name\": \"${component_name}\", \"tag\": \"${component_tag}\"}"