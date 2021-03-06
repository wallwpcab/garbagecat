/**********************************************************************************************************************
 * garbagecat                                                                                                         *
 *                                                                                                                    *
 * Copyright (c) 2008-2020 Mike Millson                                                                               *
 *                                                                                                                    * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse *
 * Public License v1.0 which accompanies this distribution, and is available at                                       *
 * http://www.eclipse.org/legal/epl-v10.html.                                                                         *
 *                                                                                                                    *
 * Contributors:                                                                                                      *
 *    Mike Millson - initial API and implementation                                                                   *
 *********************************************************************************************************************/
package org.eclipselabs.garbagecat.domain;

import org.eclipselabs.garbagecat.util.jdk.JdkUtil;
import org.junit.Assert;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:mmillson@redhat.com">Mike Millson</a>
 * 
 */
public class TestBlankLineEvent extends TestCase {

    public void testParseLogLine() {
        String logLine = "";
        Assert.assertTrue(JdkUtil.LogEventType.BLANK_LINE.toString() + " not parsed.",
                JdkUtil.parseLogLine(logLine) instanceof BlankLineEvent);
    }

    public void testReportable() {
        String logLine = "";
        Assert.assertFalse(JdkUtil.LogEventType.BLANK_LINE.toString() + " incorrectly indentified as reportable.",
                JdkUtil.isReportable(JdkUtil.identifyEventType(logLine)));
    }

    public void testLogLine() {
        String logLine = "";
        Assert.assertTrue("Log line not recognized as " + JdkUtil.LogEventType.BLANK_LINE.toString() + ".",
                BlankLineEvent.match(logLine));
    }
}
