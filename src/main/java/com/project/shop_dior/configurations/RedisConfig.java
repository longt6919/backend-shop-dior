package com.project.shop_dior.configurations;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {
    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);
    @Value("${spring.data.redis.host}") // Read 'spring.data.redis.host' property from application.yml
    private String redisHost;

    @Value("${spring.data.redis.port}") // Read 'spring.data.redis.port' property from application.yml
    private int redisPort;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        logger.info(String.format("redisHost = %s, redisPort = %d", redisHost, redisPort));
        RedisStandaloneConfiguration configuration =
                new RedisStandaloneConfiguration(redisHost, redisPort);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory cf) {
        RedisTemplate<String, Object> t = new RedisTemplate<>();
        t.setConnectionFactory(cf);
        var keySer = new StringRedisSerializer();
        var valSer = new GenericJackson2JsonRedisSerializer(); // ✅ đủ module thời gian
        t.setKeySerializer(keySer);
        t.setValueSerializer(valSer);
        t.setHashKeySerializer(keySer);
        t.setHashValueSerializer(valSer);
        t.afterPropertiesSet();
        return t;
    }

}
