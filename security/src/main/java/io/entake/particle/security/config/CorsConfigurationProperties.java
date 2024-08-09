package io.entake.particle.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix="access.control")
public class CorsConfigurationProperties {

    private List<String> origin = null;

    private List<String> methods = null;

    private Long maxage = null;

    private List<String> allowheaders = null;

    private List<String> exposeheaders = null;

    public List<String> getOrigin() {
        return origin;
    }

    public void setOrigin(List<String> origin) {
        this.origin = origin;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public Long getMaxage() {
        return maxage;
    }

    public void setMaxage(Long maxage) {
        this.maxage = maxage;
    }

    public List<String> getAllowheaders() {
        return allowheaders;
    }

    public void setAllowheaders(List<String> allowheaders) {
        this.allowheaders = allowheaders;
    }

    public List<String> getExposeheaders() {
        return exposeheaders;
    }

    public void setExposeheaders(List<String> exposeheaders) {
        this.exposeheaders = exposeheaders;
    }

}
