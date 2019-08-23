package com.summit.service.impl;

import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.dao.entity.LockInfo;
import com.summit.dao.repository.LockInfoDao;
import com.summit.entity.BackLockInfo;
import com.summit.entity.LockRequest;
import com.summit.entity.ReportParam;
import com.summit.entity.SafeReportInfo;
import com.summit.sdk.huawei.model.LcokProcessResultType;
import com.summit.service.LockInfoService;
import com.summit.util.HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import retrofit2.HttpException;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

@Slf4j
@Service
public class NBLockServiceImpl {
    @Autowired
    private HttpClient httpClient;
    @Autowired
    private LockInfoService lockInfoService;
    @Autowired
    private LockInfoDao lockInfoDao;

    /**
     * 开锁操作
     * @param lockRequest 开锁请求信息对象
     * @return RestfulEntityBySummit对象
     */
    public RestfulEntityBySummit toUnLock(LockRequest lockRequest) {
        String lockId = "";
        String terminalNum;
        if(lockRequest == null || ((terminalNum = lockRequest.getTerminalNum()) == null && (lockId = lockRequest.getLockId()) == null)
                || lockRequest.getOperName() == null){
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993 );
        }
        if(terminalNum == null){
            LockInfo lockInfo = lockInfoDao.selectById(lockId);
            if(lockInfo == null || lockInfo.getLockId() == null)
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993 );
            lockRequest.setTerminalNum(lockInfo.getLockCode());
        }
        final BackLockInfo[] backLockInfos = {null};
        final ResponseCodeEnum[] resultCode = {ResponseCodeEnum.CODE_0000};
        ResponseCodeEnum resultCodeEnum = ResponseCodeEnum.CODE_0000;
        final String[] msg = {ResponseCodeEnum.CODE_0000.getMessage()};
        httpClient.nbLockService.unLock(lockRequest)
                .flatMap(new Func1<BackLockInfo, Observable<BackLockInfo>>() {
                    @Override
                    public Observable<BackLockInfo> call(BackLockInfo backLockInfo) {
                        log.info("{}" , backLockInfo);
                        backLockInfos[0] = backLockInfo;

                        if(backLockInfo != null){
                            String type = backLockInfo.getType();
                            if(LcokProcessResultType.ERROR.getCode().equalsIgnoreCase(type)){
                                resultCode[0] = ResponseCodeEnum.CODE_9999;
                                msg[0] = backLockInfo.getContent();
                            }
                        }else{
                            resultCode[0] = ResponseCodeEnum.CODE_9999;
                            msg[0] = ResponseCodeEnum.CODE_9999.getMessage();
                        }
                        return null;
                    }
                }).onErrorResumeNext(new Func1<Throwable, Observable<BackLockInfo>>() {
            @Override
            public Observable<BackLockInfo> call(Throwable throwable) {
                if (throwable instanceof HttpException) {
                    log.error("httpException");
                    HttpException httpException = (HttpException) throwable;
                    if (httpException.code() == HttpStatus.NOT_FOUND.value()) {
                        log.error("404");
                        return Observable.error(throwable);
                    }
                }
                resultCode[0] = ResponseCodeEnum.CODE_9999;
                return Observable.error(throwable);
            }
        }).subscribe(new Observer<BackLockInfo>() {
            //最后的业务逻辑
            @Override
            public void onCompleted() {
                log.info("请求完成");
            }
            @Override
            public void onError(Throwable e) {
                resultCode[0] = ResponseCodeEnum.CODE_9999;
                log.error("请求失败", e);
            }
            @Override
            public void onNext(BackLockInfo backLockInfo) {
                log.debug("onNext");
            }
        });
        return ResultBuilder.buildError(resultCode[0] ,msg[0], backLockInfos[0]);
    }

    /**
     * 查询锁状态
     * @param lockRequest 开锁请求信息对象
     * @return RestfulEntityBySummit对象
     */
    public RestfulEntityBySummit toQueryLockStatus(LockRequest lockRequest) {
        String lockId = "";
        String terminalNum;
        if(lockRequest == null
                || ((terminalNum = lockRequest.getTerminalNum()) == null && (lockId = lockRequest.getLockId()) == null)){
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993 );
        }
        if(terminalNum == null){
            LockInfo lockInfo = lockInfoService.selectLockById(lockId);
            if(lockInfo == null || lockInfo.getLockId() == null)
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993 );
            lockRequest.setTerminalNum(lockInfo.getLockCode());
        }
        final BackLockInfo[] queryBackLockInfo = {null};
        final ResponseCodeEnum[] resultCode = {ResponseCodeEnum.CODE_0000};
        final String[] msg = {ResponseCodeEnum.CODE_0000.getMessage()};
//        CountDownLatch count = new CountDownLatch(1);
        httpClient.nbLockService.queryLockStatus(lockRequest)
                .flatMap(new Func1<BackLockInfo, Observable<BackLockInfo>>() {
                    @Override
                    public Observable<BackLockInfo> call(BackLockInfo backLockInfo) {
                        log.info("{}" , backLockInfo);
                        queryBackLockInfo[0] = backLockInfo;
                        if(backLockInfo != null){
                            String type = backLockInfo.getType();
                            if(LcokProcessResultType.ERROR.getCode().equalsIgnoreCase(type)){
                                resultCode[0] = ResponseCodeEnum.CODE_9999;
                                msg[0] = backLockInfo.getContent();
                            }
                        }else{
                            resultCode[0] = ResponseCodeEnum.CODE_9999;
                            msg[0] = ResponseCodeEnum.CODE_9999.getMessage();
                        }
                        return null;
                    }
                }).onErrorResumeNext(new Func1<Throwable, Observable<BackLockInfo>>() {
            @Override
            public Observable<BackLockInfo> call(Throwable throwable) {
                if (throwable instanceof HttpException) {
                    log.error("httpException");
                    HttpException httpException = (HttpException) throwable;
                    if (httpException.code() == HttpStatus.NOT_FOUND.value()) {
                        log.error("404");
                        return Observable.error(throwable);
                    }
                }
                resultCode[0] = ResponseCodeEnum.CODE_9999;
                return Observable.error(throwable);
            }
        }).subscribe(new Observer<BackLockInfo>() {
            @Override
            public void onCompleted() {
                log.info("请求完成");
            }
            @Override
            public void onError(Throwable e) {
                resultCode[0] = ResponseCodeEnum.CODE_9999;
                log.error("请求失败", e);
            }
            @Override
            public void onNext(BackLockInfo backLockInfo) {
                log.debug("onNext");
            }
        });
        return ResultBuilder.buildError(resultCode[0] , queryBackLockInfo[0]);
    }

    /**
     * 查询平安报信息
     * @param reportParam 平安报请求信息对象
     * @return RestfulEntityBySummit对象
     */
    public RestfulEntityBySummit toSafeReport(ReportParam reportParam) {
        if(reportParam == null || reportParam.getTerminalNum() == null
                || reportParam.getStartTime() == null || reportParam.getEndTime() == null){
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993);
        }

        final ResponseCodeEnum[] resultCode = {ResponseCodeEnum.CODE_0000};
        final SafeReportInfo[] safeReportRusult = {null};
//        CountDownLatch count = new CountDownLatch(1);
        httpClient.nbLockService.safeReport(reportParam)
                .flatMap(new Func1<SafeReportInfo, Observable<SafeReportInfo>>() {
                    @Override
                    public Observable<SafeReportInfo> call(SafeReportInfo safeReport) {
                        log.info("{}" ,safeReport);
                        safeReportRusult[0] = safeReport;
                        return null;
                    }
                }).onErrorResumeNext(new Func1<Throwable, Observable<SafeReportInfo>>() {
            @Override
            public Observable<SafeReportInfo> call(Throwable throwable) {
                if (throwable instanceof HttpException) {
                    log.error("httpException");
                    HttpException httpException = (HttpException) throwable;
                    if (httpException.code() == HttpStatus.NOT_FOUND.value()) {
                        log.error("404");
                        return Observable.error(throwable);
                    }
                }
                resultCode[0] = ResponseCodeEnum.CODE_9999;
                return Observable.error(throwable);
            }
        }).subscribe(new Observer<SafeReportInfo>() {
            @Override
            public void onCompleted() {
                log.info("请求完成");
            }
            @Override
            public void onError(Throwable e) {
                log.error("请求失败", e);
                resultCode[0] = ResponseCodeEnum.CODE_9999;
            }

            @Override
            public void onNext(SafeReportInfo safeReport) {
                log.debug("onNext");
            }

        });
        return ResultBuilder.buildError(resultCode[0] , safeReportRusult[0]);
    }
}
