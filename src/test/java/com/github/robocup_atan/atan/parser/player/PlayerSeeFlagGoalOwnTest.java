package com.github.robocup_atan.atan.parser.player;

/*
 * #%L
 * Atan
 * %%
 * Copyright (C) 2003 - 2014 Atan
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import com.github.robocup_atan.atan.model.enums.Flag;
import com.github.robocup_atan.atan.parser.BaseCommandFilter;
import com.github.robocup_atan.atan.parser.Filter;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized;

/**
 * Test class for parsing the player see command.
 *
 * @author Atan
 */
@RunWith(Parameterized.class)
public class PlayerSeeFlagGoalOwnTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {

                /**
                 * Tests for seeing the goal flags are captured in the following
                 * array structure.
                 *   0: Server command.
                 *   1: Value of isTeamEast.
                 *   2: Expected value of flag.
                 *   3: Expected value of distance.
                 *   4: Expected value of direction.
                 *   5: Expected value of distance change.
                 *   6: Expected value of direction change.
                 *   7: Expected value of body facing direction.
                 *   8: Expected value of head facing direction.
                 */

                // East team see own goal flag messages.
                {"(see 123.45 ((f g r b) 123.45 123.45 123.45 123.45 123.45 123.45))", true,  Flag.LEFT,   123.45, 123.45, 123.45, 123.45, 123.45, 123.45},
                {"(see 123.45 ((f g r) 123.45 123.45 123.45 123.45 123.45 123.45))",   true,  Flag.CENTER, 123.45, 123.45, 123.45, 123.45, 123.45, 123.45},
                {"(see 123.45 ((f g r t) 123.45 123.45 123.45 123.45 123.45 123.45))", true,  Flag.RIGHT,  123.45, 123.45, 123.45, 123.45, 123.45, 123.45},
                // West team see own goal flag messages.
                {"(see 123.45 ((f g l b) 123.45 123.45 123.45 123.45 123.45 123.45))", false, Flag.RIGHT,  123.45, 123.45, 123.45, 123.45, 123.45, 123.45},
                {"(see 123.45 ((f g l) 123.45 123.45 123.45 123.45 123.45 123.45))",   false, Flag.CENTER, 123.45, 123.45, 123.45, 123.45, 123.45, 123.45},
                {"(see 123.45 ((f g l t) 123.45 123.45 123.45 123.45 123.45 123.45))", false, Flag.LEFT,   123.45, 123.45, 123.45, 123.45, 123.45, 123.45},

            });
    }

    // Global test variables.
    private CmdParserPlayer parser;
    private TestFilter      commandFilter;
    private Filter          filter;

    // Test parameters.
    private String  command;
    private boolean isTeamEast;
    private Flag    expectedFlag;
    private double  expectedDistance;
    private double  expectedDirection;
    private double  expectedDistChange;
    private double  expectedDirChange;
    private double  expectedBodyFacingDirection;
    private double  expectedHeadFacingDirection;

    public PlayerSeeFlagGoalOwnTest(
        String  command,
        boolean isTeamEast,
        Flag    flag,
        double  distance,
        double  direction,
        double  distChange,
        double  dirChange,
        double  bodyFacingDirection,
        double  headFacingDirection) {

        this.command                     = command;
        this.isTeamEast                  = isTeamEast;
        this.expectedFlag                = flag;
        this.expectedDistance            = distance;
        this.expectedDirection           = direction;;
        this.expectedDistChange          = distChange;
        this.expectedDirChange           = dirChange;
        this.expectedBodyFacingDirection = bodyFacingDirection;
        this.expectedHeadFacingDirection = headFacingDirection;
    }

    @Before
    public void setUpTest() throws Exception {
        parser        = new CmdParserPlayer(new StringReader(""));
        commandFilter = new TestFilter();
        filter        = new Filter();
    }

    @Test
    public void parseCommand() {
        filter.run(command, commandFilter);
    }

    private class TestFilter extends BaseCommandFilter {

        private TestController controller = new TestController();
        private TestActions    action     = new TestActions();

        @Override
        public void seeCommand(String cmd) {
            try {
                parser.parseSeeCommand(cmd, controller, action);
            } catch (ParseException ex) {
                Assert.fail(ex.getMessage());
            }
        }
    }

    private class TestController extends BaseControllerPlayer {

        @Override
        public void infoSeeFlagGoalOwn(
            Flag flag,
            double distance,
            double direction,
            double distChange,
            double dirChange,
            double bodyFacingDirection,
            double headFacingDirection) {

            Assert.assertEquals(expectedFlag, flag);
            Assert.assertEquals(expectedDistance, distance, 0);
            Assert.assertEquals(expectedDirection, direction, 0);
            Assert.assertEquals(expectedDistChange, distChange, 0);
            Assert.assertEquals(expectedDirChange, dirChange, 0);
            Assert.assertEquals(expectedBodyFacingDirection, bodyFacingDirection, 0);
            Assert.assertEquals(expectedHeadFacingDirection, headFacingDirection, 0);
        }
    }

    private class TestActions extends BaseActionsPlayer {

        @Override
        public boolean isTeamEast() {
            return isTeamEast;
        }
    }

    // Test logging
    @Rule
    public TestWatcher watchman = new TestWatcher() {

        @Override
        protected void succeeded(Description description) {
            System.out.print("Passed - ");
        }

        @Override
        protected void failed(Throwable e, Description description) {
            System.out.print("FAILED - ");
        }

        @Override
        protected void finished(Description description) {
            System.out.println(command);
        }
    };
}

