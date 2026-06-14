package com.gosqu.restaurant.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;
import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory, ObjectMapper jacksonObjectMapper) {
        ObjectMapper om = jacksonObjectMapper.copy()
                .activateDefaultTypingAsProperty(
                        BasicPolymorphicTypeValidator.builder()
                                .allowIfSubType("com.gosqu.restaurant.")
                                .allowIfSubType(java.util.Collection.class)
                                .allowIfSubType(java.util.Map.class)
                                .build(),
                        ObjectMapper.DefaultTyping.NON_FINAL,
                        "@class");

        RedisSerializer<Object> serializer = new TypeAwareSerializer(om);
        var valuePair = RedisSerializationContext.SerializationPair.fromSerializer(serializer);
        var defaults = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(valuePair);

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaults)
                .withCacheConfiguration("restaurant", defaults.entryTtl(Duration.ofMinutes(5)))
                .withCacheConfiguration("menu", defaults.entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("restaurant-search", defaults.entryTtl(Duration.ofMinutes(2)))
                .build();
    }

    private record TypeAwareSerializer(ObjectMapper om) implements RedisSerializer<Object> {

        @Override
        public byte[] serialize(Object value) throws SerializationException {
            if (value == null) return new byte[0];
            try {
                return om.writeValueAsBytes(value);
            } catch (JsonProcessingException e) {
                throw new SerializationException("Serialization error", e);
            }
        }

        @Override
        public Object deserialize(byte[] bytes) throws SerializationException {
            if (bytes == null || bytes.length == 0) return null;
            try {
                return om.readValue(bytes, Object.class);
            } catch (IOException e) {
                throw new SerializationException("Deserialization error", e);
            }
        }
    }
}
