package com.service.repository;

import com.xhfxw.logistics.domain.LogisticsAddrNodeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * Created by yutinglin on 2017/5/24.
 */

public interface LogisAddtNodeInfoRepository extends JpaRepository<LogisticsAddrNodeInfo,Long>{
    
}
