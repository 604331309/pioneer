package com.slk.nettysocket.repository;

import com.slk.nettysocket.entity.ClientInfo;
import org.springframework.data.repository.CrudRepository;

/**
 * @author lshao
 * 2020/7/1
 */
public interface ClientInfoRepository extends CrudRepository<ClientInfo, String> {
    ClientInfo findClientInfoByClientid(String clientId);
}

