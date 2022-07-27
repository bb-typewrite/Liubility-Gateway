package org.liubility.gateway.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: Jdragon
 * @email: 1061917196@qq.com
 * @Date: 2020.03.18 21:30
 * @Description: swagger2的api文档配置
 */
@Configuration
@EnableOpenApi
@EnableKnife4j
public class SwaggerConfig{

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build()
                .globalRequestParameters(getGlobalRequestParameters());//注意这里
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("BB-Typing API 文档")
                .description("BB-Typing API 网关接口")
                .version("1.0.0")
                .build();
    }

    private List<RequestParameter> getGlobalRequestParameters() {
        List<RequestParameter> parameters = new ArrayList<>();
        parameters.add(new RequestParameterBuilder()
                .name("Authorization")
                .description("令牌")
                .required(false)
                .in(ParameterType.HEADER)
                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                .build());
        return parameters;
    }

    @Component
    @Primary
    public static class MySwaggerResourcesProvider implements SwaggerResourcesProvider {

        /**
         * swagger3默认的url后缀
         */
        private static final String SWAGGER2URL = "/v3/api-docs"; //要使用ui的话 改成v2 不然会出bug  比如有的地方 没有输入框
        /**
         * 网关路由
         */
        private final RouteLocator routeLocator;

        /**
         * 网关应用名称
         */
        @Value("${spring.application.name}")
        private String self;

        @Autowired
        public MySwaggerResourcesProvider(RouteLocator routeLocator) {
            this.routeLocator = routeLocator;
        }

        /**
         * 对于gateway来说这块比较重要 让swagger能找到对应的服务
         */
        @Override
        public List<SwaggerResource> get() {
            List<SwaggerResource> resources = new ArrayList<>();
            List<String> routeHosts = new ArrayList<>();
            // 获取所有可用的host：serviceId
            routeLocator.getRoutes().filter(route -> route.getUri().getHost() != null)
                    .filter(route -> !self.equals(route.getUri().getHost()))
                    .subscribe(route -> routeHosts.add(route.getUri().getHost()));

            // 记录已经添加过的server
            Set<String> dealed = new HashSet<>();
            routeHosts.forEach(instance -> {
                // 拼接url
                String url = "/" + instance.toLowerCase() + SWAGGER2URL;
                if (!dealed.contains(url)) {
                    dealed.add(url);
                    SwaggerResource swaggerResource = new SwaggerResource();
                    swaggerResource.setUrl(url);
                    swaggerResource.setName(instance);
                    swaggerResource.setSwaggerVersion("3.0");
                    resources.add(swaggerResource);
                }
            });
            return resources;
        }
    }
}
