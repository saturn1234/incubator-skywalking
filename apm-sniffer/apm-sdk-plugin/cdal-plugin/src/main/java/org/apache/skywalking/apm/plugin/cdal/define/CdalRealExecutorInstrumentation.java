package org.apache.skywalking.apm.plugin.cdal.define;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.NameMatch;

import static net.bytebuddy.matcher.ElementMatchers.any;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class CdalRealExecutorInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String ENHANCE_CLASS = "io.mycat.backend.datasource.PhysicalDBNode";

    private static final String EXECUTOR_ENGINE_CONSTRUCTOR_INTERCEPTOR_CLASS = "org.apache.skywalking.apm.plugin.cdal.define.ExecutorEngineConstructorInterceptor";

    private static final String EXECUTE_INTERCEPTOR_CLASS = "org.apache.skywalking.apm.plugin.cdal.define.CdalRealInterceptor";

    private static final String ASYNC_EXECUTE_INTERCEPTOR_CLASS = "org.apache.skywalking.apm.plugin.cdal.define.AsyncExecuteInterceptor";

    @Override
    protected ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return  new ConstructorInterceptPoint[] {
                new ConstructorInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getConstructorMatcher() {
                        return any();
                    }

                    @Override
                    public String getConstructorInterceptor() {
                        return EXECUTOR_ENGINE_CONSTRUCTOR_INTERCEPTOR_CLASS;
                    }
                }
        };
    }

    @Override
    protected InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named("getConnection");
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return EXECUTE_INTERCEPTOR_CLASS;
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return false;
                    }
                },
//            new InstanceMethodsInterceptPoint() {
//                @Override
//                public ElementMatcher<MethodDescription> getMethodsMatcher() {
//                    return named("asyncExecute");
//                }
//
//                @Override
//                public String getMethodsInterceptor() {
//                    return ASYNC_EXECUTE_INTERCEPTOR_CLASS;
//                }
//
//                @Override
//                public boolean isOverrideArgs() {
//                    return false;
//                }
//            }
        };
    }

    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(ENHANCE_CLASS);
    }
}
