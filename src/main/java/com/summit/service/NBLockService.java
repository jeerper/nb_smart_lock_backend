package com.summit.service;

import com.summit.entity.BackLockInfo;
import com.summit.entity.LockRequest;
import com.summit.entity.ReportParam;
import com.summit.entity.SafeReportInfo;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface NBLockService {
    @POST("unlock.jhtml")
    Observable<BackLockInfo> unLock(@Body LockRequest lockRequest);


    @POST("lockstatus.jhtml")
    Observable<BackLockInfo> queryLockStatus(@Body LockRequest lockRequest);

    @POST("serachsafedata.jhtml")
    Observable<SafeReportInfo> safeReport(@Body ReportParam reportInfo);
}
