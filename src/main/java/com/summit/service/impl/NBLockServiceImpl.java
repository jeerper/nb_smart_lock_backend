package com.summit.service.impl;

import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.util.ResultBuilder;
import com.summit.entity.LockInfo;
import com.summit.entity.LockRequest;
import com.summit.entity.ReportParam;
import com.summit.entity.SafeReportInfo;
import com.summit.util.HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import retrofit2.HttpException;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

@Slf4j
@Component
public class NBLockServiceImpl {
    @Autowired
    private HttpClient httpClient;

    public RestfulEntityBySummit toUnLock(LockRequest lockRequest) {
        if(lockRequest == null || lockRequest.getTerminalNum() == null
                || lockRequest.getOperName() == null){
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993 );
        }
        final LockInfo[] backLockInfo = {null};
        final ResponseCodeEnum[] resultCode = {ResponseCodeEnum.CODE_0000};
        httpClient.ubLockService.unLock(lockRequest)
                .flatMap(new Func1<LockInfo, Observable<LockInfo>>() {
                    @Override
                    public Observable<LockInfo> call(LockInfo lockInfo) {
                        log.info("{}" ,lockInfo);
                        backLockInfo[0] = lockInfo;
                        return null;
                    }
                }).onErrorResumeNext(new Func1<Throwable, Observable<LockInfo>>() {
            @Override
            public Observable<LockInfo> call(Throwable throwable) {
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
        }).subscribe(new Observer<LockInfo>() {
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
            public void onNext(LockInfo lockInfo) {
                log.debug("onNext");
            }
        });
        return ResultBuilder.buildError(resultCode[0] ,backLockInfo[0]);
    }


    public RestfulEntityBySummit toQueryLockStatus(LockRequest lockRequest) {
        if(lockRequest == null || lockRequest.getTerminalNum() == null
                || lockRequest.getOperName() == null){
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993 );
        }
        final LockInfo[] queryLockInfo = {null};
        final ResponseCodeEnum[] resultCode = {ResponseCodeEnum.CODE_0000};
//        CountDownLatch count = new CountDownLatch(1);
        httpClient.ubLockService.queryLockStatus(lockRequest)
                .flatMap(new Func1<LockInfo, Observable<LockInfo>>() {
                    @Override
                    public Observable<LockInfo> call(LockInfo lockInfo) {
                        log.info("{}" ,lockInfo);
                        queryLockInfo[0] = lockInfo;
                        return null;
                    }
                }).onErrorResumeNext(new Func1<Throwable, Observable<LockInfo>>() {
            @Override
            public Observable<LockInfo> call(Throwable throwable) {
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
        }).subscribe(new Observer<LockInfo>() {
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
            public void onNext(LockInfo lockInfo) {
                log.debug("onNext");
            }
        });
        return ResultBuilder.buildError(resultCode[0] , queryLockInfo[0]);
    }


    public RestfulEntityBySummit toSafeReport(ReportParam reportParam) {
        if(reportParam == null || reportParam.getTerminalNum() == null
                || reportParam.getStartTime() == null || reportParam.getEndTime() == null){
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993);
        }

        final ResponseCodeEnum[] resultCode = {ResponseCodeEnum.CODE_0000};
        final SafeReportInfo[] safeReportRusult = {null};
//        CountDownLatch count = new CountDownLatch(1);
        httpClient.ubLockService.safeReport(reportParam)
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
