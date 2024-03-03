package yout.component.dlock.service;

import yout.component.dlock.annotation.DLock;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 业务相关用户配置的Key获取
 * @author: yout0703
 * @date: 2023-06-22
 */
public class BusinessKeyProvider {

    private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    private final ExpressionParser parser = new SpelExpressionParser();

    public String getKeyName(JoinPoint joinPoint, DLock lock) {
        Method method = getMethod(joinPoint);
        List<String> definitionKeyValues = getSpelDefinitionKey(lock.keys(), method, joinPoint.getArgs());
        return String.join("-", definitionKeyValues);
    }

    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            try {
                method =
                        joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(), method.getParameterTypes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return method;
    }

    /**
     * 获得SpEL表达式配置的key的值
     */
    private List<String> getSpelDefinitionKey(String[] definitionKeys, Method method, Object[] parameterValues) {
        List<String> definitionKeyValues = new ArrayList<>();
        for (String definitionKey : definitionKeys) {
            if (!ObjectUtils.isEmpty(definitionKey)) {
                EvaluationContext context =
                        new MethodBasedEvaluationContext(method.getDeclaringClass(), method, parameterValues, nameDiscoverer);
                Object objKey = parser.parseExpression(definitionKey).getValue(context);
                definitionKeyValues.add(ObjectUtils.nullSafeToString(objKey));
            }
        }
        return definitionKeyValues;
    }
}
