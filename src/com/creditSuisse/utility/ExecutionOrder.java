/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Listener for selective execution of test-cases
 * @author Nicole Zhang
 * @since 11/26/2019
 */
public class ExecutionOrder implements IMethodInterceptor {

    /**
     * acquires all the test-cases that will execute and orders
     * them according to the input from the user
     * @param methods List of test methods to order
     * @param context test context containing info on the tests
     * @return ordered list of executable test methods
     */
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        Comparator<IMethodInstance> comparator = new Comparator<IMethodInstance>() {

            private int getLineNum(IMethodInstance instance) {
                int result = 0;
                String methodName = instance.getMethod().getConstructorOrMethod().getMethod().getName();
                String className = instance.getMethod().getConstructorOrMethod().getDeclaringClass().getCanonicalName();
                ClassPool pool = ClassPool.getDefault();
                try {
                    CtClass ctClass = pool.get(className);
                    CtMethod ctMethod = ctClass.getDeclaredMethod(methodName);
                    result = ctMethod.getMethodInfo().getLineNumber(0);
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
                return result;
            }

            public int compare(IMethodInstance instance1, IMethodInstance instance2) {
                return getLineNum(instance1) - getLineNum(instance2);
            }
        };
        IMethodInstance[] array = methods.toArray(new IMethodInstance[methods.size()]);
        Arrays.sort(array, comparator);
        return Arrays.asList(array);
    }
}
