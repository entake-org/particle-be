package io.sdsolutions.particle.core.interceptor;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * The Class JsonHijackingInterceptor.
 *
 * Extends HandlerInterceptor to wrap responses of just a JSON array with a "data" variable to mitigate the
 * JSON hijacking vulnerability. Handling controller responses for List, Set, and Arrays.
 *
 * @author Mike Ringrose
 * @author Nick DiMola
 *
 * https://www.owasp.org/index.php/AJAX_Security_Cheat_Sheet#Always_return_JSON_with_an_Object_on_the_outside
 * https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#mvc-ann-controller-advice
 * http://blog.codeleak.pl/2013/11/controlleradvice-improvements-in-spring.html
 * http://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/mvc/method/annotation/ResponseBodyAdvice.html
 */
@Component
public class JsonHijackingInterceptor implements HandlerInterceptor {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonHijackingInterceptor.class);

    private static final String OWASP_PREFIX = "{\"data\":";
    private static final String OWASP_POSTFIX = "}";

    /**
     *
     * @param request Request
     * @param response Response
     * @param handler Handler
     * @return true always
     * @throws Exception exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (requiresJsonFix(handler)) {
            LOGGER.trace("Is list class.  Adding configured prefix.");

            response.getOutputStream().write(OWASP_PREFIX.getBytes());
        }

        // Return true - the execution chain should proceed with the next interceptor.
        return true;
    }

    /**
     *
     * @param request Request
     * @param response Response
     * @param handler Handler
     * @param modelAndView ModelAndView
     * @throws Exception exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (requiresJsonFix(handler)) {
            LOGGER.trace("Is list class.  Adding configured postfix.");

            response.getOutputStream().write(OWASP_POSTFIX.getBytes());
        }
    }

    /**
     * Requires JSON fix.
     *
     * @param handler the handler method
     * @return true if the return type is a List, Set, or Array.
     */
    private boolean requiresJsonFix(Object handler) {
        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            LOGGER.debug("JsonHijackingInterceptor.preHandle handlerMethod: {}", method);

            Class<?> returnTypeClass = method.getReturnType();
            LOGGER.debug("returnTypeClass: {}", returnTypeClass);

            return returnTypeClass.equals(List.class) || returnTypeClass.equals(Set.class) || returnTypeClass.equals(Array.class) || returnTypeClass.isArray();
        }

        return false;
    }
}
