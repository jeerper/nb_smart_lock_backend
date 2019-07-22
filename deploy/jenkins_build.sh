#!/bin/sh -l
set -e

#项目名称
projectName="cbb_web_service_skeleton"
#组件名称
component_name="业务组件脚手架"
#组件描述
component_description="业务组件脚手架"
#组件标签
component_tag=${projectName}
#业务组件端口
Web_Service_Skeleton_Port=46000

source ${WORKSPACE}/env/env.sh

WebServer_Port=${Web_Service_Skeleton_Port}


source ${WORKSPACE}/env/build_wrapper.sh