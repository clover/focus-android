/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.activity;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.web.webdriver.Locator;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mozilla.focus.R;
import org.mozilla.focus.activity.MainActivity;
import org.mozilla.focus.activity.TestHelper;
import org.mozilla.focus.activity.helpers.MainActivityFirstrunTestRule;
import org.mozilla.focus.utils.AppConstants;

import java.io.IOException;

import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.PreferenceMatchers.withTitleText;
import static android.support.test.espresso.matcher.ViewMatchers.hasChildCount;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static android.support.test.espresso.web.sugar.Web.onWebView;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.getText;
import static android.support.test.espresso.web.webdriver.DriverAtoms.webClick;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.mozilla.focus.activity.PullDownToRefreshTest.withCustomConstraints;
import static org.mozilla.focus.activity.TestHelper.progressBar;
import static org.mozilla.focus.activity.TestHelper.waitingTime;
import static org.mozilla.focus.activity.TestHelper.webPageLoadwaitingTime;
import static org.mozilla.focus.activity.helpers.EspressoHelper.navigateToWebsite;
import static org.mozilla.focus.activity.helpers.EspressoHelper.openSettings;

@RunWith(AndroidJUnit4.class)
public class PullDownToRefreshTest {
    private static final String MOZILLA_WEBSITE_SLOGAN_SELECTOR = ".content h2";
    private static final String MOZILLA_WEBSITE_SLOGAN_TEXT = "Internet for people,\nnot profit.";

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new MainActivityFirstrunTestRule(false);

    @ClassRule
    public static final LocaleTestRule localeTestRule = new LocaleTestRule();

    @Test
    public void pullDownToRefreshTest() throws InterruptedException, UiObjectNotFoundException, IOException {
        // Go to a website
        navigateToWebsite(("www.mozilla.org"));

        TestHelper.progressBar.waitForExists(webPageLoadwaitingTime);
        Assert.assertTrue(TestHelper.progressBar.exists());
        //Assert.assertTrue(TestHelper.progressBar.waitUntilGone(webPageLoadwaitingTime));

        // Check that the webview is loaded
        assertWebsiteUrlContains("mozilla.org");

        onWebView()
                .withElement(findElement(Locator.CSS_SELECTOR, MOZILLA_WEBSITE_SLOGAN_SELECTOR))
                .check(webMatches(getText(), containsString(MOZILLA_WEBSITE_SLOGAN_TEXT)));

        // Swipe down to refresh, spinner is shown (2nd child) and progress bar is shown
        onView(withId(R.id.swipe_refresh))
                .perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)))
                .check(matches(hasChildCount(2)));

        //Assert.assertTrue(TestHelper.progressBar.waitForExists(webPageLoadwaitingTime));

        // Check that the webview is loaded again and the progress bar dissapears
        onWebView()
                .withElement(findElement(Locator.CSS_SELECTOR, MOZILLA_WEBSITE_SLOGAN_SELECTOR))
                .check(webMatches(getText(), containsString(MOZILLA_WEBSITE_SLOGAN_TEXT)));

        Assert.assertFalse(TestHelper.progressBar.exists());
    }

    private void assertWebsiteUrlContains(String substring) {
        onView(withId(R.id.display_url))
                .check(matches(isDisplayed()))
                .check(matches(withText(containsString(substring))));
    }
    public static ViewAction withCustomConstraints(final ViewAction action, final Matcher<View> constraints) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return constraints;
            }

            @Override
            public String getDescription() {
                return action.getDescription();
            }

            @Override
            public void perform(UiController uiController, View view) {
                action.perform(uiController, view);
            }
        };
    }
}
