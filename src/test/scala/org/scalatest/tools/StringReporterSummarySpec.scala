/*
 * Copyright 2001-2013 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalatest.tools

import org.scalatest.UnitSpec
import org.scalatest.events.Summary
import PrintReporter.makeDurationString
import StringReporter.summaryFragments
import org.scalatest.Resources
import org.scalatest.events.TestFailed
import org.scalatest.events.TestCanceled
import org.scalatest.exceptions.TestFailedException
import org.scalatest.exceptions.TestCanceledException
import org.scalatest.events.ExceptionalEvent
import org.scalatest.events.Ordinal
import org.scalatest.events.IndentedText
import org.scalatest.events.SeeStackDepthException
import org.scalatest.SharedHelpers._

class StringReporterSummarySpec extends UnitSpec {

  val summaryWithOneFailedTest =
    new Summary(
      testsSucceededCount = 0,
      testsFailedCount = 1,
      testsIgnoredCount = 0,
      testsPendingCount = 0,
      testsCanceledCount = 0,
      suitesCompletedCount = 1,
      suitesAbortedCount = 0,
      scopesPendingCount = 0
    )

  val summaryWithOneFailedAndOneCanceledTest =
    new Summary(
      testsSucceededCount = 0,
      testsFailedCount = 1,
      testsIgnoredCount = 0,
      testsPendingCount = 0,
      testsCanceledCount = 1,
      suitesCompletedCount = 1,
      suitesAbortedCount = 0,
      scopesPendingCount = 0
    )

  val testFailedException: Throwable =
    try { assert(false, "I meant to do that!"); new Exception("Not reached") } // TODO: Duh, Nothing! Don't need the new Exception. Try it.
    catch { case e: TestFailedException => e }
  val tfeLineNumber = thisLineNumber - 2

  val testCanceledException: Throwable =
    try { assume(false, "I meant to do that!"); new Exception("Not reached") }
    catch { case e: TestCanceledException => e }
  val tceLineNumber = thisLineNumber - 2

  val firstOrdinal = new Ordinal(123)
  val secondOrdinal = firstOrdinal.next

  val justOneTestFailed: Vector[ExceptionalEvent] = 
    Vector(
      TestFailed(
        ordinal = firstOrdinal,
        message = "I meant to do that!",
        suiteName = "StringReporterSummarySpec",
        suiteId = "org.scalatest.tools.StringReporterSummarySpec",
        suiteClassName = Some("org.scalatest.tools.StringReporterSummarySpec"),
        testName = "the summaryFragments method should fail on purpose",
        testText = "should fail on purpose",
        recordedEvents = Vector.empty,
        throwable = Some(testFailedException),
        duration = Some(16),
        formatter = Some(IndentedText("THIS SHOULD NOT BE USED", "SHOULD NOT BE USED", 1)),
        location = Some(SeeStackDepthException),
        rerunner = Some("org.scalatest.tools.StringReporterSummarySpec"),
        payload = None,
        threadName = "Thread-10",
        timeStamp = 1369965123278L
      )
    )

  val oneTestFailedAndOneTestCanceled: Vector[ExceptionalEvent] = 

    Vector(
      TestFailed(
        ordinal = firstOrdinal,
        message = "I meant to do that!",
        suiteName = "StringReporterSummarySpec",
        suiteId = "org.scalatest.tools.StringReporterSummarySpec",
        suiteClassName = Some("org.scalatest.tools.StringReporterSummarySpec"),
        testName = "the summaryFragments method should fail on purpose",
        testText = "should fail on purpose",
        recordedEvents = Vector.empty,
        throwable = Some(testFailedException),
        duration = Some(16),
        formatter = Some(IndentedText("THIS SHOULD NOT BE USED", "SHOULD NOT BE USED", 1)),
        location = Some(SeeStackDepthException),
        rerunner = Some("org.scalatest.tools.StringReporterSummarySpec"),
        payload = None,
        threadName = "Thread-10",
        timeStamp = 1369965123278L
      ),
      TestCanceled(
        ordinal = secondOrdinal,
        message = "I meant to do that!",
        suiteName = "StringReporterSummarySpec",
        suiteId = "org.scalatest.tools.StringReporterSummarySpec",
        suiteClassName = Some("org.scalatest.tools.StringReporterSummarySpec"),
        testName = "the summaryFragments method should cancel on purpose",
        testText = "should cancel on purpose",
        recordedEvents = Vector.empty,
        throwable = Some(testCanceledException),
        duration = Some(15),
        formatter = Some(IndentedText("THIS SHOULD NOT BE USED", "SHOULD NOT BE USED", 1)),
        location = Some(SeeStackDepthException),
        // rerunner = Some("org.scalatest.tools.StringReporterSummarySpec"),
        payload = None,
        threadName = "Thread-10",
        timeStamp = 1370103395076L
      )
    )

  val initialFragmentsForJustOneFailedTest: Vector[Fragment] =
    Vector(
      Fragment(Resources("runCompletedIn", makeDurationString(213L)), AnsiCyan),
      Fragment(Resources("totalNumberOfTestsRun", summaryWithOneFailedTest.testsCompletedCount.toString), AnsiCyan),
      Fragment(Resources("suiteSummary", summaryWithOneFailedTest.suitesCompletedCount.toString, summaryWithOneFailedTest.suitesAbortedCount.toString), AnsiCyan),
      Fragment(Resources("testSummary", summaryWithOneFailedTest.testsSucceededCount.toString, summaryWithOneFailedTest.testsFailedCount.toString, summaryWithOneFailedTest.testsCanceledCount.toString, summaryWithOneFailedTest.testsIgnoredCount.toString, summaryWithOneFailedTest.testsPendingCount.toString), AnsiCyan),
      Fragment(Resources("oneTestFailed"), AnsiRed),
      Fragment("StringReporterSummarySpec:", AnsiRed),
      Fragment("the summaryFragments method ", AnsiRed),
      Fragment(Resources("specTextAndNote", "- should fail on purpose", Resources("failedNote")), AnsiRed),
      Fragment("  I meant to do that! (StringReporterSummarySpec.scala:" + tfeLineNumber + ")", AnsiRed)
    )

  val fragmentsWhenNoReminderForOneFailedTest: Vector[Fragment] =
    Vector(
      Fragment(Resources("runCompletedIn", makeDurationString(213L)), AnsiCyan),
      Fragment(Resources("totalNumberOfTestsRun", summaryWithOneFailedTest.testsCompletedCount.toString), AnsiCyan),
      Fragment(Resources("suiteSummary", summaryWithOneFailedTest.suitesCompletedCount.toString, summaryWithOneFailedTest.suitesAbortedCount.toString), AnsiCyan),
      Fragment(Resources("testSummary", summaryWithOneFailedTest.testsSucceededCount.toString, summaryWithOneFailedTest.testsFailedCount.toString, summaryWithOneFailedTest.testsCanceledCount.toString, summaryWithOneFailedTest.testsIgnoredCount.toString, summaryWithOneFailedTest.testsPendingCount.toString), AnsiCyan),
      Fragment(Resources("oneTestFailed"), AnsiRed)
    )

  val fragmentsWhenNoReminderForOneFailedAndOneCanceledTest: Vector[Fragment] =
    Vector(
      Fragment(Resources("runCompletedIn", makeDurationString(213L)), AnsiCyan),
      Fragment(Resources("totalNumberOfTestsRun", summaryWithOneFailedAndOneCanceledTest.testsCompletedCount.toString), AnsiCyan),
      Fragment(Resources("suiteSummary", summaryWithOneFailedAndOneCanceledTest.suitesCompletedCount.toString, summaryWithOneFailedAndOneCanceledTest.suitesAbortedCount.toString), AnsiCyan),
      Fragment(Resources("testSummary", summaryWithOneFailedAndOneCanceledTest.testsSucceededCount.toString, summaryWithOneFailedAndOneCanceledTest.testsFailedCount.toString, summaryWithOneFailedAndOneCanceledTest.testsCanceledCount.toString, summaryWithOneFailedAndOneCanceledTest.testsIgnoredCount.toString, summaryWithOneFailedAndOneCanceledTest.testsPendingCount.toString), AnsiCyan),
      Fragment(Resources("oneTestFailed"), AnsiRed)
    )

  val initialFragmentsForOneFailedAndOneCanceledTest: Vector[Fragment] =
    Vector(
      Fragment(Resources("runCompletedIn", makeDurationString(213L)), AnsiCyan),
      Fragment(Resources("totalNumberOfTestsRun", summaryWithOneFailedAndOneCanceledTest.testsCompletedCount.toString), AnsiCyan),
      Fragment(Resources("suiteSummary", summaryWithOneFailedAndOneCanceledTest.suitesCompletedCount.toString, summaryWithOneFailedAndOneCanceledTest.suitesAbortedCount.toString), AnsiCyan),
      Fragment(Resources("testSummary", summaryWithOneFailedAndOneCanceledTest.testsSucceededCount.toString, summaryWithOneFailedAndOneCanceledTest.testsFailedCount.toString, summaryWithOneFailedAndOneCanceledTest.testsCanceledCount.toString, summaryWithOneFailedAndOneCanceledTest.testsIgnoredCount.toString, summaryWithOneFailedAndOneCanceledTest.testsPendingCount.toString), AnsiCyan),
      Fragment(Resources("oneTestFailed"), AnsiRed),
      Fragment("StringReporterSummarySpec:", AnsiRed),
      Fragment("the summaryFragments method ", AnsiRed),
      Fragment(Resources("specTextAndNote", "- should fail on purpose", Resources("failedNote")), AnsiRed),
      Fragment("  I meant to do that! (StringReporterSummarySpec.scala:" + tfeLineNumber + ")", AnsiRed)
    )

  val initialFragmentsTheOneCanceledTestReminder: Vector[Fragment] =
    Vector(
      Fragment("StringReporterSummarySpec:", AnsiYellow),
      Fragment("the summaryFragments method ", AnsiYellow),
      Fragment(Resources("specTextAndNote", "- should cancel on purpose", Resources("canceledNote")), AnsiYellow),
      Fragment("  I meant to do that! (StringReporterSummarySpec.scala:" + tceLineNumber + ")", AnsiYellow)
    )

  object `the summaryFragments method` {
    def `should produce a good summary when Summary is all zeroes` {
      val summary =
        new Summary(
          testsSucceededCount = 0,
          testsFailedCount = 0,
          testsIgnoredCount = 0,
          testsPendingCount = 0,
          testsCanceledCount = 0,
          suitesCompletedCount = 0,
          suitesAbortedCount = 0,
          scopesPendingCount = 0
        )

      val fragments =
        summaryFragments(
          true,
          Some(0L),
          Some(summary),
          Vector.empty,
          presentAllDurations = false,
          presentReminder = true,
          presentReminderWithShortStackTraces = false,
          presentReminderWithFullStackTraces = false,
          presentReminderWithoutCanceledTests = false
        )
      fragments should be (
        Vector(
          Fragment(Resources("runCompletedIn", makeDurationString(0)), AnsiCyan),
          Fragment(Resources("totalNumberOfTestsRun", summary.testsCompletedCount.toString), AnsiCyan),
          Fragment(Resources("suiteSummary", summary.suitesCompletedCount.toString, summary.suitesAbortedCount.toString), AnsiCyan),
          Fragment(Resources("testSummary", summary.testsSucceededCount.toString, summary.testsFailedCount.toString, summary.testsCanceledCount.toString, summary.testsIgnoredCount.toString, summary.testsPendingCount.toString), AnsiCyan),
          Fragment(Resources("allTestsPassed"), AnsiGreen)
        )
      )
    }

    object `when one test failed` {
      def `should produce a good summary when reminders are disabled` {
        val fragments =
          summaryFragments(
            true,
            Some(213L),
            Some(summaryWithOneFailedTest),
            justOneTestFailed,
            presentAllDurations = false,
            presentReminder = false,
            presentReminderWithShortStackTraces = false,
            presentReminderWithFullStackTraces = false,
            presentReminderWithoutCanceledTests = false
          )
        fragments should be (fragmentsWhenNoReminderForOneFailedTest)
      }

      def `should produce a good summary when reminders are enabled but the exceptional events vector is empty` {
        val fragments =
          summaryFragments(
            true,
            Some(213L),
            Some(summaryWithOneFailedTest),
            Vector.empty,
            presentAllDurations = false,
            presentReminder = true,
            presentReminderWithShortStackTraces = false,
            presentReminderWithFullStackTraces = false,
            presentReminderWithoutCanceledTests = false
          )
        fragments should be (fragmentsWhenNoReminderForOneFailedTest)
      }

      def `should produce a good summary when reminders are enabled without stack traces` {
        val fragments =
          summaryFragments(
            true,
            Some(213L),
            Some(summaryWithOneFailedTest),
            justOneTestFailed,
            presentAllDurations = false,
            presentReminder = true,
            presentReminderWithShortStackTraces = false,
            presentReminderWithFullStackTraces = false,
            presentReminderWithoutCanceledTests = false
          )

        fragments should be (initialFragmentsForJustOneFailedTest)
      }

      def `should produce a good summary when reminders are enabled with short stack traces` {
        val fragments =
          summaryFragments(
            true,
            Some(213L),
            Some(summaryWithOneFailedTest),
            justOneTestFailed,
            presentAllDurations = false,
            presentReminder = true,
            presentReminderWithShortStackTraces = true,
            presentReminderWithFullStackTraces = false,
            presentReminderWithoutCanceledTests = false
          )

        fragments.take(initialFragmentsForJustOneFailedTest.size) should be (initialFragmentsForJustOneFailedTest)
        fragments.length should equal (initialFragmentsForJustOneFailedTest.size + StringReporter.shortStackTraceSize) 
      }

      def `should produce a good summary when reminders are enabled with full stack traces` {
        val fragments =
          summaryFragments(
            true,
            Some(213L),
            Some(summaryWithOneFailedTest),
            justOneTestFailed,
            presentAllDurations = false,
            presentReminder = true,
            presentReminderWithShortStackTraces = false,
            presentReminderWithFullStackTraces = true,
            presentReminderWithoutCanceledTests = false
          )

        fragments.take(initialFragmentsForJustOneFailedTest.size) should be (initialFragmentsForJustOneFailedTest)
        fragments.length should be > (initialFragmentsForJustOneFailedTest.size + StringReporter.shortStackTraceSize) 
      }
    }

    object `when one test failed and one canceled` {
      def `should produce a good summary when reminders are disabled` {
        val fragments =
          summaryFragments(
            true,
            Some(213L),
            Some(summaryWithOneFailedAndOneCanceledTest),
            oneTestFailedAndOneTestCanceled,
            presentAllDurations = false,
            presentReminder = false,
            presentReminderWithShortStackTraces = false,
            presentReminderWithFullStackTraces = false,
            presentReminderWithoutCanceledTests = false
          )
        fragments should be (fragmentsWhenNoReminderForOneFailedAndOneCanceledTest)
      }

      def `should produce a good summary when reminders are enabled but the exceptional events vector is empty` {
        val fragments =
          summaryFragments(
            true,
            Some(213L),
            Some(summaryWithOneFailedAndOneCanceledTest),
            Vector.empty,
            presentAllDurations = false,
            presentReminder = true,
            presentReminderWithShortStackTraces = false,
            presentReminderWithFullStackTraces = false,
            presentReminderWithoutCanceledTests = false
          )
        fragments should be (fragmentsWhenNoReminderForOneFailedAndOneCanceledTest)
      }

      def `should produce a good summary when reminders are enabled without stack traces` {
        val fragments =
          summaryFragments(
            true,
            Some(213L),
            Some(summaryWithOneFailedAndOneCanceledTest),
            oneTestFailedAndOneTestCanceled,
            presentAllDurations = false,
            presentReminder = true,
            presentReminderWithShortStackTraces = false,
            presentReminderWithFullStackTraces = false,
            presentReminderWithoutCanceledTests = false
          )

        fragments.take(initialFragmentsForOneFailedAndOneCanceledTest.size) should be (initialFragmentsForOneFailedAndOneCanceledTest)
        fragments.drop(initialFragmentsForOneFailedAndOneCanceledTest.size) should be (initialFragmentsTheOneCanceledTestReminder)
      }

      def `should produce a good summary when reminders are enabled with short stack traces` {
        val fragments =
          summaryFragments(
            true,
            Some(213L),
            Some(summaryWithOneFailedAndOneCanceledTest),
            oneTestFailedAndOneTestCanceled,
            presentAllDurations = false,
            presentReminder = true,
            presentReminderWithShortStackTraces = true,
            presentReminderWithFullStackTraces = false,
            presentReminderWithoutCanceledTests = false
          )

        fragments.take(initialFragmentsForOneFailedAndOneCanceledTest.size) should be (initialFragmentsForOneFailedAndOneCanceledTest)
        fragments.drop(initialFragmentsForOneFailedAndOneCanceledTest.size + StringReporter.shortStackTraceSize).take(initialFragmentsTheOneCanceledTestReminder.size) should be (initialFragmentsTheOneCanceledTestReminder)
        fragments.length should equal (initialFragmentsForJustOneFailedTest.size + initialFragmentsTheOneCanceledTestReminder.size + (2 * StringReporter.shortStackTraceSize))
      }

      def `should produce a good summary when reminders are enabled with full stack traces` {
        val fragments =
          summaryFragments(
            true,
            Some(213L),
            Some(summaryWithOneFailedAndOneCanceledTest),
            oneTestFailedAndOneTestCanceled,
            presentAllDurations = false,
            presentReminder = true,
            presentReminderWithShortStackTraces = false,
            presentReminderWithFullStackTraces = true,
            presentReminderWithoutCanceledTests = false
          )

        fragments.take(initialFragmentsForOneFailedAndOneCanceledTest.size) should be (initialFragmentsForOneFailedAndOneCanceledTest)
        fragments.length should be > (initialFragmentsForJustOneFailedTest.size + initialFragmentsTheOneCanceledTestReminder.size + (2 * StringReporter.shortStackTraceSize))
      }
    }
/*
A test that ensures that if no summary, I get back just the run completed or run stopped message.
A test that ensures canceled ones show up if they are on
A test that ensures canceled tests are elided if they are not on
A test where I see that they come out in ordinal order in the reminder.
A test that ensures recorded events don't show up in the summary.
*/

    /* @org.scalatest.Ignore */
    @org.scalatest.Ignore def `should fail on purpose` {
      fail("I meant to do that!")
    }
    @org.scalatest.Ignore def `should cancel on purpose` {
      cancel("I meant to do that!")
    }
  }
}

