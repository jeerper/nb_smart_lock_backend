#!/bin/sh -l
set -e

#项目名称
projectName="cbb_nb_smart_lock_backend"
#组件名称
component_name="智能锁联动业务组件后端服务"
#组件描述
component_description="智能锁联动业务组件后端服务"
#组件标签
component_tag=${projectName}
#业务组件端口
Web_Service_Skeleton_Port=45012

source ${WORKSPACE}/env/env.sh

WebServer_Port=${Web_Service_Skeleton_Port}


source ${WORKSPACE}/env/build_wrapper.sh