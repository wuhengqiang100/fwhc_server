package com.kexin.admin;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kexin.admin.component.ScheduledComponent;
import com.kexin.admin.entity.pojo.User;
import com.kexin.admin.entity.tables.MachineWarning;
import com.kexin.admin.entity.vo.monitor.Monitor;
import com.kexin.admin.entity.vo.redis.RedisMessage;
import com.kexin.common.util.DateUtil.DateUtil;
import com.kexin.common.util.redis.RedisUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

@SpringBootTest
public class RedisSpringBootTests {

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    ScheduledComponent scheduledComponent;//定时组件

    @Test
    public void sendMachineAlert() throws JsonProcessingException, ParseException {
        Random rm = new Random();
        ObjectMapper mapper = new ObjectMapper();

        RedisMessage redisMessage=new RedisMessage(1,String.valueOf(rm.nextInt(2)+1),"设备1异常", DateUtil.dateToString(new Date()));
        String json = mapper.writeValueAsString(redisMessage);

        MachineWarning machineWarning=new MachineWarning();
        machineWarning.setMachineId(redisMessage.getMachineId());
        machineWarning.setLogType(redisMessage.getLogType());
        machineWarning.setNote(redisMessage.getNote());
        machineWarning.setLogDate(DateUtil.stringToDate(redisMessage.getLogDate()));
        redisTemplate.convertAndSend("machineAlert", json);
    }

    @Test
    public  void jaksonTest() throws JsonProcessingException {
//        '{"machineId":1,"logType":"1","note":"设备1异常"}';

        String json = "{\"machineId\":1,\"logType\":\"1\",\"note\":\"设备1异常\"}";

        /**
         * ObjectMapper支持从byte[]、File、InputStream、字符串等数据的JSON反序列化。
         */
        ObjectMapper mapper = new ObjectMapper();
        RedisMessage redisMessage = mapper.readValue(json, RedisMessage.class);
        System.out.println(redisMessage);
    }
    @Test
    public void testRedisUtilSetMap() {
        Monitor monitor1=new Monitor(
                "1","产品1","模板1","班组1","2020-6-3 15:43:60"
                ,"2020-6-3 15:43:60",10000,9999,1,222, (float) 0.88,
                "5000z",1,"设备1",0);
        Monitor monitor2=new Monitor(
                "2","产品2","模板2","班组2","2020-6-3 15:43:60"
                ,"",10000,9999,1,222, (float) 0.88,
                "5000z",2,"设备2",1);
        Monitor monitor3=new Monitor(
                "3","产品3","模板3","班组3","2020-6-3 15:43:60"
                ,"2020-6-3 15:43:60",10000,9999,1,222, (float) 0.88,
                "5000z",3,"设备3",1);
        Monitor monitor4=new Monitor(
                "4","产品4","模板4","班组4","2020-6-3 15:43:60"
                ,"2020-6-3 15:43:60",10000,9999,1,222, (float) 0.88,
                "5000z",4,"设备4",1);
        Monitor monitor5=new Monitor(
                "5","产品5","模板5","班组5","2020-6-3 15:43:60"
                ,"2020-6-3 15:43:60",10000,9999,1,222, (float) 0.88,
                "5000z",5,"设备5",1);
        Monitor monitor6=new Monitor(
                "6","产品6","模板6","班组6","2020-6-3 15:43:60"
                ,"2020-6-3 15:43:60",10000,9999,1,222, (float) 0.88,
                "5000z",6,"设备6",2);
//        redisUtil.hmset("m1", BeanMap.create(monitor));
        redisUtil.hmset("machine:1", BeanMap.create(monitor1));
        redisUtil.hmset("machine:2", BeanMap.create(monitor2));
        redisUtil.hmset("machine:3", BeanMap.create(monitor3));
        redisUtil.hmset("machine:4", BeanMap.create(monitor4));
        redisUtil.hmset("machine:5", BeanMap.create(monitor5));
        redisUtil.hmset("machine:6", BeanMap.create(monitor6));
    }

    @Test
    public void testRedisUtilGetMap() {

        Map<Object, Object> hmget = redisUtil.hmget("m1");
        Map<Object, Object> hmget1 = redisUtil.hmget("machine:1");
        ObjectMapper objectMapper = new ObjectMapper();
        Monitor monitor=objectMapper.convertValue(hmget1,Monitor.class);
        List<Object> machine = redisUtil.lGet("machine", 0, -1);
        Object name = redisUtil.get("name");

        System.out.println("");
    }



    @Test
    void contextLoads() {
        // redisTemplate 操作不同的数据类型，api和我们的指令是一样的
        // opsForValue 操作字符串 类似String
        // opsForList 操作List 类似List
        // opsForSet
        // opsForHash
        // opsForZSet
        // opsForGeo
        // opsForHyperLogLog
        // 除了进本的操作，我们常用的方法都可以直接通过redisTemplate操作，比如事务，和基本CRUD
        // 获取redis的连接对象
        // RedisConnection connection =
        redisTemplate.getConnectionFactory().getConnection();
        // connection.flushDb();
        // connection.flushAll();
        redisTemplate.opsForValue().set("mykey", "看源码学redis");
        System.out.println(redisTemplate.opsForValue().get("mykey"));
    }


    @Test
    public void test() throws JsonProcessingException {
        User user =new User("巫恒强",23);
//        String jsonUser=new ObjectMapper().writeValueAsString(user);//jdk序列化
        redisTemplate.opsForValue().set("user",user);
        System.out.println(redisTemplate.opsForValue().get("user"));
    }


}
