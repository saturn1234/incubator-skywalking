

package org.apache.skywalking.apm.plugin.cdal.define;


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




public class CdalInterceptor implements InstanceMethodsAroundInterceptor {


    private static final ILog logger = LogManager.getLogger(CdalInterceptor.class);

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
                             MethodInterceptResult result) throws Throwable {
        logger.info("cdal beforeMethod");
        System.out.println("cdal beforeMethod");
        String oriSQL = (String)allArguments[0];
        logger.info("oriSQL:" + oriSQL +",currentThread:" + Thread.currentThread());
        System.out.println("oriSQL:" + oriSQL);





        ContextCarrier contextCarrier = new ContextCarrier();
        CarrierItem next = contextCarrier.items();
        while (next.hasNext()) {
            next = next.next();
            next.setHeadValue("cdalHead");
        }

        AbstractSpan span = ContextManager.createEntrySpan("cdalExecute", contextCarrier);
        span.setComponent(ComponentsDefine.CDAL);
        SpanLayer.asRPCFramework(span);
        Tags.DB_STATEMENT.set(span , oriSQL);
//        SQLType sqlType = (SQLType)allArguments[0];
//        ContextManager.createLocalSpan("/SJDBC/TRUNK/" + sqlType.name()).setComponent(ComponentsDefine.SHARDING_JDBC);
    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
                              Object ret) throws Throwable {
        logger.info("cdal afterMethod");
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
