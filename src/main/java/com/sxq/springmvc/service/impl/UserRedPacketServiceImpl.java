package com.sxq.springmvc.service.impl;

import com.sxq.springmvc.dao.RedPacketDao;
import com.sxq.springmvc.dao.UserRedPacketDao;
import com.sxq.springmvc.pojo.RedPacket;
import com.sxq.springmvc.pojo.UserRedPacket;
import com.sxq.springmvc.service.RedisRedPacketService;
import com.sxq.springmvc.service.UserRedPacketService;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

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


    @Autowired
    private RedisTemplate redisTemplate = null;

    @Autowired
    private RedisRedPacketService redisRedPacketService = null;

    //Lua脚本
    private String script = "local listKey = 'red_packet_list_'..KEYS[1] \n"
            + "local redPacket = 'red_packet_'..KEYS[1] \n"
            + "local stock = tonumber(redis.call('hget', redPacket, 'stock')) \n"
            + "if stock <= 0 then return 0 end \n"
            + "stock = stock -1 \n"
            + "redis.call('hset', redPacket, 'stock', tostring(stock)) \n"
            + "redis.call('rpush', listKey, ARGV[1]) \n"
            + "if stock == 0 then return 2 end \n"
            + "return 1 \n";

    //在缓存Lua脚本后，使用该变量保存Redis返回的32位的SHA1编码，使用它去执行缓存的Lua脚本
    private String sha1 = null;

    @Override
    public Long grapRedisPacketByRedis(Long redPacketId, Long userId) {
        //当前抢红包用户和日期信息
        String args = userId + "-" + System.currentTimeMillis();
        Long result = null;
        Jedis jedis = (Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
        try{
            System.out.println("上传Lua脚本");
            if(sha1 == null){
                System.out.println("SHA == NULL");
                sha1 = jedis.scriptLoad(script);
                System.out.println("SHA1:" + sha1);
            }
            Object res = jedis.evalsha(sha1, 1, redPacketId+"", args);
            result = (Long)res;
            //返回2是为最后一个红包，此时将抢红包信息通过异步保存到数据库中
            if(result == 2){
                //获取单个小红包金额
                String unitAmountStr = jedis.hget("red_packet_" + redPacketId, "unit_amount");
                Double unitAmount = Double.parseDouble(unitAmountStr);
                System.out.println("thread_name=" + Thread.currentThread().getName());
                redisRedPacketService.saveUserRedPacketByRedis(redPacketId, unitAmount);
            }
        }finally {
            if(jedis != null && jedis.isConnected()){
                jedis.close();
            }
        }
        return result;
    }
}
