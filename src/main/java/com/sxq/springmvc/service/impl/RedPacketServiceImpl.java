package com.sxq.springmvc.service.impl;

import com.sxq.springmvc.dao.RedPacketDao;
import com.sxq.springmvc.pojo.RedPacket;
import com.sxq.springmvc.service.RedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RedPacketServiceImpl implements RedPacketService{

    @Autowired
    private RedPacketDao redPacketDao = null;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED,
        propagation = Propagation.REQUIRED)
    public RedPacket getRedPacket(Long id) {
        return redPacketDao.getRedPacket(id);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED)
    public RedPacket getRedPacketForUpdate(Long id) {
        return redPacketDao.getRedPacketForUpdate(id);
    }


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED,
    propagation = Propagation.REQUIRED)
    public int decreaseRedPacket(Long id) {
        return redPacketDao.decreaseRedPacket(id);
    }
}
