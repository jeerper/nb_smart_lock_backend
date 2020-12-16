package com.summit.service.impl;

public class Demo {
    public static void main(String[] args) {
        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    System.out.println("线程执行完毕--");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        myThread.start();
        System.out.println("程序执行完毕");
    }

    /*List<FaceInfo> finalFaceInfos = faceInfos;
    Thread myThread = new Thread(new Runnable() {
        @Override
        public void run() {
            if (!CommonUtil.isEmptyList(finalFaceInfos)){
                List<String> errorList=new ArrayList<>();
                FaceUploadZipInfo faceUploadZipInfo=new FaceUploadZipInfo();
                faceUploadZipInfo.setUpstate("faceUploading");
                genericRedisTemplate.opsForValue().set(zipId, faceUploadZipInfo, 2,  TimeUnit.MINUTES);
                for (FaceInfo faceInfo: finalFaceInfos){
                    try {
                        String faceImagesAbsolutePath = faceInfo.getFaceImage();
                        if (StrUtil.isBlank(faceInfo.getUserName())) {//人脸名称
                            errorList.add(FaceZipUploadStatus.UserName.getDescription());
                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                            continue;
                        }
                        if (StrUtil.isBlank(faceImagesAbsolutePath)) {//图片
                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.FaceUploadImageNameWrong.getDescription());
                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                            continue;
                        }
                        if (faceInfo.getFaceType()==null) {//人脸类型
                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.FaceType.getDescription());
                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                            continue;
                        }
                        if (faceInfo.getCardType()==null) {//证件类型
                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.CardType.getDescription());
                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                            continue;
                        }

                        if (StrUtil.isBlank(faceInfo.getCardId())){//证件号
                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.CardId.getDescription());
                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                            continue;
                        }
                        if (faceInfo.getGender()==null){//性别
                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.Gender.getDescription());
                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                            continue;
                        }
                        if (StrUtil.isBlank(faceInfo.getBirthday())){//生日
                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.Birthday.getDescription());
                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                            continue;
                        }
                        if (StrUtil.isBlank(faceInfo.getProvince())){//省份
                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.Province.getDescription());
                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                            continue;
                        }
                        if (StrUtil.isBlank(faceInfo.getCity())){//城市
                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.City.getDescription());
                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                            continue;
                        }
                        if (faceInfo.getFaceEndTime()==null){//有效日期
                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.FaceEndTime.getDescription());
                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                            continue;
                        }
                        if (StrUtil.isBlank(faceInfo.getDeptNames())){//所属机构
                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.DeptNames.getDescription());
                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                            continue;
                        }
                        String subNewImageBase64 = com.summit.util.FileUtil.imageToBase64Str(faceImagesAbsolutePath);
                        byte[] subNewImageBase64Byte = FileUtil.readBytes(faceImagesAbsolutePath);
                        if (!baiduSdkClient.detectFace(subNewImageBase64)) {
                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.FaceNoDetected.getDescription());
                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                            continue;
                        }
                        //判断人脸图片是否在人脸库中存在
                        List<FaceInfo> faceRepeatLibrary=new ArrayList<>();
                        List<FaceInfo> faceInfoLibrary = faceInfoManagerDao.selectList(null);
                        for (FaceInfo face : faceInfoLibrary) {
                            if (StrUtil.isBlank(face.getFaceImage())) {
                                continue;
                            }
                            String faceImageAbsolutePath = SystemUtil.getUserInfo().getCurrentDir() + face.getFaceImage();
                            try {
                                byte[] faceImageBase64 = FileUtil.readBytes(faceImageAbsolutePath);
                                if (Arrays.equals(subNewImageBase64Byte, faceImageBase64)) {
                                    faceRepeatLibrary.add(faceInfo);
                                }
                            } catch (IORuntimeException e) {
                                e.printStackTrace();
                                log.error("本地人脸库图片丢失,图片路径：" + faceImagesAbsolutePath);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (!CommonUtil.isEmptyList(faceRepeatLibrary)){
                            errorList.add(faceInfo.getUserName()+"："+FaceZipUploadStatus.FaceRepeat.getDescription());
                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                            continue;
                        }
                        String faceId = baiduSdkClient.searchFace(subNewImageBase64).getFaceId();
                        log.debug("查询百度人脸库中相似的人脸");
                        if (StrUtil.isNotBlank(faceId)) {
                            FaceInfo similarFaceInfo = faceInfoManagerDao.selectById(faceId);
                            if (similarFaceInfo != null) {
                                errorList.add("发现人脸库中有相似的人脸，名字为：" + similarFaceInfo.getUserName() + "，不能重复录入相同的人脸");
                            } else {
                                errorList.add("发现人脸库中有相似的人脸，名字为：null，不能重复录入相同的人脸");
                            }
                            genericRedisTemplate.expire(zipId, 2,  TimeUnit.MINUTES);
                            continue;
                        }
                        faceInfoManagerService.insertFaceInfoByExcel(faceInfo);
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
                if (!CommonUtil.isEmptyList(errorList)){
                    faceUploadZipInfo.setUpstate("FaceUploadError");
                    faceUploadZipInfo.setErrorList(errorList);
                }else {
                    faceUploadZipInfo.setUpstate("FaceUploadSuccess");
                }
                genericRedisTemplate.opsForValue().set(zipId, faceUploadZipInfo, 2,  TimeUnit.MINUTES);
            }
        }
    });
        myThread.start();*/
}
