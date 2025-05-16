package org.example.dictionarybot.config;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisProperties properties;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
//        TODO future
//        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration(redisProperties.getSentinel().getMaster(), new HashSet<>(redisProperties.getSentinel().getNodes()));
//        redisSentinelConfiguration.setPassword(redisProperties.getSentinel().getPassword());
        RedisStandaloneConfiguration redisSentinelConfiguration = new RedisStandaloneConfiguration();
        redisSentinelConfiguration.setHostName(properties.getHost());
        redisSentinelConfiguration.setPort(properties.getPort());
        redisSentinelConfiguration.setPassword(properties.getPassword());
        return new JedisConnectionFactory(redisSentinelConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }

}
