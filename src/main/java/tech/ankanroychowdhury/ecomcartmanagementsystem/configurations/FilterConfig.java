package tech.ankanroychowdhury.ecomcartmanagementsystem.configurations;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.ankanroychowdhury.ecomcartmanagementsystem.filters.ThrottlingFilter;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ThrottlingFilter> rateLimitingFilter() {
        FilterRegistrationBean<ThrottlingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ThrottlingFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
