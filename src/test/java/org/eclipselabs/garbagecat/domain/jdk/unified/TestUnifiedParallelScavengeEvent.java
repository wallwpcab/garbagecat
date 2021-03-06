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
package org.eclipselabs.garbagecat.domain.jdk.unified;

import java.util.ArrayList;
import java.util.List;

import org.eclipselabs.garbagecat.util.jdk.JdkRegEx;
import org.eclipselabs.garbagecat.util.jdk.JdkUtil;
import org.eclipselabs.garbagecat.util.jdk.JdkUtil.LogEventType;
import org.eclipselabs.garbagecat.util.jdk.unified.UnifiedUtil;
import org.junit.Assert;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:mmillson@redhat.com">Mike Millson</a>
 * 
 */
public class TestUnifiedParallelScavengeEvent extends TestCase {

    public void testPreprocessed() {
        String logLine = "[0.031s][info][gc,start     ] GC(0) Pause Young (Allocation Failure) PSYoungGen: "
                + "512K->464K(1024K) PSOldGen: 0K->8K(512K) Metaspace: 120K->120K(1056768K) 0M->0M(1M) 1.195ms "
                + "User=0.01s Sys=0.01s Real=0.00s";
        Assert.assertTrue(
                "Log line not recognized as " + JdkUtil.LogEventType.UNIFIED_PARALLEL_SCAVENGE.toString() + ".",
                UnifiedParallelScavengeEvent.match(logLine));
        UnifiedParallelScavengeEvent event = new UnifiedParallelScavengeEvent(logLine);
        Assert.assertEquals("Event name incorrect.", JdkUtil.LogEventType.UNIFIED_PARALLEL_SCAVENGE.toString(),
                event.getName());
        Assert.assertEquals("Time stamp not parsed correctly.", 31, event.getTimestamp());
        Assert.assertTrue("Trigger not parsed correctly.",
                event.getTrigger().matches(JdkRegEx.TRIGGER_ALLOCATION_FAILURE));
        Assert.assertEquals("Young begin size not parsed correctly.", 512, event.getYoungOccupancyInit());
        Assert.assertEquals("Young end size not parsed correctly.", 464, event.getYoungOccupancyEnd());
        Assert.assertEquals("Young available size not parsed correctly.", 1024, event.getYoungSpace());
        Assert.assertEquals("Old begin size not parsed correctly.", 0, event.getOldOccupancyInit());
        Assert.assertEquals("Old end size not parsed correctly.", 8, event.getOldOccupancyEnd());
        Assert.assertEquals("Old allocation size not parsed correctly.", 512, event.getOldSpace());
        Assert.assertEquals("Perm gen begin size not parsed correctly.", 120, event.getPermOccupancyInit());
        Assert.assertEquals("Perm gen end size not parsed correctly.", 120, event.getPermOccupancyEnd());
        Assert.assertEquals("Perm gen allocation size not parsed correctly.", 1056768, event.getPermSpace());
        Assert.assertEquals("Duration not parsed correctly.", 1195, event.getDuration());
        Assert.assertEquals("User time not parsed correctly.", 1, event.getTimeUser());
        Assert.assertEquals("Real time not parsed correctly.", 0, event.getTimeReal());
        Assert.assertEquals("Parallelism not calculated correctly.", Integer.MAX_VALUE, event.getParallelism());
    }

    public void testIdentityEventType() {
        String logLine = "[0.031s][info][gc,start     ] GC(0) Pause Young (Allocation Failure) PSYoungGen: "
                + "512K->464K(1024K) PSOldGen: 0K->8K(512K) Metaspace: 120K->120K(1056768K) 0M->0M(1M) 1.195ms "
                + "User=0.01s Sys=0.01s Real=0.00s";
        Assert.assertEquals(JdkUtil.LogEventType.UNIFIED_PARALLEL_SCAVENGE + "not identified.",
                JdkUtil.LogEventType.UNIFIED_PARALLEL_SCAVENGE, JdkUtil.identifyEventType(logLine));
    }

    public void testParseLogLine() {
        String logLine = "[0.031s][info][gc,start     ] GC(0) Pause Young (Allocation Failure) PSYoungGen: "
                + "512K->464K(1024K) PSOldGen: 0K->8K(512K) Metaspace: 120K->120K(1056768K) 0M->0M(1M) 1.195ms "
                + "User=0.01s Sys=0.01s Real=0.00s";
        Assert.assertTrue(JdkUtil.LogEventType.UNIFIED_PARALLEL_SCAVENGE.toString() + " not parsed.",
                JdkUtil.parseLogLine(logLine) instanceof UnifiedParallelScavengeEvent);
    }

    public void testIsBlocking() {
        String logLine = "[0.031s][info][gc,start     ] GC(0) Pause Young (Allocation Failure) PSYoungGen: "
                + "512K->464K(1024K) PSOldGen: 0K->8K(512K) Metaspace: 120K->120K(1056768K) 0M->0M(1M) 1.195ms "
                + "User=0.01s Sys=0.01s Real=0.00s";
        Assert.assertTrue(JdkUtil.LogEventType.UNIFIED_PARALLEL_SCAVENGE.toString() + " not indentified as blocking.",
                JdkUtil.isBlocking(JdkUtil.identifyEventType(logLine)));
    }

    public void testHydration() {
        LogEventType eventType = JdkUtil.LogEventType.UNIFIED_PARALLEL_SCAVENGE;
        String logLine = "[0.031s][info][gc,start     ] GC(0) Pause Young (Allocation Failure) PSYoungGen: "
                + "512K->464K(1024K) PSOldGen: 0K->8K(512K) Metaspace: 120K->120K(1056768K) 0M->0M(1M) 1.195ms "
                + "User=0.01s Sys=0.01s Real=0.00s";
        long timestamp = 27091;
        int duration = 0;
        Assert.assertTrue(JdkUtil.LogEventType.UNIFIED_PARALLEL_SCAVENGE.toString() + " not parsed.", JdkUtil
                .hydrateBlockingEvent(eventType, logLine, timestamp, duration) instanceof UnifiedParallelScavengeEvent);
    }

    public void testReportable() {
        Assert.assertTrue(JdkUtil.LogEventType.UNIFIED_PARALLEL_SCAVENGE.toString() + " not indentified as reportable.",
                JdkUtil.isReportable(JdkUtil.LogEventType.UNIFIED_PARALLEL_SCAVENGE));
    }

    public void testUnified() {
        List<LogEventType> eventTypes = new ArrayList<LogEventType>();
        eventTypes.add(LogEventType.UNIFIED_PARALLEL_SCAVENGE);
        Assert.assertTrue(JdkUtil.LogEventType.UNIFIED_PARALLEL_SCAVENGE.toString() + " not indentified as unified.",
                UnifiedUtil.isUnifiedLogging(eventTypes));
    }

    public void testLogLineWhitespaceAtEnd() {
        String logLine = "[0.031s][info][gc,start     ] GC(0) Pause Young (Allocation Failure) PSYoungGen: "
                + "512K->464K(1024K) PSOldGen: 0K->8K(512K) Metaspace: 120K->120K(1056768K) 0M->0M(1M) 1.195ms "
                + "User=0.01s Sys=0.01s Real=0.00s    ";
        Assert.assertTrue(
                "Log line not recognized as " + JdkUtil.LogEventType.UNIFIED_PARALLEL_SCAVENGE.toString() + ".",
                UnifiedParallelScavengeEvent.match(logLine));
    }

    public void testLogLine7SpacesAfterStart() {
        String logLine = "[15.030s][info][gc,start       ] GC(1199) Pause Young (Allocation Failure) PSYoungGen: "
                + "20544K->64K(20992K) PSOldGen: 15496K->15504K(17920K) Metaspace: 3779K->3779K(1056768K) "
                + "35M->15M(38M) 0.402ms User=0.00s Sys=0.00s Real=0.00s";
        Assert.assertTrue(
                "Log line not recognized as " + JdkUtil.LogEventType.UNIFIED_PARALLEL_SCAVENGE.toString() + ".",
                UnifiedParallelScavengeEvent.match(logLine));
    }

    public void testPreprocessedParallelCompactingOld() {
        String logLine = "[0.029s][info][gc,start     ] GC(0) Pause Young (Allocation Failure) PSYoungGen: "
                + "512K->432K(1024K) ParOldGen: 0K->8K(512K) Metaspace: 121K->121K(1056768K) 0M->0M(1M) 0.762ms "
                + "User=0.00s Sys=0.00s Real=0.00s";
        Assert.assertTrue(
                "Log line not recognized as " + JdkUtil.LogEventType.UNIFIED_PARALLEL_SCAVENGE.toString() + ".",
                UnifiedParallelScavengeEvent.match(logLine));
        UnifiedParallelScavengeEvent event = new UnifiedParallelScavengeEvent(logLine);
        Assert.assertEquals("Event name incorrect.", JdkUtil.LogEventType.UNIFIED_PARALLEL_SCAVENGE.toString(),
                event.getName());
        Assert.assertEquals("Time stamp not parsed correctly.", 29, event.getTimestamp());
        Assert.assertTrue("Trigger not parsed correctly.",
                event.getTrigger().matches(JdkRegEx.TRIGGER_ALLOCATION_FAILURE));
        Assert.assertEquals("Young begin size not parsed correctly.", 512, event.getYoungOccupancyInit());
        Assert.assertEquals("Young end size not parsed correctly.", 432, event.getYoungOccupancyEnd());
        Assert.assertEquals("Young available size not parsed correctly.", 1024, event.getYoungSpace());
        Assert.assertEquals("Old begin size not parsed correctly.", 0, event.getOldOccupancyInit());
        Assert.assertEquals("Old end size not parsed correctly.", 8, event.getOldOccupancyEnd());
        Assert.assertEquals("Old allocation size not parsed correctly.", 512, event.getOldSpace());
        Assert.assertEquals("Perm gen begin size not parsed correctly.", 121, event.getPermOccupancyInit());
        Assert.assertEquals("Perm gen end size not parsed correctly.", 121, event.getPermOccupancyEnd());
        Assert.assertEquals("Perm gen allocation size not parsed correctly.", 1056768, event.getPermSpace());
        Assert.assertEquals("Duration not parsed correctly.", 762, event.getDuration());
        Assert.assertEquals("User time not parsed correctly.", 0, event.getTimeUser());
        Assert.assertEquals("Real time not parsed correctly.", 0, event.getTimeReal());
        Assert.assertEquals("Parallelism not calculated correctly.", 100, event.getParallelism());
    }
}
