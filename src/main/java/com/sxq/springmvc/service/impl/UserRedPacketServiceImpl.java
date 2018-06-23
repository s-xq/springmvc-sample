package com.sxq.springmvc.service.impl;

import com.sxq.springmvc.dao.RedPacketDao;
import com.sxq.springmvc.dao.UserRedPacketDao;
import com.sxq.springmvc.pojo.RedPacket;
import com.sxq.springmvc.pojo.UserRedPacket;
import com.sxq.springmvc.service.UserRedPacketService;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRedPacketServiceImpl implements UserRedPacketService{

    private static final int FAILED = 0;

    @Autowired
    private UserRedPacketDao userRedPacketDao = null;

    @Autowired
    private RedPacketDao redPacketDao = null;


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED,
    propagation = Propagation.REQUIRED)
    public int grapRedPacket(Long redPacketId, Long userId) {
        //抢红包失败重试
        for (int i =0 ;i < 3; i++) {
            RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
            if (redPacket.getStock() > 0) {
                //通过version实现乐观锁
                int update = redPacketDao.decreaseRedPacketForVersion(redPacketId, redPacket.getVersion());
                if (update == 0) {
                    continue;
                }
                UserRedPacket userRedPacket = new UserRedPacket();
                userRedPacket.setRedPacketId(redPacketId);
                userRedPacket.setUserId(userId);
                userRedPacket.setAmount(redPacket.getAmount());
                userRedPacket.setNote("抢红包 " + redPacketId);
                int result = userRedPacketDao.grapRedPacket(userRedPacket);
                return result;
            } else {
                //一旦没有库存，则马上退出抢红包
                return FAILED;
            }
        }
        return FAILED;
    }
}
