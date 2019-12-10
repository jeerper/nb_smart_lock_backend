package com.summit.schedule;

import com.summit.dao.entity.FaceInfo;
import com.summit.dao.repository.FaceInfoManagerDao;
import com.summit.service.FaceInfoAccCtrlService;
import com.summit.service.FaceInfoManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2019/9/18.
 */
@Slf4j
//@Component
public class FaceInfoManagerSchedule {

    @Autowired
    private FaceInfoManagerDao faceInfoManagerDao;

    @Autowired
    private FaceInfoManagerService faceInfoManagerService;
    @Autowired
    private FaceInfoAccCtrlService faceInfoAccCtrlService;

    /**
     * 1、实时查询人脸信息有有效截至时间，一旦超过有效截至时间，内部人员和临时人员都从摄像头中删除，此时开不了锁
     * 2、然后对于超过有效时间半年的临时人员从数据库中删除，同时删除授权关系，是内部人员的话在数据库中不删除，授权关系也不删除
     */
    @Scheduled(cron = "0/35 * * * * ?")
    public void refreshFaceInfoManager() throws Exception {
        List<FaceInfo> faceInfoList = faceInfoManagerDao.selectList(null);
        for (FaceInfo faceInfo : faceInfoList) {
            Date isValidDate = new Date();
            SimpleDateFormat isValidSdf = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
            String isValidNowtime = isValidSdf.format(isValidDate);
            long isValidNowDate = isValidSdf.parse(isValidNowtime).getTime();
            Date isValidEndTime = faceInfo.getFaceEndTime();
            long isValidEndDate = isValidEndTime.getTime();
            //说明这个人脸已经过期，需要把人脸过期从0变为1
            if (isValidNowDate > isValidEndDate) {
                faceInfo.setIsValidTime(1);
                faceInfoManagerService.updateFaceInfo(faceInfo);
                faceInfoAccCtrlService.deleteFaceAccCtrlByFaceId(faceInfo.getFaceid());
            }
            //临时人员
            if (faceInfo.getFaceType() == 1) {
                Date faceEndTime = faceInfo.getFaceEndTime();
                long faceEndDate = faceEndTime.getTime();
                //设置日期格式
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                c.setTime(faceEndTime);
                c.add(Calendar.MONTH, 6);
                String banNianTime = df.format(c.getTime());
                Date banNianDate = df.parse(banNianTime);
                long banNianDateTime = banNianDate.getTime();
                Date date = new Date();
                String nowtime = df.format(date);
                long nowDate = df.parse(nowtime).getTime();
                //已经超过了截至时间并大于半年,从数据库删掉当前人脸信息,同时删除人脸门禁授权关系
                if (nowDate > banNianDateTime) {
                    faceInfoManagerService.delFaceInfoById(faceInfo.getFaceid());
                }
            }
        }
    }
}