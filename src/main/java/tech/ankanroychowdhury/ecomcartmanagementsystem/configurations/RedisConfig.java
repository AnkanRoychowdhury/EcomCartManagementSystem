package tech.ankanroychowdhury.ecomcartmanagementsystem.configurations;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.ResponseEntity;
import tech.ankanroychowdhury.ecomcartmanagementsystem.dtos.CartDto;
import tech.ankanroychowdhury.ecomcartmanagementsystem.entities.Cart;


@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisSerializer redisStringSerializer() {
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        return stringRedisSerializer;
    }

    @Bean
    public RedisTemplate<String, ResponseEntity<CartDto>> redisTemplate(RedisConnectionFactory redisConnectionFactory, RedisSerializer redisSerializer) {
        RedisTemplate<String, ResponseEntity<CartDto>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.afterPropertiesSet();
        redisTemplate.setDefaultSerializer(redisSerializer);
        return redisTemplate;
    }
}
