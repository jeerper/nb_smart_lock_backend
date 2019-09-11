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
import com.summit.sdk.huawei.model.LockProcessResultType;
import com.summit.service.LockInfoService;
import com.summit.util.HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
     * 开锁操作,同时控制超时时间
     *
     * @param lockRequest 开锁请求信息对象
     * @return RestfulEntityBySummit对象
     */
    public RestfulEntityBySummit toUnLock(LockRequest lockRequest) {
        return unLock(lockRequest);
    }

    /**
     * 查询锁状态,同时控制超时时间
     *
     * @param lockRequest 开锁请求信息对象
     * @return RestfulEntityBySummit对象
     */
    public RestfulEntityBySummit toQueryLockStatus(LockRequest lockRequest) {
        return getLockStatus(lockRequest);
    }

    private RestfulEntityBySummit unLock(LockRequest lockRequest) {
        String lockId = "";
        String terminalNum;
        if (lockRequest == null
                || ((terminalNum = lockRequest.getTerminalNum()) == null && (lockId = lockRequest.getLockId()) == null)) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993);
        }
        if (terminalNum == null) {
            LockInfo lockInfo = lockInfoDao.selectById(lockId);
            if (lockInfo == null || lockInfo.getLockId() == null) {
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993);
            }
            lockRequest.setTerminalNum(lockInfo.getLockCode());
        }
        final BackLockInfo[] backLockInfos = {null};
        final ResponseCodeEnum[] resultCode = {ResponseCodeEnum.CODE_0000};
        ResponseCodeEnum resultCodeEnum = ResponseCodeEnum.CODE_0000;
        final String[] msg = {ResponseCodeEnum.CODE_0000.getMessage()};
        try {
            BackLockInfo backLockInfo = httpClient.nbLockService.unLock(lockRequest).execute().body();
            log.debug("{}", backLockInfo);
            backLockInfos[0] = backLockInfo;
            if (backLockInfo != null) {
                int status = backLockInfo.getObjx();
                if (LockProcessResultType.CommandSuccess.getCode()!=status) {
                    resultCode[0] = ResponseCodeEnum.CODE_9999;
                    msg[0] = backLockInfo.getContent();
                }
            } else {
                resultCode[0] = ResponseCodeEnum.CODE_9999;
                msg[0] = ResponseCodeEnum.CODE_9999.getMessage();
            }

        } catch (Exception throwable) {
            if (throwable instanceof HttpException) {
                log.error("httpException");
                HttpException httpException = (HttpException) throwable;
                if (httpException.code() == HttpStatus.NOT_FOUND.value()) {
                    log.error("404");
                }
            }
            resultCode[0] = ResponseCodeEnum.CODE_9999;
            log.error("请求失败", throwable);
        }
        return ResultBuilder.buildError(resultCode[0], msg[0], backLockInfos[0]);
    }


    private RestfulEntityBySummit getLockStatus(LockRequest lockRequest) {
        String lockId = "";
        String terminalNum;
        if (lockRequest == null
                || ((terminalNum = lockRequest.getTerminalNum()) == null && (lockId = lockRequest.getLockId()) == null)) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993);
        }
        if (terminalNum == null) {
            LockInfo lockInfo = lockInfoService.selectLockById(lockId);
            if (lockInfo == null || lockInfo.getLockCode() == null)
                return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993);
            lockRequest.setTerminalNum(lockInfo.getLockCode());
        }
        final BackLockInfo[] queryBackLockInfo = {null};
        final ResponseCodeEnum[] resultCode = {ResponseCodeEnum.CODE_0000};
        final String[] msg = {ResponseCodeEnum.CODE_0000.getMessage()};

        try {
            BackLockInfo backLockInfo = httpClient.nbLockService.queryLockStatus(lockRequest).execute().body();
            log.debug("{}", backLockInfo);
            queryBackLockInfo[0] = backLockInfo;
            if (backLockInfo == null) {
                resultCode[0] = ResponseCodeEnum.CODE_9999;
                msg[0] = ResponseCodeEnum.CODE_9999.getMessage();
            }
        } catch (Exception throwable) {
            if (throwable instanceof HttpException) {
                log.error("httpException");
                HttpException httpException = (HttpException) throwable;
                if (httpException.code() == HttpStatus.NOT_FOUND.value()) {
                    log.error("404");
                }
            }
            resultCode[0] = ResponseCodeEnum.CODE_9999;
        }
        return ResultBuilder.buildError(resultCode[0], queryBackLockInfo[0]);
    }

    /**
     * 查询平安报信息
     *
     * @param reportParam 平安报请求信息对象
     * @return RestfulEntityBySummit对象
     */
    public RestfulEntityBySummit toSafeReport(ReportParam reportParam) {
        if (reportParam == null || reportParam.getTerminalNum() == null
                || reportParam.getStartTime() == null || reportParam.getEndTime() == null) {
            return ResultBuilder.buildError(ResponseCodeEnum.CODE_9993);
        }

        final ResponseCodeEnum[] resultCode = {ResponseCodeEnum.CODE_0000};
        final SafeReportInfo[] safeReportRusult = {null};
//        CountDownLatch count = new CountDownLatch(1);
        httpClient.nbLockService.safeReport(reportParam)
                .flatMap(new Func1<SafeReportInfo, Observable<SafeReportInfo>>() {
                    @Override
                    public Observable<SafeReportInfo> call(SafeReportInfo safeReport) {
                        log.debug("{}", safeReport);
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
                log.debug("请求完成");
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
        return ResultBuilder.buildError(resultCode[0], safeReportRusult[0]);
    }
}
