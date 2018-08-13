package org.apache.skywalking.apm.plugin.cdal.define;

import io.mycat.route.RouteResultsetNode;
import org.apache.skywalking.apm.agent.core.context.CarrierItem;
import org.apache.skywalking.apm.agent.core.context.ContextCarrier;
import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.tag.Tags;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.skywalking.apm.network.trace.component.ComponentsDefine;

import java.lang.reflect.Method;
import java.util.Random;

public class CdalRealInterceptor implements InstanceMethodsAroundInterceptor {


    private static final ILog logger = LogManager.getLogger(CdalRealInterceptor.class);

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
                             MethodInterceptResult result) throws Throwable {
        logger.info("cdal real beforeMethod");
        if(null == allArguments[2]){
            return;
        }
        RouteResultsetNode rsNode = (RouteResultsetNode)allArguments[2];
        logger.info("currentThread:" + Thread.currentThread() + ",oriSQL:" + rsNode.getStatement());

        AbstractSpan span = ContextManager.createExitSpan("jdbcExecute in:" + rsNode.getName()   ,rsNode.getName()+ ":3306");
        span.setComponent(ComponentsDefine.MYSQL_JDBC_DRIVER);
        SpanLayer.asDB(span);
        Tags.DB_STATEMENT.set(span , rsNode.getStatement());
//        SQLType sqlType = (SQLType)allArguments[0];
//        ContextManager.createLocalSpan("/SJDBC/TRUNK/" + sqlType.name()).setComponent(ComponentsDefine.SHARDING_JDBC);
    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
                              Object ret) throws Throwable {
        logger.info("cdal real afterMethod");
        AbstractSpan span = ContextManager.activeSpan();
        ContextManager.stopSpan();

        return ret;
    }

    @Override public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments,
                                                Class<?>[] argumentsTypes, Throwable t) {
        logger.info("cdal handleMethodException");
        System.out.println("cdal handleMethodException");
        ContextManager.activeSpan().errorOccurred().log(t);
    }
}