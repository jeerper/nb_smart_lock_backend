package com.summit.service;

import com.summit.entity.BackLockInfo;
import com.summit.entity.LockRequest;
import com.summit.entity.ReportParam;
import com.summit.entity.SafeReportInfo;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;


/**
 * 锁操作service接口
 */
public interface NBLockService {

    /**
     * 开锁操作
     * @param lockRequest 开锁请求信息对象
     * @return BackLockInfo对象
     */
    @POST("unlock.jhtml")
    Observable<BackLockInfo> unLock(@Body LockRequest lockRequest);

    /**
     * 查询锁状态
     * @param lockRequest 开锁请求信息对象
     * @return BackLockInfo对象
     */
    @POST("lockstatus.jhtml")
    Observable<BackLockInfo> queryLockStatus(@Body LockRequest lockRequest);

    /**
     * 查询平安报信息
     * @param reportInfo 平安报请求信息对象
     * @return SafeReportInfo对象
     */
    @POST("serachsafedata.jhtml")
    Observable<SafeReportInfo> safeReport(@Body ReportParam reportInfo);
}
