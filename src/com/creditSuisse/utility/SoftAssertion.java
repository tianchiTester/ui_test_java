/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility;

import org.testng.asserts.Assertion;
import org.testng.asserts.IAssert;
import org.testng.collections.Maps;
import java.util.Map;

/**
 * Asserter for automation testing conditionals in groups
 * @author Nicole Zhang
 * @since 11/20/2019
 * @todo This class needs additional method documentation
 */
public class SoftAssertion extends Assertion {

    private Map<AssertionError, IAssert<?>> errors = Maps.newLinkedHashMap();
    private boolean latestCheck = false;
    private String latestErrorMessage;

    public boolean getLatestCheck() {
        return this.latestCheck;
    }

    public void setLatestCheck(boolean status) {
        this.latestCheck = status;
    }

    public String getLatestErrorMessage() {
        return this.latestErrorMessage;
    }

    /**
     * to do
     * @param assertion to do
     * @return to do
     */
    protected void doAssert(IAssert<?> assertion) {
        this.onBeforeAssert(assertion);
        try {
            assertion.doAssert();
            this.onAssertSuccess(assertion);
        } catch (AssertionError error) {
            this.onAssertFailure(assertion, error);
            this.errors.put(error, assertion);
        } finally {
            this.onAfterAssert(assertion);
        }
    }

    /**
     * to do
     * @return to do
     */
    public void assertAll() {
        if (!this.errors.isEmpty()) {
            StringBuilder var1 = new StringBuilder("The following asserts failed:");
            boolean var2 = true;
            for (Map.Entry<AssertionError, IAssert<?>> assertionErrorIAssertEntry : this.errors.entrySet()) {
                if (var2)
                    var2 = false;
                else
                    var1.append(",");
                var1.append("\n\t");
                var1.append(((AssertionError) ((Map.Entry) assertionErrorIAssertEntry).getKey()).getMessage());
            }
            errors = Maps.newLinkedHashMap();
            throw new AssertionError(var1.toString());
        }
    }
}
